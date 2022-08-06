package com.example.watchtogether.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年08月05日 10:12
 */
@Component
@ConfigurationProperties("watchtogether")
public class WatchTogetherConfiguration {
  private String chromeDriverPath;
  private String token = "123456";
  private long videoDogCoolDown = 5 * 60 * 1000;

  public String getChromeDriverPath() {
    return chromeDriverPath;
  }

  public void setChromeDriverPath(String chromeDriverPath) {
    this.chromeDriverPath = chromeDriverPath;
    try {
      System.setProperty("webdriver.chrome.driver", Paths.get(chromeDriverPath).toRealPath().toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public void setVideoDogCoolDown(long videoDogCoolDown) {
    this.videoDogCoolDown = videoDogCoolDown;
  }

  public long getVideoDogCoolDown() {
    return videoDogCoolDown;
  }
}
