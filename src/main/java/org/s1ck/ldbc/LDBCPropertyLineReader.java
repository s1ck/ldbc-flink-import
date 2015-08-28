package org.s1ck.ldbc;

import org.apache.flink.api.java.functions.FunctionAnnotation;
import org.apache.flink.util.Collector;

import static org.s1ck.ldbc.LDBCConstants.FieldType;

/**
 * Copyright 2015 martin.
 */
public class LDBCPropertyLineReader extends LDBCLineReader<LDBCProperty> {
  private final String vertexClass;
  private final Long vertexClassId;
  private final LDBCProperty reuseProperty;

  public LDBCPropertyLineReader(String propertyClassLabel,
    String[] propertyClassFields, FieldType[] propertyClassFieldTypes,
    String vertexClass, Long vertexClassId, Long vertexClassCount) {
    super(propertyClassLabel, propertyClassFields, propertyClassFieldTypes,
      vertexClassCount);
    this.vertexClass = vertexClass;
    this.vertexClassId = vertexClassId;
    reuseProperty = new LDBCProperty();
    reuseProperty.setPropertyKey(propertyClassLabel);
  }

  @Override
  public void flatMap(String line, Collector<LDBCProperty> collector) throws
    Exception {
    if (isHeaderLine(line, vertexClass)) {
      return;
    }
    String[] fieldValues = getFieldValues(line);
    Long vertexId = getVertexId(fieldValues);
    Long uniqueVertexId =
      getUniqueID(vertexId, vertexClassId, getVertexClassCount());
    reuseProperty.setVertexId(uniqueVertexId);
    reuseProperty.setPropertyValue(getPropertyValue(fieldValues));
    collector.collect(reuseProperty);
    reset();
  }

  private Long getVertexId(String[] fieldValues) {
    return Long.parseLong(fieldValues[0]);
  }
}
