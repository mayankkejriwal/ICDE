package datasets;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import utils.CSVParser;

public class ProcessLibraries {

	public static void main(String[] args)throws IOException{
		
	}
	
	public static void generateGoldStandard()throws IOException{
		String prefix="/host/heteroDatasets/icde_experiments/libraries/";
		String output=prefix+"goldStandard_PL_pl";
		Scanner in=new Scanner(new FileReader(prefix+"PublicLibraries.csv"));
		int count=0;
		HashMap<String, Integer> map=new HashMap<String,Integer>();
		while(in.hasNextLine()){
			String[] fields=(new CSVParser()).parseLine(in.nextLine())[3].split(",");
			int i=fields.length-1;
			if(map.containsKey(fields[i]))
				System.out.println("warning! Found duplicate key "+fields[i]);
			else
				map.put(fields[i], count);
			count++;
		}
		in.close();
		PrintWriter out=new PrintWriter(new File(output));
		in=new Scanner(new FileReader(prefix+"public_libraries.csv"));
		count=0;
		while(in.hasNextLine()){
			String field=(new CSVParser()).parseLine(in.nextLine())[0];
			if(map.containsKey(field))
				out.println(map.get(field)+" "+count);
			count++;
		}
		in.close();
		out.close();
	}
	
	public static void deduplicate()throws IOException{
		String prefix="/host/heteroDatasets/icde_experiments/libraries/";
		String file="public_libraries.csv";
		String output=prefix+"public_libraries_deduped.csv";
		PrintWriter out=new PrintWriter(new File(output));
		Scanner in=new Scanner(new FileReader(prefix+file));
		HashSet<String> forbidden=new HashSet<String>();
		while(in.hasNextLine()){
			String line=in.nextLine();
			String num=(new CSVParser()).parseLine(line)[0];
			if(!forbidden.contains(num)){
				forbidden.add(num);
				out.println(line);
			}
		}
		in.close();
		out.close();
	}
}
