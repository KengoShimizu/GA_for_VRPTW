import java.util.ArrayList;
import java.util.List;

public class Population implements Cloneable {
	public List<Integer> gene = new ArrayList<Integer>();
	public List<List<Integer>> route = new ArrayList<List<Integer>>();
	public List<List<List<Integer>>> chrom = new ArrayList<List<List<Integer>>>();
	public List<List<Integer>> capa = new ArrayList<List<Integer>>();
	public List<List<Double>> total_time = new ArrayList<List<Double>>();
	public List<List<Double>> total_distance = new ArrayList<List<Double>>();
	public List<List<Integer>> unsearved_customers = new ArrayList<List<Integer>>(); // customers have not already served by a vehicle 
	public List<Double> fitness = new ArrayList<Double>();
	public List<Double> L = new ArrayList<Double>(); //L_nnh is total delivery time (considering waiting time)
	public List<Double> J = new ArrayList<Double>();; //J_nnh is total traveling time (without considering waiting time)
	@Override
	public Population clone(){
		Population p = new Population();
	    try {
	    	p = (Population)super.clone();
	    } catch (Exception e){
            e.printStackTrace();
	    }
	    return p;
	  }
}