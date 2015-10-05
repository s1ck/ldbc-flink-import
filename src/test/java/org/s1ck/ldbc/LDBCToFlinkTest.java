package org.s1ck.ldbc;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.LocalCollectionOutputFormat;
import org.apache.flink.hadoop.shaded.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.s1ck.ldbc.tuples.LDBCEdge;
import org.s1ck.ldbc.tuples.LDBCVertex;

import java.util.List;

public class LDBCToFlinkTest {

  @Test
  public void readGraphFromLocalFS() throws Exception {
    String path = LDBCToFlinkTest.class.getResource("/data").getPath();
    ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
    LDBCToFlink ldbcToFlink = new LDBCToFlink(path, env);
    performTest(env, ldbcToFlink);
  }

  protected void performTest(ExecutionEnvironment env, LDBCToFlink ldbcToFlink)
    throws Exception {

    List<LDBCVertex> vertexList = Lists.newArrayList();
    List<LDBCEdge> edgeList = Lists.newArrayList();

    ldbcToFlink.getVertices().output(
      new LocalCollectionOutputFormat<>(vertexList));
    ldbcToFlink.getEdges().output(
      new LocalCollectionOutputFormat<>(edgeList));

    env.execute();

    Assert.assertEquals(80, vertexList.size());
    System.out.println("vertexList.size(): " + vertexList.size());
    for (LDBCVertex vertex : vertexList) {
      System.out.println(vertex);
    }

    Assert.assertEquals(230, edgeList.size());

    System.out.println("edgeList.size(): " + edgeList.size());
    for (LDBCEdge edge : edgeList) {
      System.out.println(edge);
    }
  }
}
