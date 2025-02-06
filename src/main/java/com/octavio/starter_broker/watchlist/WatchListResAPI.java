package com.octavio.starter_broker.watchlist;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class WatchListResAPI {

  private WatchListResAPI(){}
  private static final Logger LOG = LoggerFactory.getLogger(WatchListResAPI.class);
  public static final  String PATH = "/account/watchlist/:accountId";

  public static void attach(Router parent) {
    final HashMap<UUID, WatchList> watchListPerAccount = new HashMap<>();


    parent.get(PATH).handler(context -> {
      var accountId = getAccountId(context);
      var watchList = Optional.ofNullable(watchListPerAccount.get(UUID.fromString(accountId)));

      if (watchList.isEmpty()) {
        context.response()
          .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
          .end(new JsonObject()
            .put("message", "watchList not found for accountId: " + accountId + " not available")
            .put("path", context.normalizedPath())
            .toBuffer()
          );
        return;
      }

      context.response()
        .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(watchList.get().toJsonObject().toBuffer());
    });

    parent.post(PATH).handler(context -> {
      var accountId = getAccountId(context);

      var json = context.body().asJsonObject();
      var watchList = json.mapTo(WatchList.class);
      watchListPerAccount.put(UUID.fromString(accountId), watchList);

      context.response()
        .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(json.toBuffer());

    });

    parent.delete(PATH).handler(context -> {
      var accountId = getAccountId(context);
      var watchList =watchListPerAccount.remove(UUID.fromString(accountId));
      context.response()
        .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(watchList.toJsonObject().toBuffer());
    });

  }

  private static String getAccountId(RoutingContext context) {
    var accountId = context.pathParam("accountId");
    LOG.info("{} for account {}", context.normalizedPath(), accountId);
    return accountId;
  }
}
