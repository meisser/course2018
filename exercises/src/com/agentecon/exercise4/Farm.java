/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise4;

import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.finance.Producer;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.decisions.IFinancials;
import com.agentecon.goods.IStock;
import com.agentecon.learning.IControl;
import com.agentecon.learning.MarketingDepartment;
import com.agentecon.learning.QuadraticMaximizer;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.PriceUnknownException;

public class Farm extends Producer {

	private static final double CAPITAL_BUFFER = 0.9;
	private static final double CAPITAL_TO_SPENDINGS_RATIO = 1 / (1 - CAPITAL_BUFFER);

	private IControl control;
	private MarketingDepartment marketing;

	public Farm(IAgentIdGenerator id, IShareholder owner, IStock money, IStock land, IProductionFunction prodFun, IStatistics stats) {
		super(id, owner, prodFun, stats.getMoney());
		this.control = new QuadraticMaximizer(0.5, getAgentId(), 100, 1000);
		this.marketing = new MarketingDepartment(getMoney(), stats.getGoodsMarketStats(), getStock(FarmingConfiguration.MAN_HOUR), getStock(FarmingConfiguration.POTATOE));
		getStock(land.getGood()).absorb(land);
		getMoney().absorb(money);
		assert getMoney().getAmount() > 0;
	}
	
	protected double getInitialBudget(IStatistics stats) {
		try {
			return stats.getGoodsMarketStats().getPriceBelief(FarmingConfiguration.MAN_HOUR) * 10;
		} catch (PriceUnknownException e) {
			return 100;
		}
	}

	@Override
	public void offer(IPriceMakerMarket market) {
		double budget = calculateBudget();
		marketing.createOffers(market, this, budget);
	}
	

//	protected double calculateBudget() {
//		return getMoney().getAmount() * (1.0 - CAPITAL_BUFFER);
//	}

	private double calculateBudget() {
		double profits = marketing.getFinancials(getInventory(), getProductionFunction()).getProfits();
		control.reportOutput(profits);
		return control.getCurrentInput();
	}

	@Override
	public void adaptPrices() {
		marketing.adaptPrices();
	}

	@Override
	public void produce() {
		super.produce();
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

	private int daysWithoutProfit = 0;

	@Override
	public boolean considerBankruptcy(IStatistics stats) {
		super.considerBankruptcy(stats);
		IFinancials fin = marketing.getFinancials(getInventory(), getProductionFunction());
		double profits = fin.getProfits();
		if (profits <= 0) {
			daysWithoutProfit++;
		} else {
			daysWithoutProfit = 0;
		}
		return daysWithoutProfit > 100;
	}

}
