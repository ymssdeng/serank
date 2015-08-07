package com.ymssdeng.serank.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class CommonHttpHeader {
  public static List<Header> get() {
    List<Header> headerList = new ArrayList<Header>();

    int type = new Random().nextInt(3);
    switch (type) {
      case 0:// FireFox
        headerList.add(new BasicHeader("Accept",
            "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
        headerList.add(new BasicHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3"));
        // headerList.add(new BasicHeader("Cache-Control", "max-age=0"));
        // headerList.add(new BasicHeader("DNT", "1"));
        headerList.add(new BasicHeader("User-Agent",
            "Mozilla/5.0 (Windows NT 6.1; rv:27.0) Gecko/20100101 Firefox/27.0"));
        headerList.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
        break;
      case 1:
        // IE
        headerList.add(new BasicHeader("Accept", "text/html, application/xhtml+xml, */*"));
        headerList.add(new BasicHeader("Accept-Language", "zh-CN"));
        // headerList.add(new BasicHeader("DNT", "1"));
        headerList.add(new BasicHeader("User-Agent",
            "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)"));
        headerList.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
        break;
      case 2:
        // Chrome
        headerList.add(new BasicHeader("Accept",
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
        headerList.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8"));
        headerList.add(new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"));
        // headerList.add(new BasicHeader("Cache-Control", "max-age=0"));
        headerList
            .add(new BasicHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.117 Safari/537.36"));
        break;
      default:
        break;
    }
    return headerList;
  }
}
