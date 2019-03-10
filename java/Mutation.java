import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Mutation {
	int j;
	double distance_temp;
	public static Mutation dist_check(List<Integer> vehi, Load_problem load) {
		Mutation m = new Mutation();
		m.distance_temp = 0.0;
		for (m.j=0; m.j<vehi.size()+1; m.j++) {
			if (m.j == 0) {
				m.distance_temp += load.dist.get(vehi.get(0)).get(0);
			}
			else if (m.j == vehi.size()) {
				m.distance_temp += load.dist.get(0).get(vehi.get(m.j-1));
				if (m.distance_temp > load.customer.get(0).due_time) { //violate the constrain
					m.distance_temp = 0.0;
					m.j -= 1;
					return m;
				}
				else { //meet the constrain
					m.j = 0;
					return m;
				}
			}
			else {
				m.distance_temp += load.dist.get(vehi.get(m.j)).get(vehi.get(m.j-1));
			}

			if (m.j < vehi.size()) {
				// a case where the vehicle can arrive within time window
				if (m.distance_temp >= load.customer.get(vehi.get(m.j)).ready_time && m.distance_temp <= load.customer.get(vehi.get(m.j)).due_time) {
					m.distance_temp += load.customer.get(vehi.get(m.j)).service_time; // add the load.customer's service time
				}
				// a case where the vehicle arrive before the time window
				else if (m.distance_temp < load.customer.get(vehi.get(m.j)).ready_time) { // the vehicle has to wait till ready time
					m.distance_temp = load.customer.get(vehi.get(m.j)).ready_time + load.customer.get(vehi.get(m.j)).service_time; // add the load.customer's service time
				}
				// a case where the vehicle arrive late for the time window
				else {
					m.distance_temp = 0.0;
					return m;
				}
			}
		}
	return m;
	}

	public static Genetic_algorithm constrained_route_reversal_mutation(Genetic_algorithm ga, Load_problem load) {
		long seed = Runtime.getRuntime().freeMemory();
		Random rnd = new Random(seed);
		double d;
		Mutation mm = new Mutation();
		for (int i=0; i<ga.pool.size(); i++) {
			d = Math.random();
			if (d<load.pm) {  //Crossover occers
				List<Integer> vehi = new ArrayList<Integer>(ga.pool.get(i).chrom.get(rnd.nextInt(ga.pool.get(i).chrom.size())));
				int vehi_ind = ga.pool.get(i).chrom.indexOf(vehi);
				if (vehi.size() == 1) {
					break;
				}
				else if (vehi.size() == 2) {
					//switch the oder between index 0 and 1
					int vehi_0 = vehi.get(0);
					vehi.set(0, vehi.get(1));
					vehi.set(1, vehi_0);
					
					double distance_temp = 0.0;
					mm = dist_check(vehi, load);
					distance_temp = mm.distance_temp;
					int j = mm.j;
					if (distance_temp == 0.0) { //violate the constrain
						//a new route is created using customer who violate constrain
						ga.pool.get(i).capa.set(vehi_ind, ga.pool.get(i).capa.get(vehi_ind) - load.customer.get(vehi.get(j)).demand);
						List<Integer> vehi_ = new ArrayList<Integer>(Arrays.asList(vehi.get(j)));
						ga.pool.get(i).chrom.add(vehi_);
						ga.pool.get(i).capa.add(load.customer.get(vehi.get(j)).demand);
						vehi.remove(vehi.get(j));
							
						mm = dist_check(ga.pool.get(i).chrom.get(ga.pool.get(i).chrom.size()-1), load);
						distance_temp = mm.distance_temp;
						j = mm.j;
						ga.pool.get(i).total_time.add(distance_temp);
					}
					else { //meet the constrain
						ga.pool.get(i).total_time.set(vehi_ind, distance_temp);
						List<Integer> vehi_ = new ArrayList<Integer>(vehi);
						ga.pool.get(i).chrom.set(vehi_ind, vehi_);
					}
				}

				else {
					int first_ind = rnd.nextInt(vehi.size()-1);
					int second_ind = first_ind + rnd.nextInt(2)+1;
					if (first_ind == vehi.size() - 2) { //the case where first_ind is the second last customer
						second_ind = first_ind + 1;
					}
					
					//switch the oder between index first_ind and second_ind
					int vehi_f = vehi.get(first_ind);
					vehi.set(first_ind, vehi.get(second_ind));
					vehi.set(second_ind, vehi_f);
					
					double distance_temp = 0.0;
					int count = 0;
					while (distance_temp == 0.0) {
						mm = dist_check(vehi, load);
						distance_temp = mm.distance_temp;
						int j = mm.j;
						if (distance_temp == 0.0) {//violate the constrain
							//a new route is created using customer who violate constrain
							ga.pool.get(i).capa.set(vehi_ind, ga.pool.get(i).capa.get(vehi_ind) - load.customer.get(vehi.get(j)).demand);
							if (count == 0) {
								List<Integer> vehi_1 = new ArrayList<Integer>(Arrays.asList(vehi.get(j)));
								ga.pool.get(i).chrom.add(vehi_1);
								ga.pool.get(i).capa.add(load.customer.get(vehi.get(j)).demand);
								vehi.remove(vehi.get(j));
								
								mm = dist_check(ga.pool.get(i).chrom.get(ga.pool.get(i).chrom.size()-1), load);
								ga.pool.get(i).total_time.add(mm.distance_temp);
								count += 1;
							}
							else if (count == 1) {
								List<Integer> vehi_1 = new ArrayList<Integer>(Arrays.asList(vehi.get(j)));
								ga.pool.get(i).chrom.add(vehi_1);
								ga.pool.get(i).capa.add(load.customer.get(vehi.get(j)).demand);
								vehi.remove(vehi.get(j));
								
								mm = dist_check(ga.pool.get(i).chrom.get(ga.pool.get(i).chrom.size()-1), load);
								ga.pool.get(i).total_time.add(mm.distance_temp);
								break;
							}
							else {
								System.out.println("error1");
							}
						}
						else { //meet the constrain
							ga.pool.get(i).total_time.set(vehi_ind, distance_temp);
							List<Integer> vehi_1 = new ArrayList<Integer>(vehi);
							ga.pool.get(i).chrom.set(vehi_ind, vehi_1);
						}
					}
				}
			}
		}
		return ga;
	}
}
	

