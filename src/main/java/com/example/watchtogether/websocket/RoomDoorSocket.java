package com.example.watchtogether.websocket;

import com.example.watchtogether.entity.VideoRoom;
import com.example.watchtogether.util.StatisticHelper;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年08月02日 17:10
 */
@Component
@ServerEndpoint("/room/{roomId}/{username}")
public class RoomDoorSocket {
  private static final Map<String, VideoRoom> rooms = new HashMap<>();

  private String roomId;
  private String username;
  private Session session;
  private String sameHeart;

  @OnOpen
  public void onOpen(Session session,
                     @PathParam("roomId") String roomId,
                     @PathParam("username") String username) {
    this.session = session;
    this.roomId = roomId;
    this.username = username;
    VideoRoom videoRoom;
    if (rooms.containsKey(roomId)) {
      // 房间已经存在，加入此房间
      videoRoom = rooms.get(roomId);
      videoRoom.getClients().add(this);
      sendMessage("client");
    } else {
      // 房间不存在，创建此房间
      videoRoom = new VideoRoom();
      videoRoom.setClients(new HashSet<>());
      videoRoom.setId(roomId);
      videoRoom.setHost(this);
      rooms.put(roomId, videoRoom);
      sendMessage("host");
      StatisticHelper.addCreatedRoomsCount(roomId);
    }
    videoRoom.getHost().sendMessage("join" + username);
    videoRoom.getClients().forEach(it -> {
      it.sendMessage("join" + username);
    });
    StatisticHelper.addWatchTime(username);
    StatisticHelper.updateRooms(rooms);
  }

  @OnMessage
  public void onMessage(String msg) {
    VideoRoom videoRoom = rooms.get(roomId);
    if (msg == null || msg.length() == 0 || "host".equals(msg) || "client".equals(msg)) {
      return;
    } else if (msg.startsWith("video")) {
      String url = msg.substring(5);
      videoRoom.setUrl(url);
      StatisticHelper.addLink(url);
    } else if (msg.startsWith("getVideo")) {
      sendMessage(String.valueOf(videoRoom.getUrl()));
      return;
    } else if (msg.startsWith("hsame")) {
      sameHeart = msg;
      return;
    } else if (msg.startsWith("doSame") && videoRoom.getHost().sameHeart != null) {
      sendMessage(videoRoom.getHost().sameHeart.substring(1));
      return;
    }

    if (this == videoRoom.getHost() && !msg.equals("heart")) {
      videoRoom.getClients().forEach(it -> it.sendMessage(msg));
    }
  }

  @OnClose
  public void onClose() {
    VideoRoom videoRoom = rooms.get(roomId);
    if (videoRoom != null) {
      if (this == videoRoom.getHost()) {
        rooms.remove(videoRoom.getId());
        videoRoom.getClients().forEach(it -> {
          try {
            it.session.close();
          } catch (IOException ignored) {

          }
        });
      } else {
        try {
          videoRoom.getClients().remove(this);
          videoRoom.getClients().forEach(it -> {
            it.sendMessage("exit" + username);

          });
        } catch (Exception ignored) {

        } finally {
          videoRoom.getHost().sendMessage("exit" + username);
        }
      }
    }

    try {
      session.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    StatisticHelper.updateRooms(rooms);
  }

  @OnError
  public void onError(Throwable t) {
    t.printStackTrace();
  }

  private void sendMessage(String message) {
    try {
      session.getBasicRemote().sendText(message);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getUsername() {
    return username;
  }
}
