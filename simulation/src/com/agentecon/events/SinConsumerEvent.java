package com.agentecon.events;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtilWithFloor;
import com.agentecon.consumer.MortalConsumer;
import com.agentecon.world.ICountry;

public class SinConsumerEvent extends ConsumerEvent {
	
	private static final double FLATNESS = 2;
	
	private int start;
	private int cycle;
	private double births;
	private int maxAge;

	public SinConsumerEvent(int start, int initialPopulation, int birthsPerCycle, int maxAge, int interval, String name, Endowment end, IUtilityFactory utility) {
		super(start, birthsPerCycle, 1, end, utility);
		this.start = start;
		this.maxAge = maxAge;
		this.cycle = interval;
		this.births = initialPopulation;
		assert FLATNESS >= 1.0;
	}
	
	public SinConsumerEvent(int start, int initialPopulation, int birthsPerCycle, int maxAge, int interval, String name, Endowment end, IUtility logUtil) {
		this(start, initialPopulation, birthsPerCycle, maxAge, interval, name, end, new IUtilityFactory() {
			
			@Override
			public IUtility create(int number) {
				return new LogUtilWithFloor();
			}
		});
	}

	@Override
	public void execute(int today, ICountry sim) {
		int day = today - start;
		assert day >= 0;
		double period = (day % cycle) * 2 * Math.PI / cycle;
		this.births += (Math.sin(period) + FLATNESS) * getCardinality() / cycle / FLATNESS;
		while (births >= 1.0){
			births -= 1.0;
			sim.add(createConsumer(sim, end, utilFun.create(count++)));
		}
	}
	
	@Override
	protected IConsumer createConsumer(IAgentIdGenerator id, Endowment end, IUtility util){
		return new MortalConsumer(id, this.maxAge, end, util);
	}
	
}
