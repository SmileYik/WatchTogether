package com.example.watchtogether.controller;

import com.example.watchtogether.config.WatchTogetherConfiguration;
import com.example.watchtogether.util.VideoUrlDog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年08月05日 10:20
 */
@CrossOrigin
@RestController
@RequestMapping("/video")
public class VideoUrlController {
  private WatchTogetherConfiguration watchTogetherConfiguration;
  private final Set<String> requests = new ConcurrentSkipListSet<>();

  @Autowired
  public void setWatchTogetherConfiguration(WatchTogetherConfiguration watchTogetherConfiguration) {
    this.watchTogetherConfiguration = watchTogetherConfiguration;
  }

  @GetMapping
  public Collection<String> getVideoUrl(HttpServletRequest servletRequest,
                                        @RequestParam("url") String oriUrl,
                                        @RequestParam(value = "wait", defaultValue = "5") int wait,
                                        @RequestParam(value = "token", required = false, defaultValue = "") String token) {

    Collection<String> collection = new ArrayList<>();
    if (!watchTogetherConfiguration.getToken().equals(token)) {
      String remoteHost = servletRequest.getRemoteHost();
      collection.add(String.format(
          "%s: 您未通过请求验证， 故仅可约%.2f分钟后再次进行请求.",
          remoteHost, watchTogetherConfiguration.getVideoDogCoolDown() / 60000.0
      ));
      if (requests.contains(remoteHost)) {
        return collection;
      }

      requests.add(remoteHost);
      Timer timer = new Timer();
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          requests.remove(remoteHost);
        }
      }, watchTogetherConfiguration.getVideoDogCoolDown());
    }




    long time = System.currentTimeMillis();
    try (VideoUrlDog videoUrlDog = new VideoUrlDog(oriUrl)) {
      collection.addAll(videoUrlDog.find(wait));
    }
    time = System.currentTimeMillis() - time;
    System.out.printf("[VideoURLDog] 花费 %dms 在 %s 中寻找以下链接: %s", time, oriUrl, collection);
    return collection;
  }

}
