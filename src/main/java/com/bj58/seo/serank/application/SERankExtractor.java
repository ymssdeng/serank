package com.bj58.seo.serank.application;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.bj58.seo.serank.exception.NoMoreException;
import com.bj58.seo.serank.model.SEType;
import com.bj58.seo.serank.model.Task;
import com.bj58.seo.serank.spider.BaseSpider;

/**
 * Rank executor using multi-thread.
 * 
 * @author Administrator
 *
 */
@Component
public class SERankExtractor implements ApplicationContextAware {
  private Logger logger = LoggerFactory.getLogger(SERankExtractor.class);

  @Value("${serank.thread.count}")
  private int threadCount = 1;
  @Value("${serank.spider.type}")
  private String sptypes = "1,5";
  @Autowired
  @Qualifier("httpImpl")
  private TaskKeywordManager tm;
  private ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
  private ExecutorService ces = Executors.newCachedThreadPool();

  void extract() {
    try {
      List<Task> tasks = tm.getTasks();
      String[] spiders = sptypes.split(",");
      for (Task task : tasks) {
        for (String s : spiders) {
          SEType seType = SEType.value(Integer.valueOf(s));
          logger.info("start spider {} of task {}", seType, task.getId_spider_task());
          for (int i = 0; i < threadCount; i++) {
            BaseSpider spider = (BaseSpider) context.getBean(seType.getHost());
            spider.setTaskModel(task);
            ces.submit(spider);

            TimeUnit.MICROSECONDS.sleep(100);
          }
        }
      }
    } catch (NoMoreException e) {
      logger.info("no more task to run");
    } catch (Exception e) {
      logger.error("Failed to execute", e);
    }
  }

  public void execute() {
    // fixed rate
    ses.scheduleAtFixedRate(new Runnable() {

      @Override
      public void run() {
        extract();
      }
      
    }, 3, 120, TimeUnit.SECONDS);
  }

  private ApplicationContext context = null;

  @Override
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    this.context = context;
  }
}
