/**
 * 
 */
package com.aashish22bansal.hadoop.financial.mapreduce;

/**
 * 
 */
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class OHLCVReducer extends Reducer<Text, OHLCVWritable, Text, Text> {

    @Override
    protected void reduce(Text key, Iterable<OHLCVWritable> values, Context context)
            throws IOException, InterruptedException {

        double open = -1;
        double high = Double.MIN_VALUE;
        double low = Double.MAX_VALUE;
        double close = 0;
        long volume = 0;

        boolean first = true;

        for (OHLCVWritable v : values) {
            if (open < 0) open = v.getOpen();
            
            if (first) {
                open = v.getOpen();
                first = false;
            }
            
            high = Math.max(high, v.getHigh());
            low = Math.min(low, v.getLow());
            close = v.getClose();
            volume += v.getVolume();
        }

        context.write(key, new Text(open + "," + high + "," + low + "," + close + "," + volume));
    }
}

