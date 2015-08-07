package com.ymssdeng.serank.proxy;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class HttpConnPoolManager {
  private static final Log log = LogFactory.getLog(HttpConnPoolManager.class);
  public static final HttpConnPoolManager singleton = new HttpConnPoolManager();
  private PoolingHttpClientConnectionManager cm;
  private Thread monitorThread;

  /**
   * 构造cm
   */
  public HttpConnPoolManager() {
    try {
      cm = new PoolingHttpClientConnectionManager();
      // 默认设置route最大连接数
      cm.setDefaultMaxPerRoute(50);
      // 连接池最大连接数
      cm.setMaxTotal(200);
      monitorThread = new IdleConnectionMonitorThread(cm);
      monitorThread.setDaemon(true);
      monitorThread.start();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public CloseableHttpClient build() {
    return HttpClients.custom().setConnectionManager(cm).build();
  }

  public static class IdleConnectionMonitorThread extends Thread {
    private static final int SCAN_PERIOD = 15000;
    private static final int IDLE_TIME = 30;
    private final HttpClientConnectionManager connMgr;
    private volatile boolean shutdown;

    public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
      super();
      this.connMgr = connMgr;
    }

    @Override
    public void run() {
      try {
        while (!shutdown) {
          synchronized (this) {
            wait(SCAN_PERIOD);
            // Close expired connections
            connMgr.closeExpiredConnections();
            // Optionally, close connections
            // that have been idle longer than IDLE_TIME sec
            connMgr.closeIdleConnections(IDLE_TIME, TimeUnit.SECONDS);
          }
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }

    public void shutdown() {
      shutdown = true;
      synchronized (this) {
        notifyAll();
      }
    }
  }
}
