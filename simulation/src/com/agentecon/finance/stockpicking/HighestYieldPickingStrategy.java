package com.agentecon.finance.stockpicking;

import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Ticker;
import com.agentecon.market.Ask;

public class HighestYieldPickingStrategy implements IStockPickingStrategy {

	private boolean fundsOnly;
	
	public HighestYieldPickingStrategy() {
		this(false);
	}

	public HighestYieldPickingStrategy(boolean fundsOnly) {
		this.fundsOnly = fundsOnly;
	}

	@Override
	public Ticker findStockToBuy(IStockMarket stocks) {
		Ticker highest = null;
		double highestYield = 0.0;
		for (Ticker t : stocks.getTradedStocks()) {
			if (!fundsOnly || t.isFund()) {
				Ask ask = stocks.getAsk(t);
				if (ask != null) {
					double price = ask.getPrice().getPrice();
					double dividend = stocks.getFirmData(t).getDailyDividendPerShare();
					double yield = dividend / price;
					if (yield > highestYield) {
						highestYield = yield;
						highest = t;
					}
				}
			}
		}
		return highest;
	}

}
