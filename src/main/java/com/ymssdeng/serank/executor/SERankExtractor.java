package com.ymssdeng.serank.executor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ymssdeng.serank.spider.AbstractSERankSpider;

/**
 * Rank executor using multi-thread.
 * 
 * @author Administrator
 *
 */
@Component
public class SERankExtractor {
  private static Logger logger = LoggerFactory.getLogger(SERankExtractor.class);

  @Value("${serank.executor.frequency}")
  private int frequency;
  @Value("${serank.thread.count}")
  private int threadCount = 1;
  private List<AbstractSERankSpider> spiders;

  public List<AbstractSERankSpider> getSpiders() {
    return spiders;
  }

  @Autowired
  public void setSpiders(List<AbstractSERankSpider> spiders) {
    this.spiders = spiders;
  }

  public int getThreadCount() {
    return threadCount;
  }

  public void setThreadCount(int threadCount) {
    this.threadCount = threadCount;
  }

  void extract() {
    if (spiders == null) {
      logger.warn("no spider configured, return");
      return;
    }

    logger.info("start to extract keyword...");
    ExecutorService es = Executors.newCachedThreadPool();
    for (AbstractSERankSpider spider : spiders) {
      // spider.setGroup(groupId);
      for (int i = 0; i < threadCount; i++) {
        es.submit(spider);
      }
    }
  }

  public void execute() {
    // single time
    if (frequency <= 0) {
      logger.info("configured to run once");
      extract();
      return;
    }

    // fixed rate
    logger.info("configured to run at fixed rate {} days", frequency);
    ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    ses.scheduleAtFixedRate(new Runnable() {

      @Override
      public void run() {
        extract();
      }
    }, 0, frequency, TimeUnit.DAYS);
  }
}
