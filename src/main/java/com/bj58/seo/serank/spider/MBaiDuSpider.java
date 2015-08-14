package com.bj58.seo.serank.spider;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bj58.seo.serank.model.Keyword;
import com.bj58.seo.serank.model.KeywordInfo;
import com.bj58.seo.serank.model.SEType;

@Scope("prototype")
@Component("m.baidu.com")
public class MBaiDuSpider extends BaseSpider {

  @Override
  protected SEType getSEType() {
    return SEType.M_BAIDU;
  }

  @Override
  protected int doGrab(Keyword keyWordModel, List<KeywordInfo> infoModels) {

    String keyword = keyWordModel.getKeyword();
    if (StringUtils.isEmpty(keyword)) {
      return SUCCESS;
    }

    // 取前三页
    for (int pn = 0; pn < 3; pn++) {
      String url =
          "http://m.baidu.com/from=1269a/s?word=" + regexEx.urlEncode(keyword) + "&pn=" + (pn * 10);

      String content = getWebProxyContentByProxy(url, 10);
      // String content = HttpUtils.get(url, 3);
      if (StringUtils.isEmpty(content)) {
        if (pn == 0) {
          return EMPTY_PAGE;
        } else {
          break;
        }
      }

      // 尝试1：
      String regList =
          "<div class=\"(result|resitem|reswrap).*?>.*?<span class=\"site\"\\s*?>.*?</span>";
      List<String> list = regexEx.matchValues(content, regList);
      if (list.size() == 0) {
        // 尝试2：
        // regList =
        // "<div class=\"resitem.*?>.*?<span class=\"site\"\\s*?>.*?</span>";
        // list = regexEx.matchValues(content, regList);
      }

      if (list.size() <= 0) {
        if (pn == 0) {
          return EMPTY_PAGE;
        } else {
          break;
        }
      }

      // 当前页排名
      String regTop = "order=(\\d+)&";

      // 提取出 搜索关键字 列表页 单条列表 所需要的取值
      List<String> regUrls = new ArrayList<String>();
      regUrls.add("<span\\s*class\\s*=\\s*\"site\"\\s*?>(.*?)</span>"); // 普通列表
      regUrls.add("<span\\s*class\\s*=\\s*\"site\"\\s*?>(.*?)&nbsp;\\S*?&nbsp;</span>"); // 普通列表

      // 真实跳转URL
      // String regTrueurl = "<a.*?href\\s*?=\\s*?\"(\\S+)\".*?>";

      for (String fld : list) {
        // 关键词排名
        int top = currentTop + 1;
        String topRet = regexEx.matchValue_1(fld, regTop);
        if (!StringUtils.isEmpty(topRet) && !StringUtils.isEmpty(topRet.trim())) {
          top = Integer.parseInt(topRet.trim()) + pn * 10;
        }

        String urlRet = null;
        for (String regUrl : regUrls) {
          urlRet = regexEx.matchValue_1(fld, regUrl);
          if (!StringUtils.isEmpty(urlRet)) {
            break;
          }
        }

        KeywordInfo infoModel = new KeywordInfo();
        infoModel.setId_keyword(keyWordModel.getId_keyword());
        infoModel.setKeyword(keyWordModel.getKeyword());
        infoModel.setMarkid(keyWordModel.getId_keyword_mark());
        infoModel.setBusiness_line(keyWordModel.getBusiness_line());
        infoModel.setCityid(keyWordModel.getCityid());
        infoModel.setCate1(keyWordModel.getCate1());
        infoModel.setCate2(keyWordModel.getCate2());
        infoModel.setCate3(keyWordModel.getCate3());
        infoModel.setCate4(keyWordModel.getCate4());
        infoModel.setTop(top);
        infoModel.setHost(getHost(regexEx.removeHtml(urlRet)));
        infoModel.setMainhost(getMainHost(regexEx.removeHtml(urlRet)));
        // infoModel.setTrue_url(regexEx.matchValue_1(fld, regTrueurl));

        List<KeywordInfo> padding = this.getPadding(infoModel);
        infoModels.addAll(padding);
      }
    }
    
    return SUCCESS;
  }

}
