package com.octavio.starter_broker.watchlist;

import com.octavio.starter_broker.db.DbResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static io.vertx.sqlclient.templates.SqlTemplate.*;

public class PutWatchListFromDBHandler implements Handler<RoutingContext> {

  private final Pool db;

  public PutWatchListFromDBHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    var accountId = WatchListResAPI.getAccountId(context);

    var json = context.body().asJsonObject();
    var watchList = json.mapTo(WatchList.class);

    var parametersBatch = watchList.getAssetList().stream().map(asset -> {
      final Map<String, Object> parameters = new HashMap<>();
      parameters.put("account_id", accountId);
      parameters.put("asset", asset.getName());
      return parameters;
    }).toList();

    db.withTransaction(client -> {
        return forUpdate(client, "DELETE FROM broker.wathclist w WHERE w.account_id=#{account_id}")
          .execute(Collections.singletonMap("account_id", accountId))
          .onFailure(DbResponse.errorHandler(context, "Failed to clear watchlist for account_id: " + accountId))
          .compose(delte ->addAllForAccountId(context, parametersBatch, client));
      })
      .onSuccess(result ->context.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code()).end());
  }

  private Future<SqlResult<Void>> addAllForAccountId(
    RoutingContext context,
    List<Map<String, Object>> parametersBatch,
    SqlConnection client
  ) {
    return forUpdate(client,
        "INSERT INTO broker.watchlist VALUE(#{account_id},#{asset})")
      .executeBatch(parametersBatch)
      .onFailure(DbResponse.errorHandler(context, "Failed to insert into watchlist"));
  }
}
