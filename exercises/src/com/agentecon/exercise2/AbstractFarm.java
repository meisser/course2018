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
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.finance.Producer;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.decisions.IFinancials;
import com.agentecon.goods.IStock;
import com.agentecon.learning.MarketingDepartment;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProductionFunction;

public abstract class AbstractFarm extends Producer {

	protected MarketingDepartment marketing;

	public AbstractFarm(IAgentIdGenerator id, IShareholder owner, IStock money, IStock land, IProductionFunction prodFun, IStatistics stats) {
		super(id, owner, prodFun, stats.getMoney());
		this.marketing = new MarketingDepartment(getMoney(), stats.getGoodsMarketStats(), getStock(FarmingConfiguration.MAN_HOUR), getStock(FarmingConfiguration.POTATOE));
		getStock(land.getGood()).absorb(land);
		getMoney().absorb(money);
		assert getMoney().getAmount() > 0;
	}

	@Override
	public void offer(IPriceMakerMarket market) {
		double budget = calculateBudget();
		marketing.createOffers(market, this, budget);
	}

	protected abstract double calculateBudget();

	@Override
	public void adaptPrices() {
		marketing.adaptPrices();
	}

	@Override
	public void produce() {
		super.produce();
	}

	private int daysWithoutProfit = 0;

	@Override
	public boolean considerBankruptcy(IStatistics stats) {
		super.considerBankruptcy(stats);
		IFinancials fin = marketing.getFinancials(getInventory(), getProductionFunction());
		double profits = fin.getProfits();
		if (profits <= 0.01) {
			daysWithoutProfit++;
		} else {
			daysWithoutProfit = 0;
		}
		return daysWithoutProfit > 20;
	}

}
