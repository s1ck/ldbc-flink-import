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
import org.s1ck.ldbc.tuples.LDBCVertex;

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
