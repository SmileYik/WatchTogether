package com.example.watchtogether.websocket;

import com.example.watchtogether.entity.VideoRoom;
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
@ServerEndpoint("/room/{roomId}")
public class RoomDoorSocket {
  private static final Map<String, VideoRoom> rooms = new HashMap<>();

  private String roomId;
  private Session session;

  @OnOpen
  public void onOpen(Session session, @PathParam("roomId") String roomId) {
    this.session = session;
    this.roomId = roomId;
    if (rooms.containsKey(roomId)) {
      VideoRoom videoRoom = rooms.get(roomId);
      videoRoom.getClients().add(this);
      sendMessage("client");
      System.out.println(session.getUserProperties());
    } else {
      VideoRoom room = new VideoRoom();
      room.setClients(new HashSet<>());
      room.setId(roomId);
      room.setHost(this);
      rooms.put(roomId, room);
      sendMessage("host");
    }
  }

  @OnMessage
  public void onMessage(String msg) {
    VideoRoom videoRoom = rooms.get(roomId);
    if (msg == null || msg.length() == 0 || "host".equals(msg) || "client".equals(msg)) {
      return;
    } else if (msg.startsWith("video")) {
      String url = msg.substring(5);
      videoRoom.setUrl(url);
    } else if (msg.startsWith("getVideo")) {
      sendMessage(String.valueOf(videoRoom.getUrl()));
      return;
    }

    if (this == videoRoom.getHost() && !msg.equals("heart")) {
      videoRoom.getClients().forEach(it -> it.sendMessage(msg));
    }
  }

  @OnClose
  public void onClose() {
    VideoRoom videoRoom = rooms.get(roomId);
    if (this == videoRoom.getHost()) {
      rooms.remove(videoRoom.getId());
      videoRoom.getClients().forEach(it -> {
        try {
          it.session.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    } else {
      videoRoom.getClients().remove(this);
    }
    try {
      session.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
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
}
