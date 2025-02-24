package com.octavio.starter_broker.db;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DbConfig {
  String host;
  int port;
  String database;
  String user;
  String password;
  String typeDb;

  @Override
  public String toString() {
    return "DbConfig{" +
      "host='" + host + '\'' +
      ", port=" + port +
      ", database='" + database + '\'' +
      ", user='" + user + '\'' +
      ", typeDb='" + typeDb + '\'' +
      ", password='********" +
      '}';
  }
}
