package org.s1ck.ldbc;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.junit.Test;

public class LDBCToFlinkLocalFSTest extends LDBCToFlinkTest {

  @Test
  public void createFromLocalFSTest() throws Exception {
    String path = LDBCToFlinkTest.class.getResource("/data").getPath();
    ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
    LDBCToFlink ldbcToFlink = new LDBCToFlink(path, env);
    performTest(env, ldbcToFlink);
  }
}
