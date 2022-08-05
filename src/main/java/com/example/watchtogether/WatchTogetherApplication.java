package com.example.watchtogether;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class WatchTogetherApplication {

  public static void main(String[] args) {
    SpringApplication.run(WatchTogetherApplication.class, args);
  }

}
