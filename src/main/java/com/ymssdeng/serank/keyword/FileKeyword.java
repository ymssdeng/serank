package com.ymssdeng.serank.keyword;

public class FileKeyword extends Keyword {
  private String filename;
  private int index;

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
