package com.agentecon.metric.variants;

import java.util.Collection;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.metric.series.TimeSeriesCollector;

public class CashStats extends SimStats {

	private TimeSeriesCollector cash;

	public CashStats(ISimulation agents, boolean individuals) {
		super(agents);
		this.cash = new TimeSeriesCollector(individuals, getMaxDay());
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		super.notifyDayEnded(stats);
		int day = stats.getDay();
		for (IAgent a : getAgents().getAgents()) {
			cash.record(day, a, a.getMoney().getAmount());
		}
		cash.flushDay(day, true);
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		return cash.getTimeSeries();
	}

}
