import java.util.ArrayList;
import java.util.List;

public class Elitism {
	public static Population elitism(Genetic_algorithm pops, Load_problem load, List<Integer> x){
		for (int i=0; i<load.elitism; i++) {
			//System.out.println(elite_pop.chrom);
			List<List<Integer>> chrom = new ArrayList<List<Integer>>();
			for (int n=0; n<pops.elite_pop.chrom.get(i).size(); n++) {
				List<Integer> chrom_ = new ArrayList<Integer>(pops.elite_pop.chrom.get(i).get(n));
				chrom.add(chrom_);
			}
			List<Integer> capa = new ArrayList<Integer>(pops.elite_pop.capa.get(i));
			List<Double> dist_sum = new ArrayList<Double>(pops.elite_pop.total_time.get(i));
			List<Integer> vect_taboo = new ArrayList<Integer>(pops.elite_pop.unsearved_customers.get(i));
			pops.pool.chrom.set(x.get(i), chrom);
			pops.pool.capa.set(x.get(i), capa);
			pops.pool.total_time.set(x.get(i), dist_sum);
			pops.pool.unsearved_customers.set(x.get(i), vect_taboo);
			pops.pool.fitness.set(x.get(i), pops.elite_pop.fitness.get(i));
		}
		return pops.pool;
	}
}
