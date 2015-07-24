package com.mdeng.serank.spider;

import java.net.URL;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Strings;
import com.mdeng.common.http.HttpRequestBuilder;
import com.mdeng.common.http.HttpRequestBuilder.StringEntityHandler;
import com.mdeng.serank.SERankRegex;
import com.mdeng.serank.SEType;
import com.mdeng.serank.keyword.KeywordRank;
import com.mdeng.serank.keyword.Rank;
import com.mdeng.serank.keyword.consumer.KeywordRankConsumer;
import com.mdeng.serank.keyword.provider.KeywordProvider;
import com.mdeng.serank.proxy.HttpProxyPool;

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
  protected KeywordProvider keywordProvider;
  @Autowired(required = false)
  protected KeywordRankConsumer keywordRankConsumer;
  @Autowired
  protected HttpProxyPool pool;
  @Value("${serank.proxy.enabled}")
  protected boolean proxyEnabled;
  @Value("${serank.spider.top}")
  protected int top = 10;
  protected int groupId;

  private static final int CONNECTION_TIMEOUT = 10 * 1000;
  private static final int SOCKET_TIMEOUT = 10 * 1000;

  protected abstract SEType getSEType();

  @Override
  public void run() {
    if (keywordProvider == null) {
      logger.error("keyword provider null");
      return;
    }

    while (keywordProvider.hasNextKeyword(groupId)) {
      KeywordRank kr = keywordProvider.nextKeyword(groupId);
      if (kr == null) {
        logger.warn("input Keyword rank null");
      } else {
        kr.setKeyword(kr.getKeyword() != null ? kr.getKeyword().trim() : null);
        if (Strings.isNullOrEmpty(kr.getKeyword())) {
          kr.setResult(GrabResult.SUCCESS);
        }

        int cur = 0;
        while (cur++ < retries && !GrabResult.SUCCESS.equals(kr.getResult())) {
          kr = grab(kr);
        }
        logger.info("Result for keyword {}:{}", kr.getKeyword(), kr.getResult());
        if (keywordRankConsumer != null && kr.getResult() == GrabResult.SUCCESS) {
          keywordRankConsumer.consume(kr);
        }
      }
    }

    logger.info("group {} finished", groupId);
  }

  protected KeywordRank grab(KeywordRank keyword) {
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
    StringEntityHandler handler = new StringEntityHandler();
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
    return keywordRankConsumer;
  }

  public void setKeywordRankConsumer(KeywordRankConsumer keywordRankConsumer) {
    this.keywordRankConsumer = keywordRankConsumer;
  }

  public KeywordProvider getKeywordProvider() {
    return keywordProvider;
  }

  public void setKeywordProvider(KeywordProvider keywordProvider) {
    this.keywordProvider = keywordProvider;
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

  public void setGroup(int groupId) {
    this.groupId = groupId;
  }
}
