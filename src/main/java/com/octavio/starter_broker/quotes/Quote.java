package com.octavio.starter_broker.quotes;

import com.octavio.starter_broker.assets.Asset;
import io.vertx.core.json.JsonObject;
import lombok.*;
import java.math.BigDecimal;

@Builder
public class Quote {

  private Asset asset;
  private BigDecimal bid;
  private BigDecimal ask;
  private BigDecimal lastPrice;
  private BigDecimal volume;

  public Quote() {

  }

  public Quote(Asset asset, BigDecimal bid, BigDecimal ask, BigDecimal lastPrice, BigDecimal volume) {
    this.asset = asset;
    this.bid = bid;
    this.ask = ask;
    this.lastPrice = lastPrice;
    this.volume = volume;
  }

  public Asset getAsset() {
    return asset;
  }

  public void setAsset(Asset asset) {
    this.asset = asset;
  }

  public BigDecimal getBid() {
    return bid;
  }

  public void setBid(BigDecimal bid) {
    this.bid = bid;
  }

  public BigDecimal getAsk() {
    return ask;
  }

  public void setAsk(BigDecimal ask) {
    this.ask = ask;
  }

  public BigDecimal getLastPrice() {
    return lastPrice;
  }

  public void setLastPrice(BigDecimal lastPrice) {
    this.lastPrice = lastPrice;
  }

  public BigDecimal getVolume() {
    return volume;
  }

  public void setVolume(BigDecimal volume) {
    this.volume = volume;
  }

  public JsonObject toJsonObject(){
    return JsonObject.mapFrom(this);
  }

  @Override
  public String toString() {
    return "Quote{" +
      "asset=" + asset +
      ", bid=" + bid +
      ", ask=" + ask +
      ", lastPrice=" + lastPrice +
      ", volume=" + volume +
      '}';
  }
}
