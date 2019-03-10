import java.util.ArrayList;
import java.util.List;

public class Population implements Cloneable{
	List<Integer> route = new ArrayList<Integer>();
	List<List<Integer>> chrom = new ArrayList<List<Integer>>();
	List<Integer> capa = new ArrayList<Integer>();
	List<Double> total_time = new ArrayList<Double>();
	List<Double> total_distance = new ArrayList<Double>();
	List<Integer> unsearved_customers = new ArrayList<Integer>(); // customers have not already served by a vehicle 
	double fitness;
	double L; //L_nnh is total delivery time (considering waiting time)
	double J; //J_nnh is total traveling time (without considering waiting time)
	int n; //the number of vehicle
	
	public void init(Population pop) { 
		route = new ArrayList<Integer>(pop.route);
		chrom = new ArrayList<List<Integer>>(pop.chrom);
		for (int i=0; i<pop.chrom.size(); i++) {
			List<Integer> chrom_ = new ArrayList<Integer>(pop.chrom.get(i));
			chrom.set(i,chrom_);
		}
		capa = new ArrayList<Integer>(pop.capa);
		total_time = new ArrayList<Double>(pop.total_time);
		total_distance = new ArrayList<Double>(pop.total_distance);
		unsearved_customers = new ArrayList<Integer>(pop.unsearved_customers);
		fitness = pop.fitness;
		L = pop.L;
		J = pop.J;
	}
	
	@Override
    public Population clone() { //基本的にはpublic修飾子を付け、自分自身の型を返り値とする
		Population b=null;

        /*ObjectクラスのcloneメソッドはCloneNotSupportedExceptionを投げる可能性があるので、try-catch文で記述(呼び出し元に投げても良い)*/
        try {
            b=(Population)super.clone(); //親クラスのcloneメソッドを呼び出す(親クラスの型で返ってくるので、自分自身の型でのキャストを忘れないようにする)
        }catch (Exception e){
            e.printStackTrace();
        }
        return b;
    }

}