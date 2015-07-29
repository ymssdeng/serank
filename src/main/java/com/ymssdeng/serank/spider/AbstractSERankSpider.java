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
import com.ymssdeng.serank.keyword.KeywordRank.Rank;
import com.ymssdeng.serank.keyword.consumer.KeywordRankConsumer;
import com.ymssdeng.serank.keyword.provider.KeywordProvider;
import com.ymssdeng.serank.proxy.HttpProxyPool;

/**
 * Abstract spider for keyword rank in search engine.
 * 
 * @author Administrator
 *
 */
public abstract class AbstractSERankSpider<T extends Keyword> implements Runnable {

  protected Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * Max grab times for each keyword
   */
  protected int retries = 3;
  protected SERankRegex serRegex = new SERankRegex();
  @Autowired
  protected KeywordProvider<T> kp;
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
        T keyword = kp.nextKeyword();
        if (keyword == null) {
          logger.warn("input Keyword rank null");
        } else {
          KeywordRank<T> kr = new KeywordRank<T>();
          kr.setKeyword(keyword);
          if (!kr.getKeyword().isValid()) {
            kr.setResult(GrabResult.SUCCESS);
          }

          int cur = 0;
          while (cur++ < retries && !GrabResult.SUCCESS.equals(kr.getResult())) {
            kr = grab(kr);
          }
          logger.info("Result for keyword {}:{}", kr.getKeyword(), kr.getResult());
          if (krc != null && kr.getResult() == GrabResult.SUCCESS) {
            krc.consume(kr);
          }
        }
      }
      logger.info("one spider finished");
    } catch (Exception e) {
      logger.error("spider run failed", e);
    }

  }

  protected KeywordRank<T> grab(KeywordRank<T> keyword) {
    String url = getUrl(keyword.getKeyword());
    String content = getPageContent(url);

    if (Strings.isNullOrEmpty(content)) {
      keyword.setResult(GrabResult.EMPTY_PAGE);
      return keyword;
    }

    List<String> divs = getDivs(content);
    if (divs == null || divs.size() == 0) {
      keyword.setResult(GrabResult.EMPTY_FIELD);
      return keyword;
    }

    for (String div : divs) {
      Rank ri = extractRank(div);
      if (ri != null) {
        keyword.addRankInfo(ri);
      }
    }

    keyword.setResult(GrabResult.SUCCESS);
    return keyword;
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

  public KeywordProvider<T> getKeywordProvider() {
    return kp;
  }

  public void setKeywordProvider(KeywordProvider<T> kp) {
    this.kp = kp;
  }

  public boolean isProxyEnabled() {
    return proxyEnabled;
  }

  public void setProxyEnabled(boolean proxyEnabled) {
    this.proxyEnabled = proxyEnabled;
  }

  protected abstract String getUrl(T keyword);

  /**
   * Extract a rank information
   * 
   * @param div
   * @return
   */
  protected abstract Rank extractRank(String div);

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
