package com.mdeng.serank.keyword.provider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mdeng.serank.keyword.FileKeyword;
import com.mdeng.serank.keyword.Keyword;

@Component
public class FileKeywordProvider implements KeywordProvider<Keyword> {

  private Logger logger = LoggerFactory.getLogger(FileKeywordProvider.class);
  @Value("${serank.keyword.dir}")
  private String dir;
  private File[] files;
  private BlockingQueue<FileKeyword> kqueue = new ArrayBlockingQueue<FileKeyword>(1000);
  private Future<?> readFuture;

  @PostConstruct
  public void init() {
    files = new File(dir).listFiles();
    ExecutorService es = Executors.newSingleThreadExecutor();
    readFuture = es.submit(new ReadFileThread());
  }

  @Override
  public boolean hasNextKeyword() {
    return !kqueue.isEmpty() || !readFuture.isDone();
  }

  @Override
  public FileKeyword nextKeyword() throws InterruptedException {
    if (!hasNextKeyword()) {
      throw new IllegalAccessError("no more keyword for input files");
    }

    return kqueue.take();
  }

  private class ReadFileThread implements Runnable {

    @Override
    public void run() {
      for (File file : files) {
        String pathstr = file.getAbsolutePath();
        Path path = Paths.get(pathstr);
        try {
          List<String> lines = Files.readAllLines(path, Charset.forName("utf-8"));
          for (int i = 0; i < lines.size(); i++) {
            FileKeyword fk = new FileKeyword();
            fk.setFilename(path.getFileName().toString());
            fk.setKeyword(lines.get(i));
            fk.setIndex(i);
            kqueue.put(fk);
          }
        } catch (IOException e) {
          logger.error("Failed to read keyword file {}", path, e);
        } catch (InterruptedException e) {
          logger.warn("read file thread interrupted");
        }
      }

    }

  }
}
