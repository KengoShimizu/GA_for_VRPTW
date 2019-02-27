import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Tournament {
	public static Population k_tournament(Genetic_algorithm pops, Load_problem load){
		pops.pool.chrom.clear();
		pops.pool.capa.clear();
		pops.pool.total_time.clear();
		pops.pool.unsearved_customers.clear();
		pops.pool.fitness.clear();
		long seed = Runtime.getRuntime().freeMemory();
		Random rnd = new Random(seed);
		for (int i=0; i<load.pop_size; i++) {
			List<Integer> x = new ArrayList<Integer>();
			List<Double> fit_temp = new ArrayList<Double>();
			for (int j=0; j<load.tourn_k; j++) {
				x.add(rnd.nextInt(pops.pop.chrom.size()));
				fit_temp.add(pops.pop.fitness.get(x.get(j)));
			}
			double minfit = fit_temp.get(0);
			for (int index = 1; index<fit_temp.size(); index ++) {
	            minfit = Math.min(minfit, fit_temp.get(index));
	        }
			if (Collections.frequency(fit_temp, minfit) != 1) {
				List<Integer> same_fitness_ind1 = new ArrayList<Integer>();
				int count_ = 0;
				for(double x1: fit_temp){	//
					if(x1 == minfit){		// selected because the same distance
						same_fitness_ind1.add(count_);
					}
					count_ += 1;
				}					// selected because of the same fitness
				int y = rnd.nextInt(same_fitness_ind1.size());
				List<List<Integer>> chrom = new ArrayList<List<Integer>>(pops.pop.chrom.get(x.get(y)));
				List<Integer> capa = new ArrayList<Integer>(pops.pop.capa.get(x.get(y)));
				List<Double> distance = new ArrayList<Double>(pops.pop.total_time.get(x.get(y)));
				List<Integer> vect_taboo = new ArrayList<Integer>(pops.pop.unsearved_customers.get(x.get(y)));
				pops.pool.chrom.add(chrom);
				pops.pool.capa.add(capa);
				pops.pool.total_time.add(distance);
				pops.pool.unsearved_customers.add(vect_taboo);
				pops.pool.fitness.add(pops.pop.fitness.get(x.get(y)));
			}
			else {
				List<List<Integer>> chrom = new ArrayList<List<Integer>>(pops.pop.chrom.get(x.get(fit_temp.indexOf(minfit))));
				List<Integer> capa = new ArrayList<Integer>(pops.pop.capa.get(x.get(fit_temp.indexOf(minfit))));
				List<Double> distance = new ArrayList<Double>(pops.pop.total_time.get(x.get(fit_temp.indexOf(minfit))));
				List<Integer> vect_taboo = new ArrayList<Integer>(pops.pop.unsearved_customers.get(x.get(fit_temp.indexOf(minfit))));
				pops.pool.chrom.add(chrom);
				pops.pool.capa.add(capa);
				pops.pool.total_time.add(distance);
				pops.pool.unsearved_customers.add(vect_taboo);
				pops.pool.fitness.add(pops.pop.fitness.get(x.get(fit_temp.indexOf(minfit))));
			}
		}
		return pops.pool;
	}
}
