package com.octavio.starter_broker.quotes;

import com.octavio.starter_broker.MainVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestQuoteRestapi {

  private static final Logger LOG = LoggerFactory.getLogger(TestQuoteRestapi.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void returns_quote_for_assets(Vertx vertx, VertxTestContext testContext) throws Throwable {
    var webclient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    webclient.get("/quotes/Luiza/1")
      .send()
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("response quote: {}", json);
        assertEquals("{\"name\":\"Luiza\",\"id\":1}", json.getJsonObject("asset").encode());
        assertEquals(200, response.statusCode());
        testContext.completeNow();
      }));
  }

  @Test
  void returns_not_found_for_anknow_asset(Vertx vertx, VertxTestContext testContext) throws Throwable {
    var webclient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    webclient.get("/quotes/Luiza/1")
      .send()
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("response quote not found: {}", json);
        assertEquals("quote for asset name: Luiza, id: 1 not available", json.getString("message"));
        assertEquals(404, response.statusCode());
        testContext.completeNow();
      }));
  }





}
