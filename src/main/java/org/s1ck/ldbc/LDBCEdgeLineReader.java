package org.s1ck.ldbc;

import org.apache.flink.util.Collector;
import org.s1ck.ldbc.LDBCConstants.FieldType;

/**
 * Copyright 2015 martin.
 */
public class LDBCEdgeLineReader extends LDBCLineReader<LDBCEdge> {

  private final Long sourceVertexClassId;
  private final String sourceVertexClass;
  private final Long targetVertexClassId;
  private final String targetVertexClass;
  private final LDBCEdge reuseEdge;

  public LDBCEdgeLineReader(String edgeClassLabel, String[] edgeClassFields,
    FieldType[] edgeClassFieldTypes, Long sourceVertexClassId,
    String sourceVertexClass, Long targetVertexClassId,
    String targetVertexClass, Long vertexClassCount) {
    super(edgeClassLabel, edgeClassFields, edgeClassFieldTypes,
      vertexClassCount);
    this.sourceVertexClassId = sourceVertexClassId;
    this.sourceVertexClass = sourceVertexClass;
    this.targetVertexClassId = targetVertexClassId;
    this.targetVertexClass = targetVertexClass;
    reuseEdge = new LDBCEdge();
  }

  @Override
  public void flatMap(String line, Collector<LDBCEdge> collector) throws
    Exception {
    if (isHeaderLine(line, sourceVertexClass)) {
      return;
    }

    String[] fieldValues = getFieldValues(line);
    Long sourceVertexId = getSourceVertexId(fieldValues);
    Long targetVertexId = getTargetVertexId(fieldValues);
    Long uniqueSourceVertexId =
      getUniqueID(sourceVertexId, sourceVertexClassId, getVertexClassCount());
    Long uniqueTargetVertexId =
      getUniqueID(targetVertexId, targetVertexClassId, getVertexClassCount());
    reuseEdge.setEdgeId(0L);
    reuseEdge.setSourceVertexId(uniqueSourceVertexId);
    reuseEdge.setTargetVertexId(uniqueTargetVertexId);
    reuseEdge.setLabel(getClassLabel(fieldValues));
    reuseEdge.setProperties(getEdgeProperties(fieldValues));
    collector.collect(reuseEdge);
    reset();
  }

  private Long getSourceVertexId(String[] fieldValues) {
    return Long.parseLong(fieldValues[0]);
  }

  private Long getTargetVertexId(String[] fieldValues) {
    return Long.parseLong(fieldValues[1]);
  }
}
