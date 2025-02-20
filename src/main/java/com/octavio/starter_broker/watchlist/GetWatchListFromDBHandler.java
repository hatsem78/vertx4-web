package com.octavio.starter_broker.watchlist;


import com.octavio.starter_broker.db.DbResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;


public class GetWatchListFromDBHandler implements Handler<RoutingContext> {

  private final Pool db;
  private static final Logger LOG = LoggerFactory.getLogger(GetWatchListFromDBHandler.class);

  public GetWatchListFromDBHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    var accountId = WatchListResAPI.getAccountId(context);
    SqlTemplate.forQuery(
        db,
        "select w.asset from broker.watchlist w where w.account_id=#{accountId}")
      .mapTo(Row::toJson)
      .execute(Collections.singletonMap("account_id", accountId))
      .onFailure(DbResponse.errorHandler(context, "Failed to fetch watchlist for accountId:" + accountId))
      .onSuccess(assets -> {

        if (!assets.iterator().hasNext()) {
          DbResponse.notFountResponse(context, "Watchlist from account_id: " + accountId + " is not available");
          return;
        }

        var response = new JsonArray();
        assets.forEach(response::add);

        LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      });
  }
}
