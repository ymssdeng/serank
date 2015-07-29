package com.mdeng.serank.keyword.provider;

import java.io.IOException;

import com.mdeng.serank.keyword.Keyword;

/**
 * 关键词提供方
 * 
 * @author hui.deng
 *
 * @param <T>
 */
public interface KeywordProvider<T extends Keyword> {

  /**
   * Whether has next keyword.
   * 
   * @return
   */
  boolean hasNextKeyword();

  /**
   * To get next keyword.
   * 
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  T nextKeyword() throws InterruptedException;
}
