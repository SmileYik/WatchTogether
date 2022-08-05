package com.example.watchtogether.controller;

import com.example.watchtogether.util.VideoUrlDog;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年08月05日 10:20
 */
@CrossOrigin
@RestController
@RequestMapping("/video")
public class VideoUrlController {

  @GetMapping
  public Collection<String> getVideoUrl(@RequestParam("url") String oriUrl, @RequestParam(value = "wait", defaultValue = "5") int wait) {
    long time = System.currentTimeMillis();
    Collection<String> collection;
    try (VideoUrlDog videoUrlDog = new VideoUrlDog(oriUrl)) {
      collection = videoUrlDog.find(wait);
    }
    time = System.currentTimeMillis() - time;
    System.out.printf("[VideoURLDog] 花费 %dms 在 %s 中寻找以下链接: %s", time, oriUrl, collection);
    return collection;
  }

}
