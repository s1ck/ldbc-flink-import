package org.s1ck.ldbc;

import org.apache.flink.api.java.tuple.Tuple3;

import java.util.List;

/**
 * Copyright 2015 martin.
 */
public class LDBCPropertyGroup extends Tuple3<Long, String, List<Object>> {
  public Long getVertexId() {
    return f0;
  }

  public void setVertexId(Long vertexId) {
    f0 = vertexId;
  }

  public String getPropertyKey() {
    return f1;
  }

  public void setPropertyKey(String propertyKey) {
    f1 = propertyKey;
  }

  public List<Object> getPropertyValues() {
    return f2;
  }

  public void setPropertValues(List<Object> propertyValues) {
    f2 = propertyValues;
  }
}