package com.octavio.starter_broker.assets;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.octavio.starter_broker.assets.AssetsRestApi.ASSETS;


public class GetAssetsHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetAssetsHandler.class);

  @Override
  public void handle(RoutingContext contex) {

    JsonArray jsonArray = new JsonArray();
    ASSETS.forEach((key, value) -> jsonArray.add(new Asset(value, key)));
    LOG.debug("Path {} responds with {}", contex.normalizedPath(), jsonArray);
    contex.response()
      .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .end(jsonArray.toBuffer());
  }
}
