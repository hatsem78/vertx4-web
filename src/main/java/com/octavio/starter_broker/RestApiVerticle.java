package com.octavio.starter_broker;

import com.octavio.starter_broker.assets.AssetsRestApi;
import com.octavio.starter_broker.config.BrokerConfig;
import com.octavio.starter_broker.config.ConfigLoader;
import com.octavio.starter_broker.quotes.QuotesResApi;
import com.octavio.starter_broker.watchlist.WatchListResAPI;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(configuration -> {
        LOG.info("Retrieved Configuration: {}", configuration);
        startHttpServerAndAttachRoutes(startPromise, configuration);
      });
  }

  private void startHttpServerAndAttachRoutes(final Promise<Void> startPromise,
                                              final BrokerConfig configuration) {
    // One pool for each Rest Api Verticle
//    final Pool db = DBPools.createPgPool(configuration, vertx);
    // Alternatively use MySQL
    // final Pool db = DBPools.createMySQLPool(configuration, vertx);

    final Router restApi = Router.router(vertx);
    restApi.route()
      .handler(BodyHandler.create())
      .failureHandler(handleFailure());
    AssetsRestApi.attach(restApi);
    QuotesResApi.attach(restApi);
    WatchListResAPI.attach(restApi);

    vertx.createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler(error -> LOG.error("HTTP Server error: ", error))
      .listen(configuration.getServerPort(), http -> {
        if (http.succeeded()) {
          startPromise.complete();
          LOG.info("HTTP server started on port {}", configuration.getServerPort());
        } else {
          startPromise.fail(http.cause());
        }
      });
  }

  private Handler<RoutingContext> handleFailure() {
    return errorContext -> {
      if (errorContext.response().ended()) {
        // Ignore completed response
        return;
      }
      LOG.error("Route Error:", errorContext.failure());
      errorContext.response()
        .setStatusCode(500)
        .end(new JsonObject().put("message", "Something went wrong :(").toBuffer());
    };
  }

}
