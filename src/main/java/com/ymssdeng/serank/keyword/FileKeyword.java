package com.ymssdeng.serank.keyword;

public class FileKeyword implements Keyword {
  private String keyword;
  private String filename;
  private int index;

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  @Override
  public String toString() {
    return "FileKeyword [filename=" + filename + ", index=" + index + ", keyword=" + keyword + "]";
  }

}
