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

import org.apache.flink.api.common.functions.CoGroupFunction;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.DataSetUtils;
import org.apache.flink.hadoop.shaded.com.google.common.collect.Iterables;
import org.apache.flink.hadoop.shaded.com.google.common.collect.Lists;
import org.apache.flink.hadoop.shaded.com.google.common.collect.Maps;
import org.apache.flink.util.Collector;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.s1ck.ldbc.functions.LDBCEdgeLineReader;
import org.s1ck.ldbc.functions.LDBCPropertyLineReader;
import org.s1ck.ldbc.functions.LDBCVertexLineReader;
import org.s1ck.ldbc.tuples.LDBCEdge;
import org.s1ck.ldbc.tuples.LDBCProperty;
import org.s1ck.ldbc.tuples.LDBCMultiValuedProperty;
import org.s1ck.ldbc.tuples.LDBCVertex;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.s1ck.ldbc.LDBCConstants.*;

/**
 * Main class to read LDBC output into Flink.
 */
public class LDBCToFlink {

  /** Logger */
  private static final Logger LOG = Logger.getLogger(LDBCToFlink.class);

  /** Flink execution environment */
  private final ExecutionEnvironment env;

  /** Hadoop Configuration */
  private final Configuration conf;

  /** Directory, where the LDBC output is stored. */
  private final String ldbcDirectory;

  /**
   * Defines how tokens are separated in a filename. For example in
   * "comment_0_0.csv" the tokens are separated by "_".
   */
  private final Pattern fileNameTokenDelimiter;

  /** List of vertex files */
  private final List<String> vertexFilePaths;

  /** List of edge files */
  private final List<String> edgeFilePaths;

  /** List of property files */
  private final List<String> propertyFilePaths;

  /**
   * Maps a vertex class (e.g. Person, Comment) to a unique identifier.
   */
  private final Map<String, Long> vertexClassToClassIDMap;

  /**
   * Used to create vertex class IDs.
   */
  private long nextVertexClassID = 0L;

  /**
   * Creates a new parser instance.
   *
   * @param ldbcDirectory path to LDBC output
   * @param env Flink execution environment
   */
  public LDBCToFlink(String ldbcDirectory, ExecutionEnvironment env) {
    this(ldbcDirectory, env, new Configuration());
  }

  /**
   * Creates a new parser instance.
   *
   * @param ldbcDirectory path to LDBC output
   * @param env Flink execution environment
   * @param conf Hadoop cluster configuration
   */
  public LDBCToFlink(String ldbcDirectory, ExecutionEnvironment env,
    Configuration conf) {
    this.ldbcDirectory = ldbcDirectory;
    this.vertexFilePaths = Lists.newArrayList();
    this.edgeFilePaths = Lists.newArrayList();
    this.propertyFilePaths = Lists.newArrayList();
    this.env = env;
    this.conf = conf;
    this.vertexClassToClassIDMap = Maps.newHashMap();
    fileNameTokenDelimiter = Pattern.compile(FILENAME_TOKEN_DELIMITER);
    init();
  }

  /**
   * Parses and transforms the LDBC vertex files to {@link LDBCVertex} tuples.
   *
   * @return DataSet containing all vertices in the LDBC graph
   */
  public DataSet<LDBCVertex> getVertices() {
    LOG.info("Reading vertices");
    final List<DataSet<LDBCVertex>> vertexDataSets =
      Lists.newArrayListWithCapacity(vertexFilePaths.size());
    for (String filePath : vertexFilePaths) {
      vertexDataSets.add(readVertexFile(filePath));
    }

    DataSet<LDBCVertex> vertices = unionDataSets(vertexDataSets);

    vertices = addMultiValuePropertiesToVertices(vertices);

    return vertices;
  }

  /**
   * Parses and transforms the LDBC edge files to {@link LDBCEdge} tuples.
   *
   * @return DataSet containing all edges in the LDBC graph.
   */
  public DataSet<LDBCEdge> getEdges() {
    LOG.info("Reading edges");
    List<DataSet<LDBCEdge>> edgeDataSets =
      Lists.newArrayListWithCapacity(edgeFilePaths.size());
    for (String filePath : edgeFilePaths) {
      edgeDataSets.add(readEdgeFile(filePath));
    }

    return DataSetUtils.zipWithUniqueId(unionDataSets(edgeDataSets))
      .map(new MapFunction<Tuple2<Long, LDBCEdge>, LDBCEdge>() {
        @Override
        public LDBCEdge map(Tuple2<Long, LDBCEdge> tuple) throws Exception {
          tuple.f1.setEdgeId(tuple.f0);
          return tuple.f1;
        }
      }).withForwardedFields("f0");
  }

  private DataSet<LDBCVertex> addMultiValuePropertiesToVertices(
    DataSet<LDBCVertex> vertices) {
    DataSet<LDBCMultiValuedProperty> groupedProperties = getProperties()
      // group properties by vertex id and property key
      .groupBy(0, 1)
        // and build tuples containing vertex id, property key and value list
      .reduceGroup(new PropertyValueGroupReducer());

    // co group vertices and property groups and update vertices
    return vertices.coGroup(groupedProperties).where(0).equalTo(0)
      .with(new VertexPropertyGroupCoGroupReducer());
  }

  private DataSet<LDBCProperty> getProperties() {
    LOG.info("Reading multi valued properties");
    List<DataSet<LDBCProperty>> propertyDataSets =
      Lists.newArrayListWithCapacity(propertyFilePaths.size());

    for (String filePath : propertyFilePaths) {
      propertyDataSets.add(readPropertyFile(filePath));
    }

    return unionDataSets(propertyDataSets);
  }

  private long getVertexClassCount() {
    return vertexFilePaths.size();
  }

  private <T> DataSet<T> unionDataSets(List<DataSet<T>> dataSets) {
    DataSet<T> finalDataSet = null;
    boolean first = true;
    for (DataSet<T> dataSet : dataSets) {
      if (first) {
        finalDataSet = dataSet;
        first = false;
      } else {
        finalDataSet = finalDataSet.union(dataSet);
      }
    }
    return finalDataSet;
  }

  private DataSet<LDBCVertex> readVertexFile(String filePath) {
    LOG.info("Reading vertices from " + filePath);

    String vertexClass = getVertexClass(getFileName(filePath)).toLowerCase();
    Long vertexClassID = getVertexClassId(vertexClass);
    Long classCount = (long) vertexFilePaths.size();

    LOG.info(String.format("vertex class: %s vertex class ID: %d", vertexClass,
      vertexClassID));

    String[] vertexClassFields = null;
    FieldType[] vertexClassFieldTypes = null;
    switch (vertexClass) {
    case VERTEX_CLASS_COMMENT:
      vertexClassFields = VERTEX_CLASS_COMMENT_FIELDS;
      vertexClassFieldTypes = VERTEX_CLASS_COMMENT_FIELD_TYPES;
      break;
    case VERTEX_CLASS_FORUM:
      vertexClassFields = VERTEX_CLASS_FORUM_FIELDS;
      vertexClassFieldTypes = VERTEX_CLASS_FORUM_FIELD_TYPES;
      break;
    case VERTEX_CLASS_ORGANISATION:
      vertexClassFields = VERTEX_CLASS_ORGANISATION_FIELDS;
      vertexClassFieldTypes = VERTEX_CLASS_ORGANISATION_FIELD_TYPES;
      break;
    case VERTEX_CLASS_PERSON:
      vertexClassFields = VERTEX_CLASS_PERSON_FIELDS;
      vertexClassFieldTypes = VERTEX_CLASS_PERSON_FIELD_TYPES;
      break;
    case VERTEX_CLASS_PLACE:
      vertexClassFields = VERTEX_CLASS_PLACE_FIELDS;
      vertexClassFieldTypes = VERTEX_CLASS_PLACE_FIELD_TYPES;
      break;
    case VERTEX_CLASS_POST:
      vertexClassFields = VERTEX_CLASS_POST_FIELDS;
      vertexClassFieldTypes = VERTEX_CLASS_POST_FIELD_TYPES;
      break;
    case VERTEX_CLASS_TAG:
      vertexClassFields = VERTEX_CLASS_TAG_FIELDS;
      vertexClassFieldTypes = VERTEX_CLASS_TAG_FIELD_TYPES;
      break;
    case VERTEX_CLASS_TAGCLASS:
      vertexClassFields = VERTEX_CLASS_TAGCLASS_FIELDS;
      vertexClassFieldTypes = VERTEX_CLASS_TAGCLASS_FIELD_TYPES;
      break;
    }
    return env.readTextFile(filePath, "UTF-8").flatMap(
      new LDBCVertexLineReader(vertexClassID, vertexClass, vertexClassFields,
        vertexClassFieldTypes, classCount));
  }

  private DataSet<LDBCEdge> readEdgeFile(String filePath) {
    LOG.info("Reading edges from " + filePath);

    String fileName = getFileName(filePath);
    String edgeClass = getEdgeClass(fileName);
    String sourceVertexClass = getSourceVertexClass(fileName);
    String targetVertexClass = getTargetVertexClass(fileName);
    Long sourceVertexClassId = getVertexClassId(sourceVertexClass);
    Long targetVertexClassId = getVertexClassId(targetVertexClass);
    Long vertexClassCount = getVertexClassCount();

    String[] edgeClassFields = null;
    FieldType[] edgeClassFieldTypes = null;
    switch (edgeClass) {
    case EDGE_CLASS_KNOWS:
      edgeClassFields = EDGE_CLASS_KNOWS_FIELDS;
      edgeClassFieldTypes = EDGE_CLASS_KNOWS_FIELD_TYPES;
      break;
    case EDGE_CLASS_HAS_TYPE:
      edgeClassFields = EDGE_CLASS_HAS_TYPE_FIELDS;
      edgeClassFieldTypes = EDGE_CLASS_HAS_TYPE_FIELD_TYPES;
      break;
    case EDGE_CLASS_IS_LOCATED_IN:
      edgeClassFields = EDGE_CLASS_IS_LOCATED_IN_FIELDS;
      edgeClassFieldTypes = EDGE_CLASS_IS_LOCATED_IN_FIELD_TYPES;
      break;
    case EDGE_CLASS_HAS_INTEREST:
      edgeClassFields = EDGE_CLASS_HAS_INTEREST_FIELDS;
      edgeClassFieldTypes = EDGE_CLASS_HAS_INTEREST_FIELD_TYPES;
      break;
    case EDGE_CLASS_REPLY_OF:
      edgeClassFields = EDGE_CLASS_REPLY_OF_FIELDS;
      edgeClassFieldTypes = EDGE_CLASS_REPLY_OF_FIELD_TYPES;
      break;
    case EDGE_CLASS_STUDY_AT:
      edgeClassFields = EDGE_CLASS_STUDY_AT_FIELDS;
      edgeClassFieldTypes = EDGE_CLASS_STUDY_AT_FIELD_TYPES;
      break;
    case EDGE_CLASS_HAS_MODERATOR:
      edgeClassFields = EDGE_CLASS_HAS_MODERATOR_FIELDS;
      edgeClassFieldTypes = EDGE_CLASS_HAS_MODERATOR_FIELD_TYPES;
      break;
    case EDGE_CLASS_HAS_MEMBER:
      edgeClassFields = EDGE_CLASS_HAS_MEMBER_FIELDS;
      edgeClassFieldTypes = EDGE_CLASS_HAS_MEMBER_FIELD_TYPES;
      break;
    case EDGE_CLASS_HAS_TAG:
      edgeClassFields = EDGE_CLASS_HAS_TAG_FIELDS;
      edgeClassFieldTypes = EDGE_CLASS_HAS_TAG_FIELD_TYPES;
      break;
    case EDGE_CLASS_HAS_CREATOR:
      edgeClassFields = EDGE_CLASS_HAS_CREATOR_FIELDS;
      edgeClassFieldTypes = EDGE_CLASS_HAS_CREATOR_FIELD_TYPES;
      break;
    case EDGE_CLASS_WORK_AT:
      edgeClassFields = EDGE_CLASS_WORK_AT_FIELDS;
      edgeClassFieldTypes = EDGE_CLASS_WORK_AT_FIELD_TYPES;
      break;
    case EDGE_CLASS_CONTAINER_OF:
      edgeClassFields = EDGE_CLASS_CONTAINER_OF_FIELDS;
      edgeClassFieldTypes = EDGE_CLASS_CONTAINER_OF_FIELD_TYPES;
      break;
    case EDGE_CLASS_IS_PART_OF:
      edgeClassFields = EDGE_CLASS_IS_PART_OF_FIELDS;
      edgeClassFieldTypes = EDGE_CLASS_IS_PART_OF_FIELD_TYPES;
      break;
    case EDGE_CLASS_IS_SUBCLASS_OF:
      edgeClassFields = EDGE_CLASS_IS_SUBCLASS_OF_FIELDS;
      edgeClassFieldTypes = EDGE_CLASS_IS_SUBCLASS_OF_FIELD_TYPES;
      break;
    case EDGE_CLASS_LIKES:
      edgeClassFields = EDGE_CLASS_LIKES_FIELDS;
      edgeClassFieldTypes = EDGE_CLASS_LIKES_FIELD_TYPES;
      break;
    }

    return env.readTextFile(filePath, "UTF-8").flatMap(
      new LDBCEdgeLineReader(edgeClass, edgeClassFields, edgeClassFieldTypes,
        sourceVertexClassId, sourceVertexClass, targetVertexClassId,
        targetVertexClass, vertexClassCount));
  }

  private DataSet<LDBCProperty> readPropertyFile(String filePath) {
    LOG.info("Reading properties from " + filePath);

    String fileName = getFileName(filePath);
    String propertyClass = getPropertyClass(fileName);
    String vertexClass = getVertexClass(fileName);
    Long vertexClassId = getVertexClassId(vertexClass);
    Long vertexClassCount = getVertexClassCount();

    String[] propertyClassFields = null;
    FieldType[] propertyClassFieldTypes = null;

    switch (propertyClass) {
    case PROPERTY_CLASS_EMAIL:
      propertyClassFields = PROPERTY_CLASS_EMAIL_FIELDS;
      propertyClassFieldTypes = PROPERTY_CLASS_EMAIL_FIELD_TYPES;
      break;
    case PROPERTY_CLASS_SPEAKS:
      propertyClassFields = PROPERTY_CLASS_SPEAKS_FIELDS;
      propertyClassFieldTypes = PROPERTY_CLASS_SPEAKS_FIELD_TYPES;
      break;
    }

    return env.readTextFile(filePath, "UTF-8").flatMap(
      new LDBCPropertyLineReader(propertyClass, propertyClassFields,
        propertyClassFieldTypes, vertexClass, vertexClassId, vertexClassCount));
  }

  private String getFileName(String filePath) {
    return filePath
      .substring(filePath.lastIndexOf(System.getProperty("file.separator")) + 1,
        filePath.length());
  }

  private String getVertexClass(String fileName) {
    return fileName.substring(0, fileName.indexOf(FILENAME_TOKEN_DELIMITER));
  }

  private String getEdgeClass(String fileName) {
    return fileNameTokenDelimiter.split(fileName)[1];
  }

  private String getPropertyClass(String fileName) {
    return fileNameTokenDelimiter.split(fileName)[1];
  }

  private String getSourceVertexClass(String fileName) {
    return fileNameTokenDelimiter.split(fileName)[0];
  }

  private String getTargetVertexClass(String fileName) {
    return fileNameTokenDelimiter.split(fileName)[2];
  }

  private boolean isVertexFile(String fileName) {
    return isValidFile(fileName) &&
      fileName.split(FILENAME_TOKEN_DELIMITER).length == 3;
  }

  private boolean isEdgeFile(String fileName) {
    return isValidFile(fileName) &&
      fileName.split(FILENAME_TOKEN_DELIMITER).length == 5 &&
      !fileName.contains(PROPERTY_CLASS_EMAIL) &&
      !fileName.contains(PROPERTY_CLASS_SPEAKS);
  }

  private boolean isPropertyFile(String fileName) {
    return isValidFile(fileName) && (fileName.contains(PROPERTY_CLASS_EMAIL) ||
      fileName.contains(PROPERTY_CLASS_SPEAKS));
  }

  private boolean isValidFile(String fileName) {
    return !fileName.startsWith(".");
  }

  private Long getVertexClassId(String vertexClass) {
    Long vertexClassID;
    if (vertexClassToClassIDMap.containsKey(vertexClass)) {
      vertexClassID = vertexClassToClassIDMap.get(vertexClass);
    } else {
      vertexClassID = nextVertexClassID++;
      vertexClassToClassIDMap.put(vertexClass, vertexClassID);
    }
    return vertexClassID;
  }

  private void init() {
    if (ldbcDirectory.startsWith("hdfs://")) {
      initFromHDFS();
    } else {
      initFromLocalFS();
    }
  }

  private void initFromHDFS() {
    try {
      FileSystem fs = FileSystem.get(conf);
      FileStatus[] fileStates = fs.listStatus(new Path(ldbcDirectory));
      for (FileStatus fileStatus : fileStates) {
        String filePath = fileStatus.getPath().getName();
        if (isVertexFile(filePath)) {
          vertexFilePaths.add(ldbcDirectory + filePath);
        } else if (isEdgeFile(filePath)) {
          edgeFilePaths.add(ldbcDirectory + filePath);
        } else if (isPropertyFile(filePath)) {
          propertyFilePaths.add(ldbcDirectory + filePath);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initFromLocalFS() {
    File folder = new File(ldbcDirectory);
    for (final File fileEntry : folder.listFiles()) {
      if (isVertexFile(fileEntry.getName())) {
        vertexFilePaths.add(fileEntry.getAbsolutePath());
      } else if (isEdgeFile(fileEntry.getName())) {
        edgeFilePaths.add(fileEntry.getAbsolutePath());
      } else if (isPropertyFile(fileEntry.getName())) {
        propertyFilePaths.add(fileEntry.getAbsolutePath());
      }
    }
  }

  private static class PropertyValueGroupReducer implements
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

  private static class VertexPropertyGroupCoGroupReducer implements
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
}
