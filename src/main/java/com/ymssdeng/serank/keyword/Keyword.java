package com.ymssdeng.serank.keyword;

/**
 * 搜索关键词
 * 
 * @author hui.deng
 *
 */
public class Keyword {
  protected String keyword;

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  // to be override
  public boolean isValid() {
    if (keyword == null) return false;
    keyword = keyword.trim();
    return keyword.length() > 0;
  }
}
