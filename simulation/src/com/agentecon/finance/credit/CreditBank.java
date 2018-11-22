package com.agentecon.finance.credit;

import java.util.ArrayList;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.finance.Firm;
import com.agentecon.firm.IBank;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.goods.IStock;
import com.agentecon.market.IMarketStatistics;

public class CreditBank extends Firm implements IBank {

	private static final double HAIRCUT = 0.1;
	private static final double INTEREST = 0.0001;

	private ArrayList<Creditor> creditors;

	public CreditBank(IAgentIdGenerator gen, Endowment end) {
		super(gen, end);
		this.creditors = new ArrayList<>();
	}

	public void manageCredit(IStockMarket market) {
		boolean marginCallsIssued = true;
		while (marginCallsIssued) {
			marginCallsIssued = false;
			updateCreditLines(market.getMarketStatistics());
			marginCallsIssued |= issueMarginCalls(market);
		}
	}

	private void updateCreditLines(IMarketStatistics stats) {
		for (Creditor account : creditors) {
			account.chargeInterestAndUpdateCreditLimit(getMoney(), stats, HAIRCUT, INTEREST);
		}
	}

	private boolean issueMarginCalls(IStockMarket market) {
		boolean callIssued = false;
		for (Creditor account : creditors) {
			callIssued |= account.issueMarginCalls(market);
		}
		return callIssued;
	}

	@Override
	public CreditAccount openCreditAccount(IAgent owner, Portfolio portfolio, IStock baseWallet) {
		Creditor creditor = new Creditor(owner, portfolio, baseWallet);
		creditors.add(creditor);
		return creditor.getAccount();
	}

	@Override
	protected double calculateDividends(int day) {
		return getMoney().getAmount() / 10;
	}

	@Override
	public double getOutstandingCredit() {
		double credit = 0;
		for (Creditor c: creditors) {
			credit += c.getAccount().getCreditUsed();
		}
		return credit;
	}

}
