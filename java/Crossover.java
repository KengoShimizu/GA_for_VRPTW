import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Crossover {
	public static Double dist_culc(List<Integer> vehi, Load_problem problem) {
		double distance_temp = 0.0;
		for (int j=0; j<vehi.size()+1; j++) {
			if (j == 0) {
				distance_temp += problem.dist.get(vehi.get(0)).get(0);
			}
			else if (j == vehi.size()) {
				distance_temp += problem.dist.get(0).get(vehi.get(j-1));
			}
			else {
				distance_temp += problem.dist.get(vehi.get(j)).get(vehi.get(j-1));
			}
		}
		return distance_temp;
	}

	public static Double time_culc(List<Integer> vehi, Load_problem problem) {
		double time_temp = 0.0;
		for (int j=0; j<vehi.size()+1; j++) {
			if (j == 0) {
				time_temp += problem.dist.get(vehi.get(j)).get(0);
			}
			else if (j == vehi.size()) {
				time_temp += problem.dist.get(0).get(vehi.get(j-1));
			}
			else {
				time_temp += problem.dist.get(vehi.get(j)).get(vehi.get(j-1));
			}

			if (j < vehi.size()) {
				// a case where the vehicle can arrive within time window
				if (time_temp >= problem.customer.get(vehi.get(j)).ready_time && time_temp <= problem.customer.get(vehi.get(j)).due_time) {
					time_temp = time_temp + problem.customer.get(vehi.get(j)).service_time; // add the customer's service time
				}
				// a case where the vehicle arrive before the time window
				else if (time_temp < problem.customer.get(vehi.get(j)).ready_time){
					time_temp = problem.customer.get(vehi.get(j)).ready_time; // the vehicle has to wait till ready time
					time_temp = time_temp + problem.customer.get(vehi.get(j)).service_time; // add the customer's service time
				}
				else if (time_temp > problem.customer.get(vehi.get(j)).due_time) return time_temp = 0.0;
			}
		}
		return time_temp;
	}

	public static Double time_culc_eva(List<Integer> vehi, Load_problem problem) {
		double time_temp = 0.0;
		for (int j=0; j<vehi.size()+1; j++) {
			if (j == 0) {
				time_temp += problem.dist.get(vehi.get(j)).get(0);
			}
			else if (j == vehi.size()) {
				time_temp += problem.dist.get(0).get(vehi.get(j-1));
			}
			else {
				time_temp += problem.dist.get(vehi.get(j)).get(vehi.get(j-1));
			}

			if (j < vehi.size()) {
				// a case where the vehicle can arrive within time window
				if (time_temp >= problem.customer.get(vehi.get(j)).ready_time && time_temp <= problem.customer.get(vehi.get(j)).due_time) {
					time_temp = time_temp + problem.customer.get(vehi.get(j)).service_time; // add the customer's service time
				}
				// a case where the vehicle arrive before the time window
				else if (time_temp < problem.customer.get(vehi.get(j)).ready_time){
					time_temp = problem.customer.get(vehi.get(j)).ready_time; // the vehicle has to wait till ready time
					time_temp = time_temp + problem.customer.get(vehi.get(j)).service_time; // add the customer's service time
				}
				else if (time_temp > problem.customer.get(vehi.get(j)).due_time) System.out.println("error");;
			}
		}
		return time_temp;
	}


	public static Genetic_algorithm best_cost_route_crossover(Genetic_algorithm ga, Load_problem problem, int gen_num) {
		long seed = Runtime.getRuntime().freeMemory();
		Random rnd = new Random(seed);
		double d;

		for (int i=0; i<ga.pool.size()/2; i++) {
			d = Math.random();
			if (d<problem.pc) {  //Crossover occers
				// create child by crossover
				int i2 = ga.pool.size()/2+i;
				Population c1 = ga.pool.get(i).clone();
				c1.init(c1);
				ga.child1 = c1;
				Population c2 = ga.pool.get(i2).clone();
				c2.init(c2);
				ga.child2 = c2;

				// select a route to occur crossover
				List<Integer> selected_route_1 = new ArrayList<Integer>(ga.child1.chrom.get(rnd.nextInt(ga.child1.chrom.size())));
				List<Integer> selected_route_2 = new ArrayList<Integer>(ga.child2.chrom.get(rnd.nextInt(ga.child2.chrom.size())));

				for (int j=0; j<selected_route_2.size(); j++) {
					for (int k=0; k<ga.child1.chrom.size(); k++) {
						if (ga.child1.chrom.get(k).contains(selected_route_2.get(j))) {
							ga.child1.chrom.get(k).remove((Integer)selected_route_2.get(j)); // remove a customer who is in the selected route
							ga.child1.unsearved_customers.add(selected_route_2.get(j));
							ga.child1.capa.set(k, ga.child1.capa.get(k) - problem.customer.get(selected_route_2.get(j)).demand); // remove a customer's demand which is in the selected route
							for (int l=0; l<ga.child1.chrom.size(); l++) {
								if (ga.child1.chrom.get(l).isEmpty()) { // remove a empty vehicle
									ga.child1.capa.remove(l);
									ga.child1.total_distance.remove(l);
									ga.child1.total_time.remove(l);
									ga.child1.chrom.remove(l);
									break;
								}
							}
							break;
						}
					}
				}

				for (int j=0; j<selected_route_1.size(); j++) {
					for (int k=0; k<ga.child2.chrom.size(); k++) {
						if (ga.child2.chrom.get(k).contains(selected_route_1.get(j))) {
							ga.child2.chrom.get(k).remove((Integer)selected_route_1.get(j)); // remove a customer who is in the selected route
							ga.child2.unsearved_customers.add(selected_route_1.get(j));
							ga.child2.capa.set(k, ga.child2.capa.get(k) - problem.customer.get(selected_route_1.get(j)).demand); // remove a customer's demand which is in the selected route
							for (int l=0; l<ga.child2.chrom.size(); l++) {
								if (ga.child2.chrom.get(l).isEmpty()) {
									ga.child2.capa.remove(l); // remove a empty vehicle
									ga.child2.total_distance.remove(l);
									ga.child2.total_time.remove(l);
									ga.child2.chrom.remove(l); // remove a empty vehicle
									break;
								}
							}
							break;
						}
					}
				}


				while (ga.child1.unsearved_customers.size() != 0){ // roop until all customer who are in selected route are assigned
					int selected_cus_2 = ga.child1.unsearved_customers.get(rnd.nextInt(ga.child1.unsearved_customers.size())); // select a customer in the selected route
					ga.child1.unsearved_customers.remove((Integer)selected_cus_2); // remove a selected customer in the selected route
					for (int vehi=0; vehi<ga.child1.chrom.size(); vehi++) {
						List<Double> distance_choose_1 = new ArrayList<Double>();
						for (int insert=0; insert<ga.child1.chrom.get(vehi).size()+1; insert++) {
							List<Integer> insert_route = new ArrayList<Integer>(ga.child1.chrom.get(vehi));
							insert_route.add(insert,selected_cus_2);
							distance_choose_1.add(time_culc(insert_route, problem));
						}
						List<Double> distance_choose_1_temp = new ArrayList<Double>(distance_choose_1);
						while (distance_choose_1_temp.contains(0.0)) {
							distance_choose_1_temp.remove(0.0);
						}
						if (distance_choose_1_temp.size() != 0) { // if there are some feasible route, len(distance_choose_1_temp) != 0
							ga.child1.capa.set(vehi, ga.child1.capa.get(vehi) + problem.customer.get(selected_cus_2).demand); // add the inserted customer's demand
							if (ga.child1.capa.get(vehi) <= problem.max_capacity) { // if this capacity meet the constrain
								double min = distance_choose_1_temp.get(0);
								for (int index = 1; index<distance_choose_1_temp.size(); index ++) {
						            min = Math.min(min, distance_choose_1_temp.get(index));
						        }
								int insert_ind;
								if (Collections.frequency(distance_choose_1_temp, min) != 1) {
									int count_ = 0;
									List<Integer> same_insert_ind = new ArrayList<Integer>();								// a case where the same index(near_ind) is
									for(double x: distance_choose_1){ //変更した
										if(x == min){
											same_insert_ind.add(count_);
										}
										count_ += 1;
									}
									insert_ind = same_insert_ind.get(rnd.nextInt(same_insert_ind.size()));
								}
								else {
									insert_ind = distance_choose_1.indexOf(min); // choose the shortest route in the distance_choose_1_temp
								}
								ga.child1.total_time.set(vehi, distance_choose_1.get(insert_ind)); // update the time in vehicle.get(vehi)
								List<Integer> insert_route_temp = new ArrayList<Integer>(ga.child1.chrom.get(vehi));
								insert_route_temp.add(insert_ind, selected_cus_2); // insert a selected customer into proper place
								List<Integer> irt = new ArrayList<Integer>(insert_route_temp);
								ga.child1.chrom.set(vehi, irt); // update the route
								break;
							}
							else { // if this capacity over the constrain
								ga.child1.capa.set(vehi, ga.child1.capa.get(vehi) - problem.customer.get(selected_cus_2).demand); // a customer is got out of the vehicle
								ga.child1.total_distance.add(0.0);
								ga.child1.total_time.add(0.0);
								ga.child1.capa.add(problem.customer.get(selected_cus_2).demand); // add a new vehicle
								List<Integer> sc2 = new ArrayList<Integer>(Arrays.asList(selected_cus_2));
								ga.child1.chrom.add(sc2);
								ga.child1.total_time.set(ga.child1.total_time.size()-1, time_culc(ga.child1.chrom.get(vehi+1),problem));
								break;
							}
						}
						else { // there is no feasible routes
							if (vehi == ga.child1.chrom.size()-1) {
								ga.child1.total_distance.add(0.0);
								ga.child1.total_time.add(0.0);
								ga.child1.capa.add(problem.customer.get(selected_cus_2).demand); // add a new vehicle
								List<Integer> sc2 = new ArrayList<Integer>(Arrays.asList(selected_cus_2));
								ga.child1.chrom.add(sc2);
								ga.child1.total_time.set(ga.child1.total_time.size()-1, time_culc(ga.child1.chrom.get(vehi+1),problem));
								break;
							}
						}
					}
				}

				List<Double> fitness = new ArrayList<Double>(); //put each fitness of solutions
				for (int s=0; s<problem.pop_size; s++) {
					fitness.add(ga.pool.get(s).fitness);
				}
				double max = fitness.get(0);
				for (int index = 1; index<fitness.size(); index ++) {
		            max = Math.max(max, fitness.get(index));
		        }
				int worst_ind = fitness.indexOf(max);
				
				if (gen_num == 1) worst_ind = rnd.nextInt(ga.pool.size());

				Population c1_pool = ga.child1.clone();
				c1_pool.init(c1_pool);
				ga.pool.set(worst_ind, c1_pool);
				ga.pool.get(worst_ind).fitness = 0.0;

				while (ga.child2.unsearved_customers.size() != 0){ // roop until all customer who are in selected route are assigned
					int selected_cus_1 = ga.child2.unsearved_customers.get(rnd.nextInt(ga.child2.unsearved_customers.size())); // select a customer in the selected route
					ga.child2.unsearved_customers.remove((Integer)selected_cus_1); // remove a selected customer in the selected route
					for (int vehi=0; vehi<ga.child2.chrom.size(); vehi++) {
						List<Double> distance_choose_2 = new ArrayList<Double>();
						for (int insert=0; insert<ga.child2.chrom.get(vehi).size()+1; insert++) {
							List<Integer> insert_route = new ArrayList<Integer>(ga.child2.chrom.get(vehi));
							insert_route.add(insert,selected_cus_1);
							distance_choose_2.add(time_culc(insert_route, problem));
						}
						List<Double> distance_choose_2_temp = new ArrayList<Double>(distance_choose_2);
						while (distance_choose_2_temp.contains(0.0)) {
							distance_choose_2_temp.remove(0.0);
						}
						if (distance_choose_2_temp.size() != 0) { // if there are some feasible route, len(distance_choose_2_temp) != 0
							ga.child2.capa.set(vehi, ga.child2.capa.get(vehi) + problem.customer.get(selected_cus_1).demand); // add the inserted customer's demand
							if (ga.child2.capa.get(vehi) <= problem.max_capacity) { // if this capacity meet the constrain
								double min = distance_choose_2_temp.get(0);
								for (int index = 1; index<distance_choose_2_temp.size(); index ++) {
						            min = Math.min(min, distance_choose_2_temp.get(index));
						        }
								int insert_ind;
								if (Collections.frequency(distance_choose_2_temp, min) != 1) {
									int count_ = 0;
									List<Integer> same_insert_ind = new ArrayList<Integer>();								// a case where the same index(near_ind) is
									for(double x: distance_choose_2){ //変更した
										if(x == min){
											same_insert_ind.add(count_);
										}
										count_ += 1;
									}
									insert_ind = same_insert_ind.get(rnd.nextInt(same_insert_ind.size()));
								}
								else {
									insert_ind = distance_choose_2.indexOf(min); // choose the shortest route in the distance_choose_2_temp
								}
								ga.child2.total_time.set(vehi, distance_choose_2.get(insert_ind)); // update the time in vehicle.get(vehi)
								List<Integer> insert_route_temp = new ArrayList<Integer>(ga.child2.chrom.get(vehi));
								insert_route_temp.add(insert_ind, selected_cus_1); // insert a selected customer into proper place
								List<Integer> irt = new ArrayList<Integer>(insert_route_temp);
								ga.child2.chrom.set(vehi, irt); // update the route
								break;
							}
							else { // if this capacity over the constrain
								ga.child2.capa.set(vehi, ga.child2.capa.get(vehi) - problem.customer.get(selected_cus_1).demand); // a customer is got out of the vehicle
								ga.child2.total_distance.add(0.0);
								ga.child2.total_time.add(0.0);
								ga.child2.capa.add(problem.customer.get(selected_cus_1).demand); // add a new vehicle
								List<Integer> sc1 = new ArrayList<Integer>(Arrays.asList(selected_cus_1));
								ga.child2.chrom.add(sc1);
								ga.child2.total_time.set(ga.child2.total_time.size()-1, time_culc(ga.child2.chrom.get(vehi+1),problem));
								break;
							}
						}
						else { // there is no feasible routes
							if (vehi == ga.child2.chrom.size()-1) {
								ga.child2.total_distance.add(0.0);
								ga.child2.total_time.add(0.0);
								ga.child2.capa.add(problem.customer.get(selected_cus_1).demand); // add a new vehicle
								List<Integer> sc1 = new ArrayList<Integer>(Arrays.asList(selected_cus_1));
								ga.child2.chrom.add(sc1);
								ga.child2.total_time.set(ga.child2.total_time.size()-1, time_culc(ga.child2.chrom.get(vehi+1),problem));
								break;
							}
						}
					}
				}
				
				fitness.clear(); //put each fitness of solutions
				for (int s=0; s<problem.pop_size; s++) {
					fitness.add(ga.pool.get(s).fitness);
				}
				max = fitness.get(0);
				for (int index = 1; index<fitness.size(); index ++) {
		            max = Math.max(max, fitness.get(index));
		        }
				worst_ind = fitness.indexOf(max);
				
				if (gen_num == 1) worst_ind = rnd.nextInt(ga.pool.size());
				
				Population c2_pool = ga.child2.clone();
				c2_pool.init(c2_pool);
				ga.pool.set(worst_ind, c2_pool);
				ga.pool.get(worst_ind).fitness = 0.0;
				
			}
		}
	return ga;
	}

	public static Genetic_algorithm Uniform_order_crossover(Genetic_algorithm ga, Load_problem problem) {
		long seed = Runtime.getRuntime().freeMemory();
		Random rnd = new Random(seed);
		double d;
		for (int i=0; i<ga.pool.size()/2; i++) {
			d = Math.random();
			if (d<problem.pc) {  //Crossover occers
				// create ga.child by crossover
				int i2 = ga.pool.size()/2+i;
				//System.out.println(ga.pool.get(i).chrom);

				List<Integer> child1_temp = new ArrayList<Integer>();
				ga.child1.chrom.clear();
				ga.child1.capa.clear();
				ga.child1.total_distance.clear();
				ga.child1.total_time.clear();
				ga.child1.fitness = 0;
				ga.child1.unsearved_customers.clear();
				ga.child2.unsearved_customers.clear();

				List<Integer> child2_temp = new ArrayList<Integer>();
				for (int j=0; j<problem.customer.size()-1; j++) {
					child1_temp.add(0);
					child2_temp.add(0);
					ga.child2.unsearved_customers.add(j+1);
					ga.child1.unsearved_customers.add(j+1);
				}
				ga.child2.chrom.clear();
				ga.child2.capa.clear();
				ga.child2.total_distance.clear();
				ga.child2.total_time.clear();
				ga.child2.fitness = 0;

				int[] mask = new int[problem.customer.size()-1]; //make a bit mask
				for (int j=0; j<problem.customer.size()-1; j++) mask[j] = rnd.nextInt(2);

				List<Integer> pool_temp_i = new ArrayList<Integer>();
				List<Integer> pool_temp_i2 = new ArrayList<Integer>();
				for (int j=0; j<ga.pool.get(i).chrom.size(); j++) {
					List<Integer> pool_temp_i_ = new ArrayList<Integer>(ga.pool.get(i).chrom.get(j));
					pool_temp_i.addAll(pool_temp_i_);
				}
				for (int j=0; j<ga.pool.get(i2).chrom.size(); j++) {
					List<Integer> pool_temp_i2_ = new ArrayList<Integer>(ga.pool.get(i2).chrom.get(j));
					pool_temp_i2.addAll(pool_temp_i2_);
				}
				for (int j=0; j<mask.length; j++) {
					if (mask[j] == 1) {
						child1_temp.set(j, pool_temp_i.get(j));
						child2_temp.set(j, pool_temp_i2.get(j));
					}
				}
				for (int j=0; j<mask.length; j++) {
					if (mask[j] == 0) {
						for (int x=0; x<pool_temp_i.size(); x++) {
							if (Collections.frequency(child1_temp, pool_temp_i2.get(x)) == 0) {
								child1_temp.set(j, pool_temp_i2.get(x));
							}
							if (Collections.frequency(child2_temp, pool_temp_i.get(x)) == 0) {
								child2_temp.set(j, pool_temp_i.get(x));
							}
						}
					}
				}

				int vehicle = 0;
				ga.child1.capa.add(0);
				for (int j=0; j<child1_temp.size(); j++) {
					if (j==0) {
						List<Integer> a = new ArrayList<Integer>(Arrays.asList(child1_temp.get(j)));
						ga.child1.chrom.add(a);
					}
					else {
						ga.child1.chrom.get(vehicle).add(child1_temp.get(j));
					}
					ga.child1.capa.set(vehicle, ga.child1.capa.get(vehicle) + problem.customer.get(child1_temp.get(j)).demand);
					ga.child1.unsearved_customers.remove((Integer)child1_temp.get(j));
					if (ga.child1.capa.get(vehicle) <= problem.max_capacity) {
						if (ga.child1.chrom.get(vehicle).get(0) == child1_temp.get(j)) {
							ga.child1.total_time.add(problem.dist.get(child1_temp.get(j)).get(0));
						}
						else {
							ga.child1.total_time.set(vehicle, ga.child1.total_time.get(vehicle) + problem.dist.get(child1_temp.get(j)).get(child1_temp.get(j-1)));
						}

						// a case where the vehicle can arrive within time window
						if (ga.child1.total_time.get(vehicle) >= problem.customer.get(child1_temp.get(j)).ready_time && ga.child1.total_time.get(vehicle) <= problem.customer.get(child1_temp.get(j)).due_time) {
							ga.child1.total_time.set(vehicle, ga.child1.total_time.get(vehicle) + problem.customer.get(child1_temp.get(j)).service_time); // add the customer's service time
						}
						// a case where the vehicle arrive before the time window
						else if (ga.child1.total_time.get(vehicle) < problem.customer.get(child1_temp.get(j)).ready_time) { // the vehicle has to wait till ready time
							ga.child1.total_time.set(vehicle, (double)problem.customer.get(child1_temp.get(j)).ready_time + problem.customer.get(child1_temp.get(j)).service_time); // add the customer's service time
						}
						// a case where the vehicle arrive late for the time window
						else {
							ga.child1.chrom.get(vehicle).remove(child1_temp.get(j));
							ga.child1.capa.set(vehicle, ga.child1.capa.get(vehicle) - problem.customer.get(child1_temp.get(j)).demand);
							ga.child1.total_time.set(vehicle, ga.child1.total_time.get(vehicle) - problem.dist.get(child1_temp.get(j)).get(child1_temp.get(j-1)));
							ga.child1.total_time.set(vehicle, ga.child1.total_time.get(vehicle) + problem.dist.get(0).get(child1_temp.get(j-1)));
							if (ga.child1.total_time.get(vehicle) > problem.customer.get(0).due_time) System.out.println("error1");
							vehicle += 1;
							List<Integer> next_ = new ArrayList<Integer>(Arrays.asList(child1_temp.get(j)));
							ga.child1.chrom.add(next_);
							ga.child1.capa.add(problem.customer.get(child1_temp.get(j)).demand);
							ga.child1.total_time.add(problem.dist.get(child1_temp.get(j)).get(0));
						}

						if (j == child1_temp.size()-1) {
							ga.child1.total_time.set(vehicle, ga.child1.total_time.get(vehicle) + problem.dist.get(child1_temp.get(j)).get(0));
							if (ga.child1.total_time.get(vehicle) > problem.customer.get(0).due_time)
								System.out.println("error2");
						}
					}

					else {
						ga.child1.chrom.get(vehicle).remove(child1_temp.get(j));
						ga.child1.capa.set(vehicle, ga.child1.capa.get(vehicle) - problem.customer.get(child1_temp.get(j)).demand);
						vehicle += 1;
						List<Integer> next = new ArrayList<Integer>(Arrays.asList(child1_temp.get(j)));
						ga.child1.chrom.add(next);
						ga.child1.capa.add(problem.customer.get(child1_temp.get(j)).demand);
						ga.child1.total_time.add(problem.dist.get(child1_temp.get(j)).get(0));
					}
				}

				List<Double> fitness = new ArrayList<Double>(); //put each fitness of solutions
				for (int s=0; s<problem.pop_size; s++) {
					fitness.add(ga.pool.get(s).fitness);
				}
				double max = fitness.get(0);
				for (int index = 1; index<fitness.size(); index ++) {
					max = Math.max(max, fitness.get(index));
				}
				int worst_ind = fitness.indexOf(max);

				Population c1_pool = ga.child1.clone();
				c1_pool.init(c1_pool);
				ga.pool.set(worst_ind, c1_pool);


				vehicle = 0;
				ga.child2.capa.add(0);
				for (int j=0; j<child2_temp.size(); j++) {
					if (j==0) {
						List<Integer> b = new ArrayList<Integer>(Arrays.asList(child2_temp.get(j)));
						ga.child2.chrom.add(b);
					}
					else {
						ga.child2.chrom.get(vehicle).add(child2_temp.get(j));
					}
					ga.child2.capa.set(vehicle, ga.child2.capa.get(vehicle) + problem.customer.get(child2_temp.get(j)).demand);
					ga.child2.unsearved_customers.remove((Integer)child2_temp.get(j));
					if (ga.child2.capa.get(vehicle) <= problem.max_capacity) {
						if (ga.child2.chrom.get(vehicle).get(0) == child2_temp.get(j)) {
							ga.child2.total_time.add(problem.dist.get(child2_temp.get(j)).get(0));
						}
						else ga.child2.total_time.set(vehicle, ga.child2.total_time.get(vehicle) + problem.dist.get(child2_temp.get(j)).get(child2_temp.get(j-1)));

						// a case where the vehicle can arrive within time window
						if (ga.child2.total_time.get(vehicle) >= problem.customer.get(child2_temp.get(j)).ready_time && ga.child2.total_time.get(vehicle) <= problem.customer.get(child2_temp.get(j)).due_time) {
							ga.child2.total_time.set(vehicle, ga.child2.total_time.get(vehicle) + problem.customer.get(child2_temp.get(j)).service_time); // add the customer's service time
						}
						// a case where the vehicle arrive before the time window
						else if (ga.child2.total_time.get(vehicle) < problem.customer.get(child2_temp.get(j)).ready_time) { // the vehicle has to wait till ready time
							ga.child2.total_time.set(vehicle, (double)problem.customer.get(child2_temp.get(j)).ready_time + problem.customer.get(child2_temp.get(j)).service_time); // add the customer's service time
						}
						// a case where the vehicle arrive late for the time window
						else {
							ga.child2.chrom.get(vehicle).remove(child2_temp.get(j));
							ga.child2.capa.set(vehicle, ga.child2.capa.get(vehicle) - problem.customer.get(child2_temp.get(j)).demand);
							ga.child2.total_time.set(vehicle, ga.child2.total_time.get(vehicle) - problem.dist.get(child2_temp.get(j)).get(child2_temp.get(j-1)));
							ga.child2.total_time.set(vehicle, ga.child2.total_time.get(vehicle) + problem.dist.get(0).get(child2_temp.get(j-1)));
							if (ga.child2.total_time.get(vehicle) > problem.customer.get(0).due_time) System.out.println("error1");
							vehicle += 1;
							List<Integer> next_ = new ArrayList<Integer>(Arrays.asList(child2_temp.get(j)));
							ga.child2.chrom.add(next_);
							ga.child2.capa.add(problem.customer.get(child2_temp.get(j)).demand);
							ga.child2.total_time.add(problem.dist.get(child2_temp.get(j)).get(0));
						}

						if (j == child2_temp.size()-1) {
							ga.child2.total_time.set(vehicle, ga.child2.total_time.get(vehicle) + problem.dist.get(child2_temp.get(j)).get(0));
							if (ga.child2.total_time.get(vehicle) > problem.customer.get(0).due_time)
								System.out.println("error2");
						}
					}

					else {
						ga.child2.chrom.get(vehicle).remove(child2_temp.get(j));
						ga.child2.capa.set(vehicle, ga.child2.capa.get(vehicle) - problem.customer.get(child2_temp.get(j)).demand);
						vehicle += 1;
						List<Integer> next = new ArrayList<Integer>(Arrays.asList(child2_temp.get(j)));
						ga.child2.chrom.add(next);
						ga.child2.capa.add(problem.customer.get(child2_temp.get(j)).demand);
						ga.child2.total_time.add(problem.dist.get(child2_temp.get(j)).get(0));
					}
				}

				fitness.clear(); //put each fitness of solutions
				for (int s=0; s<problem.pop_size; s++) {
					fitness.add(ga.pool.get(s).fitness);
				}
				max = fitness.get(0);
				for (int index = 1; index<fitness.size(); index ++) {
					max = Math.max(max, fitness.get(index));
				}
				worst_ind = fitness.indexOf(max);

				Population c2_pool = ga.child2.clone();
				c2_pool.init(c2_pool);
				ga.pool.set(worst_ind, c2_pool);

			}
		}

	return ga;
	}
}




