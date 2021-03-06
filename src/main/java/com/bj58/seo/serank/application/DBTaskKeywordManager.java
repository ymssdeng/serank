package com.bj58.seo.serank.application;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bj58.seo.core.utils.Jsons;
import com.bj58.seo.oceanusex.dal.DBField;
import com.bj58.seo.oceanusex.dal.OceanusResult;
import com.bj58.seo.oceanusex.dal.Pagination;
import com.bj58.seo.oceanusex.orm.OceanusSupport;
import com.bj58.seo.serank.exception.NoMoreException;
import com.bj58.seo.serank.exception.TaskNotRunningException;
import com.bj58.seo.serank.model.Keyword;
import com.bj58.seo.serank.model.KeywordInfo;
import com.bj58.seo.serank.model.Task;
import com.bj58.seo.serank.model.TaskCursor;
import com.bj58.seo.serank.model.TaskKeyword;
import com.bj58.seo.serank.model.TaskStatus;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Component("dbImpl")
public class DBTaskKeywordManager implements TaskKeywordManager {
  private static Logger logger = LoggerFactory.getLogger(DBTaskKeywordManager.class);
  private static List<RunningTask> runnings = new CopyOnWriteArrayList<RunningTask>();
  private static OceanusSupport<Task> tst;
  private static OceanusSupport<Keyword> kst;
  private static OceanusSupport<TaskKeyword> tkst;
  private static OceanusSupport<KeywordInfo> kis;
  private static ConcurrentMap<Integer, Object> lockMap = Maps.newConcurrentMap();
  private static int limit = 1;
  private static String sptypes = "1,5";

//  static {
//    try {
//      String configPath = WF.getConfigFolder() + WF.getNamespace() + "/oceanus_configurations.xml";
//      Oceanus.init(configPath);
//      tst = OceanusSupports.get(Task.class);
//      kst = OceanusSupports.get(Keyword.class);
//      tkst = OceanusSupports.get(TaskKeyword.class);
//      kis = OceanusSupports.get(KeywordInfo.class);
//
//      limit = MarketShareConfig.getInstance().getTaskCount();
//      List<SETypeEnum> lst = MarketShareConfig.getInstance().getSpiders();
//      for (SETypeEnum item : lst) {
//        sptypes += item.getId() + ",";
//      }
//      sptypes = sptypes.substring(0, sptypes.length() - 1);
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }

  public List<Task> getTasks() throws Exception {
    int size = runnings.size();
    if (size >= limit) {
      throw new NoMoreException("Can't hold more tasks to run");
    }

    // 如何区分异常终止的任务与正常开始的任务？
    // 正常开始将更新info为running，正常结束后又设置info为空
    // 故此处判断如果info为running即为异常终止的任务
    List<Task> tasks = Lists.newArrayList();
    // 先查start状态
    int remaining = limit - size;
    StringBuilder whereBuilder = new StringBuilder("status=?");
    List<Object> values = Lists.newArrayList();
    values.add(TaskStatus.START.getId());
    if (size > 0) {
      for (RunningTask rt : runnings) {
        whereBuilder.append(" and id_spider_task != ?");
        values.add(rt.getTask().getId_spider_task());
      }
    }
    Pagination pgn = new Pagination(1, remaining);
    OceanusResult<Task> oret = tst.pagination(whereBuilder.toString(), pgn, values.toArray());
    for (Task task : oret.getList()) {
      boolean flag = "running".equals(task.getInfo());
      if (flag) {
        logger.info("Detected unfinished task {}", task.getTaskname());
      }

      task.setInfo("running"); // 设置running flag
      tst.update(task, "info");

      tasks.add(task);
      runnings.add(new RunningTask(task, flag));
      remaining--;
    }

    // 再查executing状态
    if (remaining > 0) {
      DBField status = new DBField("status", TaskStatus.EXECUTING.getId());
      pgn = new Pagination(1, remaining);
      oret = tst.getByFields(pgn, status);
      for (Task task : oret.getList()) {
        task.setStatus(TaskStatus.START.getId()); // 设置任务状态为开始
        task.setInfo("running");
        tst.update(task, "status", "info");

        tasks.add(task);
        runnings.add(new RunningTask(task));
        remaining--;
      }
    }

    logger.info("Got {} taks to run", tasks.size());
    return tasks;
  }

  public Keyword nextKeyword(int taskId, int seTypeId) throws Exception {
    RunningTask rt = null;
    for (RunningTask item : runnings) {
      if (item.getTask().getId_spider_task() == taskId) {
        rt = item;
        break;
      }
    }
    if (rt == null) {
      throw new TaskNotRunningException("task: " + taskId + " not found in running tasks list");
    }

    // 每个task对应不同的锁
    Object obj = new Object();
    Object obj2 = lockMap.putIfAbsent(taskId, obj);
    Object obj3 = obj2 == null ? obj : obj2;
    synchronized (obj3) {
      Task task = tst.getById(taskId);
      // 任务是否被暂停
      if (task.getStatus() != TaskStatus.START.getId()) {
        runnings.remove((Object) taskId);
        throw new TaskNotRunningException("task not running: " + task);
      }

      // 查看该spider是否已被其他线程完成
      if (rt.getFinishedSpiders().contains(seTypeId)) {
        return null;
      }

      // params保存线程们正抓取的关键词
      String where = "taskid=? and id_keyword>?";
      Pagination pgn = new Pagination(1, 1);
      int index = 0;
      String params = task.getParams(); // params 作为游标
      List<TaskCursor> cursors = null;
      TaskCursor cursor = null;
      if (!Strings.isNullOrEmpty(params)) {
        cursors = Jsons.json2List(params, TaskCursor[].class);
        if (rt.isUnfinished()) {
          // 将所有se对应的keyword游标设置为最小值减1，以使用统一的where条件查询下一关键词
          for (TaskCursor tc : cursors) {
            List<Integer> ids = tc.getKewords();
            Collections.sort(ids);
            // 异常结束时线程正抓取的关键词，应重新抓取
            tc.setLastIndex(ids.get(0));
            ids.clear();
          }
          // flag只使用一次
          rt.setUnfinished(false);
        }

        for (TaskCursor tc : cursors) {
          if (tc.getSeType() == seTypeId) {
            if (tc.getLastIndex() > 0) {
              index = tc.getLastIndex() - 1;
              // lastIndex只使用一次
              tc.setLastIndex(0);
            } else {
              List<Integer> ids = tc.getKewords();
              Collections.sort(ids);
              // 下一个关键词
              index = ids.get(ids.size() - 1);
            }
            cursor = tc;
            break;
          }
        }
      }

      OceanusResult<TaskKeyword> oret = tkst.pagination(where, pgn, taskId, index);
      if (oret.getList().size() == 0) {
        String msg = String.format("no more keyword for task %s, se %d", task.getTaskname(), seTypeId);
        throw new NoMoreException(msg);
      } else {
        int kid = oret.getList().get(0).getId_keyword();
        Keyword keyword = kst.getById(kid);

        // 设置游标
        if (cursors == null) {
          cursor = new TaskCursor(seTypeId);
          cursor.setKewords(Arrays.asList(kid));
          cursors = Lists.newArrayList(cursor);
        } else {
          if (cursor == null) {
            cursor = new TaskCursor(seTypeId);
            cursor.setKewords(Arrays.asList(kid));
            // unsupported exception
            cursors = Lists.newArrayList(cursors);
            cursors.add(cursor);
          } else if (!cursor.getKewords().contains(kid)) {
            cursor.getKewords().add(kid);
          }
        }
        task.setParams(Jsons.obj2Json(cursors));
        tst.update(task, "params");

        String msg = String.format("next keyword %s of task %s for spider %d", keyword.getKeyword(), task.getTaskname(), seTypeId);
        logger.info(msg);
        return keyword;
      }
    }
  }

  public boolean setCursor(int taskId, int seTypeId, int keywordId,
      List<KeywordInfo> infos) throws Exception {
    if (infos.isEmpty()) return false;
    
    Object obj = lockMap.get(taskId);
    if (obj == null) {
      throw new IllegalStateException("set cursor before get keyword");
    }
    
    kis.insert(infos);
    synchronized(obj) {
      Task task = tst.getById(taskId);
      RunningTask rt = null;
      for (RunningTask item : runnings) {
        if (item.getTask().getId_spider_task() == taskId) {
          rt = item;
          break;
        }
      }

      String params = task.getParams(); // params 作为游标
      List<TaskCursor> cursors = Lists.newArrayList(Jsons.json2List(params, TaskCursor[].class));
      Iterator<TaskCursor> itr = cursors.iterator();
      while (itr.hasNext()) {
        TaskCursor tc = itr.next();
        if (tc.getSeType() == seTypeId) {
          // 从游标中去掉已完成的关键词
          tc.getKewords().remove((Object) keywordId);
          if (tc.getKewords().isEmpty()) {
            itr.remove();
            rt.finish(seTypeId);
          }

          if (!rt.allFinished()) {
            task.setParams(Jsons.obj2Json(cursors));
            tst.update(task, "params");
            return false;
          }
          return true;
        }
      }
      throw new IllegalStateException("not found task cursor");  
    }
  }

  static class RunningTask {
    private Set<Integer> finishedSpiders = Sets.newTreeSet();
    private Task task;
    private boolean unfinished;// 表示是否为上次异常终止的任务

    public RunningTask(Task task) {
      super();
      this.task = task;
    }

    public RunningTask(Task task, boolean unfinished) {
      super();
      this.task = task;
      this.unfinished = unfinished;
    }

    public void finish(int setype) throws Exception {
      finishedSpiders.add(setype);
      if (allFinished()) {
        task.setStatus(TaskStatus.SUCCESS.getId());
        task.setParams("");
        task.setInfo("");
        tst.update(task, "status", "params", "info");
        runnings.remove(this);
        lockMap.remove(task.getId_spider_task());
        logger.info("SUCCESS TASK {}", task.getTaskname());
      }
    }

    public Set<Integer> getFinishedSpiders() {
      return finishedSpiders;
    }

    public boolean allFinished() {
      Joiner joiner = Joiner.on(',');
      return joiner.join(finishedSpiders).equals(sptypes);
    }

    public Task getTask() {
      return task;
    }

    public void setTask(Task task) {
      this.task = task;
    }

    public boolean isUnfinished() {
      return unfinished;
    }

    public void setUnfinished(boolean unfinished) {
      this.unfinished = unfinished;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((task == null) ? 0 : task.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      RunningTask other = (RunningTask) obj;
      if (task == null) {
        if (other.task != null) return false;
      } else if (!task.equals(other.task)) return false;
      return true;
    }

  }

  public static void main(String[] args) {
    System.out.println(Jsons.obj2Json(new Integer(100)));
  }
}
