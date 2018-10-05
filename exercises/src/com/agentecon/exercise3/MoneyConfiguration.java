package com.agentecon.exercise3;

import com.agentecon.Simulation;
import com.agentecon.exercises.FarmingConfiguration;

public class MoneyConfiguration extends FarmingConfiguration {
	
	public MoneyConfiguration() {
		super(Farmer.class);
		
//		addEvent(new HelicopterMoneyEvent(2000, 1, 1, 100));
//		addEvent(new InterestEvent(2000, 0.01)); // pay 1% interest every 10 days
	}

	public static void main(String[] args) {
		MoneyConfiguration config = new MoneyConfiguration();
		Simulation sim = new Simulation(config);
		sim.run();
		config.diagnoseResult(System.out, sim);
	}
	
}
