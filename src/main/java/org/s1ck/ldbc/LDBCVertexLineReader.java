package org.s1ck.ldbc;

import org.apache.flink.util.Collector;
import org.s1ck.ldbc.LDBCConstants.FieldType;

/**
 * Copyright 2015 martin.
 */
public class LDBCVertexLineReader extends LDBCLineReader<LDBCVertex> {

  private final Long vertexClassID;
  private final LDBCVertex reuseVertex;

  public LDBCVertexLineReader(Long vertexClassId, String vertexClass,
    String[] vertexClassFields, FieldType[] vertexClassFieldTypes,
    Long vertexClassCount) {
    super(vertexClass, vertexClassFields, vertexClassFieldTypes,
      vertexClassCount);
    this.vertexClassID = vertexClassId;
    reuseVertex = new LDBCVertex();
  }

  @Override
  public void flatMap(String line, Collector<LDBCVertex> collector) throws
    Exception {
    if (isHeaderLine(line)) {
      return;
    }
    String[] fieldValues = getFieldValues(line);
    Long vertexID = getVertexID(fieldValues);
    Long uniqueVertexID =
      getUniqueID(vertexID, vertexClassID, getVertexClassCount());
    reuseVertex.setVertexId(uniqueVertexID);
    reuseVertex.setLabel(getClassLabel(fieldValues));
    reuseVertex.setProperties(getVertexProperties(fieldValues));
    collector.collect(reuseVertex);
    reset();
  }

  private Long getVertexID(String[] fieldValues) {
    return Long.parseLong(fieldValues[0]);
  }
}
