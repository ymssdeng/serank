package com.bj58.seo.serank.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * SE Rank application main entry
 * 
 * @author Administrator
 *
 */
public class SERankApplication {
  protected static Logger logger = LoggerFactory.getLogger(SERankApplication.class);

//  static {
//    try {
//      URL url =
//          SERankApplication.class.getClassLoader()
//              .getResource("oceanus/oceanus_configurations.xml");
//      String configPath = new File(url.toURI()).getAbsolutePath();
//      Oceanus.init(configPath);
//    } catch (Exception e) {
//      logger.error("Failed to init oceanus", e);
//      System.exit(1);
//    }
//  }

  @SuppressWarnings("resource")
  public static void main(String[] args) {
    AnnotationConfigApplicationContext context = null;
    context = new AnnotationConfigApplicationContext(SERankConfig.class);
    SERankExtractor executor = context.getBean(SERankExtractor.class);
    executor.execute();
  }

}
