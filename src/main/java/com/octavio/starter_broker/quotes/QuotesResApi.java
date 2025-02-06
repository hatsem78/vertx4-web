package com.octavio.starter_broker.quotes;

import com.octavio.starter_broker.assets.Asset;
import com.octavio.starter_broker.assets.AssetsRestApi;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Value
public class QuotesResApi {

  private static final Logger LOG = LoggerFactory.getLogger(QuotesResApi.class);

  QuotesResApi() {
  }

  public static void attach(Router parent) {
    parent.get("/quotes/:asset/:id").handler(context -> {

      final String assetParam = context.pathParam("asset");
      final Integer idParam = Integer.valueOf(context.pathParam("id"));


      var maybeQuote = Optional.ofNullable(AssetsRestApi.ASSETS.get(idParam));
      if (maybeQuote.isEmpty()) {
        context.response()
          .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
          .end(new JsonObject()
            .put("message", "quote for asset name: " + assetParam + ", id: " + idParam + " not available")
            .put("path", context.normalizedPath())
            .toBuffer()
          );
        return;
      }

      LOG.debug("Asset parameter: idParam {}, id {}", assetParam, idParam);

      var quote = initRandomQuote(assetParam, idParam);

      final JsonObject response = quote.toJsonObject();
      LOG.debug("Path {} responds with {}", context.normalizedPath(), response);
      context.response()
        .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(response.toBuffer());
    });

  }

  private static Quote initRandomQuote(String assetParam, Integer idParam) {
    return Quote.builder()
      .asset(new Asset(assetParam, idParam))
      .volume(randomValue())
      .ask(randomValue())
      .bid(randomValue())
      .lastPrice(randomValue())
      .build();
  }

  private static BigDecimal randomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
  }
}
