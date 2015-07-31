package com.ymssdeng.serank.task;

import java.util.Date;

public class Task {
  private int id;
  private String name;
  private int status;
  private String params;
  private Date created;
  private Date updated;
  
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  @Override
  public String toString() {
    return "SERankTask [id=" + id + ", name=" + name + ", status=" + status + ", params=" + params
        + ", created=" + created + ", updated=" + updated + "]";
  }

}
