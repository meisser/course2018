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
import com.agentecon.finance.Producer;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.decisions.IFinancials;
import com.agentecon.goods.IStock;
import com.agentecon.learning.CovarianceControl;
import com.agentecon.learning.MarketingDepartment;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.PriceUnknownException;

public class Farm extends AbstractFarm {

	private static final double CAPITAL_BUFFER = 0.9;
	private static final double CAPITAL_TO_SPENDINGS_RATIO = 1 / (1 - CAPITAL_BUFFER);

	private CovarianceControl control;

	public Farm(IAgentIdGenerator id, IShareholder owner, IStock money, IStock land, IProductionFunction prodFun, IStatistics stats) {
		super(id, owner, money, land, prodFun, stats);
		this.control = new CovarianceControl(getInitialBudget(stats), 0.2);
	}

	protected double getInitialBudget(IStatistics stats) {
		try {
			return stats.getGoodsMarketStats().getPriceBelief(FarmingConfiguration.MAN_HOUR) * 10;
		} catch (PriceUnknownException e) {
			return 100;
		}
	}

	protected double calculateBudget() {
		double profits = marketing.getFinancials(getInventory(), getProductionFunction()).getProfits();
		control.reportOutput(profits);
		return control.getCurrentInput();
	}

	@Override
	protected double calculateDividends(int day) {
		double spending = marketing.getFinancials(getInventory(), getProductionFunction()).getLatestCogs();
		double targetSize = spending * CAPITAL_TO_SPENDINGS_RATIO;
		double excessReserve = getMoney().getAmount() - targetSize;
		if (excessReserve > 0) {
			// only adjust reserves slowly
			return excessReserve / 10;
		} else {
			return 0;
		}
	}

}
