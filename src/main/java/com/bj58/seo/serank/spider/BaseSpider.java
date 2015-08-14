package com.bj58.seo.serank.spider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bj58.seo.core.http.HttpRequestBuilder;
import com.bj58.seo.core.utils.Dates;
import com.bj58.seo.serank.application.TaskKeywordManager;
import com.bj58.seo.serank.exception.NoMoreException;
import com.bj58.seo.serank.exception.TaskNotRunningException;
import com.bj58.seo.serank.model.Keyword;
import com.bj58.seo.serank.model.KeywordInfo;
import com.bj58.seo.serank.model.SEType;
import com.bj58.seo.serank.model.Task;
import com.bj58.seo.serank.proxy.CommonHttpHeader;
import com.bj58.seo.serank.proxy.HttpConnPoolManager;
import com.bj58.seo.serank.proxy.HttpProxyPool;
import com.bj58.seo.serank.pub.RegexEx;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@Scope("prototype")
@Component
public abstract class BaseSpider implements Runnable {

  public static final int GRAB_LIMIT = 30;
  public static final int SUCCESS = 0;
  public static final int EMPTY_PAGE = 1; // 未采集到页面
  public static final int EMPTY_FIELD = 2; // 未抽取到字段

  protected Logger logger = LoggerFactory.getLogger(getClass());

  protected RegexEx regexEx = new RegexEx();

  private Task taskModel;// 任务模型

  protected int currentTop = 0; // 关键词排名
  @Value("${serank.http.connecttimeout}")
  protected int connecttimeout;
  @Value("${serank.http.sockettimeout}")
  protected int sockettimeout;
  @Autowired
  protected HttpProxyPool pool;
  @Autowired
  @Qualifier("httpImpl")
  protected TaskKeywordManager tm;
  protected String exedate = Dates.formatNow("yyyy-MM-dd");
  
  @Override
  public void run() {
    final String logMsgPrefix =
        Thread.currentThread().getName() + "#[" + getSEType().getName() + "] ";

    try {

      while (true) {
        // 获取下一个关键词
        Keyword kw = tm.nextKeyword(taskModel.getId_spider_task(), getSEType().getId());
        if (kw == null) {
          break;
        }

        // 执行抓取
        this.currentTop = 0; // 初始化关键词排名
        int grabRet = EMPTY_PAGE;
        int trys = 0;
        List<KeywordInfo> infoModels = null;
        while (trys < 10) {
          infoModels = Lists.newArrayList();
          grabRet = doGrab(kw, infoModels);
          if (grabRet == SUCCESS) {
            break;
          }

          try {
            trys++;
            TimeUnit.SECONDS.sleep(2);
          } catch (InterruptedException e) {}
        }

        String logMsgSuffix = kw.getKeyword() + "(" + kw.getId_keyword() + ")";
        switch (grabRet) {
          case EMPTY_PAGE:
            logger.info("{}未采集到页面：{}", logMsgPrefix, logMsgSuffix);
            break;
          case EMPTY_FIELD:
            logger.info("{}未抽取到字段：{}", logMsgPrefix, logMsgSuffix);
            break;
          default:
            logger.info("{}成功：{}", logMsgPrefix, logMsgSuffix);
            boolean finished = tm.setCursor(taskModel.getId_spider_task(), getSEType().getId(), kw.getId_keyword(),infoModels);
            if (finished) return;
            break;
        }
      }
    } catch (NoMoreException e) {
      logger.info("{} no more keyword in task {}, exit thread", logMsgPrefix, taskModel.getTaskname());
      return;
    } catch (TaskNotRunningException e) {
      logger.warn("{} task {} not running, exit thread", logMsgPrefix, taskModel.getTaskname());
      return;
    } catch (Exception e) {
      logger.error("{} spider failed in task {}, continue", logMsgPrefix, taskModel.getTaskname(),e);
    }
  }

  protected abstract SEType getSEType();

  protected abstract int doGrab(Keyword keyWordModel, List<KeywordInfo> infoModels);

  protected String getWebProxyContentByProxy(String url, int tryTimes) {
    for (int i = 0; i < tryTimes; i++) {
      HttpHost proxy = pool.dequeue();
      if (proxy == null) continue;

      try {
        HttpGet get = new HttpGet(url);
        get.setHeaders(CommonHttpHeader.get().toArray(new Header[0]));

        Builder configBuilder = RequestConfig.custom();
        configBuilder.setConnectTimeout(connecttimeout).setSocketTimeout(sockettimeout).setProxy(proxy);
        HttpRequestBuilder builder =
            HttpRequestBuilder.create(HttpConnPoolManager.singleton.build());
        builder.method(get).config(configBuilder.build());

        ResponseHandler<String> handler =
            com.bj58.seo.core.http.HttpResponseHandlers.stringHandler();
        String content = builder.execute(handler);
        if (!Strings.isNullOrEmpty(content)) return content;
      } catch (Exception e) {
        logger.error("Try failed in time {},{}", i, e.getMessage());
      }
    }

    return null;
  }

  /**
   * 从URL里提取出Host部分
   * 
   * @param url
   * @return String
   */
  protected String getHost(String url) {
    if (url.indexOf("...") >= 0) {
      url = regexEx.matchValue_1(url, "(.*?)\\.\\.\\.");
    }

    if (url.indexOf("http://") < 0) {
      url = "http://" + url;
    }

    // "(www\\.)|(\\.com\\.cn)|(\\.net\\.cn)|(\\.org\\.cn)|(\\.gov\\.cn)|(\\.cn\\.com)"
    String host = "";
    try {
      URL ui = new URL(url);
      String tempHost = ui.getHost();
      if (tempHost != null) {
        host = tempHost.replaceAll("(www\\.)", "");
      }
    } catch (MalformedURLException e) {
      logger.error("HOST解析出错：" + url, e);
    }

    return host;
  }

  protected String getMainHost(String url) {
    if (url.indexOf("...") >= 0) {
      url = regexEx.matchValue_1(url, "(.*?)\\.\\.\\.");
    }

    if (url.indexOf("http://") < 0) {
      url = "http://" + url;
    }

    String host = "";
    try {
      URL ui = new URL(url);
      String tempHost = ui.getHost();
      if (!StringUtils.isEmpty(tempHost)) {
        String[] splits = tempHost.split("\\.");
        int len = splits.length;
        if (len < 2) {
          host = splits[0];
        } else if (tempHost.indexOf(".com.cn") >= 0 || tempHost.indexOf(".net.cn") >= 0
            || tempHost.indexOf(".org.cn") >= 0 || tempHost.indexOf(".gov.cn") >= 0
            || tempHost.indexOf(".cn.com") >= 0) {
          host = splits[len - 3] + "." + splits[len - 2] + "." + splits[len - 1];
        } else {
          host = splits[len - 2] + "." + splits[len - 1];
        }
      }
    } catch (Throwable t) {
      logger.error("HOST解析出错：" + url, t);
    }

    return host;
  }

  protected List<KeywordInfo> getPadding(KeywordInfo keywordInfoModel) {

    List<KeywordInfo> infoModels = new ArrayList<KeywordInfo>();

    int padding = Math.min(keywordInfoModel.getTop(), GRAB_LIMIT);
    for (int top = this.currentTop + 1; top < padding; top++) {
      KeywordInfo infoModel = new KeywordInfo();
      infoModel.setTaskid(getTaskModel().getId_spider_task());
      infoModel.setExedate(exedate + " 00:00:00");
      infoModel.setId_keyword(keywordInfoModel.getId_keyword());
      infoModel.setKeyword(keywordInfoModel.getKeyword());
      infoModel.setSe_type(getSEType().getId());
      infoModel.setBusiness_line(keywordInfoModel.getBusiness_line());
      infoModel.setCityid(keywordInfoModel.getCityid());
      infoModel.setCate1(keywordInfoModel.getCate1());
      infoModel.setCate2(keywordInfoModel.getCate2());
      infoModel.setCate3(keywordInfoModel.getCate3());
      infoModel.setCate4(keywordInfoModel.getCate4());
      infoModel.setTop(top);
      infoModel.setHost(getSEType().getHost());
      infoModel.setTrue_url(getSEType().getHost());

      infoModels.add(infoModel);
    }

    keywordInfoModel.setTaskid(getTaskModel().getId_spider_task());
    keywordInfoModel.setExedate(exedate + " 00:00:00");
    keywordInfoModel.setSe_type(getSEType().getId());
    infoModels.add(keywordInfoModel);

    this.currentTop = keywordInfoModel.getTop();

    return infoModels;
  }

  public Task getTaskModel() {
    return taskModel;
  }

  public void setTaskModel(Task taskModel) {
    this.taskModel = taskModel;
  }

}
