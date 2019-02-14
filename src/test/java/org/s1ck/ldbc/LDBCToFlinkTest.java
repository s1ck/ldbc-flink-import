package org.s1ck.ldbc;

import com.google.common.collect.Lists;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.LocalCollectionOutputFormat;
import org.apache.flink.test.util.MultipleProgramsTestBase;
import org.junit.Assert;
import org.s1ck.ldbc.tuples.LDBCEdge;
import org.s1ck.ldbc.tuples.LDBCVertex;

import java.util.List;

public abstract class LDBCToFlinkTest extends MultipleProgramsTestBase {

  public LDBCToFlinkTest(TestExecutionMode mode) {
    super(mode);
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
    Assert.assertEquals(230, edgeList.size());
  }
}
