/*
 * how to use
 * benchmark parameter iteration_number crossover evaluation
 *
 * crossover: 0=BCRC 1=UOX
 * evaluation: 0=WSM 1=SR 2=PR
 */

public class Main{
	public static void main(String[] args) {

		//load problem and parameters
		Load_problem problem = new Load_problem();
		problem = Load_problem.load_problem(args);

		//time start
		long start = System.currentTimeMillis();

		Genetic_algorithm pops = new Genetic_algorithm();
		pops = Genetic_algorithm.ga(problem);

		//time end
		long end = System.currentTimeMillis();
		long e_s = end - start;

		//output as .dat file
		if(problem.evaluation == 0 || problem.evaluation == 1 || problem.evaluation == 2) {
			Output.output_minimum_transition(problem, e_s, pops);
			Output.output(problem, e_s, pops);
		}

		System.out.print("fin");
	}
}