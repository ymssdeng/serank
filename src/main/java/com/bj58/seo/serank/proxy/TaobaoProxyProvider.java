package com.bj58.seo.serank.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bj58.seo.core.http.HttpRequestBuilder;
import com.bj58.seo.core.http.HttpResponseHandlers;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@Component
public class TaobaoProxyProvider implements ProxyProvider {

  private static Logger logger = LoggerFactory.getLogger(TaobaoProxyProvider.class);

  private static final String urlTemplate =
      "http://115.29.136.51/get.php?tid=A3969657691719&num=%d";

  @Override
  public List<HttpHost> getProxies(int size) {
    List<HttpHost> ret = Lists.newArrayList();

    String url = String.format(urlTemplate, size);
    String content = null;
    try {
      content = HttpRequestBuilder.create().get(url).execute(HttpResponseHandlers.stringHandler());
      if (Strings.isNullOrEmpty(content)) {
        logger.error("Failed to get proxies from taobao provider");
        return ret;
      } else if ("请求速度过快".equals(content)) {
        logger.error("too often get proxies from taobao provider");
        return ret;
      }
    } catch (Exception e) {
      logger.error("Failed to get proxies from taobao provider", e);
      return ret;
    }

    BufferedReader bufferedReader = new BufferedReader(new StringReader(content));

    String line = null;
    try {
      while ((line = bufferedReader.readLine()) != null) {
        String[] splits = line.split(":");
        if (splits.length != 2) {
          continue;
        } else if (Strings.isNullOrEmpty(splits[0]) || Strings.isNullOrEmpty(splits[0].trim())
            || Strings.isNullOrEmpty(splits[1]) || Strings.isNullOrEmpty(splits[1].trim())) {
          continue;
        }

        HttpHost httpHost = new HttpHost(splits[0].trim(), Integer.parseInt(splits[1]));
        ret.add(httpHost);
      }
    } catch (IOException e) {
      logger.error("Failed to parse proxy: {}", e.getMessage());
    }

    return ret;
  }

}
