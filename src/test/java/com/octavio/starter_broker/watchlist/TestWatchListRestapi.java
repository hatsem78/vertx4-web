package com.octavio.starter_broker.watchlist;

import com.octavio.starter_broker.MainVerticle;
import com.octavio.starter_broker.assets.Asset;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestWatchListRestapi {

  private static final Logger LOG = LoggerFactory.getLogger(TestWatchListRestapi.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void adds_and_returns_watch_list_for_account(Vertx vertx, VertxTestContext testContext) throws Throwable {
    var webclient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    var accountId = UUID.randomUUID();
    webclient.post("/account/watchlist/" + accountId)
      .sendJsonObject(getJson())
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("response quote: {}", json);
        assertEquals("{\"assetList\":[{\"name\":\"Lucas\",\"id\":1}]}", json.encode());
        assertEquals(200, response.statusCode());
        testContext.completeNow();
      }))
      .compose(next -> {
        webclient.get("/account/watchlist/" + accountId)
          .send()
          .onComplete(testContext.succeeding(response -> {
            var json = response.bodyAsJsonObject();
            LOG.info("response get: {}", json);
            assertEquals("{\"assetList\":[{\"name\":\"Lucas\",\"id\":1}]}", json.encode());
            assertEquals(200, response.statusCode());
            testContext.completeNow();
          }));
        return Future.succeededFuture();
      });

  }

  @Test
  void adds_and_deletes_watch_list_for_account(Vertx vertx, VertxTestContext testContext) throws Throwable {
    var webclient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    var accountId = UUID.randomUUID();
    webclient.post("/account/watchlist/" + accountId)
      .sendJsonObject(getJson())
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("response quote: {}", json);
        assertEquals("{\"assetList\":[{\"name\":\"Lucas\",\"id\":1}]}", json.encode());
        assertEquals(200, response.statusCode());
        testContext.completeNow();
      }))
      .compose(next -> {
        webclient.delete("/account/watchlist/" + accountId)
          .send()
          .onComplete(testContext.succeeding(response -> {
            var json = response.bodyAsJsonObject();
            LOG.info("response delete: {}", json);
            assertEquals("{\"assetList\":[{\"name\":\"Lucas\",\"id\":1}]}", json.encode());
            assertEquals(200, response.statusCode());
            testContext.completeNow();
          }));
        return Future.succeededFuture();
      });

  }

  private static JsonObject getJson() {
    return new WatchList(List.of(new Asset("Lucas", 1))).toJsonObject();
  }

}
