package com.octavio.starter_broker.config;

import com.octavio.starter_broker.db.DbConfig;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.Objects;


@Builder
@Value
@ToString
public class BrokerConfig {

  int serverPort;
  String version;
  String dbType;
  DbConfig dbConfig;

  public static BrokerConfig from(final JsonObject config) {
    final Integer serverPort = config.getInteger(ConfigLoader.SERVER_PORT);
    if (Objects.isNull(serverPort)) {
      throw new RuntimeException(ConfigLoader.SERVER_PORT + " not configured!");
    }

    final String version = config.getString("version");
    if (Objects.isNull(version)) {
      throw new RuntimeException("version is not configured in config file!");
    }

    final String serverDbType = config.getString(ConfigLoader.DB_TYPE);

    return BrokerConfig.builder()
      .serverPort(serverPort)
      .version(version)
      .dbType(serverDbType)
      .dbConfig(parseDbConfig(config))
      .build();
  }

  private static DbConfig parseDbConfig(final JsonObject config) {
    return DbConfig.builder()
      .host(config.getString(ConfigLoader.DB_HOST))
      .port(config.getInteger(ConfigLoader.DB_PORT))
      .database(config.getString(ConfigLoader.DB_DATABASE))
      .user(config.getString(ConfigLoader.DB_USER))
      .password(config.getString(ConfigLoader.DB_PASSWORD))
      .typeDb(config.getString(ConfigLoader.DB_TYPE))
      .build();
  }
}
