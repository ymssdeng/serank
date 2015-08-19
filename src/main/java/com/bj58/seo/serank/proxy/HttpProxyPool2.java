package com.bj58.seo.serank.proxy;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bj58.seo.core.http.HttpRequestBuilder;
import com.bj58.seo.core.http.HttpResponseHandlers;

@Component
public class HttpProxyPool2 {
  private Logger logger = LoggerFactory.getLogger(HttpProxyPool2.class);

  // 检测代理所用的url
  private final String PROXY_CHECK_URL = "http://static.58.com/bangbang/website/config.js";
  // 检测代理所用的url的内容key
  private final String PROXY_CHECK_URLCONTENT_KEY = "versionConf";
  // 代理队列
  private final BlockingQueue<HttpHost> queue = new LinkedBlockingQueue<HttpHost>();
  @Autowired
  private ProxyProvider provider = new TaobaoProxyProvider();

  @PostConstruct
  public void start() {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    scheduler.scheduleAtFixedRate(new ScanThread(), 0, 60, TimeUnit.SECONDS);
    scheduler.scheduleAtFixedRate(new ScanThread(), 2, 60, TimeUnit.SECONDS);
    scheduler.scheduleAtFixedRate(new ScanThread(), 4, 60, TimeUnit.SECONDS);
  }

  public HttpHost getProxy() {
    HttpHost httpHost = null;
    try {
      httpHost = queue.poll(1, TimeUnit.SECONDS);
      if (httpHost == null) {
        logger.warn("proxy queue empty");
        return null;
      }

      if (!queue.contains(httpHost)) queue.put(httpHost);
    } catch (Exception e) {}

    return httpHost;
  }

  public void declareBrokenProxy(HttpHost proxy) {
    queue.remove(proxy);
  }

  class ScanThread implements Runnable {

    @Override
    public void run() {
      List<HttpHost> hosts = null;
      while (true) {
        hosts = provider.getProxies(100);
        if (hosts != null && hosts.size() > 0) {
          break;
        } else {
          try {
            TimeUnit.SECONDS.sleep(5);
          } catch (InterruptedException e) {}
        }
      }

      try {
        for (HttpHost httpHost : hosts) {
          if (!queue.contains(httpHost) && checkProxy(httpHost)) {
            queue.put(httpHost);
            logger.info("[{}] add to proxy queue, size {}", Thread.currentThread().getName(),
                queue.size());
          }
        }
        logger.info("[{}] end scan", Thread.currentThread().getName());
      } catch (Exception e) {
        logger.error("Failed in proxy scan", e);
      }
    }

  }

  private boolean checkProxy(HttpHost httpHost) {
    try {
      CloseableHttpClient client = HttpConnPoolManager.singleton.build();
      RequestConfig config =
          RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).setProxy(httpHost)
              .build();
      HttpGet get = new HttpGet(PROXY_CHECK_URL);
      get.setHeaders(CommonHttpHeader.get().toArray(new Header[0]));
      String html =
          HttpRequestBuilder.create(client).method(get).config(config)
              .execute(HttpResponseHandlers.stringHandler());
      return (!StringUtils.isEmpty(html) && html.indexOf(PROXY_CHECK_URLCONTENT_KEY) > -1);
    } catch (Exception e) {
      return false;
    }
  }

  public static void main(String[] args) {
    HttpProxyPool2 pool2 = new HttpProxyPool2();
    pool2.start();
  }
}
