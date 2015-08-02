package com.ymssdeng.serank.keyword;

import java.util.Date;

public class KeywordRank {
  private int rank;
  private String host;
  private String mainhost;
  private Date exedate;
  
  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
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

  public Date getExedate() {
    return exedate;
  }

  public void setExedate(Date exedate) {
    this.exedate = exedate;
  }


}
