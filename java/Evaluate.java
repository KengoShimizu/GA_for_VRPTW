import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Evaluate {
	public static Genetic_algorithm weighted_sum_method(Load_problem load, Genetic_algorithm ga, int gen_num){
		for (int i=0; i<ga.pop.size(); i++) ga.pop.get(i).fitness = 0.0; //initialize fitness
		int alpha = 30; //weight for the number of vehicle
		double beta = 0.1; //weight for the total distance
		
		List<Double> fitness = new ArrayList<Double>(); //put each fitness of solutions
		for (int i=0; i<load.pop_size; i++) {
			int vehi_num = ga.pop.get(i).chrom.size();
			ga.pop.get(i).fitness = alpha * vehi_num + beta * ga.pop.get(i).J;
			fitness.add(ga.pop.get(i).fitness);
		}
		List<Double> fitness_sort = new ArrayList<Double>(fitness);
		Collections.sort(fitness_sort);

		// select elite chromosoms
		int pre_elite = 999999;
		List<Integer> same_fitness = new ArrayList<Integer>();
		int counter = 0;

		long seed = Runtime.getRuntime().freeMemory();
		Random rnd = new Random(seed);

		for (int i=0; i<load.elitism; i++) {
			int elite = fitness.indexOf(fitness_sort.get(i)); //elite index
			if (pre_elite == elite) { //in case where pre_elite and elite is same
				List<Integer> same_fitness_choose = new ArrayList<Integer>();
				int count_ = 0;
				for(double x: fitness){	
					if(x == fitness_sort.get(i)){		
						same_fitness.add(count_);
					}
					count_ += 1;
				}					
				counter += 1;
				for (int j=counter; j<same_fitness.size(); j++) {
					same_fitness_choose.add(same_fitness.get(j));
				}
		        int sfc_ind = rnd.nextInt(same_fitness_choose.size());
				elite = same_fitness_choose.get(sfc_ind);
				if (counter == same_fitness.size()-1) {
					counter = 0;
				}
			}
			pre_elite = elite;
			if (same_fitness.size() > 2) {
				pre_elite = same_fitness.get(0);
			}
			
			Population elite_pop = ga.pop.get(elite).clone();
			elite_pop.init(elite_pop);
			ga.elite_pop.set(i, elite_pop);
		}
		double min = ga.pop.get(0).fitness; // discover the minimum fitness
		int best_index = 0;
		for (int index = 1; index<ga.pop.size(); index ++) {
            if (min > ga.pop.get(index).fitness) {
            	min = ga.pop.get(index).fitness;
            	best_index = index;
            }
        }
		ga.best_fit_ind = best_index;
		return ga;
	}

	public static Genetic_algorithm summed_ranks(Load_problem load, Genetic_algorithm ga, int gen_num){
		long seed = Runtime.getRuntime().freeMemory();
		Random rnd = new Random(seed);
		for (int i=0; i<ga.pop.size(); i++) ga.pop.get(i).fitness = 0.0; //initialize fitness

		List<Integer> same_fitness = new ArrayList<Integer>();
		List<Integer> vehi_num = new ArrayList<Integer>();
		List<Double> distance = new ArrayList<Double>();
		List<Integer> same_vehinum_ind = new ArrayList<Integer>();
		List<Integer> same_distance_ind = new ArrayList<Integer>();
		double distsum;
		for (int i=0; i<ga.pop.size(); i++) {
			vehi_num.add(ga.pop.get(i).chrom.size());
			distsum = 0.0;
			for (int j=0; j<ga.pop.get(i).total_time.size(); j++) {
				distsum += ga.pop.get(i).total_time.get(j);
			}
			distance.add(distsum);
		}

		int k = 1;
		int min = vehi_num.get(0); // discover the minimum numeric number
		for (int index = 1; index<vehi_num.size(); index ++) {
            min = Math.min(min, vehi_num.get(index));
        }
		int max = vehi_num.get(0); // discover the minimum numeric number
		for (int index = 1; index<vehi_num.size(); index ++) {
            max = Math.max(max, vehi_num.get(index));
        }

		for (int i=min; i<max+1; i++) {
			int count_ = 0;
			for(int x: vehi_num){
				if(x == i){
					same_vehinum_ind.add(count_);
				}
				count_ += 1;
			}
			for (int j=0; j<same_vehinum_ind.size(); j++) {
				if (same_vehinum_ind.size() != 1) {
					ga.pop.get(same_vehinum_ind.get(j)).fitness = (double) (same_vehinum_ind.size()/2 + k) / ga.pop.size();
				}
				else {
					ga.pop.get(same_vehinum_ind.get(j)).fitness = (double) k / ga.pop.size();
				}
			}
			k += same_vehinum_ind.size();
		}


		k = 1;
		int i = 0;
		double distance_sum = 0.0;
		for (int n=0; n<distance.size(); n++) {
			distance_sum += distance.get(n);
		}
		while (distance_sum != 0.0) {
			if (distance.get(i) != 0.0) {
				int count_ = 0;
				for(double x: distance){
					if(x == distance.get(i)){
						same_distance_ind.add(count_);
					}
					count_ += 1;
				}
				for (int j=0; j<same_distance_ind.size(); j++) {
					if (same_distance_ind.size() != 1) {
						ga.pop.get(same_distance_ind.get(j)).fitness = ga.pop.get(same_distance_ind.get(j)).fitness + (same_distance_ind.size()/2+ k) / ga.pop.size();
						distance.set(same_distance_ind.get(j), 0.0);
					}
					else {
						ga.pop.get(same_distance_ind.get(j)).fitness = ga.pop.get(same_distance_ind.get(j)).fitness + k / ga.pop.size();
						distance.set(same_distance_ind.get(j), 0.0);
					}
				}
				k += same_distance_ind.size();
			}
			i += 1;
			distance_sum = 0.0;
			for (int n=0; n<distance.size(); n++) {
				distance_sum += distance.get(n);
			}
		}

		List<Double> fitness = new ArrayList<Double>(); //put each fitness of solutions
		for (int i1=0; i1<load.pop_size; i1++) {
			fitness.add(ga.pop.get(i1).fitness);
		}
		List<Double> fitness_sort = new ArrayList<Double>(fitness);
		Collections.sort(fitness_sort);
		
		int pre_elite = 999999;
		List<Integer> same_fitness1 = new ArrayList<Integer>();
		int counter = 0;

		for (int i1=0; i1<load.elitism; i1++) {
			int elite = fitness.indexOf(fitness_sort.get(i1)); //nearest customer index
			if (pre_elite == elite) {
				List<Integer> same_fitness_choose = new ArrayList<Integer>();	// a case where the same index(elite) is
				int count_ = 0;
				for(double x: fitness){
					if(x == fitness_sort.get(i1)){
						same_fitness1.add(count_);
					}
					count_ += 1;
				}	// selected because of the same fitness
				counter += 1;
				for (int j=counter; j<same_fitness1.size(); j++) {
					same_fitness_choose.add(same_fitness1.get(j));								//
				}
				elite = same_fitness_choose.get(rnd.nextInt(same_fitness_choose.size()));									//
				if (counter == same_fitness.size()-1) {											//
					counter = 0;																//
				}
			}
			pre_elite = elite;
			if (same_fitness1.size() > 2) {														//
				pre_elite = same_fitness1.get(0);
			}
			Population elite_pop = ga.pop.get(elite).clone();
			elite_pop.init(elite_pop);
			ga.elite_pop.set(i, elite_pop);
		}
		return ga;
	}

	public static Genetic_algorithm pareto_ranking(Load_problem load, Genetic_algorithm ga, int gen_num){
		long seed = Runtime.getRuntime().freeMemory();
		Random rnd = new Random(seed);

		for (int i=0; i<ga.pop.size(); i++) ga.pop.get(i).fitness = 0.0; //initialize fitness
		
		/*
		//re-calculate total_time
		pops.pop.total_time.clear();
		for (int i=0; i<pops.pop.chrom.size(); i++) {
			List<Double> dist_temp = new ArrayList<Double>();
			for (int j=0; j<pops.pop.chrom.get(i).size(); j++) {
				dist_temp.add(Crossover.time_culc_eva(pops.pop.chrom.get(i).get(j), load));
			}
			List<Double> dist_temp_ = new ArrayList<Double>(dist_temp);
			pops.pop.total_time.add(dist_temp_);
		}
		*/

		List<Integer> same_fitness = new ArrayList<Integer>();
		
		List<Integer> nonassinged_rank = new ArrayList<Integer>();
		for (int i=0; i<ga.pop.size(); i++) {
			nonassinged_rank.add(i);
			ga.pop.get(i).n = ga.pop.get(i).chrom.size();
		}

		double rank = 1.0;
		int count = 0;
		
		ga.pareto_optimal_sols.clear();
		while (nonassinged_rank.size() != 0){
			Iterator<Integer> it = nonassinged_rank.iterator();
			while(it.hasNext()){
				int i = it.next();
				count = 0;
				Iterator<Integer> it1 = nonassinged_rank.iterator();
				while (it1.hasNext()) {
					int j = it1.next();
					if (ga.pop.get(i).n >= ga.pop.get(j).n && ga.pop.get(i).J > ga.pop.get(j).J && i != j) {
						count = count + 1; //dominated
						break;
					}
				}
				if (count == 0) { //the case where index i is not deminated
					ga.pop.get(i).fitness = rank;
					if (ga.pop.get(i).fitness == 1.0) {
						for (int k=0; k<ga.pareto_optimal_sols.size(); k++) {
							if (ga.pop.get(i).n == ga.pareto_optimal_sols.get(k).n &&
								ga.pop.get(i).J == ga.pareto_optimal_sols.get(k).J) {
								ga.pareto_optimal_sols.remove(k);
							}
						}
						Population pareto = ga.pop.get(i).clone();
						pareto.init(pareto);
						ga.pareto_optimal_sols.add(pareto);
					}
					it.remove(); //index that is assigned rank is eliminated from list
				}
			}
			rank = rank + 1.0;
		}

		List<Double> fitness = new ArrayList<Double>(); //put each fitness of solutions
		for (int i=0; i<load.pop_size; i++) {
			fitness.add(ga.pop.get(i).fitness);
		}
		
		List<Double> fitness_sort = new ArrayList<Double>(fitness);
		Collections.sort(fitness_sort);
		int pre_elite = 999999;
		List<Integer> same_fitness1 = new ArrayList<Integer>();
		int counter = 0;

		for (int i=0; i<load.elitism; i++) {
			int elite = fitness.indexOf(fitness_sort.get(i)); //nearest customer index
			if (pre_elite == elite) {
				List<Integer> same_fitness_choose = new ArrayList<Integer>();	// a case where the same index(elite) is
				int count_ = 0;
				for(double x: fitness){
					if(x == fitness_sort.get(i)){
						same_fitness1.add(count_);
					}
					count_ += 1;
				}	// selected because of the same fitness
				counter += 1;
				for (int j=counter; j<same_fitness1.size(); j++) {
					same_fitness_choose.add(same_fitness1.get(j));								//
				}
				elite = same_fitness_choose.get(rnd.nextInt(same_fitness_choose.size()));									//
				if (counter == same_fitness.size()-1) {											//
					counter = 0;																//
				}
			}
			pre_elite = elite;
			if (same_fitness1.size() > 2) {														//
				pre_elite = same_fitness1.get(0);
			}
			
			switch(gen_num) {
			case 0: 
				Population elite_pop = ga.pop.get(elite).clone();
				elite_pop.init(elite_pop);
				ga.elite_pop.add(elite_pop);
				break;
			
			default: 
				elite_pop = ga.pop.get(elite).clone();
				elite_pop.init(elite_pop);
				ga.elite_pop.set(i, elite_pop);
			}
		}

		return ga;
	}

}