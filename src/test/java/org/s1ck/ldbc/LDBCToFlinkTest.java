package org.s1ck.ldbc;

import com.google.common.collect.Lists;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.LocalCollectionOutputFormat;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2015 martin.
 */
public class LDBCToFlinkTest {

  @Test
  public void testGetGraph() throws Exception {
    String path = LDBCToFlinkTest.class.getResource("/data").getPath();

    ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
    LDBCToFlink ldbcToFlink = new LDBCToFlink(path, env);

    System.out.println("Vertex file paths");
    for (String file : ldbcToFlink.getVertexFilePaths()) {
      System.out.println(file);
    }
    System.out.println("Edge file paths");
    for (String file : ldbcToFlink.getEdgeFilePaths()) {
      System.out.println(file);
    }
    System.out.println("Property file paths");
    for (String file : ldbcToFlink.getPropertyFilePaths()) {
      System.out.println(file);
    }

    List<LDBCVertex> vertexList = Lists.newArrayList();
    List<LDBCEdge> edgeList = Lists.newArrayList();

    ldbcToFlink.getVertices()
      .output(new LocalCollectionOutputFormat<>(vertexList));
    ldbcToFlink.getEdges().output(new LocalCollectionOutputFormat<>(edgeList));

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

  @Test
  public void createExtract() throws IOException {
    List<File> ldbcFiles =
      getFiles("/home/martin/Devel/Java/ldbc_snb_datagen/social_network");

    String outputPath =
      "/home/martin/Devel/Java/ldbc-flink-import/src/test/resources/data";

    for (File ldbcFile : ldbcFiles) {
      copyNLinesTo(ldbcFile, outputPath, 11);
    }

  }

  private void copyNLinesTo(File ldbcFile, String outputPath, int lines) throws
    IOException {
    Path inputFile = Paths.get(ldbcFile.toURI());
    Path outputFile = Paths.get(String
      .format("%s%s%s", outputPath, System.getProperty("file.separator"),
        ldbcFile.getName()));
    System.out.println(outputFile);

    BufferedReader br =
      Files.newBufferedReader(inputFile, Charset.forName("UTF-8"));
    BufferedWriter bw =
      Files.newBufferedWriter(outputFile, Charset.forName("UTF-8"));

    for (int i = 0; i < lines; i++) {
      bw.write(br.readLine());
      bw.newLine();
    }

    br.close();
    bw.close();
  }

  private List<File> getFiles(String path) {
    File folder = new File(path);
    List<File> ldbcFiles = new ArrayList<>();
    for (final File fileEntry : folder.listFiles()) {
      if (!fileEntry.getName().startsWith(".") &&
        !fileEntry.getName().equals("updateStream.properties")) {
        ldbcFiles.add(fileEntry);
      }
    }
    return ldbcFiles;
  }
}
