package com.ymssdeng.serank.task;

import com.ymssdeng.oceanusex.dal.OceanusEntity;
import com.ymssdeng.oceanusex.dal.RowKey;
import com.ymssdeng.oceanusex.dal.Table;

@Table(name = "t_task_keyword")
public class TaskKeyword implements OceanusEntity {

  @RowKey(autoIncrement = true)
  private int id_task_keyword = 0;
  private int taskid = 0;
  private int id_keyword = 0;

  public int getId_task_keyword() {
    return id_task_keyword;
  }

  public void setId_task_keyword(int id_task_keyword) {
    this.id_task_keyword = id_task_keyword;
  }

  public int getTaskid() {
    return taskid;
  }

  public void setTaskid(int taskid) {
    this.taskid = taskid;
  }

  public int getId_keyword() {
    return id_keyword;
  }

  public void setId_keyword(int id_keyword) {
    this.id_keyword = id_keyword;
  }

  @Override
  public String toString() {
    return "TaskKeyword [id_task_keyword=" + id_task_keyword + ", taskid=" + taskid
        + ", id_keyword=" + id_keyword + "]";
  }
  
  
}
