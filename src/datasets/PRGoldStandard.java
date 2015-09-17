package datasets;

import java.util.*;
import java.io.*;

import utils.CSVParser;

public class PRGoldStandard {

	static String prefix="/host/heteroDatasets/icde_experiments/PR/";
	
	public static void main(String[] args)throws IOException{
		Scanner in=new Scanner(new FileReader(prefix+"restaurant1.csv"));
		HashMap<Integer, Integer> map=new HashMap<Integer,Integer>();
		int count=0;
		while(in.hasNextLine()){
			String f1=(new CSVParser()).parseLine(in.nextLine())[0].toLowerCase();
			String[] q=f1.split("-");
			int length=q.length-1;
			if(q[length].contains("restaurant")){
				q[length]=q[length].replace("restaurant", "");
				int a=Integer.parseInt(q[length]);
				if(a<111)
					map.put(a, count);
			}
			count++;
		}
		in.close();
		in=new Scanner(new FileReader(prefix+"restaurant2.csv"));
		PrintWriter out=new PrintWriter(new File(prefix+"goldStandard"));
		count=0;
		while(in.hasNextLine()){
			String f1=(new CSVParser()).parseLine(in.nextLine())[0].toLowerCase();
			String[] q=f1.split("-");
			int length=q.length-1;
			if(q[length].contains("restaurant")){
				q[length]=q[length].replace("restaurant", "");
				int a=Integer.parseInt(q[length]);
				if(a<111)
					if(map.containsKey(a))
						out.println(map.get(a)+" "+count);
			}
			count++;
		}
		
		in.close();
		out.close();
	}
}
