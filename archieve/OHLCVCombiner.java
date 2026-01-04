/**
 * 
 */
package com.aashish22bansal.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * 
 */
public class OHLCVCombiner extends Reducer<Text, OHLCVWritable, Text, OHLCVWritable> {

	@Override
	protected void reduce(Text key, Iterable<OHLCVWritable> values, Context ctx)
			throws IOException, InterruptedException {

		double open = -1;
		double high = Double.MIN_VALUE;
		double low = Double.MAX_VALUE;
		double close = 0;
		long volume = 0;

		for (OHLCVWritable v : values) {
			if (open < 0)
				open = v.open;
			high = Math.max(high, v.high);
			low = Math.min(low, v.low);
			close = v.close; // last close wins
			volume += v.volume;
		}

		ctx.write(key, new OHLCVWritable(open, high, low, close, volume));
	}
}
