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
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);

  @Override
  public void start(final Promise<Void> startPromise) {
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
    final Pool db = createDbPoll(configuration);


    final Router restApi = Router.router(vertx);
    restApi.route()
      .handler(BodyHandler.create())
      .failureHandler(handleFailure());
    AssetsRestApi.attach(restApi, db);
    QuotesResApi.attach(restApi, db);
    WatchListResAPI.attach(restApi, db);

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

  private JDBCPool createDbPoll(BrokerConfig configuration) {
    switch (configuration.getDbType()) {
      case "MYSQL" -> {
        return getJdbcPoolPg(configuration);
      }
      case "POSTGRES" -> {
        return createMySQLPool(configuration);
      }
    }
    return null;
  }

  private JDBCPool getJdbcPoolPg(BrokerConfig configuration) {
    final String jdbcUrl = String.format("jdbc:%s://%s:%d/%s",
      "postgresql",
      configuration.getDbConfig().getHost(),
      configuration.getDbConfig().getPort(),
      configuration.getDbConfig().getDatabase()
    );
    final var connectionOption = new JDBCConnectOptions()
      .setJdbcUrl(jdbcUrl)
      .setUser(configuration.getDbConfig().getUser())
      .setPassword(configuration.getDbConfig().getPassword()
      );


    var poolOptions = new PoolOptions()
      .setMaxSize(4)
      .setName("pg");

    return JDBCPool.pool(vertx, connectionOption, poolOptions);
  }

  public JDBCPool createMySQLPool(final BrokerConfig configuration) {

    final String jdbcUrl = String.format("jdbc:%s://%s:%d/%s",
      "mysql",
      configuration.getDbConfig().getHost(),
      configuration.getDbConfig().getPort(),
      configuration.getDbConfig().getDatabase()
    );

    final var connectionOption = new JDBCConnectOptions()
      .setJdbcUrl(jdbcUrl)
      .setUser(configuration.getDbConfig().getUser())
      .setPassword(configuration.getDbConfig().getPassword()
      );

    var poolOptions = new PoolOptions()
      .setMaxSize(4)
      .setName("mysql");

    return JDBCPool.pool(vertx, connectionOption, poolOptions);
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
