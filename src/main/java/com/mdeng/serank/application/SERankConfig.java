package com.mdeng.serank.application;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@ComponentScan(basePackages = {"com.mdeng.serank"})
// @PropertySource("classpath:/serank.properties")
public class SERankConfig {

  @Bean
  public static PropertyPlaceholderConfigurer ppc() {
    /**
     * In order to resolve ${...} placeholders in <bean> definitions or @Value annotations using
     * properties from a PropertySource, one must register a PropertySourcesPlaceholderConfigurer.
     * This happens automatically when using <context:property-placeholder> in XML, but must be
     * explicitly registered using a static @Bean method when using @Configuration classes. See the
     * "Working with externalized values" section of @Configuration Javadoc and
     * "a note on BeanFactoryPostProcessor-returning @Bean methods" of @Bean Javadoc for details and
     * examples.
     */
    PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
    ppc.setLocation(new ClassPathResource("serank.properties"));
    return ppc;
  }
}
