/**
 * 
 */
package com.aashish22bansal.hadoop;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

/**
 * 
 */
public class OHLCVWritable implements Writable {

	public double open;
	public double high;
	public double low;
	public double close;
	public long volume;

	public OHLCVWritable() {
	}

	public OHLCVWritable(double o, double h, double l, double c, long v) {
		this.open = o;
		this.high = h;
		this.low = l;
		this.close = c;
		this.volume = v;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeDouble(open);
		out.writeDouble(high);
		out.writeDouble(low);
		out.writeDouble(close);
		out.writeLong(volume);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		open = in.readDouble();
		high = in.readDouble();
		low = in.readDouble();
		close = in.readDouble();
		volume = in.readLong();
	}
}
