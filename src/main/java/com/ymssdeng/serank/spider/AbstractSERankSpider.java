package com.ymssdeng.serank.spider;

import java.net.URL;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Strings;
import com.ymssdeng.basis.helper.http.HttpRequestBuilder;
import com.ymssdeng.basis.helper.http.HttpResponseHandlers;
import com.ymssdeng.serank.SERankRegex;
import com.ymssdeng.serank.SEType;
import com.ymssdeng.serank.keyword.Keyword;
import com.ymssdeng.serank.keyword.KeywordRank;
import com.ymssdeng.serank.keyword.consumer.KeywordRankConsumer;
import com.ymssdeng.serank.keyword.provider.KeywordProvider;
import com.ymssdeng.serank.proxy.HttpProxyPool;

/**
 * Abstract spider for keyword rank in search engine.
 * 
 * @author Administrator
 *
 */
public abstract class AbstractSERankSpider implements Runnable {

  protected Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * Max grab times for each keyword
   */
  protected int retries = 3;
  protected SERankRegex serRegex = new SERankRegex();
  @Autowired
  protected KeywordProvider kp;
  @Autowired(required = false)
  protected KeywordRankConsumer krc;
  @Autowired
  protected HttpProxyPool pool;
  @Value("${serank.proxy.enabled}")
  protected boolean proxyEnabled;
  @Value("${serank.spider.top}")
  protected int top = 10;

  private static final int CONNECTION_TIMEOUT = 10 * 1000;
  private static final int SOCKET_TIMEOUT = 10 * 1000;

  protected abstract SEType getSEType();

  @Override
  public void run() {
    if (kp == null) {
      logger.error("keyword provider null");
      return;
    }

    try {
      while (kp.hasNextKeyword()) {
        Keyword keyword = kp.nextKeyword();
        if (keyword == null) {
          logger.warn("input Keyword null");
        } else {
          KeywordRank kr = new KeywordRank();
          kr.setKeyword(keyword.getKeyword());

          int cur = 0;
          GrabResult gr = null;
          while (cur++ < retries && !GrabResult.SUCCESS.equals(gr)) {
            gr = grab(kr);
          }
          logger.info("Result for keyword {}:{}", kr.getKeyword(), gr);
          
          if (krc != null && gr == GrabResult.SUCCESS) {
            krc.consume(keyword, kr);
          }
        }
      }
      logger.info("one spider finished");
    } catch (Exception e) {
      logger.error("spider run failed", e);
    }

  }

  //TODO: should be a list of rank
  protected GrabResult grab(KeywordRank kr) {
    String url = getUrl(kr.getKeyword());
    String content = getPageContent(url);

    if (Strings.isNullOrEmpty(content)) {
      return GrabResult.EMPTY_PAGE;
    }

    List<String> divs = getDivs(content);
    if (divs == null || divs.size() == 0) {
      return GrabResult.EMPTY_FIELD;
    }

    for (String div : divs) {
      extractRank(div, kr);
    }

    return GrabResult.SUCCESS;
  }

  protected String getPageContent(String url) {
    String content = null;
    HttpRequestBuilder builder = HttpRequestBuilder.create().get(url);
    ResponseHandler<String> handler = HttpResponseHandlers.stringHandler();
    if (!proxyEnabled) {
      content = builder.execute(handler);
    } else {
      HttpHost host = pool.getProxy();
      RequestConfig config =
          RequestConfig.custom().setProxy(host).setConnectionRequestTimeout(CONNECTION_TIMEOUT)
              .setSocketTimeout(SOCKET_TIMEOUT).build();
      content = builder.config(config).execute(handler);
    }

    return content;
  }

  public KeywordRankConsumer getKeywordRankConsumer() {
    return krc;
  }

  public void setKeywordRankConsumer(KeywordRankConsumer krc) {
    this.krc = krc;
  }

  public KeywordProvider getKeywordProvider() {
    return kp;
  }

  public void setKeywordProvider(KeywordProvider kp) {
    this.kp = kp;
  }

  public boolean isProxyEnabled() {
    return proxyEnabled;
  }

  public void setProxyEnabled(boolean proxyEnabled) {
    this.proxyEnabled = proxyEnabled;
  }

  protected abstract String getUrl(String keyword);

  /**
   * Extract a rank information
   * 
   * @param div
   * @return
   */
  protected abstract void extractRank(String div, KeywordRank kr);

  /**
   * Get html div tags for keyword.
   * 
   * @param keyword
   * @return
   */
  protected abstract List<String> getDivs(String keyword);

  protected String getMainHost(String url) {
    if (url.indexOf("...") >= 0) {
      url = serRegex.matchNthValue(url, "(.*?)\\.\\.\\.", 1);
    }

    if (url.indexOf("http://") < 0) {
      url = "http://" + url;
    }

    String host = "";
    try {
      URL ui = new URL(url);
      String tempHost = ui.getHost();
      if (!Strings.isNullOrEmpty(tempHost)) {
        String[] splits = tempHost.split("\\.");
        int len = splits.length;
        if (len < 2) {
          host = splits[0];
        } else if (tempHost.indexOf(".com.cn") >= 0 || tempHost.indexOf(".net.cn") >= 0
            || tempHost.indexOf(".org.cn") >= 0 || tempHost.indexOf(".gov.cn") >= 0
            || tempHost.indexOf(".cn.com") >= 0) {
          host = splits[len - 3] + "." + splits[len - 2] + "." + splits[len - 1];
        } else {
          host = splits[len - 2] + "." + splits[len - 1];
        }
      }
    } catch (Throwable t) {
      logger.error("Failed to parse host from {}:{}", url, t.getMessage());
    }

    return host;
  }
}
