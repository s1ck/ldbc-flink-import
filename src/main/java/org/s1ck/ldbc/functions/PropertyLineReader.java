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
import org.s1ck.ldbc.tuples.LDBCProperty;

import static org.s1ck.ldbc.LDBCConstants.FieldType;

/**
 * Creates a {@link LDBCProperty} from an input line.
 */
public class PropertyLineReader extends LineReader<LDBCProperty> {
  private final String vertexClass;
  private final Long vertexClassId;
  private final LDBCProperty reuseProperty;

  public PropertyLineReader(String propertyClassLabel,
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
   try {
     String[] fieldValues = getFieldValues(line);
     Long vertexId = getVertexId(fieldValues);
     Long uniqueVertexId = getUniqueID(vertexId, vertexClassId, getVertexClassCount());
     reuseProperty.setVertexId(uniqueVertexId);
     reuseProperty.setPropertyValue(getPropertyValue(fieldValues));
     collector.collect(reuseProperty);
     reset();
   } catch (NumberFormatException ignored) { }
  }

  private Long getVertexId(String[] fieldValues) {
    return Long.parseLong(fieldValues[0]);
  }
}
