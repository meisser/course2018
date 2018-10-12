# Exercise 4 - Growth

This week's exercise is about growth, savings, and death. The first part can be solved with pen and paper, and the second part is about verifying the solution in the simulation.

## Task 1: Savings Rule

When modeling growth through the birth of new agents, one consequently should also model death. Thus, we will make our agents mortal with a life-span of exactly 500 days. Overall, the population will still grow as long as the birth rate exceeds the death rate of 1/500 = 0.2%.

Furthermore, we add retirements to the model and disallow the agents to work as soon as they turn 400 days old. So if they still want to consume potatoes during retirement, they must put aside some savings while working, and spend these savings in retirement.

This task can be solved entirely with pen and paper and good reasoning. Assuming that the agents maximize total life-time utility, how much should they save every day while working? And how much should of their savings should they spend in retirement?

You should maximize total life-time utility, i.e.:

$max \sum_{i=1}^{500} u(h_{l,i}) + u(x_{p,i})$

You can assume constant prices and a constant income stream  $w_i = w$ during for as long as the agent works (i.e. for the first 400 days). This reduces the problem to maximizing utility from potatoe consumption $x_{p,i}$:

$max \sum_{i=1}^{500} u(x_{p,i})$ subject to the budget constraint $\sum_{i=1}^{500} p x_{p,i} = \sum_{i=1}^{400} w_i = 400 w$

Once you have solved this, you derive a simple yet optimal savings heuristic for workers that do not receive a dividend. In a second step, try to adjust the heuristic to incorporate dividends, i.e. adding a constant income stream that continues in retirement. Ideally, this decision rule is robust against fluctuations in income, so you cannot simply assume that total life-time income is 400 times the latest income.

## Task 2: Simulation

To test your savings heuristic, you should implement the method managePortfolio of your [farmer](../src/com/agentecon/exercise4/Farmer.java).

Note that when the agents save too much money, there might not be enough money left to keep the economy going. To prevent this, the [GrowthConfiguration](../../simulation/src/com/agentecon/configuration/GrowthConfiguration.java) includes a CentralBankEvent which prints a little money whenever the price of a potatoe falls below 5.

## Deliverables and deadline

Document your findings in the [lab journal](exercise04-journal.md) and submit your version of the exercise 4 farmer to your repository. The current ranking can be found [here](http://meissereconomics.com/vis/simulation?sim=ex4-growth).

The deadline for submitting the lab journal to github is 2017-10-19 at 24:00.
