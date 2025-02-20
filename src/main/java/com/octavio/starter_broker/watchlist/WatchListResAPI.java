package com.octavio.starter_broker.watchlist;

import com.octavio.starter_broker.assets.GetAssetsHandler;
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

  private WatchListResAPI() {
  }

  private static final Logger LOG = LoggerFactory.getLogger(WatchListResAPI.class);
  public static final String PATH = "/account/watchlist/:accountId";

  public static void attach(Router parent) {

    final HashMap<UUID, WatchList> watchListPerAccount = new HashMap<>();

    parent.get(PATH).handler(new GetWatchListHandler(watchListPerAccount));

    parent.post(PATH).handler(new PostWatchListHandler(watchListPerAccount));

    parent.delete(PATH).handler(new DeleteWatchListHandler(watchListPerAccount));

  }

  static String getAccountId(RoutingContext context) {
    var accountId = context.pathParam("accountId");
    LOG.info("{} for account {}", context.normalizedPath(), accountId);
    return accountId;
  }
}
