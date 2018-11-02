package com.agentecon.exercise7;

import com.agentecon.consumer.IConsumer;
import com.agentecon.events.SimEvent;
import com.agentecon.market.IStatistics;
import com.agentecon.world.ICountry;

public class OutputWealthDistributionEvent extends SimEvent {

	public OutputWealthDistributionEvent(int when) {
		super(when);
	}
	
	@Override
	public void execute(int day, ICountry sim, IStatistics stats) {
		GiniCalculator calc = new GiniCalculator();
		for (IConsumer consumer: sim.getAgents().getConsumers()) {
			double wealth = consumer.getWealth(stats);
			calc.add(wealth);
			System.out.println(consumer.getAge() + "\t" + wealth);
		}
		System.out.println("Gini: " + calc.calculateGini());
	}

}
