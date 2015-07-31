package com.ymssdeng.serank.keyword.provider;

import java.io.IOException;

import com.ymssdeng.serank.keyword.Keyword;

/**
 * 关键词提供方
 * 
 * @author hui.deng
 *
 */
public interface KeywordProvider {

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
  <T extends Keyword> T nextKeyword() throws InterruptedException;
}
