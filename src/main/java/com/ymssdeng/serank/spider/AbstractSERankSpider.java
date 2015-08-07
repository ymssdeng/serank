package com.ymssdeng.serank.spider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ymssdeng.basis.helper.http.HttpRequestBuilder;
import com.ymssdeng.basis.helper.http.HttpResponseHandlers;
import com.ymssdeng.serank.SERankRegex;
import com.ymssdeng.serank.SEType;
import com.ymssdeng.serank.keyword.Keyword;
import com.ymssdeng.serank.keyword.KeywordRank;
import com.ymssdeng.serank.keyword.consumer.KeywordRankConsumer;
import com.ymssdeng.serank.keyword.provider.KeywordProvider;
import com.ymssdeng.serank.proxy.CommonHttpHeader;
import com.ymssdeng.serank.proxy.HttpConnPoolManager;
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
  @Value("${serank.http.connecttimeout}")
  protected int connecttimeout;
  @Value("${serank.http.sockettimeout}")
  protected int sockettimeout;
  @Value("${serank.http.maxretries}")
  protected int maxretries;
  @Value("${serank.http.proxy}")
  protected boolean proxyEnabled;

  @Value("${serank.spider.top}")
  protected int top = 10;

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
          List<KeywordRank> krs = Lists.newArrayList();
          GrabResult gr = grab(keyword.getKeyword(), krs);
          logger.info("Result for keyword {}:{}", keyword, gr);

          if (krc != null && gr == GrabResult.SUCCESS) {
            krc.consume(keyword, krs);
          }
        }
      }
      logger.info("one spider finished");
    } catch (Exception e) {
      logger.error("spider run failed", e);
    }

  }

  protected GrabResult grab(String keyword, List<KeywordRank> ranks) {
    String url = getUrl(keyword);
    String content = getPageContent(url);

    if (Strings.isNullOrEmpty(content)) {
      return GrabResult.EMPTY_PAGE;
    }

    List<String> divs = getDivs(content);
    if (divs == null || divs.size() == 0) {
      return GrabResult.EMPTY_FIELD;
    }

    for (String div : divs) {
      KeywordRank kr = extractRank(div);
      if (kr != null) {
        ranks.add(kr);
      }
    }

    return GrabResult.SUCCESS;
  }

  protected String getPageContent(String url) {
    String content = null;
    for (int i = 0; i < maxretries; i++) {
      CloseableHttpClient client = HttpConnPoolManager.singleton.build();
      HttpGet get = new HttpGet(url);
      get.setHeaders(CommonHttpHeader.get().toArray(new Header[0]));
      HttpRequestBuilder builder =
          HttpRequestBuilder.create(client).method(get).maxRetries(maxretries);
      Builder builder2 =
          RequestConfig.custom().setConnectTimeout(connecttimeout).setSocketTimeout(sockettimeout);
      HttpHost proxy = null;
      if (proxyEnabled) {
        proxy = pool.dequeue();
        builder2 = builder2.setProxy(proxy);
      }

      RequestConfig config = builder2.build();
      ResponseHandler<String> handler = HttpResponseHandlers.stringHandler();
      content = builder.config(config).execute(handler);
      if (!Strings.isNullOrEmpty(content)) return content;

      if (proxyEnabled) {
        pool.declareBrokenProxy(proxy);
      }
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {}
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
  protected abstract KeywordRank extractRank(String div);

  /**
   * Get html div tags for content.
   * 
   * @param content
   * @return
   */
  protected abstract List<String> getDivs(String content);

  protected String getHost(String url) {
    if (url.indexOf("...") >= 0) {
      url = serRegex.matchNthValue(url, "(.*?)\\.\\.\\.", 1);
    }

    if (url.indexOf("http://") < 0) {
      url = "http://" + url;
    }

    // "(www\\.)|(\\.com\\.cn)|(\\.net\\.cn)|(\\.org\\.cn)|(\\.gov\\.cn)|(\\.cn\\.com)"
    String host = "";
    try {
      URL ui = new URL(url);
      String tempHost = ui.getHost();
      if (tempHost != null) {
        host = tempHost.replaceAll("(www\\.)", "");
      }
    } catch (MalformedURLException e) {
      logger.error("HOST解析出错：{}", url, e);
    }

    return host;
  }

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
