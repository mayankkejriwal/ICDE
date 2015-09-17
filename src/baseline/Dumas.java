package baseline;

import general.Parameters;

import java.util.*;
import java.io.*;

import utils.AttributeSim;
import utils.CSVParser;
import utils.HungarianAlgorithm;

public class Dumas {

	LogTFIDFDups[] ds1;
	LogTFIDFDups[] ds2;
	ArrayList<String> records1;
	ArrayList<String> records2;
	int att1;
	int att2;
	double[][] matrix;
	
	public Dumas(String file1, String file2, String sortedScores, int k, double theta)throws IOException{
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
		
		
		ArrayList<String> r1=new ArrayList<String>();
		
		ArrayList<String> r2=new ArrayList<String>();
		for(int i=0; i<indices.length; i++){
			r1.add(f1.get(indices[i][0]).toLowerCase());
			r2.add(f2.get(indices[i][1]).toLowerCase());
		}
		
		records1=r1;
		records2=r2;
		
		att1=(new CSVParser()).parseLine(records1.get(0)).length;
		att2=(new CSVParser()).parseLine(records2.get(0)).length;
		
		ds1=new LogTFIDFDups[att1];
		
		for(int i=0; i<att1; i++){
			ArrayList<String> r=new ArrayList<String>();
			for(String j:records1)
				r.add(new CSVParser().parseLine(j)[i]);
			ds1[i]=new LogTFIDFDups(r);
		}
		
		ds2=new LogTFIDFDups[att2];
		for(int i=0; i<att2; i++){
			ArrayList<String> r=new ArrayList<String>();
			for(String j:records2)
				r.add(new CSVParser().parseLine(j)[i]);
			ds2[i]=new LogTFIDFDups(r);
		}
		
		matrix=new double[att1][att2];
		populateMatrix(theta);
	}
	
	private void populateMatrix(double theta) throws IOException{
		for(int k=0; k<records1.size(); k++){
			String[] a1=(new CSVParser()).parseLine(records1.get(k));
			String[] a2=(new CSVParser()).parseLine(records2.get(k));
			for(int i=0; i<att1; i++){
				String[] tokens1=a1[i].split(Parameters.splitstring);
				
				for(int j=0; j<att2; j++){
					String[] tokens2=a2[j].split(Parameters.splitstring);
					double score=0.0;
					for(String t1:tokens1){
						String m=maxTermSim(t1,tokens2,theta);
						if(m!=null)
							score+=(AttributeSim.normalizedEditSimilarity(t1, m)*
									ds1[i].tfidf1.get(k).get(t1)*
									ds2[j].tfidf1.get(k).get(m));
					}
					matrix[i][j]-=score/records1.size();
				}
				
			}
		}
	}
	
	private String maxTermSim(String a, String[] b, double theta){
		double max=theta;
		int q=-1;
		for(int i=0; i<b.length; i++){
			double sim=AttributeSim.normalizedEditSimilarity(a, b[i]);
			if(sim>max){
				max=sim;
				q=i;
			}
		}
		if(q==-1)
			return null;
		else
		
			return b[q];
		
	}
	
	//return mappings in n by 2 matrix
	public int[][] execute(){
		HungarianAlgorithm h=new HungarianAlgorithm(matrix);
		int[] q=h.execute();
		int count=0;
		for(int q1:q)
			if(q1!=-1)
				count++;
		int[][] res=new int[count][2];
		int l=0;
		for(int i=0; i<q.length; i++)
			if(q[i]!=-1){
				res[l][0]=i;
				res[l][1]=q[i];
				System.out.println(res[l][0]+" "+res[l][1]);
				l++;
			}
		return res;
	}
	
	public void execute(String outfile)throws IOException{
		HungarianAlgorithm h=new HungarianAlgorithm(matrix);
		int[] q=h.execute();
		int count=0;
		for(int q1:q)
			if(q1!=-1)
				count++;
		int[][] res=new int[count][2];
		int l=0;
		PrintWriter out=new PrintWriter(new File(outfile));
		for(int i=0; i<q.length; i++)
			if(q[i]!=-1){
				res[l][0]=i;
				res[l][1]=q[i];
				out.println(res[l][0]+" "+res[l][1]);
				l++;
			}
		out.close();
	}
	
	public static void main(String[] args)throws IOException{
		String prefix="/host/heteroDatasets/journal/used_finally/Restaurants/";
		Dumas d=new Dumas(prefix+"file1.csv",prefix+"file2.csv",
				prefix+"sortedScores/TF",1000,0.1);
		d.execute(prefix+"schema_dumas");
	}
}
