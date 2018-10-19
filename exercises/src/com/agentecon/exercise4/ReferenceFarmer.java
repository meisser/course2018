/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise4;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.MortalConsumer;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.finance.Firm;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.IStockMarket;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Quantity;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IPriceProvider;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.PriceUnknownException;
import com.agentecon.research.IFounder;
import com.agentecon.research.IInnovation;

/**
 * Unlike the Hermit, the farmer can decide to work at other farms and to buy from others. To formalize these relationships, the farmer does not produce himself anymore, but instead uses his land to
 * found a profit-maximizing firm.
 */
public class ReferenceFarmer extends MortalConsumer implements IFounder {

	private static final double CAPITAL_BUFFER = 0.80;
	public static final double MINIMUM_WORKING_HOURS = 5;

	private Good manhours;
	private double savings;

	public ReferenceFarmer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility utility) {
		super(id, maxAge, end, utility);
		this.savings = 0.0;
		this.manhours = end.getDaily()[0].getGood();
		assert this.manhours.equals(FarmingConfiguration.MAN_HOUR);
	}

	@Override
	public void managePortfolio(IStockMarket stocks) {
		boolean retired = isRetired();
		if (retired) {
			int daysLeft = getMaxAge() - getAge() + 1;
			double consumptionToday = this.savings / daysLeft;
			this.savings -= consumptionToday;
		} else {
			double dividends = getPortfolio().getLatestDividendIncome(); // how much dividends did we get today?
			double workFraction = 1.0d / getMaxAge() * getRetirementAge(); // 80%
			double retirementFraction = 1 - workFraction; // 20%
			this.savings += (getDailySpendings() - dividends) / workFraction * retirementFraction;
		}
	}

	@Override
	public IFirm considerCreatingFirm(IStatistics statistics, IInnovation research, IAgentIdGenerator id) {
		IStock myLand = getStock(FarmingConfiguration.LAND);
		if (myLand.hasSome() && statistics.getRandomNumberGenerator().nextDouble() < 0.02) {
			// I have plenty of land and feel lucky, let's see if we want to found a farm
			IProductionFunction prod = research.createProductionFunction(FarmingConfiguration.POTATOE);
			if (checkProfitability(statistics.getGoodsMarketStats(), myLand, prod)) {
				IShareholder owner = ReferenceFarmer.this;
				IStock startingCapital = getMoney().hideRelative(0.5);
				Firm farm = new Farm(id, owner, startingCapital, myLand, prod, statistics);
				farm.getInventory().getStock(manhours).transfer(getStock(manhours), 14);
				return farm;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private boolean checkProfitability(IPriceProvider prices, IStock myLand, IProductionFunction prod) {
		try {
			Quantity hypotheticalInput = getStock(manhours).hideRelative(0.5).getQuantity();
			Quantity output = prod.calculateOutput(new Quantity(HermitConfiguration.MAN_HOUR, 12), myLand.getQuantity());
			double profits = prices.getPriceBelief(output) - prices.getPriceBelief(hypotheticalInput);
			return profits > 0;
		} catch (PriceUnknownException e) {
			return true; // market is dead, maybe we are lucky
		}
	}

	@Override
	protected void trade(Inventory inv, IPriceTakerMarket market) {
		// The trading inventory is created in two stages:
		// - First we hide the savings, which we want to keep for the future
		// - Second we hide a relative amount of what is left as a buffer as usual
		Inventory inventoryWithoutSavings = inv.hide(getMoney().getGood(), savings);
		Inventory reducedInv = inventoryWithoutSavings.hideRelative(getMoney().getGood(), CAPITAL_BUFFER);

		super.workAtLeast(market, MINIMUM_WORKING_HOURS);
		super.trade(reducedInv, market);
	}

	@Override
	public double consume() {
		return super.consume();
	}

}
