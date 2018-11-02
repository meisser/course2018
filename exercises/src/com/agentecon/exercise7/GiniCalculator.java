package com.agentecon.exercise7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GiniCalculator {
	
	private List<Double> values;

	public GiniCalculator() {
		this.values = new ArrayList<>();
	}
	
	public void add(double amount) {
		values.add(amount);
	}
	
	public double calculateGini() {
		if (values.size() <= 1) {
			return 0.0;
		} else {
			double totDifference = 0.0;
			double totSum = 0.0;
			for (int i = 0; i < values.size(); i++) {
				double v1 = values.get(i);
				totSum += v1;
				for (int j = i + 1; j < values.size(); j++) {
					double v2 = values.get(j);
					totDifference += Math.abs(v1 - v2);
				}
			}
			return totDifference / (values.size() * totSum);
		}
	}
	
	public static void main(String[] args) {
		GiniCalculator calc = new GiniCalculator();
		int population = 100;
		double[] wealth = new double[population];
		for (int i=0; i<wealth.length; i++) {
			calc.add(i);
		}
		System.out.println(calc.calculateGini());
	}
	
}
