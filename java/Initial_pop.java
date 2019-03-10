import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;
 

public class Initial_pop{
	public static Genetic_algorithm clustering_algorithm(Load_problem problem, Genetic_algorithm ga){
		int pre_near_ind = 99999999;
		int counter = 0;
		
		// create a solution which meet only capacity constrain using centroid method
		for (int i=0; i<problem.pop_size; i++) {
			Population pop = new Population();
			for (int j=1; j<problem.customer.size(); j++) pop.unsearved_customers.add(j);// taboo list denotes whether the customer is assigned or not
			pop.route.clear(); // create a new route
			int count = 0;
			while (pop.unsearved_customers.size() != 0){ // loop until all customers are assigned
				pop.route.clear(); // make a new route
				Random rand = new Random();
				int centroid = pop.unsearved_customers.get(rand.nextInt(pop.unsearved_customers.size())); // centroid is chosen from unsearved_customers
				pop.unsearved_customers.remove((Integer)centroid); // centroid is served
				pop.capa.add(problem.customer.get(centroid).demand); // demand of centroid is added to capacity of vehicle
				pop.route.add(centroid); // centroid is added to a route
				List<Double> serch_dist1 = new ArrayList<Double>(problem.dist.get(centroid)); //det distance info from centroid
				Collections.sort(serch_dist1); // sort distance from centroid
		
				int j = 1;
				serch_nearest_customer_at_centroid(i, j, count, problem, pre_near_ind, counter, centroid, serch_dist1, pop);
				count += 1;

				List<Integer> route_ = new ArrayList<Integer>(pop.route);
				pop.chrom.add(route_);
				
			}
			ga.pop.add(pop);
		}

		// this part is received semifeasible solutions (meet only capacity constrain)
		// and take into account the time window constrains
		for (int i=0; i<problem.pop_size; i++) {
			for (int j=0; j<ga.pop.get(i).chrom.size(); j++) {	
				double distance_cur = 0.0;
				for (int k=0; k<ga.pop.get(i).chrom.get(j).size()+1; k++) { //pop.get(i).chrom.get(j) == vehicle
					int left_cus = -1; 
					int k_serch_l;
					if (k == 0) { // distance between depot and first customer
						distance_cur = distance_cur + problem.dist.get(0).get(ga.pop.get(i).chrom.get(j).get(0));
					}
					
					else if (k == ga.pop.get(i).chrom.get(j).size()) { // distance between last customer and depot
						left_cus = ga.pop.get(i).chrom.get(j).get(k-1); // last customer of a vehicle
						k_serch_l = k - 2;
						if (left_cus == 0) { // find a customer which is next to k on the left		
							while (left_cus == 0 && k_serch_l >= 0) {
								left_cus = ga.pop.get(i).chrom.get(j).get(k_serch_l);
								k_serch_l = k_serch_l - 1;	
							}
						}
						distance_cur = distance_cur + problem.dist.get(left_cus).get(0);
						if (distance_cur > problem.customer.get(0).due_time) { //the case where the vehicle can't make it within the due time of depot
							distance_cur = distance_cur - problem.dist.get(ga.pop.get(i).chrom.get(j).get(k-1)).get(0);
							ga.pop.get(i).unsearved_customers.add(ga.pop.get(i).chrom.get(j).get(k-1));
							ga.pop.get(i).capa.set(j, ga.pop.get(i).capa.get(j) - problem.customer.get(ga.pop.get(i).chrom.get(j).get(k-1)).demand);
							ga.pop.get(i).chrom.get(j).set(k-1, 0); // delete the customer violates the time construct
							System.out.println("the last customer is deleted");
						}
					}
						
					else { // distance between customer and customer
						left_cus = ga.pop.get(i).chrom.get(j).get(k-1);
						k_serch_l = k - 2;
						if (left_cus == 0) { // find a customer is next to k on the left
							while (left_cus == 0 && k_serch_l >= 0) {
								left_cus = ga.pop.get(i).chrom.get(j).get(k_serch_l);
								k_serch_l -= 1;	
							}
						}
						distance_cur = distance_cur + problem.dist.get(left_cus).get(ga.pop.get(i).chrom.get(j).get(k));
					}
					
					if (k < ga.pop.get(i).chrom.get(j).size()){
						// a case where the vehicle can arrive within time window
						if (distance_cur >= problem.customer.get(ga.pop.get(i).chrom.get(j).get(k)).ready_time && distance_cur <= problem.customer.get(ga.pop.get(i).chrom.get(j).get(k)).due_time) {
							distance_cur = distance_cur + problem.customer.get(ga.pop.get(i).chrom.get(j).get(k)).service_time; // add the customer's service time	
						}
						// a case where the vehicle arrive before the time window
						else if (distance_cur < problem.customer.get(ga.pop.get(i).chrom.get(j).get(k)).ready_time){
							distance_cur = problem.customer.get(ga.pop.get(i).chrom.get(j).get(k)).ready_time; // the vehicle has to wait till ready time
							distance_cur = distance_cur+ problem.customer.get(ga.pop.get(i).chrom.get(j).get(k)).service_time; // add the customer's service time
						}
						// a case where the vehicle arrive late for the time window
						else if (distance_cur > problem.customer.get(ga.pop.get(i).chrom.get(j).get(k)).due_time) {
							if (k == 0) {
								distance_cur = distance_cur - problem.dist.get(0).get(ga.pop.get(i).chrom.get(j).get(0));
							}
							else {
								distance_cur = distance_cur - problem.dist.get(left_cus).get(ga.pop.get(i).chrom.get(j).get(k));
							}
							ga.pop.get(i).unsearved_customers.add(ga.pop.get(i).chrom.get(j).get(k));
							ga.pop.get(i).capa.set(j ,ga.pop.get(i).capa.get(j) - problem.customer.get(ga.pop.get(i).chrom.get(j).get(k)).demand);
							ga.pop.get(i).chrom.get(j).set(k, 0); // delete the customer violates the time construct
						}		
					}
				}
				ga.pop.get(i).total_time.add(distance_cur);
			}
		
			for (int i1=0; i1<ga.pop.size(); i1++) {
				for (int j=0; j<ga.pop.get(i1).chrom.size(); j++) {
					if (ga.pop.get(i1).chrom.get(j).contains(0)) {
						int count_ = 0;
						List<Integer> a = new ArrayList<Integer>();	//a has index which is 0 in vehicle							
						for(int x: ga.pop.get(i1).chrom.get(j)){	
							if(x == 0){	
								a.add(count_);
							}
							count_ += 1;	
						}
						for (int k=0; k<a.size(); k++) {
							ga.pop.get(i1).chrom.get(j).remove((Integer)0);
						}
					}
				}
			}
		}	
		return ga;
	}
	
	public static void serch_nearest_customer_at_centroid(int i, int j, int count, Load_problem problem, int pre_near_ind, int counter, int centroid, List<Double> serch_dist, Population pop) {
		List<Integer> same_dist_ind = new ArrayList<Integer>();
		int near_ind = problem.dist.get(centroid).indexOf(serch_dist.get(j)); //nearest customer index
		int count_ = 0;
		if (pre_near_ind == near_ind) {	// a case where the same index(near_ind) is selected because the same distance
			for(double x: problem.dist.get(centroid)){	
				if(x == serch_dist.get(j)){		
					same_dist_ind.add(count_);
				}
				count_ += 1;
			}
			counter += 1;
			near_ind = same_dist_ind.get(counter);
			if (counter == same_dist_ind.size()-1){											
				counter = 0;
			}
		}
		pre_near_ind = near_ind;
		
		if (same_dist_ind.size() > 2) pre_near_ind = same_dist_ind.get(0);

		if (pop.unsearved_customers.size() == 0) return; // a case where this centroid is the last customer	

		if (pop.unsearved_customers.contains(near_ind) == false) { // customer has already assigned
			j = j + 1;
			if (j < problem.customer.size()) {
				serch_nearest_customer_at_centroid(i, j, count, problem, pre_near_ind, counter, centroid, serch_dist, pop);
			}
			else if (j == problem.customer.size()) return;
			
		}

		else if (pop.unsearved_customers.contains(near_ind)) {  // customer has not assigned yet
			pop.capa.set(count, pop.capa.get(count) + problem.customer.get(near_ind).demand);
			if (pop.capa.get(count) <= problem.max_capacity) { // the vehicle have capacity for the customer
				pop.unsearved_customers.remove((Integer)near_ind); // the customer is assigned
				pop.route.add(near_ind); // the vehicle pick up the customer
				j = j + 1;
				if (j < problem.customer.size()){
					serch_nearest_customer_at_centroid(i, j, count, problem, pre_near_ind, counter, centroid, serch_dist, pop);
				}
				else if (j == problem.customer.size()) return;
			}
			else { // the vehicle don't have capacity for the customer
				pop.capa.set(count, pop.capa.get(count) - problem.customer.get(near_ind).demand);
				return;
			}
		}
	}


	public static Genetic_algorithm nearest_route_algorithm(Load_problem problem, Genetic_algorithm ga) {
		long seed = Runtime.getRuntime().freeMemory();
		Random rnd = new Random(seed);
		for (int i=0; i<problem.pop_size; i++) {
			while (ga.pop.get(i).unsearved_customers.size() != 0) {
		        int selected_nac_ind = ga.pop.get(i).unsearved_customers.get(rnd.nextInt(ga.pop.get(i).unsearved_customers.size()));
				List<Double> serch_dist = new ArrayList<Double>(problem.dist.get(selected_nac_ind));
				Collections.sort(serch_dist);
				int j = 0;
				int counter = 0;
				int pre_near_ind = 99999999;
				while (ga.pop.get(i).unsearved_customers.contains(selected_nac_ind) && j < problem.customer.size()-1) {
					j += 1;
					List<Integer> same_dist_ind = new ArrayList<Integer>();
					int near_ind = problem.dist.get(selected_nac_ind).indexOf(serch_dist.get(j)); //nearest customer index
					
					// a case where the same index(near_ind) is selected because the same distance
					if (pre_near_ind == near_ind) {	
						int count_1 = 0; 
						for(double x: problem.dist.get(selected_nac_ind)){
							if(x == serch_dist.get(j)){
								same_dist_ind.add(count_1);
							}
							count_1 += 1;
						}											
						counter += 1;	
						near_ind = same_dist_ind.get(counter);													
						if (counter == same_dist_ind.size()-1) {														
							counter = 0;
						}
					}
					pre_near_ind = near_ind;
					if (same_dist_ind.size() > 2) {																	
						pre_near_ind = same_dist_ind.get(0);	
					}
					// a case where the same index(near_ind) is selected because the same distance
					
					if (ga.pop.get(i).unsearved_customers.contains(near_ind) == false) {
						List<Double> distance_choose = new ArrayList<Double>();
						int k_count = -1;
						for (int k=0; k<ga.pop.get(i).chrom.size(); k++) {
							k_count += 1;
							if (ga.pop.get(i).chrom.get(k).contains(near_ind)) {
								break;
							}
						}
						for (int insert=0; insert<ga.pop.get(i).chrom.get(k_count).size()+1; insert++) {
							List<Integer> insert_route = new ArrayList<Integer>(ga.pop.get(i).chrom.get(k_count));
							insert_route.add(insert, selected_nac_ind);
							for (int p=0; p<insert_route.size()+1; p++) {
								if (p == 0) {
									distance_choose.add(problem.dist.get(insert_route.get(0)).get(0));
								}
								else if (p == insert_route.size()) {
									distance_choose.set(insert, distance_choose.get(insert) + problem.dist.get(0).get(insert_route.get(p-1)));
									if (distance_choose.get(insert) > problem.customer.get(0).due_time) {
										distance_choose.set(insert, 0.0);
										break;
								}
								}
								else {
									distance_choose.set(insert, distance_choose.get(insert) + problem.dist.get(insert_route.get(p)).get(insert_route.get(p-1)));
								}
								
								if (p < insert_route.size()){
									// a case where the vehicle can arrive within time window
									if (distance_choose.get(insert) >= problem.customer.get(insert_route.get(p)).ready_time && distance_choose.get(insert) <= problem.customer.get(insert_route.get(p)).due_time) {
										distance_choose.set(insert, distance_choose.get(insert) + problem.customer.get(insert_route.get(p)).service_time); // add the customer's service time
									}
									// a case where the vehicle arrive before the time window
									else if (distance_choose.get(insert) < problem.customer.get(insert_route.get(p)).ready_time) {  // the vehicle has to wait till ready time
										distance_choose.set(insert, (double) problem.customer.get(insert_route.get(p)).ready_time + problem.customer.get(insert_route.get(p)).service_time); // add the customer's service time
									}
									// a case where the vehicle arrive late for the time window
									else {
										distance_choose.set(insert, 0.0);
										break;
									}
								}
							}
						}
					
						
						List<Double> distance_choose_temp = new ArrayList<Double>(distance_choose);
						while (distance_choose_temp.contains(0.0)) {
							distance_choose_temp.remove((double)0.0);
						}
						if (distance_choose_temp.size() != 0) {
							ga.pop.get(i).capa.set(k_count, ga.pop.get(i).capa.get(k_count) + problem.customer.get(selected_nac_ind).demand);
							if (ga.pop.get(i).capa.get(k_count) <= problem.max_capacity) {
								
								double min = distance_choose_temp.get(0); // discover the minimum numeric number
								for (int index = 1; index<distance_choose_temp.size(); index ++) {
						            min = Math.min(min, distance_choose_temp.get(index));
						        }
								
								int insert_ind = distance_choose.indexOf(min);
								ga.pop.get(i).total_time.set(k_count, distance_choose.get(insert_ind));
								List<Integer> insert_route_temp = new ArrayList<Integer>(ga.pop.get(i).chrom.get(k_count));
								insert_route_temp.add(insert_ind, selected_nac_ind);
								ga.pop.get(i).chrom.set(k_count, insert_route_temp);
								ga.pop.get(i).unsearved_customers.remove((Integer)selected_nac_ind);
								break;
							}
							else {
								ga.pop.get(i).capa.set(k_count, ga.pop.get(i).capa.get(k_count) - problem.customer.get(selected_nac_ind).demand);
							}
						}
					}
					if (j == problem.customer.size()-1) {
						List<Integer> sni_list = new ArrayList<Integer>(Arrays.asList(selected_nac_ind));
						ga.pop.get(i).chrom.add(sni_list);
						ga.pop.get(i).unsearved_customers.remove((Integer)selected_nac_ind);
						ga.pop.get(i).capa.add(problem.customer.get(selected_nac_ind).demand);
						ga.pop.get(i).total_time.add(problem.dist.get(0).get(selected_nac_ind));
						// a case where the vehicle can arrive within time window
						if (ga.pop.get(i).total_time.get(ga.pop.get(i).total_time.size()-1) >= problem.customer.get(selected_nac_ind).ready_time && ga.pop.get(i).total_time.get(ga.pop.get(i).total_time.size()-1) <= problem.customer.get(selected_nac_ind).due_time) {
							ga.pop.get(i).total_time.set(ga.pop.get(i).total_time.size()-1, ga.pop.get(i).total_time.get(ga.pop.get(i).total_time.size()-1) + problem.customer.get(selected_nac_ind).service_time); // add the customer's service time
						}
						// a case where the vehicle arrive before the time window
						else { // the vehicle has to wait till ready time
							ga.pop.get(i).total_time.set(ga.pop.get(i).total_time.size()-1, (double) problem.customer.get(selected_nac_ind).ready_time + problem.customer.get(selected_nac_ind).service_time); // add the customer's service time
						}
						ga.pop.get(i).total_time.set(ga.pop.get(i).total_time.size()-1, ga.pop.get(i).total_time.get(ga.pop.get(i).total_time.size()-1) + problem.dist.get(0).get(selected_nac_ind));
					}
				}
			}
		}
	return ga;
	}
}


