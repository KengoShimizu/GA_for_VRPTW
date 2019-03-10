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

		Genetic_algorithm ga = new Genetic_algorithm();
		ga = Genetic_algorithm.ga(problem, ga, start);

		//time end
		long end = System.currentTimeMillis();
		long e_s = end - start;

		//output as .dat file
		Output.output_minimum_transition(problem, e_s, ga);
		Output.output(problem, e_s, ga);

		System.out.print("fin");
	}
}