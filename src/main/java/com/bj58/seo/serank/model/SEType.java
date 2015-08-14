package com.bj58.seo.serank.model;

import java.util.Arrays;
import java.util.List;

public enum SEType {
  BAIDU(1, "百度", "baidu.com"),

  SOUGOU(2, "搜狗", "sogou.com"),

  GOOGLE(3, "谷歌", "google.com"),

  QIHU360(4, "奇虎360", "so.com"),

  M_BAIDU(5, "百度-M", "m.baidu.com"),

  M_QIHU360(6, "奇虎360-M", "m.so.com");

  private int id = 0;
  private String name = null;
  private String host = null;

  private SEType(int id, String name, String host) {
    this.id = id;
    this.name = name;
    this.host = host;
  }

  private static List<SEType> allSETypes = Arrays.asList(BAIDU, QIHU360, M_BAIDU, M_QIHU360);

  public static List<SEType> getAllSETypes() {
    return allSETypes;
  }

  public static SEType value(int id) {
    SEType se = null;
    switch (id) {
      case 1:
        se = BAIDU;
        break;
      case 2:
        se = SOUGOU;
        break;
      case 3:
        se = GOOGLE;
        break;
      case 4:
        se = QIHU360;
        break;
      case 5:
        se = M_BAIDU;
        break;
      case 6:
        se = M_QIHU360;
        break;
      default:
        throw new IllegalArgumentException("unknown search engine type " + id);
    }

    return se;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getHost() {
    return host;
  }

}
