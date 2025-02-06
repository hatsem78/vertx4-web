package com.octavio.starter_broker.assets;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;


public class AssetsRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(AssetsRestApi.class);
  public static final Map<Integer,  String> ASSETS = new HashMap<>();

  private AssetsRestApi() {
    throw new IllegalStateException("AssetsRestApi class");
  }

  public static void attach(Router parent) {
    parent.get("/assets").handler(contex -> {
      JsonArray jsonArray = new JsonArray();

      ASSETS.put(1, "Luiza");
      ASSETS.put(2, "Jorge");

      ASSETS.forEach((key,value)->{
        jsonArray.add(new Asset(value, key));
      });

      LOG.debug("Path {} responds with {}", contex.normalizedPath(), jsonArray);
      contex.response()
        .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(jsonArray.toBuffer());

    });
  }
}
