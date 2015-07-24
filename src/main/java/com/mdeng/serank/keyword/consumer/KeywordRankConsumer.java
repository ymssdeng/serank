package com.mdeng.serank.keyword.consumer;

import com.mdeng.serank.keyword.KeywordRank;

public interface KeywordRankConsumer {

  /**
   * Consume a keyword rank information.
   * 
   * @param keywordRank
   */
  void consume(KeywordRank keywordRank);
}
