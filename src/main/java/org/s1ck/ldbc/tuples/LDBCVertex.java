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

package org.s1ck.ldbc.tuples;

import org.apache.flink.api.java.tuple.Tuple3;

import java.util.Map;

/**
 * Represents an LDBC vertex.
 *
 * f0: vertex identifier
 * f1: vertex label
 * f2: vertex properties
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
