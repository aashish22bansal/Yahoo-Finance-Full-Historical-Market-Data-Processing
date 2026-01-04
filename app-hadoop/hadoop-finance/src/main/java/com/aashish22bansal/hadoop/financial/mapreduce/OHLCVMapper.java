/**
 * 
 */
package com.aashish22bansal.hadoop.financial.mapreduce;

/**
 * 
 */
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class OHLCVMapper
        extends Mapper<LongWritable, Text, Text, OHLCVWritable> {

    private Text outKey = new Text();
    private OHLCVWritable outValue = new OHLCVWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        String line = value.toString();

        // Skip empty lines
        if (line == null || line.trim().isEmpty()) return;

        // Skip header
        if (line.startsWith("Date,")) return;

        String[] tokens = line.split(",");

        // Must be exactly 7 columns
        if (tokens.length != 7) return;

        try {
            String date = tokens[0];
            String ticker = tokens[1];

            // Skip weird metadata line: ",,AAPL,AAPL..."
            if (date.isEmpty() || ticker.isEmpty()) return;

            double open = Double.parseDouble(tokens[2]);
            double high = Double.parseDouble(tokens[3]);
            double low = Double.parseDouble(tokens[4]);
            double close = Double.parseDouble(tokens[5]);
            long volume = Long.parseLong(tokens[6]);

            // Key = Ticker|Date
            outKey.set(ticker + "|" + date);

            outValue.set(open, high, low, close, volume);

            context.write(outKey, outValue);

        } catch (Exception e) {
            // Bad record → safely skip
        	System.out.println(e);
        }
    }
}


/* Attempt 2
public class OHLCVMapper
        extends Mapper<LongWritable, Text, Text, OHLCVWritable> {

    private Text outKey = new Text();
    private OHLCVWritable outValue = new OHLCVWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        String line = value.toString().trim();

        // Skip header
        if (line.startsWith("date") || line.isEmpty()) {
            return;
        }

        // Split by whitespace (tabs or spaces)
        String[] fields = line.split("\\s+");

        // Safety check
        if (fields.length < 6) {
            return;
        }

        String date = fields[0];
        double open = Double.parseDouble(fields[1]);
        double high = Double.parseDouble(fields[2]);
        double low = Double.parseDouble(fields[3]);
        double close = Double.parseDouble(fields[4]);
        long volume = Long.parseLong(fields[5]);

        outKey.set(date);
        outValue.set(open, high, low, close, volume);

        context.write(outKey, outValue);
    }
}
*/


/* Attempt 1
public class OHLCVMapper extends Mapper<LongWritable, Text, Text, OHLCVWritable> {

    private boolean isHeader = true;

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        if (isHeader) {
            isHeader = false;
            return;
        }

        String[] fields = value.toString().split(",");

        if (fields.length < 7) return;

        String date = fields[0];

        double open = Double.parseDouble(fields[1]);
        double high = Double.parseDouble(fields[2]);
        double low = Double.parseDouble(fields[3]);
        double close = Double.parseDouble(fields[4]);
        long volume = Long.parseLong(fields[6]);

        context.write(new Text(date),
                new OHLCVWritable(open, high, low, close, volume));
    }
}
*/
