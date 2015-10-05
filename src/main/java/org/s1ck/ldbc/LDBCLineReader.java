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

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.shaded.com.google.common.collect.Maps;
import org.s1ck.ldbc.LDBCConstants.FieldType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import static org.s1ck.ldbc.LDBCConstants.TYPE_DISCRIMINATOR_FIELD;

public abstract class LDBCLineReader<OUT> implements
  FlatMapFunction<String, OUT> {

  private final static int LOG2_LONG_MAX_VALUE = log2(Long.MAX_VALUE);

  private final Pattern fieldDelimiterPattern;

  protected final String[] classFields;

  private final FieldType[] classFieldTypes;

  private final Long vertexClassCount;

  private final SimpleDateFormat dateTimeFormat;

  private final SimpleDateFormat dateFormat;

  private final Map<String, Object> reuseMap;

  private int typeFieldIndex = -1;

  private String classLabel;

  private boolean firstLine;

  public LDBCLineReader(String classLabel, String[] classFields,
    FieldType[] classFieldTypes, Long vertexClassCount) {
    this.classLabel = classLabel;
    this.classFields = classFields;
    this.classFieldTypes = classFieldTypes;
    this.vertexClassCount = vertexClassCount;

    fieldDelimiterPattern = Pattern.compile(LDBCConstants.FIELD_DELIMITER);
    reuseMap = Maps.newHashMapWithExpectedSize(classFields.length);
    firstLine = true;
    dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    dateTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

    for (int i = 0; i < classFieldTypes.length; i++) {
      if (classFields[i].equals(TYPE_DISCRIMINATOR_FIELD)) {
        typeFieldIndex = i;
        break;
      }
    }
  }

  protected String getClassLabel(String[] fieldValues) {
    return (typeFieldIndex > 0) ? fieldValues[typeFieldIndex] : classLabel;
  }

  protected Long getVertexClassCount() {
    return vertexClassCount;
  }

  protected boolean isHeaderLine(String line) {
    return isHeaderLine(line, classFields[0]);
  }

  protected boolean isHeaderLine(String line, String prefix) {
    if (firstLine) {
      firstLine = false;
      return line.toLowerCase().startsWith(prefix.toLowerCase());
    }
    return false;
  }

  protected String[] getFieldValues(String line) {
    return fieldDelimiterPattern.split(line);
  }

  protected Map<String, Object> getVertexProperties(String[] fieldValues) throws
    ParseException {
    return getProperties(fieldValues, 1);
  }

  protected Map<String, Object> getEdgeProperties(String[] fieldValues) throws
    ParseException {
    return getProperties(fieldValues, 2);
  }

  protected Object getPropertyValue(String[] fieldValues) throws
    ParseException {
    return getValue(classFieldTypes[1], fieldValues[1]);
  }

  private Map<String, Object> getProperties(String[] fieldValues,
    int offset) throws ParseException {
    for (int i = offset; i < fieldValues.length; i++) {
      // if data contains a type field, it is not stored as property
      // e.g., place can be city, country or continent
      if (i != typeFieldIndex) {
        Object fieldValue = getValue(classFieldTypes[i], fieldValues[i]);
        reuseMap.put(classFields[i], fieldValue);
      }
    }
    return reuseMap;
  }

  private Object getValue(FieldType fieldType, String fieldValue) throws
    ParseException {
    Object o;
    switch (fieldType) {
    case INT:
      o = Integer.parseInt(fieldValue);
      break;
    case LONG:
      o = Long.parseLong(fieldValue);
      break;
    case DATETIME:
      o = dateTimeFormat.parse(fieldValue);
      break;
    case DATE:
      o = dateFormat.parse(fieldValue);
      break;
    default:
      o = fieldValue;
    }

    return o;
  }

  protected void reset() {
    reuseMap.clear();
  }

  protected static int log2(long value) {
    if (value > Integer.MAX_VALUE) {
      return 64 - Integer.numberOfLeadingZeros((int) (value >> 32));
    } else {
      return 32 - Integer.numberOfLeadingZeros((int) value);
    }
  }

  protected static long getUniqueID(long id, long idClass, long classCount) {
    long shift = log2(classCount);
    if (log2(id) + shift < LOG2_LONG_MAX_VALUE) {
      return (id << shift) + idClass;
    } else {
      throw new IllegalArgumentException(
        String.format("id %d is too large to be unified", id));
    }
  }
}
