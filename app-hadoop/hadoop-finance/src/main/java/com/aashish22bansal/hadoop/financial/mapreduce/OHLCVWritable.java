/**
 * 
 */
package com.aashish22bansal.hadoop.financial.mapreduce;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class OHLCVWritable implements Writable {

    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;

    public OHLCVWritable() {}

    public OHLCVWritable(double open, double high, double low, double close, long volume) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    // 🔹 THIS WAS MISSING
    public void set(double open, double high, double low, double close, long volume) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
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

    public double getOpen() { return open; }
    public double getHigh() { return high; }
    public double getLow() { return low; }
    public double getClose() { return close; }
    public long getVolume() { return volume; }

    public void setHigh(double high) { this.high = high; }
    public void setLow(double low) { this.low = low; }
    public void setClose(double close) { this.close = close; }
    public void addVolume(long v) { this.volume += v; }

    @Override
    public String toString() {
        return open + "," + high + "," + low + "," + close + "," + volume;
    }
}

