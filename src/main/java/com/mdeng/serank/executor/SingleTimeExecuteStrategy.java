package com.mdeng.serank.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mdeng.serank.keyword.provider.KeywordGroupProvider;

/**
 * Single time execute strategy
 * 
 * @author Administrator
 *
 */
@Component
public class SingleTimeExecuteStrategy implements ExecuteStrategy {

  @Autowired
  private SERankExtractor executor;
  @Autowired
  private KeywordGroupProvider kgp;

  @Override
  public SERankExtractor getExecutor() {
    return executor;
  }

  @Override
  public void setExecutor(SERankExtractor executor) {
    this.executor = executor;
  }

  @Override
  public KeywordGroupProvider getKeywordGroupProvider() {
    return kgp;
  }

  @Override
  public void setKeywordGroupProvider(KeywordGroupProvider kgp) {
    this.kgp = kgp;
  }

  @Override
  public void execute() {
    while (kgp.hasNextGroup()) {
      int groupId = kgp.nextGroup();
      executor.extract(groupId);
    }

  }
}
