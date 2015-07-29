package com.ymssdeng.serank.application;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ymssdeng.serank.executor.SERankExtractor;

/**
 * SE Rank application main entry
 * 
 * @author Administrator
 *
 */
public class SERankApplication {

  @SuppressWarnings("resource")
  public static void main(String[] args) {
    AnnotationConfigApplicationContext context = null;
    context = new AnnotationConfigApplicationContext(SERankConfig.class);
    SERankExtractor executor = context.getBean(SERankExtractor.class);
    executor.execute(); 
  }

}
