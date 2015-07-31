package com.ymssdeng.serank.keyword;

import java.util.Date;

/**
 * 关键词排名信息
 * 
 * @author hui.deng
 *
 */
public class CateKeywordRank implements Keyword {

  private int id = 0;
  private int taskid = 0;
  private Date exedate = null;
  private int keyword_id = 0;
  private String keyword = null;
  private int markid = 0;
  private int se_type = 0;
  private int local_id = 0;
  private int cate1 = 0;
  private int cate2 = 0;
  private int cate3 = 0;
  private int cate4 = 0;
  private int top = 0;
  private String host = null;
  private String mainhost = null;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getTaskid() {
    return taskid;
  }

  public void setTaskid(int taskid) {
    this.taskid = taskid;
  }

  public Date getExedate() {
    return exedate;
  }

  public void setExedate(Date exedate) {
    this.exedate = exedate;
  }

  public int getKeyword_id() {
    return keyword_id;
  }

  public void setKeyword_id(int keyword_id) {
    this.keyword_id = keyword_id;
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

  @Override
  public String toString() {
    return "KeywordRank [id=" + id + ", taskid=" + taskid + ", exedate=" + exedate
        + ", keyword_id=" + keyword_id + ", keyword=" + keyword + ", markid=" + markid
        + ", se_type=" + se_type + ", local_id=" + local_id + ", cate1=" + cate1 + ", cate2="
        + cate2 + ", cate3=" + cate3 + ", cate4=" + cate4 + ", top=" + top + ", host=" + host
        + ", mainhost=" + mainhost + "]";
  }


}
