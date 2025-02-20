package com.octavio.starter_broker.quotes;


import com.octavio.starter_broker.db.entity.QuoteEntity;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.sqlclient.templates.SqlTemplate;

import static com.octavio.starter_broker.db.DbResponse.notFountResponse;

import java.util.Collections;

import static com.octavio.starter_broker.db.DbResponse.errorHandler;

public class GetQuotesFromDbHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetQuotesFromDbHandler.class);
  private final Pool db;

  public GetQuotesFromDbHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    final String assetParam = context.pathParam("asset");
    LOG.debug("Asset parameter: {}", assetParam);
    SqlTemplate.forQuery(
        db,
        "Select * from broker.quotes where id asset=#{asset}"
      )
      .mapTo(QuoteEntity.class)
      .execute(Collections.singletonMap("asset", assetParam))
      .onFailure(errorHandler(context, "Failure get quote for asset " + assetParam + " from db"))
      .onSuccess(quotes -> {
        if (!quotes.iterator().hasNext()) {
          notFountResponse(context, "quote for asset " + assetParam + " not available!");
          return;
        }
        var response = quotes.iterator().next().toJsonObject();
        LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());

      });


  }
}

