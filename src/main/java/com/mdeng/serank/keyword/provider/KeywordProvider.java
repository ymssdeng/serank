package com.mdeng.serank.keyword.provider;

import com.mdeng.serank.keyword.KeywordRank;

/**
 * Keyword is grouped
 * 
 * @author Administrator
 *
 */
public interface KeywordProvider {

  /**
   * Whether has next keyword in current group.
   * 
   * @return
   */
  boolean hasNextKeyword(int groupId);

  /**
   * To get next keyword in current group.
   * 
   * @return
   */
  KeywordRank nextKeyword(int groupId);
}
