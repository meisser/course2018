package com.agentecon.finance;

import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Ticker;

public interface IStockPickingStrategy {

	public Ticker findStockToBuy(IStockMarket stocks);

}
