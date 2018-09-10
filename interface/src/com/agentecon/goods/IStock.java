package com.agentecon.goods;

import com.agentecon.util.Numbers;

public interface IStock {

	/**
	 * The type of the good.
	 */
	public Good getGood();

	/**
	 * The amount that is available. For the HiddenStock class, part of the stock can be hidden.
	 */
	public double getAmount();

	/**
	 * Consumes the available stock.
	 */
	public double consume();

	public void remove(double quantity);

	public void add(double quantity);

	/**
	 * Will be protected from overnight depreciation
	 */
	public void addFreshlyProduced(double quantity);

	public default void transfer(IStock source, double amount) {
		assert source.getGood().equals(getGood());
		assert this != source : "You cannot transfer/sell " + getGood() + " to yourself";
		assert source.getAmount() - amount >= -Numbers.EPSILON;
		assert this.getAmount() + amount >= -Numbers.EPSILON;
		if (amount > 0){
			this.add(amount);
			source.remove(amount);
		} else {
			this.remove(-amount);
			source.add(-amount);
		}
		assert source.getAmount() < 1000000000;
		assert getAmount() < 1000000000;
	}

	/**
	 * Take everything that is available (i.e. not hidden) from the provided source and add it to 'this' stock.
	 */
	public void absorb(IStock source);

	/**
	 * Returns a reference to this stock with an absolute part of the amount hidden
	 */
	public default IStock hide(double amount) {
		return new HiddenStock(this, amount);
	}

	/**
	 * Returns a reference to this stock with a relative part of the amount hidden. According fractions of added reserves are also hidden.
	 */
	public default IStock hideRelative(double fraction) {
		return new RelativeHiddenStock(this, fraction);
	}

	public default ISubStock createSubAccount(double minimum, double initialFraction) {
		return new SubStock(this, minimum, getAmount() * initialFraction);
	}

	public default Quantity getQuantity() {
		return new Quantity(getGood(), getAmount());
	}

	/**
	 * Positive opposite of "isEmpty". Should be preferred as negative formulations tend to confuse more.
	 */
	public default boolean hasSome() {
		return getAmount() > Numbers.EPSILON;
	}

	/**
	 * The same as !hasSome()
	 */
	public default boolean isEmpty() {
		return !hasSome();
	}

	public void deprecate();

	public IStock duplicate();

}