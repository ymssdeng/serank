package com.ymssdeng.serank.spider;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.bj58.seo.market.model.KeyWordInfoModel;
import com.ymssdeng.serank.SEType;
import com.ymssdeng.serank.keyword.KeywordRank;

public class MBaiduRankSpider extends AbstractSERankSpider {

  @Override
  protected SEType getSEType() {
    return SEType.M_BAIDU;
  }

  @Override
  protected String getUrl(String keyword) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected KeywordRank extractRank(String div) {
    return null;
  }

  @Override
  protected List<String> getDivs(String content) {
    String reg = "<div class=\"(result|resitem|reswrap).*?>.*?<span class=\"site\"\\s*?>.*?</span>";
    return serRegex.matchValues(content, reg);
  }

}
