/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.finance;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.IStock;
import com.agentecon.learning.ConstantFactorBelief;
import com.agentecon.learning.IBelief;
import com.agentecon.market.IOffer;
import com.agentecon.market.IPriceMakerMarket;

/**
 * Exercise 8: implement this class such that it passes as many tests as possible.
 * 
 * To run the test, right-click on "MarketMakingTest" and choose "Debug as.." "JUnit Test".
 * 
 * Feel free to get inspired by my (futile) attempts, MarketMakingOldVersion and MarketMakingWithSpreadBelief.
 */
public class MarketMaking extends AbstractMarketMaking {

	private static final double SPENDING_FRACTION = 0.2;

	private static final double MAX_PRICE = 100000;

	private IBelief bidSizeInShares;
	private IBelief askSizeInMoney;

	private IOffer prevBid, prevAsk;

	public MarketMaking(IStock wallet, IStock shares, double initialPrice, double targetInventoryInNumberOfShares) {
		super(wallet, shares);

		this.bidSizeInShares = new ConstantFactorBelief(targetInventoryInNumberOfShares, 0.03);
		this.askSizeInMoney = new ConstantFactorBelief(initialPrice * targetInventoryInNumberOfShares, 0.03);
	}
	
	@Override
	public void trade(IPriceMakerMarket market, IAgent owner) {
		if (prevAsk != null) {
			adjustAskSize(prevAsk.isUsed());
		}
		if (prevBid != null) {
			adjustBidSize(prevBid.isUsed());
		}
		ensurePositiveSpread();
		double bid = getBid(); // use the value before placing the ask, as the ask might lead to a change
		prevAsk = super.placeAsk(market, owner, getPosition().getAmount() * SPENDING_FRACTION, getAsk());
		prevBid = super.placeBid(market, owner, bidSizeInShares.getValue(), bid);
	}

	protected void adjustAskSize(boolean upwards) {
		askSizeInMoney.adapt(upwards);
	}

	protected void adjustBidSize(boolean upwards) {
		bidSizeInShares.adaptWithFloor(upwards, 0.01);
	}

	private void ensurePositiveSpread() {
		double bid = getBid();
		double ask = getAsk();
		while (bid > ask) {
			adjustAskSize(true);
			ask = getAsk();
			bid = getBid();
			increaseSpreadSomeMore();
			ask = getAsk();
			bid = getBid();
			adjustBidSize(true);
			ask = getAsk();
			bid = getBid();
		}
	}

	protected void increaseSpreadSomeMore() {
		bidSizeInShares.adapt(true);
	}

	@Override
	public double getBid() {
		return getWallet().getAmount() * SPENDING_FRACTION / bidSizeInShares.getValue();
	}

	@Override
	public double getAsk() {
		IStock pos = getPosition();
		if (pos.isEmpty()) {
			return MAX_PRICE;
		} else {
			return askSizeInMoney.getValue() / getPosition().getAmount() * SPENDING_FRACTION;
		}
	}

}
