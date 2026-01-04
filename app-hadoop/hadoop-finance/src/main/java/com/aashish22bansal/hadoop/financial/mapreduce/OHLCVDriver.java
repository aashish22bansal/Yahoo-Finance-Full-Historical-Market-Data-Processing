/**
 * 
 */
package com.aashish22bansal.hadoop.financial.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.aashish22bansal.hadoop.financial.mapreduce.FinancialOHLCVJob;

public class OHLCVDriver {

    public static void main(String[] args) throws Exception {

        // THIS IS THE CRITICAL PART
        if (args.length != 3) {
            System.err.println("Usage: OHLCVDriver - Arg 0: " + args[0] + " - Arg 1: " + args[1] + " - Arg 2: " + args[2]);
            System.exit(1);
        }
        
        System.err.println("Proceeding with Configuration: OHLCVDriver Command Arguments - Arg 0: " + args[0] + " - Arg 1: " + args[1] + " - Arg 2: " + args[2]);

        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "Financial OHLCV Job");
        job.setJarByClass(OHLCVDriver.class);

        FinancialOHLCVJob.configure(job);

        FileInputFormat.addInputPath(job, new Path(args[1]));
        FileOutputFormat.setOutputPath(job, new Path(args[2]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}


/* Attempt 5
public class OHLCVDriver {

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("Usage: OHLCVDriver <input> <output>");
            System.exit(1);
        }

        Configuration conf = new Configuration();

        int exitCode = ToolRunner.run(
                conf,
                new FinancialOHLCVJob(),
                args
        );

        System.exit(exitCode);
    }
}
*/

/* Attempt 4
public class OHLCVDriver extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("Usage: OHLCVDriver <input> <output>");
            return 1;
        }

        Configuration conf = getConf();

        Job job = FinancialOHLCVJob.createJob(
                conf,
                new Path(args[0]),
                new Path(args[1])
        );

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(
                new Configuration(),
                new OHLCVDriver(),
                args
        );
        System.exit(exitCode);
    }
}
*/

/*
public class OHLCVDriver extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("Usage: OHLCVDriver <input> <output>");
            return -1;
        }

        Job job = Job.getInstance(getConf(), "Financial OHLCV Processing");
        job.setJarByClass(OHLCVDriver.class);

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

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(
                new Configuration(),
                new OHLCVDriver(),
                args
        );
        System.exit(exitCode);
    }
}
*/


/* Attempt 2:
public class OHLCVDriver {

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("Usage: OHLCVDriver <input> <output>");
            System.exit(1);
        }

        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "Financial OHLCV Aggregation");
        job.setJarByClass(OHLCVDriver.class);

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

/* Attempt 3
public class OHLCVDriver {

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("Usage: OHLCVDriver <input> <output>");
            System.exit(1);
        }

        Configuration conf = new Configuration();

        int exitCode = ToolRunner.run(
                conf,
                new FinancialOHLCVJob(),
                args
        );

        System.exit(exitCode);
    }
}
*/

