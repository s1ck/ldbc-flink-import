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

import com.google.common.collect.Lists;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.util.Collector;
import org.s1ck.ldbc.tuples.LDBCMultiValuedProperty;
import org.s1ck.ldbc.tuples.LDBCProperty;

import java.util.List;

public class PropertyValueGroupReducer  implements
  GroupReduceFunction<LDBCProperty, LDBCMultiValuedProperty> {
  private final LDBCMultiValuedProperty reusePropertyGroup;

  public PropertyValueGroupReducer() {
    reusePropertyGroup = new LDBCMultiValuedProperty();
  }

  @Override
  public void reduce(Iterable<LDBCProperty> iterable,
    Collector<LDBCMultiValuedProperty> collector) throws Exception {
    Long vertexId = null;
    String propertyKey = null;
    boolean first = true;
    List<Object> propertyList = Lists.newArrayList();
    for (LDBCProperty ldbcProperty : iterable) {
      if (first) {
        vertexId = ldbcProperty.getVertexId();
        propertyKey = ldbcProperty.getPropertyKey();
        first = false;
      }
      propertyList.add(ldbcProperty.getPropertyValue());
    }
    reusePropertyGroup.setVertexId(vertexId);
    reusePropertyGroup.setPropertyKey(propertyKey);
    reusePropertyGroup.setPropertValues(propertyList);
    collector.collect(reusePropertyGroup);
  }
}
