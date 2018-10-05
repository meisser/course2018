package com.agentecon.exercise3;

import com.agentecon.Simulation;
import com.agentecon.exercises.FarmingConfiguration;

public class MoneyConfigurationWithHelicopter extends FarmingConfiguration {
	
	public MoneyConfigurationWithHelicopter() {
		super(Farmer.class);
		
		addEvent(new HelicopterMoneyEvent(2000, 1, 1, 100));
//		addEvent(new InterestEvent(2000, 0.01)); // pay 1% interest every 10 days
	}

	public static void main(String[] args) {
		MoneyConfigurationWithHelicopter config = new MoneyConfigurationWithHelicopter();
		Simulation sim = new Simulation(config);
		sim.run();
		config.diagnoseResult(System.out, sim);
	}
	
}
