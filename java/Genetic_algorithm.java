import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Genetic_algorithm {
	List<Population> pool = new ArrayList<Population>();
	List<Population> elite_pop = new ArrayList<Population>();
	List<Population> pop = new ArrayList<Population>();
	List<Population> pareto_optimal_sols = new ArrayList<Population>();
	Population child1 = new Population();
	Population child2 = new Population();
	List<Integer> min_vehinum_tracker = new ArrayList<Integer>();
	List<Double> min_time_tracker = new ArrayList<Double>();
	List<Double> min_distance_tracker = new ArrayList<Double>();
	int best_fit_ind; //used in weighted sum method
	

	public static Genetic_algorithm ga(Load_problem problem, Genetic_algorithm ga, long start) {

		//initialize population
		ga = Initial_pop.clustering_algorithm(problem, ga);
		ga = Initial_pop.nearest_route_algorithm(problem, ga);
		
		//set gen_num = 0
		int gen_num = 0;
		
		//re-calculate the distance
		for (int i=0; i<ga.pop.size(); i++) { 
			ga.pop.get(i).total_distance.clear();
			ga.pop.get(i).J = 0.0;
			for (int j=0; j<ga.pop.get(i).chrom.size(); j++) {
				ga.pop.get(i).total_distance.add(Crossover.dist_culc(ga.pop.get(i).chrom.get(j), problem));
				ga.pop.get(i).J = ga.pop.get(i).J + ga.pop.get(i).total_distance.get(j);
			}
		}
		
		//evaluate initial population
		if(problem.evaluation == 0) ga = Evaluate.weighted_sum_method(problem, ga, gen_num);
		if(problem.evaluation == 1) ga = Evaluate.summed_ranks(problem, ga, gen_num);
		if(problem.evaluation == 2) ga = Evaluate.pareto_ranking(problem, ga, gen_num);
		
		//array x contains indexes that elite_pop is supposed to be arranged
		List<Integer> x = new ArrayList<Integer>();
		Random rnd = new Random();
		for (int i=0; i<problem.elitism; i++) {
			x.add(rnd.nextInt(ga.pop.size()));
		}
		
		//tracking minimum vehicle number, total time and total distance
		int min_vehinum = ga.pop.get(0).chrom.size();
		//double min_time = ga.pop.get(0).L;
		double min_distance = ga.pop.get(0).J;
		for (int i=1; i<ga.pop.size(); i++) {
			min_vehinum = Math.min(min_vehinum, ga.pop.get(i).chrom.size());
			//min_time = Math.min(min_time, ga.pop.get(i).L);
			min_distance = Math.min(min_distance, ga.pop.get(i).J);
		}
		ga.min_vehinum_tracker.add(min_vehinum);
		//ga.min_time_tracker.add(min_time);
		ga.min_distance_tracker.add(min_distance);

		int improve_check=0;
		
		for (gen_num=1; gen_num<problem.maxgen; gen_num++ ) {
			long end = System.currentTimeMillis();
			long e_s = end - start;
			if (e_s > problem.time*1000) return ga;

			ga = Tournament.k_tournament(ga, problem, gen_num);
			ga = Elitism.elitism(ga, problem, x);
			if(problem.crossover == 0) ga = Crossover.best_cost_route_crossover(ga, problem, gen_num);
			if(problem.crossover == 1) ga = Crossover.Uniform_order_crossover(ga, problem);
			ga = Mutation.constrained_route_reversal_mutation(ga, problem);
			ga = Elitism.elitism(ga, problem, x);
			
			/*
			////////
			int count;
			for (int i=0; i<ga.pop.size(); i++) {
				count = 0;
				for (int j=0; j<ga.pop.get(i).chrom.size(); j++) {
					count = count + ga.pop.get(i).chrom.get(j).size();
				}		
				if (count != 100) System.out.println("error");
			}
			////////
			*/

			for (int i=0; i<ga.pop.size(); i++) {
				Population pool = ga.pool.get(i).clone();
				pool.init(pool);
				ga.pop.set(i, pool);
			}

			//re-calculate the distance
			for (int i=0; i<ga.pop.size(); i++) { 
				ga.pop.get(i).total_distance.clear();
				ga.pop.get(i).J = 0.0;
				for (int j=0; j<ga.pop.get(i).chrom.size(); j++) {
					ga.pop.get(i).total_distance.add(Crossover.dist_culc(ga.pop.get(i).chrom.get(j), problem));
					ga.pop.get(i).J = ga.pop.get(i).J + ga.pop.get(i).total_distance.get(j);
				}
			}

			if(problem.evaluation == 0) ga = Evaluate.weighted_sum_method(problem, ga, gen_num);
			if(problem.evaluation == 1) ga = Evaluate.summed_ranks(problem, ga, gen_num);
			if(problem.evaluation == 2) ga = Evaluate.pareto_ranking(problem, ga, gen_num);

			//tracking minimum vehicle number, total time and total distance
			min_vehinum = ga.pop.get(0).chrom.size();
			//min_time = ga.pop.L.get(0);
			min_distance = ga.pop.get(0).J;
			for (int i=1; i<ga.pop.size(); i++) {
				min_vehinum = Math.min(min_vehinum, ga.pop.get(i).chrom.size());
				//min_time = Math.min(min_time, ga.pop.L.get(i));
				min_distance = Math.min(min_distance, ga.pop.get(i).J);
			}
			ga.min_vehinum_tracker.add(min_vehinum);
			//ga.min_time_tracker.add(min_time);
			ga.min_distance_tracker.add(min_distance);
			
			if (ga.min_vehinum_tracker.get(gen_num) < ga.min_vehinum_tracker.get(gen_num-1)
				|| ga.min_distance_tracker.get(gen_num) < ga.min_distance_tracker.get(gen_num-1)) {
				//new better solution was found
				improve_check = 0;
			}
			else {
				//solution does not get better
				improve_check = improve_check + 1;
				if (improve_check >= problem.improve) {
					return ga;
				}
			}
		}
		return ga;
	}
}
