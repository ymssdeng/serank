package com.mdeng.serank.keyword.provider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mdeng.serank.keyword.KeywordRank;

@Component
public class FileKeywordProvider implements KeywordGroupProvider, KeywordProvider {

  private Logger logger = LoggerFactory.getLogger(FileKeywordProvider.class);
  @Value("${serank.keyword.dir}")
  private String dir;
  private File[] files;
  private int fileIndex = 0;
  private int groupId = 0;
  private int keywordIndex = 0;
  private List<String> lines;

  @PostConstruct
  public void init() {
    files = new File(dir).listFiles();
  }

  @Override
  public boolean hasNextGroup() {
    if (files == null) return false;
    return fileIndex < files.length;
  }

  @Override
  public int nextGroup() {
    if (!hasNextGroup()) return 0;

    String pathstr = files[fileIndex++].getAbsolutePath();
    Path path = Paths.get(pathstr);

    // read file
    try {
      lines = Files.readAllLines(path, Charset.forName("utf-8"));
    } catch (IOException e) {
      logger.error("Failed to read keyword file {}:{}", path, e.getMessage());
      return 0;
    }

    // group_1.txt
    String filename = path.getFileName().toString().split("\\.")[0];
    groupId = Integer.valueOf(filename.replace("group_", ""));
    return groupId;
  }

  @Override
  public synchronized boolean hasNextKeyword(int groupId) {
    if (groupId <= 0) return false;
    return this.groupId == groupId ? keywordIndex < lines.size() : false;
  }

  @Override
  public synchronized KeywordRank nextKeyword(int groupId) {
    if (!hasNextKeyword(groupId)) return null;
    KeywordRank kr = new KeywordRank();
    kr.setGroup(groupId);
    kr.setKeyword(lines.get(keywordIndex++).trim());
    return kr;
  }

}
