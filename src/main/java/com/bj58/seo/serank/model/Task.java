package com.bj58.seo.serank.model;

import java.util.Date;

import com.bj58.seo.oceanusex.dal.OceanusEntity;
import com.bj58.seo.oceanusex.dal.RowKey;
import com.bj58.seo.oceanusex.dal.Table;

@Table(name = "t_spider_task")
public class Task implements OceanusEntity {

  @RowKey(autoIncrement = true)
  private int id_spider_task = 0;
  private String taskname = null;
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
    return "Task [id_spider_task=" + id_spider_task + ", taskname=" + taskname + ", params="
        + params + ", status=" + status + ", info=" + info + ", createtime=" + createtime
        + ", updatetime=" + updatetime + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id_spider_task;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Task other = (Task) obj;
    if (id_spider_task != other.id_spider_task) return false;
    return true;
  }


}
