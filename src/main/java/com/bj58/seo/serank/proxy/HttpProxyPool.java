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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bj58.seo.core.http.HttpRequestBuilder;
import com.bj58.seo.core.http.HttpResponseHandlers;

/**
 * Simple HTTP proxy pool
 * 
 * @author Administrator
 *
 */
@Component
public class HttpProxyPool {
  private static Logger logger = LoggerFactory.getLogger(HttpProxyPool.class);

  // get proxy size
  private final int PROXY_SIZE = 100;
  // 队列最小size
  private final int Queue_MINSIZE = 20;
  // 队列最大size
  private final int Queue_MAXSIZE = 1000;
  // 定时扫描周期 seconds
  private final int SCAN_INTERVAL = 60;
  // 定时补充队列周期 seconds
  private final int REPLENISH_INTERVAL = 2 * 60;
  // 验证代理结果缓存时间
  private final int PROXY_STATE_CACHETIME = 10 * 60;
  // 检测代理所用的url
  private final String PROXY_CHECK_URL = "http://static.58.com/bangbang/website/config.js";
  // 检测代理所用的url的内容key
  private final String PROXY_CHECK_URLCONTENT_KEY = "versionConf";
  // 代理队列
  private final BlockingQueue<HttpHost> queue = new LinkedBlockingQueue<HttpHost>();
  // 定时器
  private ScheduledExecutorService scheduler;
  // 定时器线程数
  private final int SCHEDULED_COREPOOLSIZE = 3;
  @Autowired
  private ProxyProvider provider;
  @Value("${serank.http.connecttimeout}")
  private int connecttimeout;
  @Value("${serank.http.sockettimeout}")
  private int sockettimeout;

  // 补充队列时的锁,防止队列出现重复数据;此锁已注释,重复数据不再考虑.
  // private ReentrantLock lock = new ReentrantLock();

  @PostConstruct
  public void start() {
    try {
      scheduler = Executors.newScheduledThreadPool(SCHEDULED_COREPOOLSIZE);
      scheduler.scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {
          try {
            replenishQueue();
          } catch (Exception e) {
            logger.error(e.getMessage(), e);
          }
        }
      }, 0, REPLENISH_INTERVAL, TimeUnit.SECONDS);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
    scheduler.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        try {
          scanQueue();
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
        }
      }
    }, SCAN_INTERVAL, SCAN_INTERVAL, TimeUnit.SECONDS);
  }

  private void scanQueue() {
    logger.info("begin scan,queue size:{}", queue.size());
    HttpHost[] arrayProxy = queue.toArray(new HttpHost[0]);
    for (HttpHost httpHost : arrayProxy) {
      boolean checkResult = checkProxy(httpHost);
      if (!checkResult) {
        queue.remove(httpHost);
      }
    }
    logger.info("end scan,queue size:{}", queue.size());
  }

  private boolean checkProxy(HttpHost httpHost) {
    try {
      CloseableHttpClient client = HttpConnPoolManager.singleton.build();
      RequestConfig config =
          RequestConfig.custom().setConnectTimeout(connecttimeout).setSocketTimeout(sockettimeout)
              .setProxy(httpHost).build();
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

  private void replenishQueue() {
    if (queue.size() >= Queue_MAXSIZE) return;
    try {
      List<HttpHost> list = provider.getProxies(PROXY_SIZE);
      if (list != null) {
        for (HttpHost httpHost : list) {
          if (!queue.contains(httpHost)) {
            queue.add(httpHost);
          }
        }
      }
      logger.info("after replenish queue size:{}", queue.size());
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  public HttpHost dequeue() {
    if (queue.size() <= Queue_MINSIZE) {
      logger.info("trying to replenish queue...");
      replenishQueue();
    }
    try {
      HttpHost httpHost = queue.take();
      if (!queue.contains(httpHost)) queue.put(httpHost);
      return httpHost;
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return null;
    }
  }

  public void declareBrokenProxy(HttpHost proxy) {
    queue.remove(proxy);
  }

  public int size() {
    return queue.size();
  }

  public ProxyProvider getProvider() {
    return provider;
  }

  public void setProvider(ProxyProvider provider) {
    this.provider = provider;
  }

  public int getConnecttimeout() {
    return connecttimeout;
  }

  public void setConnecttimeout(int connecttimeout) {
    this.connecttimeout = connecttimeout;
  }

  public int getSockettimeout() {
    return sockettimeout;
  }

  public void setSockettimeout(int sockettimeout) {
    this.sockettimeout = sockettimeout;
  }

  public static void main(String[] args) {}

}
