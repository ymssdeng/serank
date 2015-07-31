package com.ymssdeng.serank.keyword;

/**
 * 分类别的关键词
 * 
 * @author hui.deng
 *
 */
public class CateKeyword implements Keyword {
  private int id;
  private String keyword;
  private int local_id;
  private int cate1;
  private int cate2;
  private int cate3;
  private int cate4;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public int getLocal_id() {
    return local_id;
  }

  public void setLocal_id(int local_id) {
    this.local_id = local_id;
  }

  public int getCate1() {
    return cate1;
  }

  public void setCate1(int cate1) {
    this.cate1 = cate1;
  }

  public int getCate2() {
    return cate2;
  }

  public void setCate2(int cate2) {
    this.cate2 = cate2;
  }

  public int getCate3() {
    return cate3;
  }

  public void setCate3(int cate3) {
    this.cate3 = cate3;
  }

  public int getCate4() {
    return cate4;
  }

  public void setCate4(int cate4) {
    this.cate4 = cate4;
  }

  @Override
  public String toString() {
    return "CateKeyword [id=" + id + ", keyword=" + keyword + ", local_id=" + local_id + ", cate1="
        + cate1 + ", cate2=" + cate2 + ", cate3=" + cate3 + ", cate4=" + cate4 + "]";
  }

}
