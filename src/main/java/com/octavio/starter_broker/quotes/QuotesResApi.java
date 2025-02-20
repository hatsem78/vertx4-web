package com.octavio.starter_broker.quotes;

import io.vertx.ext.web.Router;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Value
public class QuotesResApi {

  private static final Logger LOG = LoggerFactory.getLogger(QuotesResApi.class);

  QuotesResApi() {
  }

  public static void attach(Router parent) {
    parent.get("/quotes/:asset/:id").handler(new GetQuotesHandler());
  }
}
