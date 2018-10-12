/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise3;

import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.exercise2.AbstractFarm;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.firm.IShareholder;
import com.agentecon.goods.IStock;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.PriceUnknownException;

public class Farm2 extends AbstractFarm {

	private static final double CAPITAL_BUFFER = 0.9;

	public Farm2(IAgentIdGenerator id, IShareholder owner, IStock money, IStock land, IProductionFunction prodFun, IStatistics stats) {
		super(id, owner, money, land, prodFun, stats);
	}

	protected double getInitialBudget(IStatistics stats) {
		try {
			return stats.getGoodsMarketStats().getPriceBelief(FarmingConfiguration.MAN_HOUR) * 10;
		} catch (PriceUnknownException e) {
			return 100;
		}
	}

	protected double calculateBudget() {
		return getMoney().getAmount() * (1.0 - CAPITAL_BUFFER);
	}

	@Override
	protected double calculateDividends(int day) {
		double money = getMoney().getAmount();
		if (money > 1000) {
			return (money - 1000) / 10;
		} else {
			return 0.0;
		}
	}

}
