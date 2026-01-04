/**
 * 
 */
package com.aashish22bansal.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * 
 */
public class OHLCVMapper extends Mapper<LongWritable, Text, Text, OHLCVWritable> {

	private Text outKey = new Text();

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

		// Skip header
		if (value.toString().startsWith("Date"))
			return;

		String[] fields = value.toString().split(",");
		if (fields.length < 6)
			return;

		String date = fields[0]; // 2020-01-02
		double open = Double.parseDouble(fields[1]);
		double high = Double.parseDouble(fields[2]);
		double low = Double.parseDouble(fields[3]);
		double close = Double.parseDouble(fields[4]);
		long volume = Long.parseLong(fields[5]);

		// Extract symbol from filename
		String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
		String symbol = fileName.replace(".csv", "");

		// YYYY-MM
		String month = date.substring(0, 7);

		outKey.set(symbol + "|" + month);
		context.write(outKey, new OHLCVWritable(open, high, low, close, volume));
	}
}
