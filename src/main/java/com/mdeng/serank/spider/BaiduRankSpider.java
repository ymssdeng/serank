package com.mdeng.serank.spider;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.mdeng.common.utils.Charsets;
import com.mdeng.serank.SEType;
import com.mdeng.serank.keyword.Rank;

/**
 * Baidu PC rank spider
 * 
 * @author hui.deng
 *
 */
@Component
public class BaiduRankSpider extends AbstractSERankSpider {

  @Override
  protected SEType getSEType() {
    return SEType.Baidu;
  }

  @Override
  protected Rank extractRank(String div) {
    String regTop = " id=\"(\\d+)\" ";
    String top = serRegex.matchNthValue(div, regTop, 1);
    if (Strings.isNullOrEmpty(top) || Strings.isNullOrEmpty(top.trim())) return null;

    List<String> regUrls = new ArrayList<String>();
    regUrls.add("<span\\s*class\\s*=\\s*\"g\">(.*?)(&nbsp;)?\\S*?(&nbsp;)?</span>"); // 普通列表
    regUrls.add("<span\\s*class\\s*=\\s*\"c\\-showurl\">(.*?)&nbsp;\\S*?&nbsp;</span>"); // 百度百科
    regUrls.add("<span\\s*class\\s*=\\s*\"c\\-showurl\">(.*?) *?\\S*? *?</span>"); // 其他
    regUrls.add("<span\\s*class\\s*=\\s*\"c\\-showurl\"> *?(\\S*?) *?</span>"); // 其他

    String url = null;
    for (String regUrl : regUrls) {
      // 空字符串也可能匹配，选取匹配的非空串
      url = serRegex.matchNonEmptyValue(div, regUrl);
      if (!Strings.isNullOrEmpty(url)) {
        break;
      }
    }

    Rank rank = new Rank();
    rank.setRank(Integer.valueOf(top));
    rank.setHost(getMainHost(Charsets.removeHtml(url)));
    return rank;
  }

  @Override
  protected List<String> getDivs(String content) {
    String reg = "<div class=\"result\\S*?\\s*c\\-container\\s*.*?</span>";
    return serRegex.matchValues(content, reg);
  }

  @Override
  protected String getUrl(String keyword) {
    // TODO encode keyword
    String url = "http://www.baidu.com/s?wd=" + keyword + "&pn=0&rn="+top;
    return url;
  }

}
