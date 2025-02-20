package com.octavio.starter_broker.assets;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Asset {
  String name;
  Integer id;

  @Override
  public String toString() {
    return "Asset{" +
      "name='" + name + '\'' +
      ", id=" + id +
      '}';
  }
}
