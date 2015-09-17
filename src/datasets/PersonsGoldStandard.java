package datasets;


import java.io.*;
import java.util.*;

import utils.CSVParser;

public class PersonsGoldStandard {

	static String prefix="/host/heteroDatasets/icde_experiments/Persons/";
	
	public static void main(String[] args)throws IOException{
		HashMap<String,String> gold=new HashMap<String,String>();
		Scanner in1=new Scanner(new FileReader(prefix+"entity1.csv"));
		Scanner in2=new Scanner(new FileReader(prefix+"entity2.csv"));
		while(in1.hasNextLine())
			if(in2.hasNextLine())
				gold.put(new CSVParser().parseLine(in1.nextLine())[0], 
						new CSVParser().parseLine(in2.nextLine())[0]);
		
		in1.close();
		in2.close();
		in1=new Scanner(new FileReader(prefix+"person1.csv"));
		in2=new Scanner(new FileReader(prefix+"person2.csv"));
		HashMap<String,Integer> l1=new HashMap<String,Integer>();
		HashMap<String,Integer> l2=new HashMap<String,Integer>();
		int count=0;
		while(in1.hasNextLine()){
			l1.put(new CSVParser().parseLine(in1.nextLine())[0],count);
			count++;
		}
		in1.close();
		count=0;
		while(in2.hasNextLine()){
			l2.put(new CSVParser().parseLine(in2.nextLine())[0],count);
			count++;
		}
		in2.close();
		PrintWriter out=new PrintWriter(new File(prefix+"goldStandard"));
		for(String a: gold.keySet()){
			if(l1.get(a)==null){
				System.out.println(a);
				continue;
			}
			int c1=l1.get(a);
			int c2=l2.get(gold.get(a));
			out.println(c1+" "+c2);
		}
		
		out.close();
	}
	
	
}
