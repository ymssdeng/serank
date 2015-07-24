package com.mdeng.serank.executor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mdeng.serank.keyword.provider.KeywordGroupProvider;

/**
 * Fixed rate execute strategy
 * 
 * @author Administrator
 *
 */
@Component
public class FixedRateExecuteStrategy implements ExecuteStrategy {

  @Autowired
  private SERankExtractor executor;
  @Value("${serank.executor.frequency}")
  private int frequency;
  @Autowired
  private KeywordGroupProvider kgp;
  public int getFrequency() {
    return frequency;
  }

  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  @Override
  public void setExecutor(SERankExtractor executor) {
    this.executor = executor;
  }

  @Override
  public SERankExtractor getExecutor() {
    return executor;
  }

  @Override
  public void execute() {
    ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    ses.scheduleAtFixedRate(new Runnable() {

      @Override
      public void run() {
        while (kgp.hasNextGroup()) {
          int groupId = kgp.nextGroup();
          executor.extract(groupId);  
        }
      }
    }, 0, frequency, TimeUnit.SECONDS);
  }

  @Override
  public void setKeywordGroupProvider(KeywordGroupProvider kgp) {
    this.kgp = kgp;
    
  }

  @Override
  public KeywordGroupProvider getKeywordGroupProvider() {
    return kgp;
  }

}
