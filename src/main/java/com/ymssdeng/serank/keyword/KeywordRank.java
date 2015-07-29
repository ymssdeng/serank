package com.ymssdeng.serank.keyword;

import java.util.List;

import com.google.common.collect.Lists;
import com.ymssdeng.serank.spider.GrabResult;

/**
 * 关键词排名信息
 * 
 * @author hui.deng
 *
 * @param <T>
 */
public class KeywordRank<T extends Keyword> {
  private T keyword;
  private GrabResult result = GrabResult.EMPTY_PAGE;
  private List<Rank> rankInfos = Lists.newArrayList();

  public T getKeyword() {
    return keyword;
  }

  public void setKeyword(T keyword) {
    this.keyword = keyword;
  }

  public GrabResult getResult() {
    return result;
  }

  public void setResult(GrabResult result) {
    this.result = result;
  }

  public List<Rank> getRankInfos() {
    return rankInfos;
  }

  public void setRankInfos(List<Rank> rankInfos) {
    this.rankInfos = rankInfos;
  }

  public void addRankInfo(Rank rInfo) {
    this.rankInfos.add(rInfo);
  }

  public static class Rank {
    private String host;
    private int rank;

    public String getHost() {
      return host;
    }

    public void setHost(String host) {
      this.host = host;
    }

    public int getRank() {
      return rank;
    }

    public void setRank(int rank) {
      this.rank = rank;
    }
  }
}
