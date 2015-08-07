package com.ymssdeng.serank.task;

import java.util.Date;

import com.ymssdeng.oceanusex.dal.OceanusEntity;
import com.ymssdeng.oceanusex.dal.RowKey;
import com.ymssdeng.oceanusex.dal.Table;

@Table(name="t_spider_task")
public class Task implements OceanusEntity {
  @RowKey(autoIncrement=true)
  private int id_spider_task = 0;
  private String taskname = null;
  //@Transient ?
  private String exedate = null;
  private String params = null;
  private int status = 0;
  private String info = null;
  private Date createtime = null;
  private Date updatetime = null;

  public int getId_spider_task() {
      return id_spider_task;
  }

  public void setId_spider_task(int id_spider_task) {
      this.id_spider_task = id_spider_task;
  }

  public String getTaskname() {
      return taskname;
  }

  public void setTaskname(String taskname) {
      this.taskname = taskname;
  }

  public String getExedate() {
      return exedate;
  }

  public void setExedate(String exedate) {
      this.exedate = exedate;
  }

  public String getParams() {
      return params;
  }

  public void setParams(String params) {
      this.params = params;
  }

  public int getStatus() {
      return status;
  }

  public void setStatus(int status) {
      this.status = status;
  }

  public String getInfo() {
      return info;
  }

  public void setInfo(String info) {
      this.info = info;
  }

  public Date getCreatetime() {
      return createtime;
  }

  public void setCreatetime(Date createtime) {
      this.createtime = createtime;
  }

  public Date getUpdatetime() {
      return updatetime;
  }

  public void setUpdatetime(Date updatetime) {
      this.updatetime = updatetime;
  }

  @Override
  public String toString() {
    return "Task [id_spider_task=" + id_spider_task + ", taskname=" + taskname + ", exedate="
        + exedate + ", params=" + params + ", status=" + status + ", info=" + info
        + ", createtime=" + createtime + ", updatetime=" + updatetime + "]";
  }

}
