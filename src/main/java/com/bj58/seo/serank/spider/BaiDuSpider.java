package com.bj58.seo.serank.spider;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bj58.seo.serank.model.Keyword;
import com.bj58.seo.serank.model.KeywordInfo;
import com.bj58.seo.serank.model.SEType;
import com.bj58.seo.serank.model.Task;

@Scope("prototype")
@Component("baidu.com")
public class BaiDuSpider extends BaseSpider {

  public static void main(String[] args) {
    BaiDuSpider s = new BaiDuSpider();
    Keyword keyWordModel = new Keyword();
    keyWordModel.setKeyword("畜禽养殖");

    Task taskModel = new Task();
    taskModel.setId_spider_task(99);
    s.setTaskModel(taskModel);

    List<KeywordInfo> infoModels = null;
    s.doGrab(keyWordModel, infoModels);
  }

  @Override
  protected SEType getSEType() {
    return SEType.BAIDU;
  }

  @Override
  protected int doGrab(Keyword keyWordModel, List<KeywordInfo> infoModels) {
    String keyword = keyWordModel.getKeyword();
    if (StringUtils.isEmpty(keyword)) {
      return SUCCESS;
    }

    String url = "http://www.baidu.com/s?wd=" + regexEx.urlEncode(keyword) + "&pn=0&rn=30";
    String content = getWebProxyContentByProxy(url, 5);

    // for local debug
    // try {
    // content = HttpTools.get(new URI(url), new HttpTools.StringEntityHandler());
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    if (StringUtils.isEmpty(content)) {
      return EMPTY_PAGE;
    }

    // 提取出 搜索关键字 列表页 单条列表
    // 注意：
    // 一、百度“文库”能匹配出来，但信息不完整，所以在结果中Host为空
    // 二、百度“最新相关信息”不能匹配出来
    // 三、百度“贴吧”不能匹配出来
    String regList = "<div class=\"result\\S*?\\s*c\\-container\\s*.*?</span>";
    List<String> list = regexEx.matchValues(content, regList);
    if (list.size() <= 0) {
      return EMPTY_FIELD;
    }

    // 提取出 搜索关键字 列表页 单条列表 所需要的取值，参考<BaiDuSpider.demo>
    String regTop = " id=\"(\\d+)\" ";

    List<String> regUrls = new ArrayList<String>();
    regUrls.add("<span\\s*class\\s*=\\s*\"g\">(.*?)(&nbsp;)?\\S*?(&nbsp;)?</span>"); // 普通列表
    regUrls.add("<span\\s*class\\s*=\\s*\"c\\-showurl\">(.*?)&nbsp;\\S*?&nbsp;</span>"); // 百度百科
    regUrls.add("<span\\s*class\\s*=\\s*\"c\\-showurl\">(.*?) *?\\S*? *?</span>"); // 其他
    regUrls.add("<span\\s*class\\s*=\\s*\"c\\-showurl\"> *?(\\S*?) *?</span>"); // 其他
    // 百度知道、最新相关信息、百度下载、百度贴吧等匹配出来Host可能为空

    // 真实跳转URL
    // String regTrueurl = "<a.*?href\\s*?=\\s*?\".*?//(\\S+)\".*?>";

    for (String fld : list) {
      String topRet = regexEx.matchValue_1(fld, regTop);
      if (StringUtils.isEmpty(topRet) || StringUtils.isEmpty(topRet.trim())) {
        // 取不到排名信息
        continue;
      }
      int top = Integer.parseInt(topRet);

      String urlRet = null;
      for (String regUrl : regUrls) {
        // 空字符串也可能匹配，选取匹配的非空串
        urlRet = regexEx.matchValue_NonEmpty(fld, regUrl);
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
      // System.out.println(infoModel.getHost()+ " " + infoModel.getMainhost());
    }

    return SUCCESS;
  }

}
