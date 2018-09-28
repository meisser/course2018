# Exercise 2 - The Farmer

## Introduction

Instead of farming for itself in solitude as in exercise 1, your agent is now interacting with others through a common market. In fact, there will be multiple instances of your agent class in the simulation and not just one. The resulting score is the average of these instances.

The utility and production functions are still the same as the last time, but maybe we can do better collectively?

Note that the simulation is configured such that in the efficient equilibrium, the optimal number of firms is lower than number of consumers. In fact, it is easy to see that with seven hermits working on six farms, total output is slightly higher than before:

The hermit's optimal production: $(12.75-6)^{0.6} 100^{0.2}=7.899$

Seven hermits using the same amount of man-hours on six farms: $\frac{6}{7} (\frac{7}{6} 12.75-6)^{0.6} 100^{0.2} = 7.97911$

The interesting question is, will the agents be able to coordinate on the efficient equilibrium?

Your task is twofold:

## Qualitative Theory Question

Will the firms make a profit and pay out a dividend in the efficient equilibrium?

## Experimentation

The [Farmer class](../src/com/agentecon/exercise2/Farmer.java) has been introduced. Instead of living like a Hermit only for himself, the Farmer creates a farm, to which anyone (including himself) can sell his man-hours and buy potatoes from. The farm does all the production. There are 32 farmers and the farmers do not need to sell their man-hours to their own farm. In fact, it is efficient is not every farmer creates a farm.

The behavior of the farmer is given, and so is most of the behavior of the farm. You task is to experiment with the dividend policy as well as its spending budget to buy inputs. These are calculated in the methods calculateBudget and calculateDividends. From a system dynamics point of view, the dividends control the size of the firm. For better comparability, you can test two farm variants against each other in the same simulation, 
[Farm1](../src/com/agentecon/exercise2/Farm1.java) and [Farm2](../src/com/agentecon/exercise2/Farm2.java). When creating a new farm, farmers alternatingly choose one of the two variants. Also, you can check out how your farms are doing in comparison to those of the other teams [online](http://meissereconomics.com/vis/simulation?sim=ex2-farmer).

If your farmers manage to achieve a similar utility as the hermit on its own, you are already doing very well. Also, the competitive economy might get somewhat messy in comparison to what your agents do locally when they are only among themselves.

## Formalities

Document your findings in the [lab journal](exercise02-journal.md) as you try out different ideas to make your agent behave well.

The deadline for submitting your farmer and the lab journal to github is 2017-10-04 at 24:00.
