package com.octavio.starter_broker.assets;

import io.vertx.ext.web.Router;


import java.util.HashMap;
import java.util.Map;


public class AssetsRestApi {

  private AssetsRestApi() {
    throw new IllegalStateException("AssetsRestApi class");
  }

  public static final Map<Integer, String> ASSETS = new HashMap<>();

  public static void attach(Router parent) {
    parent.get("/assets").handler(new GetAssetsHandler());
  }
}
