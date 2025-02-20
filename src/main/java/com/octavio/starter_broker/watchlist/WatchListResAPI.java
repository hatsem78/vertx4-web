package com.octavio.starter_broker.watchlist;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class WatchListResAPI {

  private WatchListResAPI() {
  }

  private static final Logger LOG = LoggerFactory.getLogger(WatchListResAPI.class);

  public static final String PATH = "/account/watchlist/:accountId";

  public static void attach(Router parent, final Pool db) {

    final HashMap<UUID, WatchList> watchListPerAccount = new HashMap<>();

    parent.get(PATH).handler(new GetWatchListHandler(watchListPerAccount));

    parent.post(PATH).handler(new PostWatchListHandler(watchListPerAccount));

    parent.delete(PATH).handler(new DeleteWatchListHandler(watchListPerAccount));

    final String dbPath = "/db/account/watchlist/:accountId";
    parent.get(dbPath).handler(new GetWatchListFromDBHandler(db));
    parent.get(dbPath).handler(new PutWatchListFromDBHandler(db));
    parent.get(dbPath).handler(new DeleteWatchListFromDBHandler(db));

  }

  static String getAccountId(RoutingContext context) {
    var accountId = context.pathParam("accountId");
    LOG.info("{} for account {}", context.normalizedPath(), accountId);
    return accountId;
  }
}
