package com.agentecon.exercise3;

import com.agentecon.Simulation;
import com.agentecon.exercises.FarmingConfiguration;

public class MoneyConfiguration2 extends FarmingConfiguration {
	
	public MoneyConfiguration2() {
		super(Farmer2.class);
		
//		addEvent(new HelicopterMoneyEvent(2000, 1, 1, 100));
//		addEvent(new InterestEvent(2000, 0.01)); // pay 1% interest every 10 days
	}

	public static void main(String[] args) {
		MoneyConfiguration2 config = new MoneyConfiguration2();
		Simulation sim = new Simulation(config);
		sim.run();
		config.diagnoseResult(System.out, sim);
	}
	
}
