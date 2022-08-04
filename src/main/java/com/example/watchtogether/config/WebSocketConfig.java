package com.example.watchtogether.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年08月02日 14:24
 */
@Configuration
public class WebSocketConfig {
  @Bean
  public ServerEndpointExporter serverEndpointExporter() {
    return new ServerEndpointExporter();
  }
}
