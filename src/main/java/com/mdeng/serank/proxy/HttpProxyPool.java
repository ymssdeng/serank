package com.mdeng.serank.proxy;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.mdeng.common.http.HttpRequestBuilder;

/**
 * Simple HTTP proxy pool
 * 
 * @author Administrator
 *
 */
@Component
public class HttpProxyPool {
  private static final int DEFAULT_POOL_SIZE = 100;
  private static final String CHECK_PROXY_URL = "http://www.baidu.com/";

  /**
   * Proxy can be shared in multiple HTTP requests
   */
  private List<HttpHost> pool = Lists.newArrayList();
  private ProxyProvider provider;
  @Value("${serank.proxy.enabled}")
  private boolean proxyEnabled;
  public HttpProxyPool() {}

  @Autowired
  public HttpProxyPool(ProxyProvider provider) {
    if (provider != null && proxyEnabled) {
      this.provider = provider;
      init();
      monitor();
    }
  }

  private void monitor() {
    Executors.newScheduledThreadPool(1).schedule(new Runnable() {

      @Override
      public void run() {
        Iterator<HttpHost> iterator = pool.iterator();
        while (iterator.hasNext()) {
          HttpHost host = iterator.next();
          if (!checkProxy(host)) {
            iterator.remove();
          }
        }

        if (pool.size() < DEFAULT_POOL_SIZE) {
          List<HttpHost> hosts = provider.getProxies(DEFAULT_POOL_SIZE - pool.size());
          for (HttpHost host : hosts) {
            if (!pool.contains(host)) {
              pool.add(host);
            }
          }
        }
      }
    }, 60, TimeUnit.SECONDS);

  }

  private void init() {
    List<HttpHost> proxies = provider.getProxies(DEFAULT_POOL_SIZE);
    for (HttpHost httpHost : proxies) {
      if (!pool.contains(httpHost)) { // && checkProxy(httpHost)
        pool.add(httpHost);
      }
    }
  }

  public HttpHost getProxy() {
    if (pool.size() > 0) {
      // randomly return a proxy
      return pool.get(new Random().nextInt(pool.size()));
    }
    return null;
  }

  public boolean checkProxy(HttpHost proxy) {
    if (proxy == null) return false;

    RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
    StatusLine sl =
        HttpRequestBuilder.create().get(CHECK_PROXY_URL).config(config)
            .execute(new HttpRequestBuilder.StatusLineHandler());
    return sl != null && sl.getStatusCode() == 200;
  }
}
