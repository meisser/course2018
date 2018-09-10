package com.agentecon.finance;

import java.util.ArrayList;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;
import com.agentecon.production.IPriceProvider;
import com.agentecon.production.PriceUnknownException;
import com.agentecon.util.Numbers;

public class TradingPortfolio extends Portfolio {

	public TradingPortfolio(IStock money, boolean consumer) {
		super(money, consumer);
	}

	public double getCombinedValue(IPriceProvider prices, int timeHorizon) throws PriceUnknownException {
		return getSubstanceValue(prices) + getEarningsValue(timeHorizon);
	}

	public double getEarningsValue(int timeHorizon) {
		return getLatestDividendIncome() * timeHorizon;
	}

	public double getSubstanceValue(IPriceProvider prices) throws PriceUnknownException {
		double value = wallet.getAmount();
		for (Position p : inv.values()) {
			value += p.getAmount() * prices.getPriceBelief(p.getTicker());
		}
		return value;
	}

	public double sell(IStockMarket stocks, IAgent owner, double fraction) {
		double moneyBefore = wallet.getAmount();
		double sharesToSell = 0.0;
		for (Ticker ticker : new ArrayList<>(inv.keySet())) {
			Position pos = inv.get(ticker);
			sharesToSell += pos.getAmount() * fraction;
			double actuallySold = stocks.sell(owner, pos, wallet, sharesToSell);
			sharesToSell -= actuallySold;
			if (pos.isEmpty()) {
				disposePosition(ticker);
			}
		}
		return wallet.getAmount() - moneyBefore;
	}
	
	/**
	 * Invest according to the default strategy, weighting the chances of 
	 * choosing a stock by its market capitalization.
	 * This is similar to what an Index-ETF does.
	 */
	public double invest(IStockMarket stocks, IAgent owner, double budget) {
		return invest(new IStockPickingStrategy() {
			
			@Override
			public Ticker findStockToBuy(IStockMarket stocks) {
				return stocks.getRandomStock(true);
			}
		}, stocks, owner, budget);
	}
	
	public double invest(Ticker t, IStockMarket dsm, DefaultInvestmentFund owner, double budget) {
		return invest(new IStockPickingStrategy() {
			
			@Override
			public Ticker findStockToBuy(IStockMarket stocks) {
				return t;
			}
		}, dsm, owner, budget);
	}
	
	public double invest(IStockPickingStrategy strategy, IStockMarket stocks, IAgent owner, double budget) {
		double moneyBefore = wallet.getAmount();
		budget = Math.min(moneyBefore, budget);
		if (Numbers.isBigger(budget, 0.0)) {
			Ticker any = strategy.findStockToBuy(stocks);
			if (any != null && stocks.hasAsk(any)) {
				double before = wallet.getAmount();
				Position pos = getPosition(any);
				addPosition(stocks.buy(owner, any, pos, wallet, budget));
				double spent = before - wallet.getAmount();
				invest(strategy, stocks, owner, budget - spent);
			}
		}
		return moneyBefore - wallet.getAmount();
	}

	@Override
	public TradingPortfolio clone(IStock money) {
		return (TradingPortfolio) super.clone(money);
	}

}
