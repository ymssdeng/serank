package com.mdeng.serank.keyword.provider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mdeng.serank.keyword.Keyword;

@Component
public class FileKeywordProvider implements KeywordProvider<Keyword> {

  private Logger logger = LoggerFactory.getLogger(FileKeywordProvider.class);
  @Value("${serank.keyword.dir}")
  private String dir;
  private File[] files;
  private AtomicInteger fileIndex = new AtomicInteger();
  private static ThreadLocal<FileKeyword> tl = new ThreadLocal<FileKeyword>();

  private class FileKeyword {
    private List<String> lines;
    private int cursor;

    public List<String> getLines() {
      return lines;
    }

    public void setLines(List<String> lines) {
      this.lines = lines;
    }

    public int getCursor() {
      return cursor;
    }

    public void setCursor(int cursor) {
      this.cursor = cursor;
    }
  }

  @PostConstruct
  public void init() {
    files = new File(dir).listFiles();
  }

  @Override
  public boolean hasNextKeyword() throws IOException {
    FileKeyword fk = null;
    if ((fk = tl.get()) == null) {
      int index = fileIndex.getAndIncrement();
      if (index >= files.length) {
        throw new IllegalAccessError("no more file to be accessed");
      }
      fk = readFile(index);
      tl.set(fk);
    }

    return fk.getCursor() < fk.getLines().size();
  }

  private FileKeyword readFile(int index) throws IOException {
    String pathstr = files[index].getAbsolutePath();
    Path path = Paths.get(pathstr);

    // read file
    try {
      List<String> lines = Files.readAllLines(path, Charset.forName("utf-8"));
      FileKeyword fk = new FileKeyword();
      fk.setLines(lines);
      return fk;
    } catch (IOException e) {
      logger.error("Failed to read keyword file {}", path, e);
      throw e;
    }
  }

  @Override
  public Keyword nextKeyword() throws IOException {
    if (!hasNextKeyword()) {
      throw new IllegalAccessError("no more keyword for current file");
    }

    FileKeyword fk = tl.get();
    Keyword keyword = new Keyword();
    keyword.setKeyword(fk.getLines().get(fk.getCursor()));
    fk.setCursor(fk.getCursor() + 1);
    return keyword;
  }

}
