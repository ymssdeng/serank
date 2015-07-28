package com.mdeng.serank.keyword.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mdeng.serank.keyword.Keyword;
import com.mdeng.serank.keyword.KeywordRank;

/**
 * Log keyword rank to file
 * 
 * @author Administrator
 *
 */
@Component
public class KeywordRankLogConsumer implements KeywordRankConsumer {

  private Logger appLogger = LoggerFactory.getLogger(KeywordRankLogConsumer.class);
  private Logger rankLogger = LoggerFactory.getLogger("consumer");

  @Override
  public <T extends Keyword> void consume(KeywordRank<T> kr) {
    //TODO: log keyword rank

  }

}
