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
	
	
	public static Population best_cost_route_crossover(Genetic_algorithm pops, Load_problem problem) {
		long seed = Runtime.getRuntime().freeMemory();
		Random rnd = new Random(seed);
		double d;
		
		for (int i=0; i<pops.pool.chrom.size()/2; i++) {
			d = Math.random();
			if (d<problem.pc) {  //Crossover occers
				// create child by crossover
				int i2 = pops.pool.chrom.size()/2+i;
				//System.out.println(pops.pool.chrom.get(i));
				
				List<List<Integer>> chrom = new ArrayList<List<Integer>>();
				for (int n=0; n<pops.pool.chrom.get(i).size(); n++) {
					List<Integer> chrom_ = new ArrayList<Integer>(pops.pool.chrom.get(i).get(n));
					chrom.add(chrom_);
				}
				List<Integer> capa = new ArrayList<Integer>(pops.pool.capa.get(i));
				List<Double> distance = new ArrayList<Double>(pops.pool.total_time.get(i));
				List<Integer> uc = new ArrayList<Integer>(pops.pool.unsearved_customers.get(i));
				
				pops.child1.chrom = chrom;
				pops.child1.capa = capa;
				pops.child1.distance = distance;
				pops.child1.unsearved_customers = uc;
				pops.child1.fitness = 0;
				
				List<List<Integer>> chrom1 = new ArrayList<List<Integer>>();
				for (int n=0; n<pops.pool.chrom.get(i2).size(); n++) {
					List<Integer> chrom1_ = new ArrayList<Integer>(pops.pool.chrom.get(i2).get(n));
					chrom1.add(chrom1_);
				}
				List<Integer> capa1 = new ArrayList<Integer>(pops.pool.capa.get(i2));
				List<Double> distance1 = new ArrayList<Double>(pops.pool.total_time.get(i2));
				List<Integer> uc1 = new ArrayList<Integer>(pops.pool.unsearved_customers.get(i2));
				
				pops.child2.chrom = chrom1;
				pops.child2.capa = capa1;
				pops.child2.distance = distance1;
				pops.child2.unsearved_customers = uc1;
				pops.child2.fitness = 0;
				
				// select a route to occur crossover
				List<Integer> selected_route_1 = new ArrayList<Integer>(pops.child1.chrom.get(rnd.nextInt(pops.child1.chrom.size())));
				List<Integer> selected_route_2 = new ArrayList<Integer>(pops.child2.chrom.get(rnd.nextInt(pops.child2.chrom.size())));
				
				for (int j=0; j<selected_route_2.size(); j++) {
					for (int k=0; k<pops.child1.chrom.size(); k++) {
						if (pops.child1.chrom.get(k).contains(selected_route_2.get(j))) {
							pops.child1.chrom.get(k).remove((Integer)selected_route_2.get(j)); // remove a customer who is in the selected route
							pops.child1.unsearved_customers.add(selected_route_2.get(j));
							pops.child1.capa.set(k, pops.child1.capa.get(k) - problem.customer.get(selected_route_2.get(j)).demand); // remove a customer's demand which is in the selected route
							int count = 0;
							for (int l=0; l<pops.child1.chrom.size(); l++) {
								if (pops.child1.chrom.get(l).isEmpty()) { // remove a empty vehicle
									pops.child1.capa.remove((Integer)0);
									pops.child1.distance.remove(0.0);
									pops.child1.chrom.remove(pops.child1.chrom.indexOf(pops.child1.chrom.get(l)));
									break;
								}
								count += 1;
							}
							if (count == pops.child1.chrom.size()-1) { // there is no empty vehicle
								pops.child1.distance.set(k, time_culc(pops.child1.chrom.get(k),problem));
							}
							break;
						}
					}
				}
				
				for (int j=0; j<selected_route_1.size(); j++) {
					for (int k=0; k<pops.child2.chrom.size(); k++) {
						if (pops.child2.chrom.get(k).contains(selected_route_1.get(j))) {
							pops.child2.chrom.get(k).remove((Integer)selected_route_1.get(j)); // remove a customer who is in the selected route
							pops.child2.unsearved_customers.add(selected_route_1.get(j));
							pops.child2.capa.set(k, pops.child2.capa.get(k) - problem.customer.get(selected_route_1.get(j)).demand); // remove a customer's demand which is in the selected route
							int count1 = 0;
							for (int l=0; l<pops.child2.chrom.size(); l++) {
								if (pops.child2.chrom.get(l).isEmpty()) {
									pops.child2.capa.remove((Integer)0); // remove a empty vehicle
									pops.child2.distance.remove(0.0);
									pops.child2.chrom.remove(pops.child2.chrom.indexOf(pops.child2.chrom.get(l))); // remove a empty vehicle
									break;
								}
								count1 += 1;
							}
							if (count1 == pops.child2.chrom.size()-1) {
								pops.child2.distance.set(k, time_culc(pops.child2.chrom.get(k),problem));
							}
							break;
						}
					}
				}
				
				
				while (selected_route_2.size() != 0 && pops.child1.unsearved_customers.size() != 0){ // roop until all customer who are in selected route are assigned
					int selected_cus_2 = selected_route_2.get(rnd.nextInt(selected_route_2.size())); // select a customer in the selected route
					selected_route_2.remove((Integer)selected_cus_2); // remove a selected customer in the selected route
					for (int vehi=0; vehi<pops.child1.chrom.size(); vehi++) {
						List<Double> distance_choose_1 = new ArrayList<Double>();
						for (int insert=0; insert<pops.child1.chrom.get(vehi).size()+1; insert++) {
							List<Integer> insert_route = new ArrayList<Integer>(pops.child1.chrom.get(vehi));
							insert_route.add(insert,selected_cus_2);
							distance_choose_1.add(time_culc(insert_route, problem));/////////////
						}
						List<Double> distance_choose_1_temp = new ArrayList<Double>(distance_choose_1);
						while (distance_choose_1_temp.contains(0.0)) {
							distance_choose_1_temp.remove(0.0);
						}
						if (distance_choose_1_temp.size() != 0) { // if there are some feasible route, len(distance_choose_1_temp) != 0
							pops.child1.capa.set(vehi, pops.child1.capa.get(vehi) + problem.customer.get(selected_cus_2).demand); // add the inserted customer's demand
							if (pops.child1.capa.get(vehi) <= problem.max_capacity) { // if this capacity meet the constrain
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
								pops.child1.distance.set(vehi, distance_choose_1.get(insert_ind)); // update the distance in vehicle.get(vehi)
								List<Integer> insert_route_temp = new ArrayList<Integer>(pops.child1.chrom.get(vehi));
								insert_route_temp.add(insert_ind, selected_cus_2); // insert a selected customer into proper place
								List<Integer> irt = new ArrayList<Integer>(insert_route_temp);
								pops.child1.chrom.set(vehi, irt); // update the route
								pops.child1.unsearved_customers.remove((Integer)selected_cus_2); // a customer is assigned
								break;
							}
							else { // if this capacity over the constrain
								pops.child1.capa.set(vehi, pops.child1.capa.get(vehi) - problem.customer.get(selected_cus_2).demand); // a customer is got out of the vehicle
								pops.child1.unsearved_customers.remove((Integer)selected_cus_2); // a customer is assigned
								pops.child1.distance.add(0.0);
								pops.child1.capa.add(problem.customer.get(selected_cus_2).demand); // add a new vehicle
								List<Integer> sc2 = new ArrayList<Integer>(Arrays.asList(selected_cus_2));
								pops.child1.chrom.add(sc2);
								pops.child1.distance.set(pops.child1.distance.indexOf(0.0), time_culc(pops.child1.chrom.get(vehi+1),problem));
								break;
							}
						}
						else { // there is no feasible routes
							if (vehi == pops.child1.chrom.size()-1) {
								pops.child1.unsearved_customers.remove((Integer)selected_cus_2); // a customer is assigned
								pops.child1.distance.add(0.0);
								pops.child1.capa.add(problem.customer.get(selected_cus_2).demand); // add a new vehicle
								List<Integer> sc2 = new ArrayList<Integer>(Arrays.asList(selected_cus_2));
								pops.child1.chrom.add(sc2);
								pops.child1.distance.set(pops.child1.distance.indexOf(0.0), time_culc(pops.child1.chrom.get(vehi+1),problem));
								break;
							}
						}
					}
				}				
						
				double max = pops.pool.fitness.get(0);
				for (int index = 1; index<pops.pool.fitness.size(); index ++) {
		            max = Math.max(max, pops.pool.fitness.get(index));
		        }
				int worst_ind = pops.pool.fitness.indexOf(max);
				
				List<List<Integer>> chrom_c1 = new ArrayList<List<Integer>>();
				for (int n=0; n<pops.child1.chrom.size(); n++) {
					List<Integer> chrom1_ = new ArrayList<Integer>(pops.child1.chrom.get(n));
					chrom_c1.add(chrom1_);
				}
				List<Integer> capa_c1 = new ArrayList<Integer>(pops.child1.capa);
				List<Double> distance_c1 = new ArrayList<Double>(pops.child1.distance);
				List<Integer> vect_taboo_c1 = new ArrayList<Integer>(pops.child1.unsearved_customers);
				
				pops.pool.chrom.set(worst_ind, chrom_c1);
				pops.pool.capa.set(worst_ind, capa_c1);
				pops.pool.total_time.set(worst_ind, distance_c1);
				pops.pool.unsearved_customers.set(worst_ind, vect_taboo_c1);
				pops.pool.fitness.set(worst_ind, pops.child1.fitness);
		
				while (selected_route_1.size() != 0 && pops.child2.unsearved_customers.size() != 0){ // roop until all customer who are in selected route are assigned
					int selected_cus_1 = selected_route_1.get(rnd.nextInt(selected_route_1.size())); // select a customer in the selected route
					selected_route_1.remove((Integer)selected_cus_1); // remove a selected customer in the selected route
					for (int vehi=0; vehi<pops.child2.chrom.size(); vehi++) {
						List<Double> distance_choose_2 = new ArrayList<Double>();
						for (int insert=0; insert<pops.child2.chrom.get(vehi).size()+1; insert++) {
							List<Integer> insert_route = new ArrayList<Integer>(pops.child2.chrom.get(vehi));
							insert_route.add(insert,selected_cus_1);
							distance_choose_2.add(time_culc(insert_route, problem));/////////////
						
						}
						List<Double> distance_choose_2_temp = new ArrayList<Double>(distance_choose_2);
						while (distance_choose_2_temp.contains(0.0)) {
							distance_choose_2_temp.remove(0.0);
						}
						if (distance_choose_2_temp.size() != 0) { // if there are some feasible route, len(distance_choose_2_temp) != 0
							pops.child2.capa.set(vehi, pops.child2.capa.get(vehi) + problem.customer.get(selected_cus_1).demand); // add the inserted customer's demand
							if (pops.child2.capa.get(vehi) <= problem.max_capacity) { // if this capacity meet the constrain
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
								pops.child2.distance.set(vehi, distance_choose_2.get(insert_ind)); // update the distance in vehicle.get(vehi)
								List<Integer> insert_route_temp = new ArrayList<Integer>(pops.child2.chrom.get(vehi));
								insert_route_temp.add(insert_ind, selected_cus_1); // insert a selected customer into proper place
								List<Integer> irt = new ArrayList<Integer>(insert_route_temp);
								pops.child2.chrom.set(vehi, irt); // update the route
								pops.child2.unsearved_customers.remove((Integer)selected_cus_1); // a customer is assigned
								break;
							}
							else { // if this capacity over the constrain
								pops.child2.capa.set(vehi, pops.child2.capa.get(vehi) - problem.customer.get(selected_cus_1).demand); // a customer is got out of the vehicle
								pops.child2.unsearved_customers.remove((Integer)selected_cus_1); // a customer is assigned
								pops.child2.distance.add(0.0);
								pops.child2.capa.add(problem.customer.get(selected_cus_1).demand); // add a new vehicle
								List<Integer> sc1 = new ArrayList<Integer>(Arrays.asList(selected_cus_1));
								pops.child2.chrom.add(sc1);
								pops.child2.distance.set(pops.child2.distance.indexOf(0.0), time_culc(pops.child2.chrom.get(vehi+1),problem));
								break;
							}
						}
						else { // there is no feasible routes
							if (vehi == pops.child2.chrom.size()-1) {
								pops.child2.unsearved_customers.remove((Integer)selected_cus_1); // a customer is assigned
								pops.child2.distance.add(0.0);
								pops.child2.capa.add(problem.customer.get(selected_cus_1).demand); // add a new vehicle
								List<Integer> sc1 = new ArrayList<Integer>(Arrays.asList(selected_cus_1));
								pops.child2.chrom.add(sc1);
								pops.child2.distance.set(pops.child2.distance.indexOf(0.0), time_culc(pops.child2.chrom.get(vehi+1),problem));
								break;
							}
						}
					}
				}				
						
				double max1 = pops.pool.fitness.get(0);
				for (int index = 1; index<pops.pool.fitness.size(); index ++) {
		            max1 = Math.max(max1, pops.pool.fitness.get(index));
		        }
				int worst_ind1 = pops.pool.fitness.indexOf(max1);
				
				List<List<Integer>> chrom_c2 = new ArrayList<List<Integer>>();
				for (int n=0; n<pops.child2.chrom.size(); n++) {
					List<Integer> chrom2_ = new ArrayList<Integer>(pops.child2.chrom.get(n));
					chrom_c2.add(chrom2_);
				}
				List<Integer> capa_c2 = new ArrayList<Integer>(pops.child2.capa);
				List<Double> distance_c2 = new ArrayList<Double>(pops.child2.distance);
				List<Integer> vect_taboo_c2 = new ArrayList<Integer>(pops.child2.unsearved_customers);
				
				pops.pool.chrom.set(worst_ind1, chrom_c2);
				pops.pool.capa.set(worst_ind1, capa_c2);
				pops.pool.total_time.set(worst_ind1, distance_c2);
				pops.pool.unsearved_customers.set(worst_ind1, vect_taboo_c2);
				pops.pool.fitness.set(worst_ind1, pops.child2.fitness);
				
			}
		}
	return pops.pool;
	}

	public static Population Uniform_order_crossover(Genetic_algorithm pops, Load_problem problem) {
		long seed = Runtime.getRuntime().freeMemory();
		Random rnd = new Random(seed);
		double d;
		for (int i=0; i<pops.pool.chrom.size()/2; i++) {
			d = Math.random();
			if (d<problem.pc) {  //Crossover occers
				// create pops.child by crossover
				int i2 = pops.pool.chrom.size()/2+i;
				//System.out.println(pops.pool.chrom.get(i));
				
				List<Integer> child1_temp = new ArrayList<Integer>();
				pops.child1.chrom.clear();
				pops.child1.capa.clear();
				pops.child1.distance.clear();
				pops.child1.fitness = 0;
				pops.child1.unsearved_customers.clear();
				pops.child2.unsearved_customers.clear();
				
				List<Integer> child2_temp = new ArrayList<Integer>();
				for (int j=0; j<problem.customer.size()-1; j++) {
					child1_temp.add(0);
					child2_temp.add(0);
					pops.child2.unsearved_customers.add(j+1);
					pops.child1.unsearved_customers.add(j+1);
				}
				pops.child2.chrom.clear();
				pops.child2.capa.clear();
				pops.child2.distance.clear();
				pops.child2.fitness = 0;

				int[] mask = new int[problem.customer.size()-1];
				for (int j=0; j<problem.customer.size()-1; j++) mask[j] = rnd.nextInt(2);
				
				List<Integer> pool_temp_i = new ArrayList<Integer>();
				List<Integer> pool_temp_i2 = new ArrayList<Integer>();
				for (int j=0; j<pops.pool.chrom.get(i).size(); j++) {
					List<Integer> pool_temp_i_ = new ArrayList<Integer>(pops.pool.chrom.get(i).get(j));
					pool_temp_i.addAll(pool_temp_i_);
				}
				for (int j=0; j<pops.pool.chrom.get(i2).size(); j++) {
					List<Integer> pool_temp_i2_ = new ArrayList<Integer>(pops.pool.chrom.get(i2).get(j));
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
				pops.child1.capa.add(0);
				for (int j=0; j<child1_temp.size(); j++) {
					if (j==0) {
						List<Integer> a = new ArrayList<Integer>(Arrays.asList(child1_temp.get(j)));
						pops.child1.chrom.add(a);
					}
					else {
						pops.child1.chrom.get(vehicle).add(child1_temp.get(j));
					}
					pops.child1.capa.set(vehicle, pops.child1.capa.get(vehicle) + problem.customer.get(child1_temp.get(j)).demand);
					pops.child1.unsearved_customers.remove((Integer)child1_temp.get(j));
					if (pops.child1.capa.get(vehicle) <= problem.max_capacity) {
						if (pops.child1.chrom.get(vehicle).get(0) == child1_temp.get(j)) {
							pops.child1.distance.add(problem.dist.get(child1_temp.get(j)).get(0));
						}
						else {
							pops.child1.distance.set(vehicle, pops.child1.distance.get(vehicle) + problem.dist.get(child1_temp.get(j)).get(child1_temp.get(j-1)));
						}

						// a case where the vehicle can arrive within time window
						if (pops.child1.distance.get(vehicle) >= problem.customer.get(child1_temp.get(j)).ready_time && pops.child1.distance.get(vehicle) <= problem.customer.get(child1_temp.get(j)).due_time) {
							pops.child1.distance.set(vehicle, pops.child1.distance.get(vehicle) + problem.customer.get(child1_temp.get(j)).service_time); // add the customer's service time
						}
						// a case where the vehicle arrive before the time window
						else if (pops.child1.distance.get(vehicle) < problem.customer.get(child1_temp.get(j)).ready_time) { // the vehicle has to wait till ready time
							pops.child1.distance.set(vehicle, (double)problem.customer.get(child1_temp.get(j)).ready_time + problem.customer.get(child1_temp.get(j)).service_time); // add the customer's service time
						}
						// a case where the vehicle arrive late for the time window
						else {
							pops.child1.chrom.get(vehicle).remove(child1_temp.get(j));
							pops.child1.capa.set(vehicle, pops.child1.capa.get(vehicle) - problem.customer.get(child1_temp.get(j)).demand);
							pops.child1.distance.set(vehicle, pops.child1.distance.get(vehicle) - problem.dist.get(child1_temp.get(j)).get(child1_temp.get(j-1))); 
							pops.child1.distance.set(vehicle, pops.child1.distance.get(vehicle) + problem.dist.get(0).get(child1_temp.get(j-1)));
							if (pops.child1.distance.get(vehicle) > problem.customer.get(0).due_time) System.out.println("error1");
							vehicle += 1;
							List<Integer> next_ = new ArrayList<Integer>(Arrays.asList(child1_temp.get(j)));
							pops.child1.chrom.add(next_);
							pops.child1.capa.add(problem.customer.get(child1_temp.get(j)).demand);
							pops.child1.distance.add(problem.dist.get(child1_temp.get(j)).get(0));
						}

						if (j == child1_temp.size()-1) {
							pops.child1.distance.set(vehicle, pops.child1.distance.get(vehicle) + problem.dist.get(child1_temp.get(j)).get(0));
							if (pops.child1.distance.get(vehicle) > problem.customer.get(0).due_time)
								System.out.println("error2");
						}
					}
				
					else {	
						pops.child1.chrom.get(vehicle).remove(child1_temp.get(j));
						pops.child1.capa.set(vehicle, pops.child1.capa.get(vehicle) - problem.customer.get(child1_temp.get(j)).demand);
						vehicle += 1;
						List<Integer> next = new ArrayList<Integer>(Arrays.asList(child1_temp.get(j)));
						pops.child1.chrom.add(next);
						pops.child1.capa.add(problem.customer.get(child1_temp.get(j)).demand);
						pops.child1.distance.add(problem.dist.get(child1_temp.get(j)).get(0));
					}
				}
				
					
				double max = pops.pool.fitness.get(0);
				for (int index = 1; index<pops.pool.fitness.size(); index ++) {
					max = Math.max(max, pops.pool.fitness.get(index));
				}
				int worst_ind = pops.pool.fitness.indexOf(max);
					
				List<List<Integer>> chrom_c1 = new ArrayList<List<Integer>>();
				for (int n=0; n<pops.child1.chrom.size(); n++) {
					List<Integer> chrom1_ = new ArrayList<Integer>(pops.child1.chrom.get(n));
					chrom_c1.add(chrom1_);
				}
				List<Integer> capa_c1 = new ArrayList<Integer>(pops.child1.capa);
				List<Double> distance_c1 = new ArrayList<Double>(pops.child1.distance);
				List<Integer> vect_taboo_c1 = new ArrayList<Integer>(pops.child1.unsearved_customers);
					
				pops.pool.chrom.set(worst_ind, chrom_c1);
				pops.pool.capa.set(worst_ind, capa_c1);
				pops.pool.total_time.set(worst_ind, distance_c1);
				pops.pool.unsearved_customers.set(worst_ind, vect_taboo_c1);
				pops.pool.fitness.set(worst_ind, pops.child1.fitness);
			

				vehicle = 0;
				pops.child2.capa.add(0);
				for (int j=0; j<child2_temp.size(); j++) {
					if (j==0) {
						List<Integer> b = new ArrayList<Integer>(Arrays.asList(child2_temp.get(j)));
						pops.child2.chrom.add(b);
					}
					else {
						pops.child2.chrom.get(vehicle).add(child2_temp.get(j));
					}
					pops.child2.capa.set(vehicle, pops.child2.capa.get(vehicle) + problem.customer.get(child2_temp.get(j)).demand);
					pops.child2.unsearved_customers.remove((Integer)child2_temp.get(j));
					if (pops.child2.capa.get(vehicle) <= problem.max_capacity) {
						if (pops.child2.chrom.get(vehicle).get(0) == child2_temp.get(j)) {
							pops.child2.distance.add(problem.dist.get(child2_temp.get(j)).get(0));
						}
						else pops.child2.distance.set(vehicle, pops.child2.distance.get(vehicle) + problem.dist.get(child2_temp.get(j)).get(child2_temp.get(j-1)));

						// a case where the vehicle can arrive within time window
						if (pops.child2.distance.get(vehicle) >= problem.customer.get(child2_temp.get(j)).ready_time && pops.child2.distance.get(vehicle) <= problem.customer.get(child2_temp.get(j)).due_time) {
							pops.child2.distance.set(vehicle, pops.child2.distance.get(vehicle) + problem.customer.get(child2_temp.get(j)).service_time); // add the customer's service time
						}
						// a case where the vehicle arrive before the time window
						else if (pops.child2.distance.get(vehicle) < problem.customer.get(child2_temp.get(j)).ready_time) { // the vehicle has to wait till ready time
							pops.child2.distance.set(vehicle, (double)problem.customer.get(child2_temp.get(j)).ready_time + problem.customer.get(child2_temp.get(j)).service_time); // add the customer's service time
						}
						// a case where the vehicle arrive late for the time window
						else {
							pops.child2.chrom.get(vehicle).remove(child2_temp.get(j));
							pops.child2.capa.set(vehicle, pops.child2.capa.get(vehicle) - problem.customer.get(child2_temp.get(j)).demand);
							pops.child2.distance.set(vehicle, pops.child2.distance.get(vehicle) - problem.dist.get(child2_temp.get(j)).get(child2_temp.get(j-1)));
							pops.child2.distance.set(vehicle, pops.child2.distance.get(vehicle) + problem.dist.get(0).get(child2_temp.get(j-1)));
							if (pops.child2.distance.get(vehicle) > problem.customer.get(0).due_time) System.out.println("error1");
							vehicle += 1;
							List<Integer> next_ = new ArrayList<Integer>(Arrays.asList(child2_temp.get(j)));
							pops.child2.chrom.add(next_);
							pops.child2.capa.add(problem.customer.get(child2_temp.get(j)).demand);
							pops.child2.distance.add(problem.dist.get(child2_temp.get(j)).get(0));
						}

						if (j == child2_temp.size()-1) {
							pops.child2.distance.set(vehicle, pops.child2.distance.get(vehicle) + problem.dist.get(child2_temp.get(j)).get(0));
							if (pops.child2.distance.get(vehicle) > problem.customer.get(0).due_time)
								System.out.println("error2");
						}
					}
				
					else {	
						pops.child2.chrom.get(vehicle).remove(child2_temp.get(j));
						pops.child2.capa.set(vehicle, pops.child2.capa.get(vehicle) - problem.customer.get(child2_temp.get(j)).demand);
						vehicle += 1;
						List<Integer> next = new ArrayList<Integer>(Arrays.asList(child2_temp.get(j)));
						pops.child2.chrom.add(next);
						pops.child2.capa.add(problem.customer.get(child2_temp.get(j)).demand);
						pops.child2.distance.add(problem.dist.get(child2_temp.get(j)).get(0));
					}
				}
				
					
				double max1 = pops.pool.fitness.get(0);
				for (int index = 1; index<pops.pool.fitness.size(); index ++) {
					max1 = Math.max(max1, pops.pool.fitness.get(index));
				}
				int worst_ind1 = pops.pool.fitness.indexOf(max1);
					
				List<List<Integer>> chrom_c2 = new ArrayList<List<Integer>>();
				for (int n=0; n<pops.child2.chrom.size(); n++) {
					List<Integer> chrom2_ = new ArrayList<Integer>(pops.child2.chrom.get(n));
					chrom_c2.add(chrom2_);
				}
				List<Integer> capa_c2 = new ArrayList<Integer>(pops.child2.capa);
				List<Double> distance_c2 = new ArrayList<Double>(pops.child2.distance);
				List<Integer> vect_taboo_c2 = new ArrayList<Integer>(pops.child2.unsearved_customers);
					
				pops.pool.chrom.set(worst_ind1, chrom_c2);
				pops.pool.capa.set(worst_ind1, capa_c2);
				pops.pool.total_time.set(worst_ind1, distance_c2);
				pops.pool.unsearved_customers.set(worst_ind1, vect_taboo_c2);
				pops.pool.fitness.set(worst_ind1, pops.child2.fitness);
			}
		}
	return pops.pool;
	}
}




