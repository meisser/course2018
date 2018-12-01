package com.agentecon.exercise9;

public class TimeSeries {

	private double[] values;
	private int current;
	private int entries;
	
	public TimeSeries(int length) {
		this.values = new double[length];
		this.current = 0;
		this.entries = 0;
	}
	
	public void add(double value) {
		this.entries++;
		this.values[current++] = value;
		if (current == values.length) {
			current = 0;
		}
	}
	
	public double getMin() {
		double min = 0.0;
		int limit = Math.min(entries, values.length);
		for (int i=0; i<limit; i++) {
			min = Math.min(min, values[i]);
		}
		return min;
	}
	
	public double getMax() {
		double max = 0.0;
		int limit = Math.min(entries, values.length);
		for (int i=0; i<limit; i++) {
			max = Math.max(max, values[i]);
		}
		return max;
	}
	
	public double getAverage() {
		double sum = 0.0;
		int limit = Math.min(entries, values.length);
		for (int i=0; i<limit; i++) {
			sum += values[i];
		}
		return sum / limit;
	}
	
	public double get(int lookback) {
		assert lookback < values.length;
		return values[(current - lookback) % this.values.length];
	}

}
