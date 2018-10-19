package com.agentecon.exercise5;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import com.agentecon.finance.stockpicking.IStockPickingStrategy;
import com.agentecon.firm.FirmFinancials;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.market.Ask;
import com.agentecon.util.IAverage;

/**
 * A stock picking strategy to choose the stocks that should be bought.
 */
public class StockPickingStrategy implements IStockPickingStrategy {

	private Random random; // a random number generator, useful to make a random choice
	private Portfolio portfolio;
	
	private HashMap<Ticker, IAverage> averageDividend;

	public StockPickingStrategy(Random random, Portfolio portfolio) {
		this.portfolio = portfolio;
		this.random = random;
	}

	/**
	 * Select a stock to buy.
	 */
	@Override
	public Ticker findStockToBuy(IStockMarket stocks) {
		Collection<Ticker> listedStocks = stocks.getTradedStocks(); // a list of traded stocks
		Ticker aFarm = selectRandomFarm(stocks); // a random farm
		if (aFarm != null) {
			FirmFinancials financialData = stocks.getFirmData(aFarm);
			double dividendYield = financialData.getDailyDividendPerShare() / financialData.getSharePrice();
			Ask ask = stocks.getAsk(aFarm); // the lowest ask for that particular firm in the orderbook
			Position existingPosition = portfolio.getPosition(aFarm);
			boolean weAlreadyOwnSomeOfThatStock = existingPosition != null;
			if (weAlreadyOwnSomeOfThatStock) {
				double numberOfSharesWeOwn = existingPosition.getAmount();
			}
		}
		return stocks.getRandomStock(false); // the strategy everyone else is following
	}

	protected Ticker selectRandomFarm(IStockMarket stocks) {
		Collection<Ticker> listedStocks = stocks.getTradedStocks(); 
		ArrayList<Ticker> farms = new ArrayList<>();
		for (Ticker t: listedStocks) {
			if (t.getType().endsWith("Farm") && stocks.hasAsk(t)){
				farms.add(t);
			}
		}
		if (farms.isEmpty()) {
			return null;
		} else {
			return farms.get(random.nextInt(farms.size()));
		}
	}

}
