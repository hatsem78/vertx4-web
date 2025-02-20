package com.octavio.starter_broker.db.entity;

import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class QuoteEntity {

  String asset;
  BigDecimal bid;
  BigDecimal ask;
  BigDecimal lastPrice;
  BigDecimal volume;

  public JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }

}
