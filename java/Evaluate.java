import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Evaluate {
	Population pop = new Population();
	Population elite_pop = new Population();
	List<Double> best_fit = new ArrayList<Double>();
	public static Evaluate weighted_sum_method(Load_problem load, Genetic_algorithm pops, int gen_num, List<Double> best_fit){
		Evaluate e = new Evaluate();
		pops.pop.fitness.clear();
		int alpha = 30;
		double beta = 0.1;
		for (int i=0; i<load.pop_size; i++) {
			int vehi_num = pops.pop.chrom.get(i).size();
			double dist_fit = 0;
			for (int j=0; j<pops.pop.total_time.get(i).size(); j++) {
				dist_fit += pops.pop.total_time.get(i).get(j);
			}
			pops.pop.fitness.add(alpha * vehi_num + beta * dist_fit);
		}
		e.pop = pops.pop;

		List<Double> fitness_sort = new ArrayList<Double>(pops.pop.fitness);
		Collections.sort(fitness_sort);

		// select elite chromosoms
		int pre_elite = 999999;
		List<Integer> same_fitness = new ArrayList<Integer>();
		int counter = 0;
		pops.elite_pop.chrom.clear();
		pops.elite_pop.capa.clear();
		pops.elite_pop.total_time.clear();
		pops.elite_pop.unsearved_customers.clear();
		pops.elite_pop.fitness.clear();

		long seed = Runtime.getRuntime().freeMemory();
		Random rnd = new Random(seed);

		for (int i=0; i<load.elitism; i++) {
			int elite = pops.pop.fitness.indexOf(fitness_sort.get(i)); //nearest customer index
			if (pre_elite == elite) {
				List<Integer> same_fitness_choose = new ArrayList<Integer>();// a case where the same index(elite) is
				int count_ = 0;
				for(double x: pops.pop.fitness){	//
					if(x == fitness_sort.get(i)){		// selected because the same distance
						same_fitness.add(count_);
					}
					count_ += 1;
				}					// selected because of the same fitness
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
			List<List<Integer>> chrom = new ArrayList<List<Integer>>(pops.pop.chrom.get(elite));
			List<Integer> capa = new ArrayList<Integer>(pops.pop.capa.get(elite));
			List<Double> distance = new ArrayList<Double>(pops.pop.total_time.get(elite));
			List<Integer> vect_taboo = new ArrayList<Integer>(pops.pop.unsearved_customers.get(elite));
			e.elite_pop.chrom.add(chrom);
			e.elite_pop.capa.add(capa);
			e.elite_pop.total_time.add(distance);
			e.elite_pop.unsearved_customers.add(vect_taboo);
			e.elite_pop.fitness.add(pops.pop.fitness.get(elite));
		}
		double min = pops.pop.fitness.get(0); // discover the minimum numeric number
		for (int index = 1; index<pops.pop.fitness.size(); index ++) {
            min = Math.min(min, pops.pop.fitness.get(index));
        }
		e.best_fit.add(min);
		return e;
	}

	public static Evaluate summed_ranks(Load_problem load, Genetic_algorithm pops, int gen_num, List<Double> best_fit){
		long seed = Runtime.getRuntime().freeMemory();
		Random rnd = new Random(seed);
		Evaluate e = new Evaluate();
		pops.pop.fitness.clear();
		for (int i=0; i<load.pop_size; i++) {
			pops.pop.fitness.add(0.0);
		}

		pops.elite_pop.chrom.clear();
		pops.elite_pop.capa.clear();
		pops.elite_pop.total_time.clear();
		pops.elite_pop.unsearved_customers.clear();
		pops.elite_pop.fitness.clear();

		List<Integer> same_fitness = new ArrayList<Integer>();
		List<Integer> vehi_num = new ArrayList<Integer>();
		List<Double> distance = new ArrayList<Double>();
		List<Integer> same_vehinum_ind = new ArrayList<Integer>();
		List<Integer> same_distance_ind = new ArrayList<Integer>();
		double distsum;
		for (int i=0; i<pops.pop.chrom.size(); i++) {
			vehi_num.add(pops.pop.chrom.get(i).size());
			distsum = 0.0;
			for (int j=0; j<pops.pop.total_time.get(i).size(); j++) {
				distsum += pops.pop.total_time.get(i).get(j);
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
					pops.pop.fitness.set(same_vehinum_ind.get(j), (double) (same_vehinum_ind.size()/2 + k) / pops.pop.chrom.size());
				}
				else {
					pops.pop.fitness.set(same_vehinum_ind.get(j), (double) k / pops.pop.chrom.size());
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
						pops.pop.fitness.set(same_distance_ind.get(j), pops.pop.fitness.get(same_distance_ind.get(j)) + (same_distance_ind.size()/2+ k) / pops.pop.chrom.size());
						distance.set(same_distance_ind.get(j), 0.0);
					}
					else {
						pops.pop.fitness.set(same_distance_ind.get(j), pops.pop.fitness.get(same_distance_ind.get(j)) + k / pops.pop.chrom.size());
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

		List<Double> fitness_sort = new ArrayList<Double>(pops.pop.fitness);
		Collections.sort(fitness_sort);
		int pre_elite = 999999;
		List<Integer> same_fitness1 = new ArrayList<Integer>();
		int counter = 0;

		for (int i1=0; i1<load.elitism; i1++) {
			int elite = pops.pop.fitness.indexOf(fitness_sort.get(i1)); //nearest customer index
			if (pre_elite == elite) {
				List<Integer> same_fitness_choose = new ArrayList<Integer>();	// a case where the same index(elite) is
				int count_ = 0;
				for(double x: pops.pop.fitness){
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
			List<List<Integer>> chrom = new ArrayList<List<Integer>>(pops.pop.chrom.get(elite));
			List<Integer> capa = new ArrayList<Integer>(pops.pop.capa.get(elite));
			List<Double> distance1 = new ArrayList<Double>(pops.pop.total_time.get(elite));
			List<Integer> vect_taboo = new ArrayList<Integer>(pops.pop.unsearved_customers.get(elite));
			e.elite_pop.chrom.add(chrom);
			e.elite_pop.capa.add(capa);
			e.elite_pop.total_time.add(distance1);
			e.elite_pop.unsearved_customers.add(vect_taboo);
			e.elite_pop.fitness.add(pops.pop.fitness.get(elite));
			e.pop = pops.pop;
		}
		return e;
	}

	public static Evaluate pareto_ranking(Load_problem load, Genetic_algorithm pops, int gen_num, List<Double> best_fit){
		long seed = Runtime.getRuntime().freeMemory();
		Random rnd = new Random(seed);
		Evaluate e = new Evaluate();

		//initialize fitnesses
		pops.pop.fitness.clear();
		for (int i=0; i<load.pop_size; i++) {
			pops.pop.fitness.add(0.0);
		}

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

		//re-calculate total_distance
		pops.pop.total_distance.clear();
		for (int i=0; i<pops.pop.chrom.size(); i++) {
			List<Double> dist_temp = new ArrayList<Double>();
			for (int j=0; j<pops.pop.chrom.get(i).size(); j++) {
				dist_temp.add(Crossover.dist_culc(pops.pop.chrom.get(i).get(j), load));
			}
			List<Double> dist_temp_ = new ArrayList<Double>(dist_temp);
			pops.pop.total_distance.add(dist_temp_);
		}

		//initialize elite
		pops.elite_pop.chrom.clear();
		pops.elite_pop.capa.clear();
		pops.elite_pop.total_time.clear();
		pops.elite_pop.unsearved_customers.clear();
		pops.elite_pop.fitness.clear();

		List<Integer> same_fitness = new ArrayList<Integer>();
		List<Integer> vehi_num = new ArrayList<Integer>(); //total number of vehicle

		double timesum;
		double distsum;
		pops.pop.L.clear();
		pops.pop.J.clear();
		List<Integer> nonassinged_rank = new ArrayList<Integer>();
		for (int i=0; i<pops.pop.chrom.size(); i++) {
			vehi_num.add(pops.pop.chrom.get(i).size());
			nonassinged_rank.add(i);
			timesum = 0.0;
			distsum = 0.0;
			for (int j=0; j<pops.pop.total_time.get(i).size(); j++) {
				timesum += pops.pop.total_time.get(i).get(j);
				distsum += pops.pop.total_distance.get(i).get(j);
			}
			pops.pop.L.add(timesum); //total time of each vehicle
			pops.pop.J.add(distsum); //total distance of each vehicle
		}


		double rank = 1.0;
		int count = 0;
		while (pops.pop.fitness.contains(0.0)){
			Iterator<Integer> it = nonassinged_rank.iterator();
			while(it.hasNext()){
				int i = it.next();
				count = 0;
				Iterator<Integer> it1 = nonassinged_rank.iterator();
				while (it1.hasNext()) {
					int j = it1.next();
					if (vehi_num.get(i) >= vehi_num.get(j) && pops.pop.J.get(i) >= pops.pop.J.get(j) && i != j) {
						count = count + 1; //dominated
						break;
					}
				}
				if (count == 0) { //the case where index i is not deminated
					pops.pop.fitness.set(i, rank);
					it.remove(); //index that is assigned rank is eliminated from list
				}
			}
			rank = rank + 1.0;
		}

		List<Double> fitness_sort = new ArrayList<Double>(pops.pop.fitness);
		Collections.sort(fitness_sort);
		int pre_elite = 999999;
		List<Integer> same_fitness1 = new ArrayList<Integer>();
		int counter = 0;

		for (int i=0; i<load.elitism; i++) {
			int elite = pops.pop.fitness.indexOf(fitness_sort.get(i)); //nearest customer index
			if (pre_elite == elite) {
				List<Integer> same_fitness_choose = new ArrayList<Integer>();	// a case where the same index(elite) is
				int count_ = 0;
				for(double x: pops.pop.fitness){
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

			List<List<Integer>> chrom = new ArrayList<List<Integer>>(pops.pop.chrom.get(elite));
			List<Integer> capa = new ArrayList<Integer>(pops.pop.capa.get(elite));
			List<Double> distance1 = new ArrayList<Double>(pops.pop.total_time.get(elite));
			List<Integer> vect_taboo = new ArrayList<Integer>(pops.pop.unsearved_customers.get(elite));
			e.elite_pop.chrom.add(chrom);
			e.elite_pop.capa.add(capa);
			e.elite_pop.total_time.add(distance1);
			e.elite_pop.unsearved_customers.add(vect_taboo);
			e.elite_pop.fitness.add(pops.pop.fitness.get(elite));
			e.pop = pops.pop;
		}

		return e;
	}

}