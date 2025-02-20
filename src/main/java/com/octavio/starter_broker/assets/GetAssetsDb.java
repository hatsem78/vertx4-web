package com.octavio.starter_broker.assets;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.octavio.starter_broker.db.DbResponse.errorHandler;

public class GetAssetsDb implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(GetAssetsDb.class);
  private final Pool db;

  public GetAssetsDb(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    this.db.query("SELECT a.value FROM broker.assets a")
      .execute()
      .onFailure(errorHandler(context, "Failure get assets from db"))
      .onSuccess(result -> {
        var response = new JsonArray();
        result.forEach(row -> response.add(row.getValue("value").toString()));
        LOG.debug("Path {} responds with {}", context.normalizedPath(), response);
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      });
  }
}
