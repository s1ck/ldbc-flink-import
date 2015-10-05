/*
 * This file is part of ldbc-flink-import.
 *
 * ldbc-flink-import is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ldbc-flink-import is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *
 * You should have received a copy of the GNU General Public License
 * along with ldbc-flink-import. If not, see <http://www.gnu.org/licenses/>.
 */

package org.s1ck.ldbc.functions;

import org.apache.flink.util.Collector;
import org.s1ck.ldbc.LDBCConstants.FieldType;
import org.s1ck.ldbc.tuples.LDBCEdge;

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
