package com.stackoverflow.questions.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;

@Configuration
public class ChannelsConfig {

  @Bean(name = "studentErrorChannel")
  public MessageChannel studentErrorChannel() {
    return MessageChannels.queue("studentErrorChannel").get();
  }

}
