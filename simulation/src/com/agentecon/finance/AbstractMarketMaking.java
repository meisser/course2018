package com.agentecon.finance;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.Position;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;
import com.agentecon.market.IOffer;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.Price;

public abstract class AbstractMarketMaking {

	private IStock shares;
	private IStock wallet;

	public AbstractMarketMaking(IStock wallet, IStock shares) {
		this.wallet = wallet;
		this.shares = shares;
	}
	
	public Good getTicker() {
		return shares.getGood();
	}

	protected IStock getPosition() {
		return shares;
	}

	protected IStock getWallet() {
		return wallet;
	}

	public abstract void trade(IPriceMakerMarket dsm, IAgent owner);

	public abstract double getBid();

	public abstract double getAsk();

	protected IOffer placeBid(IPriceMakerMarket dsm, IAgent owner, double sharesToBuy) {
		return placeBid(dsm, owner, sharesToBuy, getBid());
	}

	protected IOffer placeBid(IPriceMakerMarket dsm, IAgent owner, double sharesToBuy, double price) {
		if (sharesToBuy > 0.0) {
			Bid bid = createBid(owner, sharesToBuy, price);
			dsm.offer(bid);
			return bid;
		} else {
			return null;
		}
	}

	protected Bid createBid(IAgent owner, double sharesToBuy, double price) {
		if (shares instanceof Position) {
			return new BidFin(owner, wallet, (Position) shares, new Price(shares.getGood(), price), sharesToBuy);
		} else {
			return new Bid(owner, wallet, shares, price, sharesToBuy);
		}
	}

	protected IOffer placeAsk(IPriceMakerMarket dsm, IAgent owner, double sharesToOffer) {
		return placeAsk(dsm, owner, sharesToOffer, getAsk());
	}

	protected IOffer placeAsk(IPriceMakerMarket dsm, IAgent owner, double sharesToOffer, double price) {
		if (sharesToOffer > 0.0) {
			Ask ask = createAsk(owner, sharesToOffer, price);
			dsm.offer(ask);
			return ask;
		} else {
			return null;
		}
	}

	protected Ask createAsk(IAgent owner, double sharesToOffer, double price) {
		if (shares instanceof Position) {
			return new AskFin(owner, wallet, (Position) shares, new Price(shares.getGood(), price), sharesToOffer);
		} else {
			return new Ask(owner, wallet, shares, new Price(shares.getGood(), price), sharesToOffer);
		}
	}

	public double getPrice() {
		return (getBid() + getAsk()) / 2;
	}

	public double getSpread() {
		return getAsk() - getBid();
	}

	@Override
	public String toString() {
		return getBid() + " to " + getAsk();
	}

}