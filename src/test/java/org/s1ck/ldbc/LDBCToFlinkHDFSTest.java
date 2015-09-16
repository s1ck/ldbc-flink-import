package org.s1ck.ldbc;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

/**
 * Created by s1ck on 16.09.15.
 */
public class LDBCToFlinkHDFSTest extends LDBCToFlinkTest {

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  private static final String CLUSTER_1 = "cluster1";
  private static final String HDFS_DATA_PATH = "/data/";

  private Configuration conf;
  private MiniDFSCluster cluster;
  private FileSystem fs;

  @Before
  public void setup() throws IOException {
    File clusterDir = tmpFolder.newFolder(CLUSTER_1);
    System.clearProperty(MiniDFSCluster.PROP_TEST_BUILD_DATA);
    conf = new HdfsConfiguration();
    conf.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, clusterDir.getAbsolutePath());
    cluster = new MiniDFSCluster.Builder(conf).build();

    fs = FileSystem.get(conf);

    String fromPath = LDBCToFlinkTest.class.getResource("/data").getPath();
    fs.copyFromLocalFile(new Path(fromPath), new Path(HDFS_DATA_PATH));
  }

  @After
  public void tearDown() throws Exception {
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
