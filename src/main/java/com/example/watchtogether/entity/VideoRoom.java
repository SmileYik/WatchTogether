package com.example.watchtogether.entity;

import com.example.watchtogether.websocket.RoomDoorSocket;
import lombok.Data;

import java.util.Collection;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年08月03日 17:23
 */
@Data
public class VideoRoom {
  private String id;
  private RoomDoorSocket host;
  private Collection<RoomDoorSocket> clients;
  private String url;
}
