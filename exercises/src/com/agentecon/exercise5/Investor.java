/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise5;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.MortalConsumer;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.finance.stockpicking.IStockPickingStrategy;
import com.agentecon.firm.IStockMarket;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IPriceTakerMarket;

/**
 * A mortal consumer that invests in the stock market to save for retirement.
 */
public class Investor extends MortalConsumer {
	
	private IStockPickingStrategy strategy;

	public Investor(IAgentIdGenerator id, int maxAge, Endowment end, IUtility utility) {
		super(id, maxAge, end, utility);
		this.strategy = new StockPickingStrategy(id.getRand(), getPortfolio());
	}

	@Override
	public void managePortfolio(IStockMarket stocks) {
		boolean retired = isRetired();
		int daysLeft = getMaxAge() - getAge() + 1;
		if (retired) {
			double proceeds = getPortfolio().sell(stocks, this, 1.0d / daysLeft);
			listeners.notifyDivested(this, proceeds); // notify listeners for inflow / outflow statistics
		} else {
			double dividends = getPortfolio().getLatestDividendIncome();
			double workFraction = 1.0d / getMaxAge() * getRetirementAge(); // 80%
			double retirementFraction = 1 - workFraction; // 20%
			double toInvest = (getDailySpendings() - dividends) / workFraction * retirementFraction;
			double actualInvestment = getPortfolio().invest(strategy, stocks, this, toInvest);
			listeners.notifyInvested(this, actualInvestment); // notify listeners for inflow / outflow statistics
		}
	}
	
	@Override
	protected void trade(Inventory inv, IPriceTakerMarket market) {
		// If a farm goes bankrupt, the shareholder get the remaining land of the farm. In that case, let's simply sell the land.
		IStock myLand = getStock(FarmingConfiguration.LAND);
		if (myLand.hasSome()) {
			market.sellSome(this, getMoney(), myLand);
		}
		super.trade(inv, market);
	}
	
}
