package com.octavio.starter_broker.watchlist;

import com.octavio.starter_broker.assets.Asset;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchList {
  List<Asset> assetList;

  JsonObject toJsonObject(){
    return JsonObject.mapFrom(this);
  }
}
