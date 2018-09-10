/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.consumer;

import com.agentecon.Simulation;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.configuration.GrowthConfiguration;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.finance.HighestYieldPickingStrategy;
import com.agentecon.firm.IStockMarket;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.util.Numbers;

/**
 * Unlike the Hermit, the farmer can decide to work at other farms and to buy from others. To formalize these relationships, the farmer does not produce himself anymore, but instead uses his land to
 * found a profit-maximizing firm.
 */
public class InvestingConsumer extends MortalConsumer {

	private static final double DISCOUNT_RATE = 0.995;

	private static final double CAPITAL_BUFFER = 0.80;
	public static final double MINIMUM_WORKING_HOURS = 8;

	private Good manhours;

	public InvestingConsumer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility utility) {
		super(id, maxAge, end, utility);
		this.manhours = end.getDaily()[0].getGood();
		assert this.manhours.equals(FarmingConfiguration.MAN_HOUR);
	}

	@Override
	public void managePortfolio(IStockMarket stocks) {
		boolean retired = isRetired();
		int daysLeft = getMaxAge() - getAge() + 1;
		if (retired) {
			double proceeds = getPortfolio().sell(stocks, this, 1.0d / daysLeft);
			listeners.notifyDivested(this, proceeds); // notify listeners for inflow / outflow statistics
		} else {
			int daysToRetirement = getRetirementAge() - getAge();
			double dividends = getPortfolio().getLatestDividendIncome();
			double constantFactor = Numbers.geometricSum(DISCOUNT_RATE, daysToRetirement);
			double consumption = getDailySpendings();
			double optimalSavings = (consumption * (daysLeft - 1) - dividends / (1 - DISCOUNT_RATE)) / constantFactor + dividends - getDailySpendings();
			double actualInvestment = getPortfolio().invest(new HighestYieldPickingStrategy(), stocks, this, optimalSavings);
			listeners.notifyInvested(this, actualInvestment); // notify listeners for inflow / outflow statistics
		}
	}

	// @Override
	// public void managePortfolio(IStockMarket stocks) {
	// boolean retired = isRetired();
	// if (retired) {
	// int daysLeft = getMaxAge() - getAge() + 1;
	// double proceeds = getPortfolio().sell(stocks, this, 1.0d / daysLeft);
	// listeners.notifyDivested(this, proceeds); // notify listeners for statistics
	// } else {
	// double dividends = getPortfolio().getLatestDividendIncome(); // how much dividends did we get today?
	// double workFraction = 1.0d / getMaxAge() * getRetirementAge(); // 80%
	// double retirementFraction = 1 - workFraction; // 20%
	// double toInvest = (getDailySpendings() - dividends) / workFraction * retirementFraction;
	// double actualInvestment = getPortfolio().invest(stocks, this, toInvest);
	// listeners.notifyInvested(this, actualInvestment); // notify listeners for statistics
	// }
	// }
	//
	// @Override
	// public IFirm considerCreatingFirm(IStatistics statistics, IInnovation research, IAgentIdGenerator id) {
	// IStock myLand = getStock(FarmingConfiguration.LAND);
	// if (myLand.getAmount() < SELL_LAND_IF_LESS) {
	// return null;
	// } else if (myLand.hasSome() && getMoney().hasSome() && statistics.getRandomNumberGenerator().nextDouble() < 0.02) {
	// // I have plenty of land and feel lucky, let's see if we want to found a farm
	// IProductionFunction prod = research.createProductionFunction(FarmingConfiguration.POTATOE);
	// if (checkProfitability(statistics.getGoodsMarketStats(), myLand, prod)) {
	// IShareholder owner = InvestingConsumer.this;
	// IStock startingCapital = getMoney().hideRelative(0.5);
	// Firm farm = new LandBuyingFarm(id, owner, startingCapital, myLand, (CobbDouglasProduction) prod, statistics);
	// farm.getInventory().getStock(manhours).transfer(getStock(manhours), 14);
	// return farm;
	// } else {
	// return null;
	// }
	// } else {
	// return null;
	// }
	// }
	//
	// private boolean checkProfitability(IPriceProvider prices, IStock myLand, IProductionFunction prod) {
	// try {
	// Quantity hypotheticalInput = getStock(manhours).hideRelative(0.5).getQuantity();
	// Quantity output = prod.calculateOutput(new Quantity(HermitConfiguration.MAN_HOUR, 12), myLand.getQuantity());
	// double profits = prices.getPriceBelief(output) - prices.getPriceBelief(hypotheticalInput);
	// return profits > 0;
	// } catch (PriceUnknownException e) {
	// return true; // market is dead, maybe we are lucky
	// }
	// }

	@Override
	protected void trade(Inventory inv, IPriceTakerMarket market) {
		IStock myLand = getStock(FarmingConfiguration.LAND);
		if (myLand.hasSome()) {
			market.sellSome(this, getMoney(), myLand);
		}
		Inventory reducedInv = inv.hideRelative(getMoney().getGood(), CAPITAL_BUFFER);
		super.workAtLeast(market, MINIMUM_WORKING_HOURS);
		super.trade(reducedInv, market);
	}

	@Override
	public double consume() {
		return super.consume();
	}

	public static void main(String[] args) {
		// You can run this to test whether the simulation actually completes
		// To analyze the results, you should use the SimulationServer
		GrowthConfiguration configuration = new GrowthConfiguration(InvestingConsumer.class);
		System.out.print("Creating and running the simulation...");
		// Create the simulation based on that configuration
		Simulation sim = new Simulation(configuration);
		long t0 = System.nanoTime();
		sim.run(); // run the simulation
		long t1 = System.nanoTime();
		System.out.println(" done after " + (t1 - t0) / 1000000 + "ms");
	}

}
