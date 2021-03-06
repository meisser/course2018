package com.agentecon.configuration;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.IAgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.InvestingConsumer;
import com.agentecon.events.GrowthEvent;
import com.agentecon.events.IUtilityFactory;
import com.agentecon.events.MinPopulationGrowthEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.exercises.HighProductivityConfiguration;
import com.agentecon.finance.MarketMaker;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;
import com.agentecon.research.IInnovation;
import com.agentecon.world.ICountry;

public class StocksConfiguration2 extends HighProductivityConfiguration implements IUtilityFactory, IInnovation {

	private static final int BASIC_AGENTS = 30;

	public static final double GROWTH_RATE = 0.0025;
	public static final int MAX_AGE = 500;
	private static final int GROW_UNTIL = 2000; // day at which growth stops

	public StocksConfiguration2() throws SocketTimeoutException, IOException {
		super(new IAgentFactory() {

			private int number = 1;

			@Override
			public IConsumer createConsumer(IAgentIdGenerator id, Endowment end, IUtility utility) {
				int maxAge = number++ * MAX_AGE / BASIC_AGENTS;
				return new InvestingConsumer(id, maxAge, end, utility);
			}
		}, BASIC_AGENTS);
		IStock[] dailyEndowment = new IStock[] { new Stock(MAN_HOUR, HermitConfiguration.DAILY_ENDOWMENT) };
		Endowment workerEndowment = new Endowment(getMoney(), new IStock[0], dailyEndowment);
		createBasicPopulation(workerEndowment);
		addMarketMakers();
		addEvent(new CentralBankEvent(POTATOE));
	}

	protected void addMarketMakers() {
		addEvent(new SimEvent(0, 0, 10) {

			@Override
			public void execute(int day, ICountry sim) {
				for (int i = 0; i < getCardinality(); i++) {
					IStock money = new Stock(getMoney(), 1000);
					sim.add(new MarketMaker(sim, money));
				}
			}
		});
	}

	protected void createBasicPopulation(Endowment workerEndowment) {
		addEvent(new MinPopulationGrowthEvent(0, BASIC_AGENTS) {

			@Override
			protected void execute(ICountry sim) {
				IConsumer cons = new InvestingConsumer(sim, MAX_AGE, workerEndowment, create(0));
				sim.add(cons);
			}

		});
		addEvent(new GrowthEvent(0, GROWTH_RATE, false) {

			@Override
			protected void execute(ICountry sim) {
				if (sim.getDay() < GROW_UNTIL) {
					IConsumer cons = new InvestingConsumer(sim, MAX_AGE, workerEndowment, create(0));
					sim.add(cons);
				}
			}

		});
		addEvent(new GrowthEvent(GROW_UNTIL, 1.0d / MAX_AGE, false) {

			@Override
			protected void execute(ICountry sim) {
				IConsumer cons = new InvestingConsumer(sim, MAX_AGE, workerEndowment, create(0));
				sim.add(cons);
			}

		});
	}
	
	@Override
	public int getMaxAge() {
		return MAX_AGE;
	}

}
