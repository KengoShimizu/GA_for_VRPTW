import java.util.List;

public class Elitism {
	public static Genetic_algorithm elitism(Genetic_algorithm ga, Load_problem load, List<Integer> x){
		for (int i=0; i<load.elitism; i++) {
			Population pool = ga.elite_pop.get(i).clone();
			pool.init(pool);
			ga.pool.set(x.get(i), pool);
		}
		return ga;
	}
}
