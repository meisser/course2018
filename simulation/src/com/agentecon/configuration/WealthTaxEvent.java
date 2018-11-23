package com.agentecon.configuration;

import java.util.Collection;

import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.events.SimEvent;
import com.agentecon.market.IStatistics;
import com.agentecon.world.ICountry;

public class WealthTaxEvent extends SimEvent {
	
	private static final double TAX_RATE = 0.0001;

	public WealthTaxEvent() {
		super(1, 1, 1);
	}

	@Override
	public void execute(int day, ICountry sim, IStatistics stats) {
		double taxRevenue = 0.0;
		for (IAgent a: sim.getAgents().getAgents()) {
			double wealth = a.getWealth(stats);
			double tax = wealth * TAX_RATE;
			a.getMoney().remove(tax);
			taxRevenue += tax;
		}
		Collection<IConsumer> consumers = sim.getAgents().getConsumers();
		double transfer = taxRevenue / consumers.size();
		for (IConsumer c: sim.getAgents().getConsumers()) {
			c.getMoney().add(transfer);
			taxRevenue -= transfer;
		}
		if (taxRevenue > 0) {
			consumers.iterator().next().getMoney().add(taxRevenue);
		} else if (taxRevenue < 0) {
			consumers.iterator().next().getMoney().remove(-taxRevenue);
		}
	}

}
