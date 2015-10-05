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

package org.s1ck.ldbc;

import org.apache.flink.api.java.tuple.Tuple5;

import java.util.Map;

public class LDBCEdge extends
  Tuple5<Long, String, Long, Long, Map<String, Object>> {

  public Long getEdgeId() {
    return f0;
  }

  public void setEdgeId(Long edgeId) {
    f0 = edgeId;
  }

  public String getLabel() {
    return f1;
  }

  public void setLabel(String label) {
    f1 = label;
  }

  public Long getSourceVertexId() {
    return f2;
  }

  public void setSourceVertexId(Long sourceVertexId) {
    f2 = sourceVertexId;
  }

  public Long getTargetVertexId() {
    return f3;
  }

  public void setTargetVertexId(Long targetVertexId) {
    f3 = targetVertexId;
  }

  public Map<String, Object> getProperties() {
    return f4;
  }

  public void setProperties(Map<String, Object> properties) {
    f4 = properties;
  }
}
