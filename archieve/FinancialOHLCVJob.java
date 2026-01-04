/**
 * 
 */
package com.aashish22bansal.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 
 */
public class FinancialOHLCVJob {

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();

		Job job = Job.getInstance(conf, "Financial OHLCV Aggregation");

		job.setJarByClass(FinancialOHLCVJob.class);

		job.setMapperClass(OHLCVMapper.class);
		job.setCombinerClass(OHLCVCombiner.class);
		job.setReducerClass(OHLCVReducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(OHLCVWritable.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		// 🔒 Memory safety
		job.getConfiguration().set("mapreduce.map.memory.mb", "1024");
		job.getConfiguration().set("mapreduce.reduce.memory.mb", "2048");

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
