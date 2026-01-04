/**
 * 
 */
package com.aashish22bansal.hadoop.financial.mapreduce;

/**
 * 
 */
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class FinancialOHLCVJob {

    public static void configure(Job job) {

        job.setMapperClass(OHLCVMapper.class);
        job.setReducerClass(OHLCVReducer.class);
        job.setCombinerClass(OHLCVCombiner.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(OHLCVWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(OHLCVWritable.class);
    }
}


/* Attempt 5
public class FinancialOHLCVJob extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {

        Job job = Job.getInstance(getConf(), "Financial OHLCV Aggregation");

        job.setJarByClass(FinancialOHLCVJob.class);

        job.setMapperClass(OHLCVMapper.class);
        job.setCombinerClass(OHLCVCombiner.class);
        job.setReducerClass(OHLCVReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(OHLCVWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(OHLCVWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        return job.waitForCompletion(true) ? 0 : 1;
    }
}
*/

/* Attempt 4
  public class FinancialOHLCVJob {

    public static Job createJob(
            Configuration conf,
            Path input,
            Path output
    ) throws Exception {

        Job job = Job.getInstance(conf, "Financial OHLCV Aggregation");
        job.setJarByClass(FinancialOHLCVJob.class);

        job.setMapperClass(OHLCVMapper.class);
        job.setCombinerClass(OHLCVCombiner.class);
        job.setReducerClass(OHLCVReducer.class);

        job.setMapOutputKeyClass(org.apache.hadoop.io.Text.class);
        job.setMapOutputValueClass(OHLCVWritable.class);

        job.setOutputKeyClass(org.apache.hadoop.io.Text.class);
        job.setOutputValueClass(OHLCVWritable.class);

        FileInputFormat.addInputPath(job, input);
        FileOutputFormat.setOutputPath(job, output);

        return job;
    }
}
*/

/* Attempt 3
public class FinancialOHLCVJob extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {

        Job job = Job.getInstance(getConf(), "Financial OHLCV Aggregation");
        job.setJarByClass(FinancialOHLCVJob.class);

        job.setMapperClass(OHLCVMapper.class);
        job.setCombinerClass(OHLCVCombiner.class);
        job.setReducerClass(OHLCVReducer.class);

        job.setMapOutputKeyClass(org.apache.hadoop.io.Text.class);
        job.setMapOutputValueClass(OHLCVWritable.class);

        job.setOutputKeyClass(org.apache.hadoop.io.Text.class);
        job.setOutputValueClass(OHLCVWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        return job.waitForCompletion(true) ? 0 : 1;
    }
}
*/

/* Attempt 2
public class FinancialOHLCVJob {

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("Usage: FinancialOHLCVJob <input> <output>");
            System.exit(1);
        }

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

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
*/
