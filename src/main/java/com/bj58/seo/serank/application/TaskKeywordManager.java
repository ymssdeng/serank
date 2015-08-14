package com.bj58.seo.serank.application;

import java.util.List;

import com.bj58.seo.serank.model.Keyword;
import com.bj58.seo.serank.model.KeywordInfo;
import com.bj58.seo.serank.model.Task;

public interface TaskKeywordManager {

  List<Task> getTasks() throws Exception;

  Keyword nextKeyword(int taskId, int seTypeId) throws Exception;

  boolean setCursor(int taskId, int seTypeId, int keywordId, List<KeywordInfo> infos) throws Exception;
}
