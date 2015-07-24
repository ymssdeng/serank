package com.mdeng.serank.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class SERankReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

  @Override
  protected void reduce(Text key, Iterable<IntWritable> values, Context context)
      throws IOException, InterruptedException {
    // (date_group,1)
    // (date_group,1)
    // (date_group,1)
    // -> (date_group,3)
    IntWritable count = new IntWritable(0);
    for (IntWritable value : values) {
      count.set(count.get() + value.get());
    }
    context.write(key, count);
  }
}
