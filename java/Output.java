import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Output {
	public static void output(Load_problem problem, long e_s, Genetic_algorithm pops){
		String[] crossover = {"BCRC","UOX"};
		String[] evaluation = {"WSM","SR","PR"};
		String make_filename = problem.benchmark+"_"+crossover[problem.crossover]+"_"+evaluation[problem.evaluation]+"_ps"+String.valueOf(problem.pop_size)+"_mgs"+String.valueOf(problem.maxgen)+"_k"+String.valueOf(problem.tourn_k)+"_e"+String.valueOf(problem.elitism)+"_pc"+String.valueOf(problem.pc)+"_pm"+String.valueOf(problem.pm);
		Path path1 = Paths.get("/Users/shimizukengo/Desktop/important/study_abroad/classes/Evolutional_Computation/assignment/program_java/_VRPTW_/output/"+problem.experiment);
		Path path2 = Paths.get(path1+"/"+make_filename);
		
		//If the folder does NOT exit, make this folder in this path
		if (Files.exists(path1) == false) {
			try{
				Files.createDirectory(path1);
			}catch(IOException e){
			    System.out.println(e);
			}
		}

		//If the folder does NOT exit, make this folder in this path
		if (Files.exists(path2) == false) {
			try{
				Files.createDirectory(path2);
			}catch(IOException e){
			    System.out.println(e);
			}
		}
		int count = -1;
		for (int p=0; p<pops.pop.chrom.size(); p++) {
			if (pops.pop.fitness.get(p) == 1.0) {
				count = count + 1;
				String path = "/Users/shimizukengo/Desktop/important/study_abroad/classes/Evolutional_Computation/assignment/program_java/_VRPTW_/output/"+problem.experiment+"/"+make_filename+"/"+make_filename+"_"+problem.ite_num+"_position"+count+".dat";
				File newfile = new File(path);
				try{
					newfile.createNewFile();
					FileWriter filewriter = new FileWriter(newfile);
	
					filewriter.write("#computetime:"+String.valueOf(e_s)+"[ms]\tfitness:"+String.valueOf(pops.elite_pop.fitness.get(0))+"\tdistance:"+String.valueOf(pops.min_distance_tracker.get(problem.maxgen-1))+"\tveni_num\t"+String.valueOf(pops.min_vehinum_tracker.get(problem.maxgen-1))+"\n");
					filewriter.write("#pop_size:"+String.valueOf(problem.pop_size)+"\tgeneration:"+String.valueOf(problem.maxgen)+"\tk:"+String.valueOf(problem.tourn_k)+"\tPc:"+String.valueOf(problem.pc)+"\tPm:"+String.valueOf(problem.pm)+"\n");
					filewriter.write("#position x\tposition y\tgeneration\tbest fitness\n");
	
					for (int i=0; i<pops.pop.chrom.get(count).size(); i++) {
						for (int j=0; j<pops.pop.chrom.get(count).get(i).size(); j++) {
							filewriter.write(problem.customer.get(pops.pop.chrom.get(count).get(i).get(j)).pos[0]+"\t"+problem.customer.get(pops.pop.chrom.get(count).get(i).get(j)).pos[1]+"\n");
						}
					}
	
					filewriter.close();
				}catch(IOException e){
					System.out.println(e);
				}
			}
		}
	}
	

	public static void output_minimum_transition(Load_problem problem, long e_s, Genetic_algorithm pops){
		String[] crossover = {"BCRC","UOX"};
		String[] evaluation = {"WSM","SR","PR"};
		String make_filename = problem.benchmark+"_"+crossover[problem.crossover]+"_"+evaluation[problem.evaluation]+"_ps"+String.valueOf(problem.pop_size)+"_mgs"+String.valueOf(problem.maxgen)+"_k"+String.valueOf(problem.tourn_k)+"_e"+String.valueOf(problem.elitism)+"_pc"+String.valueOf(problem.pc)+"_pm"+String.valueOf(problem.pm);
		Path path1 = Paths.get("/Users/shimizukengo/Desktop/important/study_abroad/classes/Evolutional_Computation/assignment/program_java/_VRPTW_/output/"+problem.experiment);
		Path path2 = Paths.get(path1+"/"+make_filename);

		//If the folder does NOT exit, make this folder in this path
		if (Files.exists(path1) == false) {
			try{
				Files.createDirectory(path1);
			}catch(IOException e){
			    System.out.println(e);
			}
		}
		//If the folder does NOT exit, make this folder in this path
		if (Files.exists(path2) == false) {
			try{
				Files.createDirectory(path2);
			}catch(IOException e){
			    System.out.println(e);
			}
		}

		//make file
		String path = "/Users/shimizukengo/Desktop/important/study_abroad/classes/Evolutional_Computation/assignment/program_java/_VRPTW_/output/"+problem.experiment+"/"+make_filename+"/"+make_filename+"_"+problem.ite_num+".dat";
		File newfile = new File(path);
		try{
		    newfile.createNewFile();
		    FileWriter filewriter = new FileWriter(newfile);

		    //write comment in .dat file
		    filewriter.write("#computetime:"+String.valueOf(e_s)+"[ms]\n");
			filewriter.write("#pop_size:"+String.valueOf(problem.pop_size)+"\tgeneration:"+String.valueOf(problem.maxgen)+"\tk:"+String.valueOf(problem.tourn_k)+"\tPc:"+String.valueOf(problem.pc)+"\tPm:"+String.valueOf(problem.pm)+"\n");
			filewriter.write("#generation\tvehi_num\ttotal_distance\ttotal_time\n");

			//write data in .dat file
			for (int i=0; i<problem.maxgen; i++) {
				filewriter.write(String.valueOf(i)+"\t"+String.valueOf(pops.min_vehinum_tracker.get(i))+"\t"+String.valueOf(pops.min_distance_tracker.get(i))+"\t"+String.valueOf(pops.min_time_tracker.get(i))+"\n");
			}

		    filewriter.close();
		}catch(IOException e){
		    System.out.println(e);
		}
	}

}
