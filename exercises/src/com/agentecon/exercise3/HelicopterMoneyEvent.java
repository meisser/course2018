package com.agentecon.exercise3;

import com.agentecon.agent.IAgent;
import com.agentecon.events.SimEvent;
import com.agentecon.world.ICountry;

public class HelicopterMoneyEvent extends SimEvent {
	
	private int luckyAgentId;
	private double amount;
	
	public HelicopterMoneyEvent(int startDay, int executionInterval, int luckyAgentId, double amount) {
		super(startDay, executionInterval, 1);
		this.luckyAgentId = luckyAgentId;
		this.amount = amount;
	}

	@Override
	public void execute(int day, ICountry sim) {
		IAgent luckyJoe = sim.getAgents().getAgent(luckyAgentId);
		if (luckyJoe != null) {
//			System.out.println("Giving " + luckyJoe + " some helicopter money.");
			luckyJoe.getMoney().add(amount);
		}
	}

}
