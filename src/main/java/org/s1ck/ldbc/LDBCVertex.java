package org.s1ck.ldbc;

import org.apache.flink.api.java.tuple.Tuple3;

import java.util.Map;

/**
 * Copyright 2015 martin.
 */
public class LDBCVertex extends Tuple3<Long, String, Map<String, Object>> {

  public Long getVertexId() {
    return f0;
  }

  public void setVertexId(Long vertexId) {
    f0 = vertexId;
  }

  public String getLabel() {
    return f1;
  }

  public void setLabel(String vertexLabel) {
    f1 = vertexLabel;
  }

  public Map<String, Object> getProperties() {
    return f2;
  }

  public void setProperties(Map<String, Object> properties) {
    f2 = properties;
  }
}
