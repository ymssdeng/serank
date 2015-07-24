package com.mdeng.serank.keyword.provider;

/**
 * Keyword is grouped
 * 
 * @author hui.deng
 *
 */
public interface KeywordGroupProvider {
  /**
   * Whether has next group
   * 
   * @return
   */
  boolean hasNextGroup();

  /**
   * To get next group
   * 
   * @return group id
   */
  int nextGroup();
}
