package com.bj58.seo.serank.model;

import java.util.List;

public class TaskCursor {
  private int seType;
  private List<Integer> kewords;
  private int lastIndex;
  
  public TaskCursor() {
    super();
  }

  public TaskCursor(int seType) {
    super();
    this.seType = seType;
  }

  public int getSeType() {
    return seType;
  }

  public void setSeType(int seType) {
    this.seType = seType;
  }

  public List<Integer> getKewords() {
    return kewords;
  }

  public void setKewords(List<Integer> kewords) {
    this.kewords = kewords;
  }

  public void clearKeywords() {
    kewords.clear();
  }

  public int getLastIndex() {
    return lastIndex;
  }

  public void setLastIndex(int lastIndex) {
    this.lastIndex = lastIndex;
  }


}
