package com.mdeng.serank.executor;

import com.mdeng.serank.keyword.provider.KeywordGroupProvider;

/**
 * Strategy to run SERank application
 * 
 * @author Administrator
 *
 */
public interface ExecuteStrategy {

  public abstract void setExecutor(SERankExtractor executor);

  public abstract SERankExtractor getExecutor();

  public abstract void execute();

  void setKeywordGroupProvider(KeywordGroupProvider kgp);

  KeywordGroupProvider getKeywordGroupProvider();

}
