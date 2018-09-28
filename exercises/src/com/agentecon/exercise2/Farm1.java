/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise2;

import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.firm.IShareholder;
import com.agentecon.goods.IStock;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProductionFunction;

public class Farm1 extends AbstractFarm {

	public Farm1(IAgentIdGenerator id, IShareholder owner, IStock money, IStock land, IProductionFunction prodFun, IStatistics stats) {
		super(id, owner, money, land, prodFun, stats);
	}

	@Override
	protected double calculateBudget() {
		return 100; // Why not spending 100? :)

		// Things that might or might not be useful here:
		// double fixedCosts = getProductionFunction().getFixedCost(FarmingConfiguration.MAN_HOUR);
		// double manHoursPrice = marketing.getPriceBelief(FarmingConfiguration.MAN_HOUR);
		// double availableCash = getMoney().getAmount();
		// etc.
	}

	@Override
	protected double calculateDividends(int day) {
		double money = getMoney().getAmount();
		
		// Idea 1: return percentage
		return money * 0.05;
		
		// Idea 2: return money above a threshold
//		if (money > 1000) {
//			return (money - 1000) / 10;
//		} else {
//			return 0.0;
//		}
	}

}
