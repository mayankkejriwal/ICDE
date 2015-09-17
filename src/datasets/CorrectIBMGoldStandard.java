package datasets;

import java.util.*;
import java.io.*;

import utils.CSVParser;
//IBM gold standard was faulty; this is a correction
public class CorrectIBMGoldStandard {

	//ibm is expected to be file1, also first column in gs
	public static void correct(String file1, String gs, String output)throws IOException{
		HashMap<Integer, Integer> map=new HashMap<Integer, Integer>();
		int count=0;
		Scanner in=new Scanner(new FileReader(file1));
		while(in.hasNextLine()){
			int i=Integer.parseInt((new CSVParser()).parseLine(in.nextLine())[0]);
			map.put(i-1,count);
			count++;
		}
		in.close();
		
		in=new Scanner(new FileReader(gs));
		PrintWriter out=new PrintWriter(new File(output));
		while(in.hasNextLine()){
			String[] p=in.nextLine().split(" ");
			out.println(map.get(Integer.parseInt(p[0]))+" "+p[1]);
		}
		
		in.close();
		out.close();
	}
	
	public static void main(String[] args)throws IOException{
		String prefix="/host/heteroDatasets/icde_experiments/game3/";
		
		correct(prefix+"ibm.csv",prefix+"goldStandard_ibm_vgchartz",prefix+"new");
		
		
	}
}
