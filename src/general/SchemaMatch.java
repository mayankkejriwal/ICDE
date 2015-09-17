package general;

import java.io.*;
import java.util.*;

import utils.AttributeSim;
import utils.CSVParser;
public class SchemaMatch {

	
	double[][] matrix;	//number of rows always <= no. of columns
	boolean rev=false;	//true if row corresponds to file2 and column to file1
	
	//file1 and file2 must not have been prepped for RL
	//only considers top k of sortedscores for schema match
	
	public SchemaMatch(String file1, String file2, String sortedScores, int k)throws IOException{
		Scanner in=new Scanner(new FileReader(sortedScores));
		int[][] indices=new int[k][2];
		int count=0;
		while(in.hasNextLine() && count<k){
			String[] fields=in.nextLine().split("\t")[0].split(" ");
			indices[count][0]=Integer.parseInt(fields[0]);
			indices[count][1]=Integer.parseInt(fields[1]);
			count++;
		}
		in.close();
		
		ArrayList<String> f1=new ArrayList<String>();
		in=new Scanner(new FileReader(file1));
		while(in.hasNextLine())
			f1.add(in.nextLine());
		in.close();
		
		ArrayList<String> f2=new ArrayList<String>();
		in=new Scanner(new FileReader(file2));
		while(in.hasNextLine())
			f2.add(in.nextLine());
		in.close();
		
		
		int l1=(new CSVParser()).parseLine(f1.get(0)).length;
		int l2=(new CSVParser()).parseLine(f2.get(0)).length;
		
		int row=l1;
		int column=l2;
		
		if(l2<l1){
			column=l1;
			row=l2;
			rev=true;
		}
		
		matrix=new double[row][column];
		for(int[] i:indices){
			String[] fields1=(new CSVParser()).parseLine(f1.get(i[0]));
			String[] fields2=(new CSVParser()).parseLine(f2.get(i[1]));		
			if(rev){
				String[] fields3=fields1;
				fields1=fields2;
				fields2=fields3;
			}
			
			for(int m=0; m<fields1.length; m++)
				for(int n=0; n<fields2.length; n++)
					matrix[m][n]+=(AttributeSim.paddedBigramJaccard(fields1[m], fields2[n])/k);
		}
		
		
		
	}
	
	private HashMap<Integer,HashSet<Integer>> revMap(HashMap<Integer,HashSet<Integer>> orig){
		HashMap<Integer,HashSet<Integer>> res=new HashMap<Integer,HashSet<Integer>>();
		for(int key: orig.keySet()){
			
			for(int val:orig.get(key)){
				if(!res.containsKey(val))
					res.put(val, new HashSet<Integer>());
				res.get(val).add(key);
			}
		}
		return res;
		
	}
	
	private HashMap<Integer,HashSet<Integer>> performSchemaMatch(){
		
		double matrixAverage=0.0;
		for(double[] a:matrix)
			for(double b:a)
				matrixAverage+=b;
		
		matrixAverage/=matrix.length*matrix[0].length;
		
		HashMap<Integer,HashSet<Integer>> res=new HashMap<Integer,HashSet<Integer>>();
		for(int i=0; i<matrix.length; i++){
			
			double rowAverage=0.0;
			for(double q:matrix[i])
				rowAverage+=q;
			rowAverage/=matrix[i].length;
			
			double threshold=matrixAverage;
			if(matrixAverage<rowAverage)
				threshold=rowAverage;
			
			for(int j=0; j<matrix[i].length; j++)
				if(matrix[i][j]>=threshold){
					if(!res.containsKey(i))	
						res.put(i,new HashSet<Integer>());
					res.get(i).add(j);
				}
			
		}
		if(!rev)
			return res;
		else return revMap(res);
	}
	
	public void printMatchToFile(String file)throws IOException{
		HashMap<Integer,HashSet<Integer>> schemaMatch=performSchemaMatch();
		PrintWriter out=new PrintWriter(new File(file));
		for(int i:schemaMatch.keySet())
			for(int j:schemaMatch.get(i))
				out.println(i+" "+j);
		out.close();
	}
	
	public void printResults(String schemaGoldStandard)throws IOException{
		HashMap<Integer,HashSet<Integer>> res=performSchemaMatch();
		
		Scanner in=new Scanner(new FileReader(schemaGoldStandard));
		HashSet<String> gold=new HashSet<String>();
		
		while(in.hasNextLine()){
			gold.add(in.nextLine());
		}
		
		in.close();
		
		double prec=0.0;
		double rec=0.0;
		int count=0;
		int total=0;
		
		for(int i:res.keySet()){
			total+=res.get(i).size();
			for(int j:res.get(i)){
				String k=i+" "+j;
				if(gold.contains(k))
					count++;
				
			}
		}
		
		if(total==0)
			System.out.println("No mappings in set!");
		else{
			prec=count*1.0/total;
			rec=count*1.0/gold.size();
			System.out.println("precision: "+prec);
			System.out.println("recall: "+rec);
		}
	}
	
	public static void main(String[] args)throws IOException{
		String prefix="/host/heteroDatasets/icde_experiments/PR/";
		String file1="restaurant1.csv";
		String file2="restaurant2.csv";
		int k=10;
		SchemaMatch m=new SchemaMatch(prefix+file1,prefix+file2,
				prefix+"sortedScores/J",k);
		m.printMatchToFile(prefix+"schema_10");
		m.printResults(prefix+"goldStandard_schema");
		//System.out.println(m.rev);
	}
}
