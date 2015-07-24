package com.mdeng.serank.mapreduce;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

public class SERankMRTest {
  MapDriver<Object, Text, Text, IntWritable> mapDriver;
  ReduceDriver<Text, IntWritable, Text, IntWritable> reduceDriver;

  @Before
  public void setUp() {
    SERankMapper mapper = new SERankMapper();
    SERankReducer reducer = new SERankReducer();
    mapDriver = MapDriver.newMapDriver(mapper);
    reduceDriver = ReduceDriver.newReduceDriver(reducer);
  }

  @Test
  public void testMapper() {
    mapDriver.withInput(new Object(), new Text("20150524_1_0086酒吧消费_1_")).runTest();
  }

  @Test
  public void testMapper2() {
    mapDriver.withInput(new Object(), new Text("20150524_1_0086酒吧消费_2_58.com"))
        .withOutput(new Text("20150524_1"), new IntWritable(1)).runTest();
  }

  @Test
  public void testReducer() {
    List<IntWritable> values = new ArrayList<IntWritable>();
    values.add(new IntWritable(1));
    values.add(new IntWritable(1));
    reduceDriver.withInput(new Text("20150524_1"), values);
    reduceDriver.withOutput(new Text("20150524_1"), new IntWritable(2));
    reduceDriver.runTest();
  }

}
