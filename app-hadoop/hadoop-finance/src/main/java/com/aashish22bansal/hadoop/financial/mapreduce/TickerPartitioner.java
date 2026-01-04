/**
 * 
 */
package com.aashish22bansal.hadoop.financial.mapreduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

/**	
 * 
 */
public class TickerPartitioner extends Partitioner<Text, OHLCVWritable> {

	@Override
	public int getPartition(Text key, OHLCVWritable value, int numPartitions) {
		String ticker = key.toString().split("\\|")[0];
		return Math.abs(ticker.hashCode()) % numPartitions;
	}
}
