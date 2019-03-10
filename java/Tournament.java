import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Tournament {
	public static Genetic_algorithm k_tournament(Genetic_algorithm ga, Load_problem load, int gen_num){
		long seed = Runtime.getRuntime().freeMemory();
		Random rnd = new Random(seed);
		
		for (int i=0; i<load.pop_size; i++) {
			List<Integer> x = new ArrayList<Integer>();
			List<Double> fitness = new ArrayList<Double>();
			for (int j=0; j<load.tourn_k; j++) {
				x.add(rnd.nextInt(ga.pop.size())); //get index of solution at random 
				fitness.add(ga.pop.get(x.get(j)).fitness);; //get fitness at this solution
			}
			double minfit = fitness.get(0);
			for (int index = 1; index<fitness.size(); index ++) {
	            minfit = Math.min(minfit, fitness.get(index)); //find the minimum fitness out of the solutions
	        }
			if (Collections.frequency(fitness, minfit) != 1) { //in case where the number of minimum fitness is not only one
				List<Integer> same_fitness_ind1 = new ArrayList<Integer>();
				int count_ = 0;
				for(double x1: fitness){
					if(x1 == minfit){
						same_fitness_ind1.add(count_);
					}
					count_ += 1;
				}
				int y = rnd.nextInt(same_fitness_ind1.size());
				switch(gen_num) {
				case 1: 
					Population pool = ga.pop.get(y).clone();
					pool.init(pool);
					ga.pool.add(pool);
					break;
				
				default: 
					pool = ga.pop.get(y).clone();
					pool.init(pool);
					ga.pool.set(i, pool);
				}
				
			}
			else {
				switch(gen_num) {
				case 1: 
					Population pool = ga.pop.get(x.get(fitness.indexOf(minfit))).clone();
					pool.init(pool);
					ga.pool.add(pool);
					break;
				
				default: 
					pool = ga.pop.get(x.get(fitness.indexOf(minfit))).clone();
					pool.init(pool);
					ga.pool.set(i, pool);
				}
				
			}
		}
		return ga;
	}
}
