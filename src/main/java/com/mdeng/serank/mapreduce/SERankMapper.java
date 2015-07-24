package com.mdeng.serank.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class SERankMapper extends Mapper<Object, Text, Text, IntWritable> {

  @Override
  protected void map(Object keyin, Text valuein, Context context) throws IOException,
      InterruptedException {
    // date_group_keyword_top_host
    // -> (date_group,1)
    String[] items = valuein.toString().split("_");
    if (items.length != 5) return;

    int top = Integer.valueOf(items[3]);
    if (top <= 10 && items[4].equals("58.com")) {
      Text keyout = new Text(items[0] + "_" + items[1]);
      IntWritable valueout = new IntWritable(1);
      context.write(keyout, valueout);
    }
  }
}
