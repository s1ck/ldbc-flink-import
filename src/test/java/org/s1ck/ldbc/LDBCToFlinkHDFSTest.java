package org.s1ck.ldbc;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;

@RunWith(Parameterized.class)
public class LDBCToFlinkHDFSTest extends LDBCToFlinkTest {

  @ClassRule
  public static TemporaryFolder tmpFolder = new TemporaryFolder();

  private static final String CLUSTER_1 = "cluster1";
  private static final String HDFS_DATA_PATH = "/data/";

  private static Configuration conf;
  private static MiniDFSCluster cluster;
  private static FileSystem fs;

  public LDBCToFlinkHDFSTest(TestExecutionMode mode) {
    super(mode);
  }

  @BeforeClass
  public static void setup() throws Exception {
    File clusterDir = tmpFolder.newFolder(CLUSTER_1);
    System.clearProperty(MiniDFSCluster.PROP_TEST_BUILD_DATA);
    conf = new HdfsConfiguration();
    conf.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, clusterDir.getAbsolutePath());
    cluster = new MiniDFSCluster.Builder(conf).build();

    fs = FileSystem.get(conf);

    String fromPath = LDBCToFlinkTest.class.getResource("/data").getPath();
    fs.copyFromLocalFile(new Path(fromPath), new Path(HDFS_DATA_PATH));
  }

  @AfterClass
  public static void tearDown() throws Exception {
    cluster.shutdown();
  }

  @Test
  public void createFromHDFSTest() throws Exception {
    ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
    String ldbcDirectory = fs.getUri() + HDFS_DATA_PATH;
    LDBCToFlink ldbcToFlink = new LDBCToFlink(ldbcDirectory, env, conf);
    performTest(env, ldbcToFlink);
  }
}
