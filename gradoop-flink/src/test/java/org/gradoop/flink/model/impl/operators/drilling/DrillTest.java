/**
 * Copyright © 2014 - 2017 Leipzig University (Database Research Group)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradoop.flink.model.impl.operators.drilling;

import org.gradoop.flink.model.GradoopFlinkTestBase;
import org.gradoop.flink.model.impl.LogicalGraph;
import org.gradoop.flink.model.impl.operators.drilling.drillfunctions.DrillUpDivideBy;
import org.gradoop.flink.util.FlinkAsciiGraphLoader;
import org.junit.Test;


public class DrillTest extends GradoopFlinkTestBase {

  //----------------------------------------------------------------------------
  // Tests for drill up
  //----------------------------------------------------------------------------


  @Test(expected = NullPointerException.class)
  public void testVertexRollUpNoProperty() {
    FlinkAsciiGraphLoader loader = getLoaderFromString(getDrillInput());

    LogicalGraph input = loader.getLogicalGraphByVariable("input");

    LogicalGraph output = input.callForGraph(new Drill.DrillBuilder()
      .drillVertex(true)
      .buildDrillUp());
  }

  @Test(expected = NullPointerException.class)
  public void testVertexRollUpFunctionOnly() {
    FlinkAsciiGraphLoader loader = getLoaderFromString(getDrillInput());

    LogicalGraph input = loader.getLogicalGraphByVariable("input");

    LogicalGraph output = input
      .callForGraph(new Drill.DrillBuilder()
        .setFunction(new DrillUpDivideBy(1000L))
        .drillVertex(true)
        .buildDrillUp());
  }

  @Test
  public void testVertexRollUpPropertyKeyFunction() throws Exception {
    FlinkAsciiGraphLoader loader = getLoaderFromString(getDrillInput());

    LogicalGraph input = loader.getLogicalGraphByVariable("input");

    loader.appendToDatabaseFromString("expected[" +
      "(v00:Forum {topic : \"rdf\",memberCount : 1563145L,memberCount__1 : 1563145521L})" +
      "(v01:Forum {topic : \"graph\",memberCount: 451341L,memberCount__1: 451341564L})" +
      "(v02:User {gender : \"male\",birthMillis : 500000000000L})" +
      "(v03:User {gender : \"male\",birthMillis : 530000000000L})" +
      "(v04:User {gender : \"male\",birthMillis : 560000000000L})" +
      "(v05:User {gender : \"female\",birthMillis : 590000000000L})" +
      "(v02)-[:member {until : 1550000000000L}]->(v00)" +
      "(v03)-[:member {until : 1550000000000L}]->(v00)" +
      "(v03)-[:member {until : 1550000000000L}]->(v01)" +
      "(v04)-[:member {until : 1550000000000L}]->(v01)" +
      "(v05)-[:member {until : 1550000000000L}]->(v01)" +
      "(v02)-[:knows {since : 1350000000000L}]->(v03)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v02)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v04)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v05)" +
      "(v05)-[:knows {since : 1350000000000L}]->(v04)" +
      "]");

    LogicalGraph output = input
      .drillUpVertex("memberCount", new DrillUpDivideBy(1000L));

    collectAndAssertTrue(
      output.equalsByElementData(loader.getLogicalGraphByVariable("expected")));
  }

  @Test
  public void testVertexRollUpPropertyKeyFunctionNewPropertyKey() throws Exception {
    FlinkAsciiGraphLoader loader = getLoaderFromString(getDrillInput());

    LogicalGraph input = loader.getLogicalGraphByVariable("input");

    loader.appendToDatabaseFromString("expected[" +
      "(v00:Forum {topic : \"rdf\",memberCount : 1563145521L,memberCount_in_K: 1563145L})" +
      "(v01:Forum {topic : \"graph\",memberCount: 451341564L,memberCount_in_K: 451341L})" +
      "(v02:User {gender : \"male\",birthMillis : 500000000000L})" +
      "(v03:User {gender : \"male\",birthMillis : 530000000000L})" +
      "(v04:User {gender : \"male\",birthMillis : 560000000000L})" +
      "(v05:User {gender : \"female\",birthMillis : 590000000000L})" +
      "(v02)-[:member {until : 1550000000000L}]->(v00)" +
      "(v03)-[:member {until : 1550000000000L}]->(v00)" +
      "(v03)-[:member {until : 1550000000000L}]->(v01)" +
      "(v04)-[:member {until : 1550000000000L}]->(v01)" +
      "(v05)-[:member {until : 1550000000000L}]->(v01)" +
      "(v02)-[:knows {since : 1350000000000L}]->(v03)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v02)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v04)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v05)" +
      "(v05)-[:knows {since : 1350000000000L}]->(v04)" +
      "]");

    LogicalGraph output = input
      .callForGraph(new Drill.DrillBuilder()
        .setPropertyKey("memberCount")
        .setNewPropertyKey("memberCount_in_K")
        .setFunction(new DrillUpDivideBy(1000L))
        .drillVertex(true)
        .buildDrillUp());


    collectAndAssertTrue(
      output.equalsByElementData(loader.getLogicalGraphByVariable("expected")));
  }

  @Test
  public void testVertexRollUpChainedRollUp() throws Exception {
    FlinkAsciiGraphLoader loader = getLoaderFromString(getDrillInput());

    LogicalGraph input = loader.getLogicalGraphByVariable("input");

    loader.appendToDatabaseFromString("expected[" +
      "(v00:Forum {topic : \"rdf\",memberCount : 1563L,memberCount__1: 1563145521L," +
        "memberCount__2: 1563145L})" +
      "(v01:Forum {topic : \"graph\",memberCount: 451L,memberCount__1: 451341564L," +
        "memberCount__2: 451341L})" +
      "(v02:User {gender : \"male\",birthMillis : 500000000000L})" +
      "(v03:User {gender : \"male\",birthMillis : 530000000000L})" +
      "(v04:User {gender : \"male\",birthMillis : 560000000000L})" +
      "(v05:User {gender : \"female\",birthMillis : 590000000000L})" +
      "(v02)-[:member {until : 1550000000000L}]->(v00)" +
      "(v03)-[:member {until : 1550000000000L}]->(v00)" +
      "(v03)-[:member {until : 1550000000000L}]->(v01)" +
      "(v04)-[:member {until : 1550000000000L}]->(v01)" +
      "(v05)-[:member {until : 1550000000000L}]->(v01)" +
      "(v02)-[:knows {since : 1350000000000L}]->(v03)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v02)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v04)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v05)" +
      "(v05)-[:knows {since : 1350000000000L}]->(v04)" +
      "]");

    LogicalGraph output = input
      .drillUpVertex("memberCount", new DrillUpDivideBy(1000L))
      .drillUpVertex("memberCount", new DrillUpDivideBy(1000L));

    collectAndAssertTrue(
      output.equalsByElementData(loader.getLogicalGraphByVariable("expected")));
  }

  @Test
  public void testVertexRollUpNewPropertyKeyChainedRollUp() throws Exception {
    FlinkAsciiGraphLoader loader = getLoaderFromString(getDrillInput());

    LogicalGraph input = loader.getLogicalGraphByVariable("input");

    loader.appendToDatabaseFromString("expected[" +
      "(v00:Forum {topic : \"rdf\",memberCount : 1563145521L,memberCount_in_K: 1563145L," +
        "memberCount_in_M: 1563L})" +
      "(v01:Forum {topic : \"graph\",memberCount: 451341564L,memberCount_in_K: 451341L," +
        "memberCount_in_M: 451L})" +
      "(v02:User {gender : \"male\",birthMillis : 500000000000L})" +
      "(v03:User {gender : \"male\",birthMillis : 530000000000L})" +
      "(v04:User {gender : \"male\",birthMillis : 560000000000L})" +
      "(v05:User {gender : \"female\",birthMillis : 590000000000L})" +
      "(v02)-[:member {until : 1550000000000L}]->(v00)" +
      "(v03)-[:member {until : 1550000000000L}]->(v00)" +
      "(v03)-[:member {until : 1550000000000L}]->(v01)" +
      "(v04)-[:member {until : 1550000000000L}]->(v01)" +
      "(v05)-[:member {until : 1550000000000L}]->(v01)" +
      "(v02)-[:knows {since : 1350000000000L}]->(v03)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v02)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v04)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v05)" +
      "(v05)-[:knows {since : 1350000000000L}]->(v04)" +
      "]");

    LogicalGraph output = input
      .callForGraph(new Drill.DrillBuilder()
        .setPropertyKey("memberCount")
        .setNewPropertyKey("memberCount_in_K")
        .setFunction(new DrillUpDivideBy(1000L))
        .drillVertex(true)
        .buildDrillUp())
      .callForGraph(new Drill.DrillBuilder()
        .setPropertyKey("memberCount_in_K")
        .setNewPropertyKey("memberCount_in_M")
        .setFunction(new DrillUpDivideBy(1000L))
        .drillVertex(true)
        .buildDrillUp());

    collectAndAssertTrue(
      output.equalsByElementData(loader.getLogicalGraphByVariable("expected")));
  }

  @Test
  public void testVertexRollUpMixedChainedRollUp() throws Exception {
    FlinkAsciiGraphLoader loader = getLoaderFromString(getDrillInput());

    LogicalGraph input = loader.getLogicalGraphByVariable("input");

    loader.appendToDatabaseFromString("expected[" +
      "(v00:Forum {topic : \"rdf\",memberCount : 1563145521L})" +
      "(v01:Forum {topic : \"graph\",memberCount: 451341564L})" +
      "(v02:User {gender : \"male\",birthMillis : 500000000000L,birth : 138888L," +
        "birth__1 : 500000000L,birth__2 : 8333333L,birthDays : 5787L})" +
      "(v03:User {gender : \"male\",birthMillis : 530000000000L,birth : 147222L," +
        "birth__1 : 530000000L,birth__2 : 8833333L,birthDays : 6134L})" +
      "(v04:User {gender : \"male\",birthMillis : 560000000000L,birth : 155555L," +
        "birth__1 : 560000000L,birth__2 : 9333333L,birthDays : 6481L})" +
      "(v05:User {gender : \"female\",birthMillis : 590000000000L,birth : 163888L," +
        "birth__1 : 590000000L,birth__2 : 9833333L,birthDays : 6828L})" +
      "(v02)-[:member {until : 1550000000000L}]->(v00)" +
      "(v03)-[:member {until : 1550000000000L}]->(v00)" +
      "(v03)-[:member {until : 1550000000000L}]->(v01)" +
      "(v04)-[:member {until : 1550000000000L}]->(v01)" +
      "(v05)-[:member {until : 1550000000000L}]->(v01)" +
      "(v02)-[:knows {since : 1350000000000L}]->(v03)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v02)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v04)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v05)" +
      "(v05)-[:knows {since : 1350000000000L}]->(v04)" +
      "]");

    LogicalGraph output = input
      .callForGraph(new Drill.DrillBuilder()
        .setPropertyKey("birthMillis")
        .setNewPropertyKey("birth")
        .setFunction(new DrillUpDivideBy(1000L))
        .drillVertex(true)
        .buildDrillUp())
      .callForGraph(new Drill.DrillBuilder()
        .setPropertyKey("birth")
        .setFunction(new DrillUpDivideBy(60L))
        .drillVertex(true)
        .buildDrillUp())
      .callForGraph(new Drill.DrillBuilder()
        .setPropertyKey("birth")
        .setFunction(new DrillUpDivideBy(60L))
        .drillVertex(true)
        .buildDrillUp())
      .callForGraph(new Drill.DrillBuilder()
        .setPropertyKey("birth")
        .setNewPropertyKey("birthDays")
        .setFunction(new DrillUpDivideBy(24L))
        .drillVertex(true)
        .buildDrillUp());

    collectAndAssertTrue(
      output.equalsByElementData(loader.getLogicalGraphByVariable("expected")));
  }



  @Test
  public void testEdgeRollUpPropertyKeyFunction() throws Exception {
    FlinkAsciiGraphLoader loader = getLoaderFromString(getDrillInput());

    LogicalGraph input = loader.getLogicalGraphByVariable("input");

    loader.appendToDatabaseFromString("expected[" +
      "(v00:Forum {topic : \"rdf\",memberCount : 1563145521L})" +
      "(v01:Forum {topic : \"graph\",memberCount: 451341564L})" +
      "(v02:User {gender : \"male\",birthMillis : 500000000000L})" +
      "(v03:User {gender : \"male\",birthMillis : 530000000000L})" +
      "(v04:User {gender : \"male\",birthMillis : 560000000000L})" +
      "(v05:User {gender : \"female\",birthMillis : 590000000000L})" +
      "(v02)-[:member {until : 1550000000L,until__1 : 1550000000000L}]->(v00)" +
      "(v03)-[:member {until : 1550000000L,until__1 : 1550000000000L}]->(v00)" +
      "(v03)-[:member {until : 1550000000L,until__1 : 1550000000000L}]->(v01)" +
      "(v04)-[:member {until : 1550000000L,until__1 : 1550000000000L}]->(v01)" +
      "(v05)-[:member {until : 1550000000L,until__1 : 1550000000000L}]->(v01)" +
      "(v02)-[:knows {since : 1350000000000L}]->(v03)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v02)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v04)" +
      "(v03)-[:knows {since : 1350000000000L}]->(v05)" +
      "(v05)-[:knows {since : 1350000000000L}]->(v04)" +
      "]");

    LogicalGraph output = input
      .drillUpEdge("until", new DrillUpDivideBy(1000L));

    collectAndAssertTrue(
      output.equalsByElementData(loader.getLogicalGraphByVariable("expected")));
  }

  private String getDrillInput() {
    return "input[" +
      "(v0:Forum {topic : \"rdf\",memberCount : 1563145521L})" +
      "(v1:Forum {topic : \"graph\",memberCount: 451341564L})" +
      "(v2:User {gender : \"male\",birthMillis : 500000000000L})" +
      "(v3:User {gender : \"male\",birthMillis : 530000000000L})" +
      "(v4:User {gender : \"male\",birthMillis : 560000000000L})" +
      "(v5:User {gender : \"female\",birthMillis : 590000000000L})" +
      "(v2)-[:member {until : 1550000000000L}]->(v0)" +
      "(v3)-[:member {until : 1550000000000L}]->(v0)" +
      "(v3)-[:member {until : 1550000000000L}]->(v1)" +
      "(v4)-[:member {until : 1550000000000L}]->(v1)" +
      "(v5)-[:member {until : 1550000000000L}]->(v1)" +
      "(v2)-[:knows {since : 1350000000000L}]->(v3)" +
      "(v3)-[:knows {since : 1350000000000L}]->(v2)" +
      "(v3)-[:knows {since : 1350000000000L}]->(v4)" +
      "(v3)-[:knows {since : 1350000000000L}]->(v5)" +
      "(v5)-[:knows {since : 1350000000000L}]->(v4)" +
      "]";
  }
}
