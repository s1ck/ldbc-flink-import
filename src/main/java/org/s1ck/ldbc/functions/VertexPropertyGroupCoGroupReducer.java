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

import com.google.common.collect.Iterables;
import org.apache.flink.api.common.functions.CoGroupFunction;
import org.apache.flink.util.Collector;
import org.s1ck.ldbc.tuples.LDBCMultiValuedProperty;
import org.s1ck.ldbc.tuples.LDBCVertex;

public class VertexPropertyGroupCoGroupReducer implements
  CoGroupFunction<LDBCVertex, LDBCMultiValuedProperty, LDBCVertex> {

  @Override
  public void coGroup(Iterable<LDBCVertex> ldbcVertices,
    Iterable<LDBCMultiValuedProperty> ldbcPropertyGroups,
    Collector<LDBCVertex> collector) throws Exception {
    // there is only one vertex in the iterable
    LDBCVertex finalVertex = Iterables.get(ldbcVertices, 0);
    // add multi value property to the vertex (if any)
    for (LDBCMultiValuedProperty propertyGroup : ldbcPropertyGroups) {
      finalVertex.getProperties().put(
        propertyGroup.getPropertyKey(),
        propertyGroup.getPropertyValues()
      );
    }
    collector.collect(finalVertex);
  }
}
