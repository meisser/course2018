package com.agentecon.exercise7;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.Simulation;
import com.agentecon.configuration.FlowConfiguration;

public class LifeExpectancyConfiguration extends FlowConfiguration {
	
	private static final int MAX_AGE = 1000; // life expectancy. Retirement is defined in MortalConsumer to start at 80% of max age.

	public LifeExpectancyConfiguration() throws SocketTimeoutException, IOException {
		super(MAX_AGE);
	}
	
	public static void main(String[] args) throws SocketTimeoutException, IOException {
		LifeExpectancyConfiguration config = new LifeExpectancyConfiguration();
		config.addEvent(new OutputWealthDistributionEvent(3000));
		Simulation sim = new Simulation(config);
		sim.run();
	}

}
