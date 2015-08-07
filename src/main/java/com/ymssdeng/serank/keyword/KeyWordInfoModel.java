package com.ymssdeng.serank.keyword;

import com.ymssdeng.oceanusex.dal.OceanusEntity;
import com.ymssdeng.oceanusex.dal.RowKey;
import com.ymssdeng.oceanusex.dal.Table;

@Table(name = "t_keyword_info")
public class KeyWordInfoModel implements OceanusEntity {

  @RowKey(autoIncrement = true)
  private int id_keyword_info = 0;
  private int taskid = 0;
  private String exedate = null;
  private int id_keyword = 0;
  private String keyword = null;
  private int markid = 0;
  private int se_type = 0;
  private int business_line = 0;
  private int cityid = 0;
  private int cate1 = 0;
  private int cate2 = 0;
  private int cate3 = 0;
  private int cate4 = 0;
  private int top = 0;
  private String host = null;
  private String mainhost = null;
  private String true_url = null;

  public int getId_keyword_info() {
    return id_keyword_info;
  }

  public void setId_keyword_info(int id_keyword_info) {
    this.id_keyword_info = id_keyword_info;
  }

  public int getTaskid() {
    return taskid;
  }

  public void setTaskid(int taskid) {
    this.taskid = taskid;
  }

  public String getExedate() {
    return exedate;
  }

  public void setExedate(String exedate) {
    this.exedate = exedate;
  }

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

  public int getMarkid() {
    return markid;
  }

  public void setMarkid(int markid) {
    this.markid = markid;
  }

  public int getSe_type() {
    return se_type;
  }

  public void setSe_type(int se_type) {
    this.se_type = se_type;
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

  public int getTop() {
    return top;
  }

  public void setTop(int top) {
    this.top = top;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getMainhost() {
    return mainhost;
  }

  public void setMainhost(String mainhost) {
    this.mainhost = mainhost;
  }

  public String getTrue_url() {
    return true_url;
  }

  public void setTrue_url(String true_url) {
    this.true_url = true_url;
  }

}
