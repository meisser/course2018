# Exercise 4 - Growth

This week's exercise is about growth, savings, and death. The first part can be solved with pen and paper, and the second part is about verifying the solution in the simulation.

## Task 1: Savings Rule

When modeling growth through the birth of new agents, one consequently should also model death. Thus, we will make our agents mortal with a life-span of exactly 500 days. Overall, the population will still grow as long as the birth rate exceeds the death rate of 1/500 = 0.2%.

Furthermore, we add retirements to the model and disallow the agents to work as soon as they turn 400 days old. So if they still want to consume potatoes during retirement, they must put aside some savings while working, and spend these savings in retirement.

This task can be solved entirely with pen and paper and good reasoning. Assuming that the agents maximize total life-time utility, how much should they save every day while working? And how much should of their savings should they spend in retirement?

You should maximize total life-time utility without applying a discount rate, i.e.:

$max \sum_{i=1}^{500} u(h_{l,i}) + u(x_{p,i})$

You can assume constant prices and a constant income stream from work $w_i = w$ for as long as the agent works (i.e. for the first 400 days) as well as constant income stream from dividends $d_i = d$ that lasts all his life (disregarding the possibility of selling the farm). These assumptions remove all flexibility regarding the number of hours worked per day, reducing the problem to maximizing life-time utility from potatoe consumption $x_{p,i}$:

$max \sum_{i=1}^{500} u(x_{p,i})$ subject to the budget constraint $\sum_{i=1}^{500} p x_{p,i} = \sum_{i=1}^{400} w_i + \sum_{i=1}^{500} d_i = 400 w + 500 d = W$

So how much of his life-time wealth $W$ should a consumer spend per day? What savings heuristics does that imply? I.e. how much of his daily income should the consumer put aside while working and how much of his savings should the consumer spend once he is retired?

## Task 2: Simulation

To test your savings heuristic, you should implement the method managePortfolio of your [farmer](../src/com/agentecon/exercise4/Farmer.java).

Note that when the agents save too much money, there might not be enough money left to keep the economy going. To prevent this, the [GrowthConfiguration](../../simulation/src/com/agentecon/configuration/GrowthConfiguration.java) includes a CentralBankEvent which prints a little money whenever the price of a potatoe falls below 5.

## Deliverables and deadline

Document your findings in the [lab journal](exercise04-journal.md) and submit your version of the exercise 4 farmer to your repository. The current ranking can be found [here](http://meissereconomics.com/vis/simulation?sim=ex4-growth).

The deadline for submitting the lab journal to github is 2017-10-19 at 24:00.
