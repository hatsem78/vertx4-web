package com.octavio.starter_broker.watchlist;

import com.octavio.starter_broker.db.DbResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.Collections;

public class DeleteWatchListFromDBHandler implements Handler<RoutingContext> {

  private final Pool db;

  public DeleteWatchListFromDBHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {

    var accountId = WatchListResAPI.getAccountId(context);
    SqlTemplate.forUpdate(db,
        "DELETE FROM broker.watchlist w where w.account_id=#{accountId}")
      .execute(Collections.singletonMap("account_id", accountId))
      .onFailure(DbResponse.errorHandler(context, "Failed to delete watchlist for account_id:" + accountId))
      .onSuccess(result->context.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code()).end());

  }
}
