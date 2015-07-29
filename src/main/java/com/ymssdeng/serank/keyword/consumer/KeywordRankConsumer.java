package com.ymssdeng.serank.keyword.consumer;

import com.ymssdeng.serank.keyword.Keyword;
import com.ymssdeng.serank.keyword.KeywordRank;

public interface KeywordRankConsumer {

  /**
   * Consume a keyword rank information.
   * 
   * @param keywordRank
   */
  <T extends Keyword> void consume(KeywordRank<T> keywordRank);
}
