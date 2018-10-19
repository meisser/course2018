# Exercise 5 - Stocks

In this week's exercise, you should improve the investment strategy of a single agent that is born on day 2499 of a simulation that runs for 3000 days. The population is configured to grow until day 2000, at which day the birth rate is set to 0.2%, corresponding to the mortality. This results in the population dynamics shown in the following chart, that starts following a random walk on day 2000, as the birth rate is probabilistic:

![population](images/population.jpg "Population Structure")

Given that the population stays more or less the same, one could expect more or less stable prices and production. This is indeed the case on the goods market, but not on the stock market. Dividend yields are way too high, inconsistent across firm types, and prices fluctuate wildly:

![stocks](images/stockmarket.jpg "Stock prices")

So the efficient market hypothesis clearly does not hold. How should it? So far, the only active investors in the market follow a trivial equal weight strategy, where they buy each stock with equal probability. Also, the market maker earns nice profits from their market making, but they only follow a mechanistic rule to do so, disregarding all firm fundamentals.

This is a paradise for an active investor! Simply by choosing stocks better, you can enable your agent to lead a luxurious life in comparison to all the others, that only invest passively.

## Task: Stock Picking

The provided implementation of the Investor class does the same as everyone else, namely saving as usual and invest the savings using an equal weight strategy.

Your task is to find a better [stock picking strategy](../src/com/agentecon/exercise5/StockPickingStrategy.java). The agent should still follow a buy-and-hold strategy (i.e. not selling any stocks until retirement), but should choose them a little more wisely than in the default. It might be helpful to look at the "dividend" statistics in the web interface.

## Deliverables and deadline

Document your findings in the [lab journal](exercise05-journal.md) and submit your version of the exercise 5 investor to your repository. The current ranking can be found [here](http://meissereconomics.com/vis/simulation?sim=ex5-stocks).

The deadline for submitting the lab journal to github is 2017-10-26 at 24:00.
