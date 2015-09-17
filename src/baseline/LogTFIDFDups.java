package baseline;

import general.Parameters;

import java.util.*;
import java.io.*;

public class LogTFIDFDups {

	
	private HashMap<String, Double> idf1;
	private HashMap<String, Double> idf2;
	
	private ArrayList<HashMap<String,Double>> tf1;
	private ArrayList<HashMap<String,Double>> tf2;
	
	
	//normalized
	ArrayList<HashMap<String,Double>> tfidf1;
	ArrayList<HashMap<String,Double>> tfidf2;
	int corpussize1;
	int corpussize2;
	HashMap<String, HashSet<Integer>> inv_1;
	HashMap<String, HashSet<Integer>> inv_2;
	
	boolean one=false;
	
	double thresh=0.01;
	
	public LogTFIDFDups(String file1, String file2)throws IOException{
		//file1
		
		Scanner in=new Scanner(new FileReader(file1));
		idf1=new HashMap<String,Double>();
		tf1=new ArrayList<HashMap<String,Double>>();
		inv_1=new HashMap<String,HashSet<Integer>>();
		int count=0;
		while(in.hasNextLine()){
			String[] q=in.nextLine().split(Parameters.splitstring);
			HashMap<String,Double> m=new HashMap<String,Double>();
			HashSet<String> forbidden=new HashSet<String>();
			int total=0;
			for(String q1:q){
				
					q1=q1.toLowerCase().trim();
					if(q1.length()==0)
						continue;
					total++;
					if(!m.containsKey(q1))
						m.put(q1, 1.0);
					else
						m.put(q1, m.get(q1)+1);
					
					if(!forbidden.contains(q1)){
						forbidden.add(q1);
						if(!idf1.containsKey(q1))
							idf1.put(q1, 1.0);
						else
							idf1.put(q1, idf1.get(q1)+1);
					}
					
					if(!inv_1.containsKey(q1))
						inv_1.put(q1, new HashSet<Integer>());
					inv_1.get(q1).add(count);
			}
			if(total!=0)
				for(String f:m.keySet())
					m.put(f,m.get(f)/total);
			
			tf1.add(m);		
			count++;
		}
		in.close();
		corpussize1=tf1.size();
		
		//file2
		
		in=new Scanner(new FileReader(file2));
		idf2=new HashMap<String,Double>();
		tf2=new ArrayList<HashMap<String,Double>>();
		inv_2=new HashMap<String,HashSet<Integer>>();
		count=0;
		while(in.hasNextLine()){
			String[] q=in.nextLine().split(Parameters.splitstring);
			HashMap<String,Double> m=new HashMap<String,Double>();
			int total=0;
			HashSet<String> forbidden=new HashSet<String>();
			for(String q1:q){
				
					q1=q1.toLowerCase().trim();
					if(q1.length()==0)
						continue;
					total++;
					if(!m.containsKey(q1))
						m.put(q1, 1.0);
					else
						m.put(q1, m.get(q1)+1);
					
					if(!forbidden.contains(q1)){
						forbidden.add(q1);
						if(!idf2.containsKey(q1))
							idf2.put(q1, 1.0);
						else
							idf2.put(q1, idf2.get(q1)+1);
					}
					
					
					if(!inv_2.containsKey(q1))
						inv_2.put(q1, new HashSet<Integer>());
					inv_2.get(q1).add(count);
			}
			if(total!=0)
				for(String f:m.keySet())
					m.put(f,m.get(f)/total);
			tf2.add(m);			
			count++;
		}
		in.close();
		corpussize2=tf2.size();
		
		//normalize idf
		for(String a:idf1.keySet())
			idf1.put(a,corpussize1*1.0/idf1.get(a));
		for(String a:idf2.keySet())
			idf2.put(a,corpussize2*1.0/idf2.get(a));
		
		tfidf1=new ArrayList<HashMap<String,Double>>();
		tfidf2=new ArrayList<HashMap<String,Double>>();
		normalize();
	}
	
	//just for a single file. Everything ending with '2' is inapplicable
	public LogTFIDFDups(ArrayList<String> records){
		one=true;
		idf1=new HashMap<String,Double>();
		tf1=new ArrayList<HashMap<String,Double>>();
		inv_1=new HashMap<String,HashSet<Integer>>();
		int count=0;
		for(String r:records){
			String[] q=r.split(Parameters.splitstring);
			HashMap<String,Double> m=new HashMap<String,Double>();
			HashSet<String> forbidden=new HashSet<String>();
			int total=0;
			for(String q1:q){
				
					q1=q1.toLowerCase().trim();
					if(q1.length()==0)
						continue;
					total++;
					if(!m.containsKey(q1))
						m.put(q1, 1.0);
					else
						m.put(q1, m.get(q1)+1);
					
					if(!forbidden.contains(q1)){
						forbidden.add(q1);
						if(!idf1.containsKey(q1))
							idf1.put(q1, 1.0);
						else
							idf1.put(q1, idf1.get(q1)+1);
					}
					
					if(!inv_1.containsKey(q1))
						inv_1.put(q1, new HashSet<Integer>());
					inv_1.get(q1).add(count);
			}
			if(total!=0)
				for(String f:m.keySet())
					m.put(f,m.get(f)/total);
			
			tf1.add(m);		
			count++;
		}
		
		corpussize1=tf1.size();
		for(String a:idf1.keySet())
			idf1.put(a,corpussize1*1.0/idf1.get(a));
		tfidf1=new ArrayList<HashMap<String,Double>>();
		normalize();
	}
	
	public LogTFIDFDups(ArrayList<String> records1, ArrayList<String> records2)throws IOException{
		//file1
		
		
		idf1=new HashMap<String,Double>();
		tf1=new ArrayList<HashMap<String,Double>>();
		inv_1=new HashMap<String,HashSet<Integer>>();
		int count=0;
		for(String r:records1){
			String[] q=r.split(Parameters.splitstring);
			HashMap<String,Double> m=new HashMap<String,Double>();
			HashSet<String> forbidden=new HashSet<String>();
			int total=0;
			for(String q1:q){
				
					q1=q1.toLowerCase().trim();
					if(q1.length()==0)
						continue;
					total++;
					if(!m.containsKey(q1))
						m.put(q1, 1.0);
					else
						m.put(q1, m.get(q1)+1);
					
					if(!forbidden.contains(q1)){
						forbidden.add(q1);
						if(!idf1.containsKey(q1))
							idf1.put(q1, 1.0);
						else
							idf1.put(q1, idf1.get(q1)+1);
					}
					
					if(!inv_1.containsKey(q1))
						inv_1.put(q1, new HashSet<Integer>());
					inv_1.get(q1).add(count);
			}
			if(total!=0)
				for(String f:m.keySet())
					m.put(f,m.get(f)/total);
			
			tf1.add(m);		
			count++;
		}
		
		corpussize1=tf1.size();
		
		//file2
		
		
		idf2=new HashMap<String,Double>();
		tf2=new ArrayList<HashMap<String,Double>>();
		inv_2=new HashMap<String,HashSet<Integer>>();
		count=0;
		for(String r:records2){
			String[] q=r.split(Parameters.splitstring);
			HashMap<String,Double> m=new HashMap<String,Double>();
			int total=0;
			HashSet<String> forbidden=new HashSet<String>();
			for(String q1:q){
				
					q1=q1.toLowerCase().trim();
					if(q1.length()==0)
						continue;
					total++;
					if(!m.containsKey(q1))
						m.put(q1, 1.0);
					else
						m.put(q1, m.get(q1)+1);
					
					if(!forbidden.contains(q1)){
						forbidden.add(q1);
						if(!idf2.containsKey(q1))
							idf2.put(q1, 1.0);
						else
							idf2.put(q1, idf2.get(q1)+1);
					}
					
					
					if(!inv_2.containsKey(q1))
						inv_2.put(q1, new HashSet<Integer>());
					inv_2.get(q1).add(count);
			}
			if(total!=0)
				for(String f:m.keySet())
					m.put(f,m.get(f)/total);
			tf2.add(m);			
			count++;
		}
		
		corpussize2=tf2.size();
		
		//normalize idf
		for(String a:idf1.keySet())
			idf1.put(a,corpussize1*1.0/idf1.get(a));
		for(String a:idf2.keySet())
			idf2.put(a,corpussize2*1.0/idf2.get(a));
		
		tfidf1=new ArrayList<HashMap<String,Double>>();
		tfidf2=new ArrayList<HashMap<String,Double>>();
		normalize();
	}
	
	private void normalize(){
		for(HashMap<String,Double> a:tf1){
			HashMap<String,Double> res=new HashMap<String,Double>();
			double total=0;
			for(String m:a.keySet()){
				res.put(m, Math.log(a.get(m)+1)*Math.log(idf1.get(m)+1));
				total+=Math.log(a.get(m)+1)*Math.log(idf1.get(m)+1);
			}
			for(String m:res.keySet())
				res.put(m, res.get(m)/total);
			tfidf1.add(res);
		}
		if(!one)
		for(HashMap<String,Double> a:tf2){
			HashMap<String,Double> res=new HashMap<String,Double>();
			double total=0;
			for(String m:a.keySet()){
				res.put(m, Math.log(a.get(m)+1)*Math.log(idf2.get(m)+1));
				total+=Math.log(a.get(m)+1)*Math.log(idf2.get(m)+1);
			}
			for(String m:res.keySet())
				res.put(m, res.get(m)/total);
			tfidf2.add(res);
		}
			
	}
	
	//not sorted!
	public void printScoredFile(String outfile)throws IOException{
		PrintWriter out=new PrintWriter(new File(outfile));
		for(String s:inv_1.keySet())
			if(inv_2.containsKey(s))
				printScoresPairs(inv_1.get(s),inv_2.get(s),out);
		out.close();
	}
	
	private void printScoresPairs(HashSet<Integer> t1, HashSet<Integer> t2, PrintWriter out){
		double score=0.0;
		for(int i:t1){
			
			HashMap<String, Double> s1=tfidf1.get(i);
			
			for(int j:t2){
				score=0;
				HashMap<String, Double> s2=tfidf2.get(j);
				for(String l:s1.keySet()){
					if(!s2.containsKey(l))
						continue;
					score+=s1.get(l)*s2.get(l);
				}
				if(score>=thresh)
					out.println(i+" "+j+"\t"+score+" "+"TF");
			}
		}
	}
	
	public static void main(String[] args)throws IOException{
		String prefix="/host/heteroDatasets/journal/used_finally/Restaurants/";
		LogTFIDFDups m=new LogTFIDFDups(prefix+"file1.csv",prefix+"file2.csv");
		m.printScoredFile(prefix+"TF_output");
	}
}
