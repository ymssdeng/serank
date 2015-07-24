package com.mdeng.serank.keyword;

import java.util.List;

import com.google.common.collect.Lists;
import com.mdeng.serank.spider.GrabResult;

public class KeywordRank {
  private int group;
  private String keyword;
  private GrabResult result = GrabResult.EMPTY_PAGE;
  private List<Rank> rankInfos = Lists.newArrayList();

  public int getGroup() {
    return group;
  }

  public void setGroup(int group) {
    this.group = group;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
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
}
