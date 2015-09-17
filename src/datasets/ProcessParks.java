package datasets;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import utils.CSVParser;

public class ProcessParks {
	
	public static void main(String[] args)throws IOException{
		generateGoldStandard();
	}
	
	public static void generateGoldStandard()throws IOException{
		String prefix="/host/heteroDatasets/icde_experiments/parks1/";
		String output=prefix+"goldStandard_NPP_nps";
		Scanner in=new Scanner(new FileReader(prefix+"National_Parks_Plus.csv"));
		int count=0;
		ArrayList<String> NPP_lines=new ArrayList<String>();
		HashMap<Integer, HashSet<Integer>> latitude=new HashMap<Integer, HashSet<Integer>>();
		HashMap<Integer, HashSet<Integer>> longitude=new HashMap<Integer, HashSet<Integer>>();
		while(in.hasNextLine()){
			String line=in.nextLine();
			NPP_lines.add(line);
			String[] fields=(new CSVParser()).parseLine(line);
			
			double lat=Double.parseDouble(fields[0]);
			double lo=Double.parseDouble(fields[1]);
			int l1=(int) Math.round(lat);
			int l2=(int) Math.round(lo);
			
			if(!latitude.containsKey(l1))
				latitude.put(l1, new HashSet<Integer>());
			latitude.get(l1).add(count);
			
			if(!longitude.containsKey(l2))
				longitude.put(l2, new HashSet<Integer>());
			longitude.get(l2).add(count);
			
			
			count++;
		}
		
		in.close();
		PrintWriter out=new PrintWriter(new File(output));
		in=new Scanner(new FileReader(prefix+"national_park_service.csv"));
		count=0;
		
		while(in.hasNextLine()){
			String[] fields=(new CSVParser()).parseLine(in.nextLine());
			
			int i=fields.length-1;
			double lat=Double.parseDouble(fields[i]);
			double lo=Double.parseDouble(fields[i-1]);
			int l1=(int) Math.round(lat);
			int l2=(int) Math.round(lo);
			
			if(latitude.containsKey(l1)&&longitude.containsKey(l2)){
				HashSet<Integer> p=intersectHashSets(latitude.get(l1),longitude.get(l2));
				if(p.size()>=1)
					out.println(getClosest(lat,lo,p,NPP_lines)+" "+count);
				
			}
			
			count++;
		}
		in.close();
		out.close();
	}
	
	private static int getClosest(double lat, double lo, HashSet<Integer> p, ArrayList<String> lines) throws IOException{
		if(p.size()==1)
			for(int i:p)
				return i;
		
		
		double min=Double.MAX_VALUE;
		int index=-1;
		
		for(int i:p){
			String[] fields=(new CSVParser()).parseLine(lines.get(i));
			
			double lat1=Double.parseDouble(fields[0]);
			double lo1=Double.parseDouble(fields[1]);
			if(l2norm(lat1,lo1,lat,lo)<min){
				min=l2norm(lat1,lo1,lat,lo);
				index=i;
			}
		}
		
		return index;
		
	}
	
	private static double l2norm(double lat, double lo,double lat1, double lo1){
		return Math.sqrt(Math.pow(lat-lat1,2)+Math.pow(lo-lo1,2));
	}
	
	private static HashSet<Integer> intersectHashSets(HashSet<Integer> a, HashSet<Integer> b){
		HashSet<Integer> res=new HashSet<Integer>();
		for(int a1:a)
			if(b.contains(a1))
				res.add(a1);
		return res;
	}
}
