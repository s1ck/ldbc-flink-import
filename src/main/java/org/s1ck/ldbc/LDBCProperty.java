package org.s1ck.ldbc;

import org.apache.flink.api.java.tuple.Tuple3;

/**
 * Copyright 2015 martin.
 */
public class LDBCProperty extends Tuple3<Long, String, Object> {
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

  public Object getPropertyValue() {
    return f2;
  }

  public void setPropertyValue(Object propertyValue) {
    f2 = propertyValue;
  }
}
