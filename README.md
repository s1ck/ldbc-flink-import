[![Build Status](https://travis-ci.org/s1ck/ldbc-flink-import.svg?branch=master)](https://travis-ci.org/s1ck/ldbc-flink-import)

## ldbc-flink-import

Used to load the output of the [LDBC-SNB Data Generator](https://github.com/ldbc/ldbc_snb_datagen)
into [Apache Flink](https://github.com/apache/flink) DataSets for further processing.
The LDBC data generator is designed to produce directed labeled graphs that mimic the
characteristics of those graphs of real data. A detailed description of the schema 
produced by the data generator, as well as the format of the output files, can be 
found in the latest version of the official [LDBC-SNB specification document](https://github.com/ldbc/ldbc_snb_docs).

![LDBC Schema](https://github.com/ldbc/ldbc_snb_docs/blob/master/tex/figures/schema/schema.png "LDBC Schema")
https://github.com/ldbc/ldbc_snb_docs/blob/master/tex/figures/schema/schema.svg

The tool reads the LDBC output files from a given directory (either local or HDFS)
and creates two datasets containing all vertices and edges. Vertices and edges are
represented by tuples. A vertex stores an id which is unique among all vertices,
a vertex label and key-value properties represented by a HashMap. An edge stores
an id which is unique among all edges, an edge label, source and target vertex 
identifiers and key-value properties.

### Usage

Add dependency to your maven project:

```
<repositories>
  <repository>
    <id>dbleipzig</id>
    <name>Database Group Leipzig University</name>
    <url>https://wdiserv1.informatik.uni-leipzig.de:443/archiva/repository/dbleipzig/</url>
    <releases>
      <enabled>true</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
   </repository>
</repositories>

<dependency>
  <groupId>org.s1ck</groupId>
  <artifactId>ldbc-flink-import</artifactId>
  <version>0.1</version>
</dependency>
```

* Use in your project

```java
LDBCToFlink ldbcToFlink = new LDBCToFlink(
      "/path/to/ldbc/output", // or "hdfs://..."
      ExecutionEnvironment.getExecutionEnvironment());

DataSet<LDBCVertex> vertices = ldbcToFlink.getVertices();
DataSet<LDBCEdge> edges = ldbcToFlink.getEdges();
```

### License

Licensed under the GNU General Public License, v3: http://www.gnu.org/licenses/gpl-3.0.html
