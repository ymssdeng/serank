package com.bj58.seo.serank.model;

import com.bj58.seo.oceanusex.dal.OceanusEntity;
import com.bj58.seo.oceanusex.dal.RowKey;
import com.bj58.seo.oceanusex.dal.Table;

@Table(name = "t_keyword")
public class Keyword implements OceanusEntity {

  @RowKey(autoIncrement = true)
  private int id_keyword = 0;
  private String keyword = null;
  private int business_line = 0;
  private int cityid = 0;
  private int cate1 = 0;
  private int cate2 = 0;
  private int cate3 = 0;
  private int cate4 = 0;
  private int id_keyword_mark = 0;

  public int getId_keyword() {
    return id_keyword;
  }

  public void setId_keyword(int id_keyword) {
    this.id_keyword = id_keyword;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public int getBusiness_line() {
    return business_line;
  }

  public void setBusiness_line(int business_line) {
    this.business_line = business_line;
  }

  public int getCityid() {
    return cityid;
  }

  public void setCityid(int cityid) {
    this.cityid = cityid;
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

  public int getId_keyword_mark() {
    return id_keyword_mark;
  }

  public void setId_keyword_mark(int id_keyword_mark) {
    this.id_keyword_mark = id_keyword_mark;
  }

  @Override
  public String toString() {
    return "KeyWordModel [id_keyword=" + id_keyword + ", keyword=" + keyword + ", business_line="
        + business_line + ", cityid=" + cityid + ", cate1=" + cate1 + ", cate2=" + cate2
        + ", cate3=" + cate3 + ", cate4=" + cate4 + ", id_keyword_mark=" + id_keyword_mark + "]";
  }

}
