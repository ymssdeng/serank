package com.bj58.seo.serank.application;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bj58.seo.core.http.HttpRequestBuilder;
import com.bj58.seo.core.http.HttpResponseHandlers;
import com.bj58.seo.core.utils.Jsons;
import com.bj58.seo.serank.exception.NoMoreException;
import com.bj58.seo.serank.exception.TaskNotRunningException;
import com.bj58.seo.serank.model.Keyword;
import com.bj58.seo.serank.model.KeywordInfo;
import com.bj58.seo.serank.model.Task;

@Component("httpImpl")
public class HttpTaskKeywordManager implements TaskKeywordManager {
  private Logger logger = LoggerFactory.getLogger(HttpTaskKeywordManager.class);
  public static final String API_URL = "http://api.seoadmin.union.vip.58.com";

  @Value("${serank.task.count}")
  private int limit = 5;

  public List<Task> getTasks() throws Exception {
    String url = String.format("%s/get_task?limit=%d", API_URL, limit);
    logger.info("GET {}", url);
    String str = HttpRequestBuilder.create().get(url).execute(HttpResponseHandlers.stringHandler());
    if ("NO_MORE".equals(str)) {
      throw new NoMoreException();
    }

    return Jsons.json2List(str, Task[].class);
  }

  public Keyword nextKeyword(int taskId, int seTypeId) throws Exception {
    String url = String.format("%s/next_keyword?taskid=%d&se_type=%d", API_URL, taskId, seTypeId);
    logger.info("GET {}", url);
    String str = HttpRequestBuilder.create().get(url).execute(HttpResponseHandlers.stringHandler());
    if ("NO_MORE".equals(str)) {
      throw new NoMoreException();
    } else if ("NOT_RUNNING".equals(str)) {
      throw new TaskNotRunningException();
    }

    return Jsons.json2Obj(str, Keyword.class);
  }

  public boolean setCursor(int taskId, int seTypeId, int keywordId, List<KeywordInfo> infos)
      throws Exception {
    if (infos.isEmpty()) return false;

    String url =
        String.format("%s/receive_data?taskid=%d&se_type=%d&keywordid=%d", API_URL, taskId,
            seTypeId, keywordId);
    logger.info("POST {}", url);
    String listdata = Jsons.obj2Json(infos);
    NameValuePair pair = new BasicNameValuePair("listdata", listdata);
    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
    pairs.add(pair);
    HttpPost post = new HttpPost(url);
    post.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));
    String str =
        HttpRequestBuilder.create().method(post).execute(HttpResponseHandlers.stringHandler());
    if ("OK".equals(str)) {
      return false;
    } else if ("FINISHED".equals(str)) {
      logger.info("FINISHED task {}", taskId);
      return true;
    }
    throw new IllegalStateException("unknown result " + str);
  }
}
