package com.mdeng.serank.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Executor to execute strategy
 * 
 * @author Administrator
 *
 */
@Component
public class StrategyExecutor implements ApplicationContextAware {

  private static Logger logger = LoggerFactory.getLogger(StrategyExecutor.class);
  private ApplicationContext context;
  @Value("${serank.executor.strategy}")
  private String strategy;

  public void execute() {
    ExecuteStrategy es = null;
    if ("single".equals(strategy)) {
      es = context.getBean(SingleTimeExecuteStrategy.class);
    } else if ("rate".equals(strategy)) {
      es = context.getBean(FixedRateExecuteStrategy.class);
    } else {
      logger.error("unknown strategy");
      return;
    }

    String clazz = es.getClass().toString();
    logger.info(clazz.substring(clazz.lastIndexOf(".")) + " configured");
    es.execute();
  }

  @Override
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    this.context = context;
  }
}
