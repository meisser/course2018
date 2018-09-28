/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise2;

import com.agentecon.Simulation;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.finance.Firm;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;
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
public class AlternateFarmer extends Consumer implements IFounder {

	public static final double MINIMUM_WORKING_HOURS = 5;

	private Good manhours;

	public AlternateFarmer(IAgentIdGenerator id, Endowment end, IUtility utility) {
		super(id, end, utility);
		this.manhours = end.getDaily()[0].getGood();
		assert this.manhours.equals(HermitConfiguration.MAN_HOUR);
	}

	@Override
	public IFirm considerCreatingFirm(IStatistics statistics, IInnovation research, IAgentIdGenerator id) {
		IStock myLand = getStock(FarmingConfiguration.LAND);
		if (myLand.hasSome() && statistics.getRandomNumberGenerator().nextDouble() < 0.05) {
			// I have plenty of land and feel lucky, let's see if we want to found a farm
			IProductionFunction prod = research.createProductionFunction(FarmingConfiguration.POTATOE);
			if (checkProfitability(statistics.getGoodsMarketStats(), myLand, prod)) {
				IShareholder owner = AlternateFarmer.this;
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
		// In the beginning, shelves can be empty and thus there is no incentive
		// to work (sell man-hours) either.
		// To kick-start the economy, we require the farmer to sell some of his
		// man-hours anyway, even if he cannot
		// buy anything with the earned money.
		super.workAtLeast(market, MINIMUM_WORKING_HOURS);

		// After having worked the minimum amount, work some more and buy goods for consumption in an optimal balance.
		// Before calling the optimal trade function, we create a facade inventory that hides 80% of the money.
		// That way, we can build up some savings to smoothen fluctuations and to create new firms. In equilibrium,
		// the daily amount spent is the same, but more smooth over time.
		Inventory reducedInv = inv.hideRelative(getMoney().getGood(), 0.8);
		super.trade(reducedInv, market);
	}

	@Override
	public double consume() {
		return super.consume();
	}

	// The "static void main" method is executed when running a class
	public static void main(String[] args) {
		// Create the simulation configuration and specify which agent classes should participate
		// The simulation will create multiple instances of every class.
		FarmingConfiguration configuration = new FarmingConfiguration(AlternateFarmer.class);

		System.out.print("Creating and running the simulation...");
		// Create the simulation based on that configuration
		Simulation sim = new Simulation(configuration);
		sim.run(); // run the simulation

		System.out.println(" done.");

		// The configuration has a nice method to analyse the simulation for relevant metrics
		configuration.diagnoseResult(System.out, sim);

		System.out.println();
		System.out.println("A more advanced way of running the simulation is to start the class com.agentecon.web.SimulationServer from the Arena project.");
	}

}
