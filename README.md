# GA_for_VRPTW
This code is written in JAVA, and this Genetic Algorithm (GA) method is written for solving VRPTW.  
To solve VRPTW, this code loads a txt file from [Solomon benchmark](https://www.sintef.no/projectweb/top/vrptw/solomon-benchmark/).

&emsp; Procedure of this GA (**Genetic_algorithm.java**):  
&emsp; &emsp; Create initial population (**initial_pop.java**)  
&emsp; &emsp; Evaluate initial population (**Evaluate.java**)  
&emsp; &emsp; loop until max_generation{  
&emsp; &emsp; &emsp; Tournament selection (**Tournament.java**)  
&emsp; &emsp; &emsp; Elitism (**Elitism.java**)  
&emsp; &emsp; &emsp; Crossover (**Crossover.java**)  
&emsp; &emsp; &emsp; Mutation (**Mutation.java**)  
&emsp; &emsp; &emsp; Evaluate population (**Evaluate.java**)  
&emsp; &emsp; }  
  

**initial_pop.java**  
- Clustering algorithm (create semifeasible solutions which don't meet time window constrain)  
- Nearest route algorithm (create feasible solution from clustering algorithm)  
  
**Evaluate.java**  
- Weightes sum method  
- Summed rank method  
- Pareto ranking method  
  
**Tournament.java**  
- K-tournament method  
  
**Elitism.java**  
- Elitism  

**Crossover.java**  
- Best cost route crossover algorithm  
- Uniformed order with bit mask  
  
**Mutation.java**  
- Constrained route reversal mutation  
