package com.example.watchtogether.util;

import com.example.watchtogether.entity.VideoRoom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年08月08日 9:28
 */
public class StatisticHelper {
  private static Set<String> onlineRooms = new HashSet<>();
  private static long createdRoomsCount = 0;
  private static long watchTimes = 0;
  private static Set<String> onlineWatchers = new HashSet<>();
  private static long useVideoUrlDogTimes = 0;

  static {
    load();
  }

  public static void updateRooms(Map<String, VideoRoom> onlineRooms) {
    StatisticHelper.onlineRooms = onlineRooms.keySet();
    onlineWatchers = new HashSet<>();
    onlineRooms.values().forEach(it -> {
      it.getClients().forEach(user -> onlineWatchers.add(user.getUsername()));
      onlineWatchers.add(it.getHost().getUsername());
    });
  }

  public static void addWatchTime(String username) {
    ++watchTimes;
    save();
    writeLineToFile("users", username);
  }

  public static void addCreatedRoomsCount(String roomName) {
    ++createdRoomsCount;
    save();
    writeLineToFile("rooms", roomName);
  }

  public static void addUesVideoUrlDogTimes() {
    ++useVideoUrlDogTimes;
    save();
  }

  public static void addLink(String link) {
    writeLineToFile("links", link);
  }

  private static void writeLineToFile(String fileName, String line) {
    try {
      Files.write(
          Paths.get(fileName),
          (line + "\n").getBytes(StandardCharsets.UTF_8),
          StandardOpenOption.WRITE,
          StandardOpenOption.APPEND,
          StandardOpenOption.CREATE
      );
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public long getCreatedRoomsCount() {
    return createdRoomsCount;
  }

  public Set<String> getOnlineWatchers() {
    return onlineWatchers;
  }

  public long getWatchTimes() {
    return watchTimes;
  }

  public Set<String> getOnlineRooms() {
    return onlineRooms;
  }

  public long getUseVideoUrlDogTimes() {
    return useVideoUrlDogTimes;
  }

  private static void save() {
    Properties properties = new Properties();
    properties.setProperty("watchTimes", watchTimes + "");
    properties.setProperty("createdRoomsCount", createdRoomsCount + "");
    properties.setProperty("useVideoUrlDogTimes", useVideoUrlDogTimes + "");
    File file = new File("statistic.properties");
    try {
      properties.store(Files.newOutputStream(file.toPath()), "Statistic");
    } catch (IOException ignored) {

    }
  }

  private static void load() {
    Properties properties = new Properties();
    File file = new File("statistic.properties");
    if (file.exists()) {
      try {
        properties.load(Files.newInputStream(file.toPath()));
        watchTimes = Long.parseLong(properties.getProperty("watchTimes"));
        createdRoomsCount = Long.parseLong(properties.getProperty("createdRoomsCount"));
        useVideoUrlDogTimes = Long.parseLong(properties.getProperty("useVideoUrlDogTimes"));
      } catch (Exception ignored) {

      }
    }
  }
}
