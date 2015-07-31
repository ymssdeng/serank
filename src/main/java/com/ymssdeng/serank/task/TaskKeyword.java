package com.ymssdeng.serank.task;

public class TaskKeyword {
  private int id;
  private int task_id;
  private int keyword_id;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getTask_id() {
    return task_id;
  }

  public void setTask_id(int task_id) {
    this.task_id = task_id;
  }

  public int getKeyword_id() {
    return keyword_id;
  }

  public void setKeyword_id(int keyword_id) {
    this.keyword_id = keyword_id;
  }

  @Override
  public String toString() {
    return "SERankTaskKeyword [id=" + id + ", task_id=" + task_id + ", keyword_id=" + keyword_id
        + "]";
  }


}
