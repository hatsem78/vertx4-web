package com.octavio.starter_broker;

import com.octavio.starter_broker.assets.AssetsRestApi;
import com.octavio.starter_broker.quotes.QuotesResApi;
import com.octavio.starter_broker.watchlist.WatchListResAPI;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  public static final int PORT = 8888;

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    final Router restApi = Router.router(vertx);
    restApi
      .route()
      .handler(BodyHandler.create())
      .failureHandler(errorContext->{
      if(errorContext.response().ended()){
        return;
      }
      LOG.error("Router error", errorContext.failure());

      errorContext.response()
        .setStatusCode(500)
        .end(new JsonObject().put("message", "Somthing went error :(").toBuffer());

    });
    AssetsRestApi.attach(restApi);
    QuotesResApi.attach(restApi);
    WatchListResAPI.attach(restApi);

    vertx.createHttpServer().requestHandler(restApi)
      .exceptionHandler(error->LOG.error("Http Server error {}", error.getMessage()))
      .listen(PORT)
      .onComplete(http -> {
        if (http.succeeded()) {
          startPromise.complete();
          LOG.info("HTTP server started on port 8888");
        } else {
          startPromise.fail(http.cause());
        }
      });
  }
}
