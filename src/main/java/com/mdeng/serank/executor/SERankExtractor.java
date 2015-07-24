package com.mdeng.serank.executor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mdeng.serank.spider.AbstractSERankSpider;

/**
 * Rank executor using multi-thread.
 * 
 * @author Administrator
 *
 */
@Component
public class SERankExtractor {
  private static Logger logger = LoggerFactory.getLogger(SERankExtractor.class);

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

  public void extract(int groupId) {
    logger.info("start to extract groud {} ...", groupId);
    if (spiders == null) {
      logger.warn("no spider configured, return");
      return;
    }

    ExecutorService es = Executors.newCachedThreadPool();
    for (AbstractSERankSpider spider : spiders) {
      spider.setGroup(groupId);
      for (int i = 0; i < threadCount; i++) {
        es.submit(spider);
      }
    }

  }

  // public static void main(String[] args) {
  // SERankExecutor executor = new SERankExecutor();
  // List<AbstractSERankSpider> lst = Lists.newArrayList();
  // BaiduRankSpider spider = new BaiduRankSpider();
  // spider.setKeywordProvider(new BasicKeywordProvider());
  // lst.add(spider);
  // executor.setSpiders(lst);
  //
  // Stopwatch watch = new Stopwatch();
  // executor.setThreadCount(1);
  // watch.start();
  // executor.execute();
  // watch.mark();
  // System.out.println("1 thread:" + watch.getDuration(TimeUnit.SECONDS).get(0));
  //
  // spider.setKeywordProvider(new BasicKeywordProvider());
  // watch = new Stopwatch();
  // executor.setThreadCount(3);
  // watch.start();
  // executor.execute();
  // watch.mark();
  // System.out.println("3 thread:" + watch.getDuration(TimeUnit.SECONDS).get(0));
  //
  // spider.setKeywordProvider(new BasicKeywordProvider());
  // watch = new Stopwatch();
  // executor.setThreadCount(5);
  // watch.start();
  // executor.execute();
  // watch.mark();
  // System.out.println("5 thread:" + watch.getDuration(TimeUnit.SECONDS).get(0));
  // System.exit(0);
  // }

}
