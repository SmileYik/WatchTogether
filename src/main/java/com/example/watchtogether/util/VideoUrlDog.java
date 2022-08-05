package com.example.watchtogether.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.Closeable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年08月05日 13:53
 */
public class VideoUrlDog implements Closeable {
  private static final Pattern PATTERN = Pattern.compile("(?<=documentURL\":\")(http.*\")");
  private final WebDriver webDriver;

  public VideoUrlDog(String url) {
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.setHeadless(true);
    LoggingPreferences loggingPreferences = new LoggingPreferences();
    loggingPreferences.enable(LogType.PERFORMANCE, Level.ALL);
    chromeOptions.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);
    webDriver = new ChromeDriver(chromeOptions);
    webDriver.get(url);
  }

  /**
   * 搜索页面中的视频链接.
   * @param waitTime 等待时间.
   * @return 视频链接.
   */
  public Collection<String> find(int waitTime) {
    Collection<String> urls = new HashSet<>();
    findVideoUrl(urls, webDriver, waitTime);
    Collection<String> m3u8s = analyzeLog();
    urls.addAll(m3u8s);
    return analyzeUrl(urls);
  }

  /**
   * 分析链接.
   * @param urls 已经找到的视频链接.
   * @return 分析后的链接.
   */
  private Collection<String> analyzeUrl(Collection<String> urls) {
    Collection<String> collection = new HashSet<>();
    LinkedList<String> stack = new LinkedList<>(urls);

    while (!stack.isEmpty()) {
      String url = stack.removeFirst();
      // 跳过blob开头的链接
      if (url.startsWith("blob")) {
        continue;
      }

      collection.add(url);
      // 去除前面的http4个字符
      String _url = url.substring(4);
      int idx;
      // 搜索这个链接里是否嵌套某一链接
      if ((idx = _url.indexOf("http")) != -1) {
        String sub = _url.substring(idx);
        try {
          sub = URLDecoder.decode(sub, "UTF-8");
        } catch (UnsupportedEncodingException e) {
          continue;
        }
        stack.addFirst(sub);
      }
    }
    return collection;
  }

  private void kill() {
    if (webDriver != null) {
      webDriver.quit();
    }
  }

  /**
   * 分析产生的日志里是否具有m3u8链接.
   * @return m3u8链接.
   */
  private Collection<String> analyzeLog() {
    Collection<String> urls = new HashSet<>();
    LogEntries logEntries = webDriver.manage().logs().get(LogType.PERFORMANCE);
    for (LogEntry entry : logEntries) {
      String message = entry.getMessage();
      Matcher matcher = PATTERN.matcher(message);
      while (matcher.find()) {
        int start = matcher.start();
        int end = matcher.end();
        String str = message.substring(start, end);
        if (str.contains("\"")) {
          str = str.substring(0, str.indexOf("\""));
        }
        if (str.contains(".m3u8")) {
          urls.add(str);
        }
      }
    }
    return urls;
  }

  /**
   * 搜索video标签内的src。
   * @param urls 返回的链接集合.
   * @param webDriver WebDriver
   * @param wait 等待时间（秒）
   */
  private void findVideoUrl(Collection<String> urls, WebDriver webDriver, int wait) {
    // 查找是否有video标签
    WebDriverWait webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(wait));
    try {
      webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("video")));
      List<WebElement> videos = webDriver.findElements(By.tagName("video"));
      for (WebElement video : videos) {
        urls.add(video.getAttribute("src"));
      }
    } catch (Exception ignored) {

    }

    // 搜索iframe
    List<WebElement> iframes = webDriver.findElements(By.tagName("iframe"));
    for (WebElement iframe : iframes) {
      webDriver.switchTo().frame(iframe);
      findVideoUrl(urls, webDriver, wait);
      webDriver.switchTo().parentFrame();
    }
  }

  /**
   * Closes this stream and releases any system resources associated
   * with it. If the stream is already closed then invoking this
   * method has no effect.
   *
   * <p> As noted in {@link AutoCloseable#close()}, cases where the
   * close may fail require careful attention. It is strongly advised
   * to relinquish the underlying resources and to internally
   * <em>mark</em> the {@code Closeable} as closed, prior to throwing
   * the {@code IOException}.
   *
   */
  @Override
  public void close() {
    kill();
  }
}
