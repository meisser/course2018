# Exercise 2 - The Farmer

Instead of farming for itself in solitude as in exercise 1, your agent is now interacting with others through a common market. In fact, there will be multiple instances of your agent class in the simulation and not just one. The resulting score is the average of these instances.

The utility and production functions are still the same as the last time, but maybe we can do better collectively?

Note that the simulation is configured such that in the efficient equilibrium, the optimal number of firms is lower than number of consumers. In fact, it is easy to see that with seven hermits working on six farms, total output is slightly higher than before:

The hermit's optimal production: $(12.75-6)^{0.6} 100^{0.2}=7.899$

Seven hermits using the same amount of man-hours on six farms: $\frac{6}{7} (\frac{7}{6} 12.75-6)^{0.6} 100^{0.2} = 7.97911$

The question is, will the agents be able to coordinate on such an equilibrium? Is it even an equilibrium? Is it stable? It is not clear a priori whether this is the case, as our market is not complete. I.e. we cannot trade shares yet, and we cannot trade land yet.

If you want, you can solve this problem mathematically. Alternatively, you can also just play around with your [Farmer class](../src/com/agentecon/exercise2/Farmer.java) and the [Farm](../src/com/agentecon/exercise2/Farm.java) he is creating to find out how to achieve a better utility. After having pushed your code to github, you can check out the [resulting ranking online](http://meissereconomics.com/vis/simulation?sim=ex2-farmer).

The main two variables to tune are the spendings and the dividends of the firm. These are calculated in the methods calculateBudget and calculateDividends. From a system dynamics point of view, they are control variables for the size of the firm. If your farmers manage to achieve a similar utility as the hermit on its own, you are already doing very well. Also, the competitive economy might get somewhat messy in comparison to what your agents do locally when they are only among themselves.

Document your findings in the [lab journal](exercise02-journal.md) as you try out different ideas to make your agent behave well.

The deadline for submitting your farmer and the lab journal to github is 2017-10-04 at 24:00.
