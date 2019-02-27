import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Genetic_algorithm {
	Population pool = new Population();
	Population elite_pop = new Population();
	Population pop = new Population();
	Population_child child1 = new Population_child();
	Population_child child2 = new Population_child();
	List<Integer> min_vehinum_tracker = new ArrayList<Integer>();
	List<Double> min_time_tracker = new ArrayList<Double>();
	List<Double> min_distance_tracker = new ArrayList<Double>();

	public static Genetic_algorithm ga(Load_problem problem) {
		Genetic_algorithm pops = new Genetic_algorithm();

		//initialize population
		pops.pop = Initial_pop.clustering_algorithm(problem, pops.pop);
		pops.pop = Initial_pop.nearest_route_algorithm(problem, pops.pop);
		
		//set gen_num = 0
		int gen_num = 0;
		List<Double> best_fit = new ArrayList<Double>();
		Evaluate eva = new Evaluate();
		
		//evaluate initial population
		if(problem.evaluation == 0) eva = Evaluate.weighted_sum_method(problem, pops, gen_num, best_fit);
		if(problem.evaluation == 1) eva = Evaluate.summed_ranks(problem, pops, gen_num, best_fit);
		if(problem.evaluation == 2) eva = Evaluate.pareto_ranking(problem, pops, gen_num, best_fit);
		
		//pop and elite_pop are loaded from evaluation method
		pops.pop = eva.pop;
		pops.elite_pop = eva.elite_pop;
		
		//List<Double> best_fit1 = new ArrayList<Double>();
		//best_fit1.add(eva.best_fit.get(0));
		
		//array x contains indexes that elite_pop is supposed to be arranged
		List<Integer> x = new ArrayList<Integer>();
		Random rnd = new Random();
		for (int i=0; i<problem.elitism; i++) {
			x.add(rnd.nextInt(pops.pop.chrom.size()));
		}
		
		//tracking minimum vehicle number, total time and total distance
		int min_vehinum = pops.pop.chrom.get(0).size();
		double min_time = pops.pop.L.get(0);
		double min_distance = pops.pop.J.get(0);
		for (int i=1; i<pops.pop.chrom.size(); i++) {
			min_vehinum = Math.min(min_vehinum, pops.pop.chrom.get(i).size());
			min_time = Math.min(min_time, pops.pop.L.get(i));
			min_distance = Math.min(min_distance, pops.pop.J.get(i));
		}
		pops.min_vehinum_tracker.add(min_vehinum);
		pops.min_time_tracker.add(min_time);
		pops.min_distance_tracker.add(min_distance);

		for (gen_num=1; gen_num<problem.maxgen; gen_num++ ) {

			pops.pool = Tournament.k_tournament(pops, problem);
			pops.pool = Elitism.elitism(pops, problem, x);
			if(problem.crossover == 0) pops.pool = Crossover.best_cost_route_crossover(pops, problem);
			if(problem.crossover == 1) pops.pool = Crossover.Uniform_order_crossover(pops, problem);
			pops.pool = Mutation.constrained_route_reversal_mutation(pops.pool, problem);
			pops.pool = Elitism.elitism(pops, problem, x);

			List<List<List<Integer>>> chrom = new ArrayList<List<List<Integer>>>();
			for (int n=0; n<pops.pool.chrom.size(); n++) {
				List<List<Integer>> chrom_1 = new ArrayList<List<Integer>>();
				for (int m=0; m<pops.pool.chrom.get(n).size(); m++) {
					List<Integer> chrom_2 = new ArrayList<Integer>(pops.pool.chrom.get(n).get(m));
					chrom_1.add(chrom_2);
				}
				chrom.add(chrom_1);
			}
			List<List<Integer>> capa = new ArrayList<List<Integer>>();
			for (int n=0; n<pops.pool.chrom.size(); n++) {
				List<Integer> capa_ = new ArrayList<Integer>(pops.pool.capa.get(n));
				capa.add(capa_);
			}
			List<List<Double>> distance1 = new ArrayList<List<Double>>();
			for (int n=0; n<pops.pool.total_time.size(); n++) {
				List<Double> distance_ = new ArrayList<Double>(pops.pool.total_time.get(n));
				distance1.add(distance_);
			}
			List<List<Integer>> vect_taboo = new ArrayList<List<Integer>>();
			for (int n=0; n<pops.pool.unsearved_customers.size(); n++) {
				List<Integer> vect_taboo_ = new ArrayList<Integer>(pops.pool.unsearved_customers.get(n));
				vect_taboo.add(vect_taboo_);
			}
			pops.pop.chrom = chrom;
			pops.pop.capa = capa;
			pops.pop.total_time = distance1;
			pops.pop.unsearved_customers = vect_taboo;

			pops.pool.chrom.clear();
			pops.pool.capa.clear();
			pops.pool.total_time.clear();
			pops.pool.unsearved_customers.clear();

			pops.pop.total_time.clear();
			for (int i=0; i<pops.pop.chrom.size(); i++) {
				List<Double> dist_temp = new ArrayList<Double>();
				for (int j=0; j<pops.pop.chrom.get(i).size(); j++) {
					dist_temp.add(Crossover.dist_culc(pops.pop.chrom.get(i).get(j), problem));
				}
				List<Double> dist_temp_ = new ArrayList<Double>(dist_temp);
				pops.pop.total_time.add(dist_temp_);
			}

			if(problem.evaluation == 0) eva = Evaluate.weighted_sum_method(problem, pops, gen_num, best_fit);
			if(problem.evaluation == 1) eva = Evaluate.summed_ranks(problem, pops, gen_num, best_fit);
			if(problem.evaluation == 2) eva = Evaluate.pareto_ranking(problem, pops, gen_num, best_fit);
			pops.pop = eva.pop;
			pops.elite_pop = eva.elite_pop;
			//best_fit1.add(eva.best_fit.get(0));

			//tracking minimum vehicle number, total time and total distance
			min_vehinum = pops.pop.chrom.get(0).size();
			min_time = pops.pop.L.get(0);
			min_distance = pops.pop.J.get(0);
			for (int i=1; i<pops.pop.chrom.size(); i++) {
				min_vehinum = Math.min(min_vehinum, pops.pop.chrom.get(i).size());
				min_time = Math.min(min_time, pops.pop.L.get(i));
				min_distance = Math.min(min_distance, pops.pop.J.get(i));
			}
			pops.min_vehinum_tracker.add(min_vehinum);
			pops.min_time_tracker.add(min_time);
			pops.min_distance_tracker.add(min_distance);


		}
		if (problem.elitism == 0) {
			double min = pops.pop.fitness.get(0); // discover the minimum numeric number
			for (int index = 1; index<pops.pop.fitness.size(); index ++) {
	            min = Math.min(min, pops.pop.fitness.get(index));
	        }
			int index = pops.pop.fitness.indexOf(min);
			pops.elite_pop.chrom.add(pops.pop.chrom.get(index));
			pops.elite_pop.capa.add(pops.pop.capa.get(index));
			pops.elite_pop.total_time.add(pops.pop.total_time.get(index));
			pops.elite_pop.unsearved_customers.add(pops.pop.unsearved_customers.get(index));
			pops.elite_pop.fitness.add(pops.pop.fitness.get(index));
		}
		return pops;
	}
}
