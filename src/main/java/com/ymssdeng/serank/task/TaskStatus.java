package com.ymssdeng.serank.task;

public enum TaskStatus {

  CREATE(0, "已创建"),

  INIT(1, "已初始化"),

  WAITING(2, "等待中"),

  START(3, "已开始"),

  SUCCESS(8, "执行完成"),

  FAIL(9, "执行失败");

  private int id = 0;
  private String status = null;

  private TaskStatus(int id, String status) {
    this.id = id;
    this.status = status;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
