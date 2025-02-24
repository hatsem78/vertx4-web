package com.octavio.starter_broker.db;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbResponse {
  private static final Logger LOG = LoggerFactory.getLogger(DbResponse.class);

  private DbResponse() {
  }

  public static Handler<Throwable> errorHandler(RoutingContext context, String massage) {
    return error -> {
      LOG.error("Failure: ", error);
      context.response()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(new JsonObject()
          .put("message", massage)
          .put("path", context.normalizedPath())
          .toBuffer()
        );
    };
  }

  public static void  notFountResponse(RoutingContext context, String message) {
    context.response()
      .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
      .end(new JsonObject()
        .put("message", message)
        .put("path", context.normalizedPath())
        .toBuffer()
      );
  }

}
