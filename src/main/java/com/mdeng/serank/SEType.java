package com.mdeng.serank;

public enum SEType {
  Baidu("baidu.com"), Baidu_M("m.baidu.com"), Sougou("sougou.com");

  private String host;

  private SEType(String host) {
    this.host = host;
  }

  public String getHost() {
    return host;
  }

}
