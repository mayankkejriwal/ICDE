package utils;

import general.Parameters;

import java.util.ArrayList;
import java.util.HashSet;

public class AttributeSim {
	
	private static int minDistance(String word1, String word2) {
		int len1 = word1.length();
		int len2 = word2.length();
	 
		// len1+1, len2+1, because finally return dp[len1][len2]
		int[][] dp = new int[len1 + 1][len2 + 1];
	 
		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}
	 
		for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}
	 
		//iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);
	 
				//if last two chars equal
				if (c1 == c2) {
					//update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;
	 
					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}
	 
		return dp[len1][len2];
	}
	
	public static double normalizedEditSimilarity(String a, String b){
		double dist=minDistance(a,b);
		if(a.length()>=b.length())
			return 1.0-(dist/a.length());
		else
			return 1.0-(dist/b.length());
	}
	
	public static double paddedBigramJaccard(String att1, String att2){
		String[] a1=att1.split(Parameters.splitstring);
		String[] a2=att2.split(Parameters.splitstring);
		if(IsNumOnlyArray(a1)&&IsNumOnlyArray(a2))
			return NumDistance(a1,a2);
		String b1="";
		String b2="";
		for(String aa1:a1)
			if(aa1.trim().length()==0)
				continue;
			else
				b1+=("*"+aa1+"*");
		
		
		for(String aa2:a2)
			if(aa2.trim().length()==0)
				continue;
			else
				b2+=("*"+aa2+"*");
		
		HashSet<String> t1=bigrams(b1);
		HashSet<String> t2=bigrams(b2);
		
		int common=0;
		for(String tt1:t1)
			if(t2.contains(tt1))
				common++;
		
		return common*1.0/(common+t1.size()+t2.size());
	}
	
	private static double NumDistance(String[] num1, String[] num2){
		ArrayList<Double> d1=new ArrayList<Double>();
		ArrayList<Double> d2=new ArrayList<Double>();
		for(String n:num1)
			if(n.trim().length()==0)
				continue;
			else
				d1.add(Double.parseDouble(n));
		
		for(String n:num2)
			if(n.trim().length()==0)
				continue;
			else
				d2.add(Double.parseDouble(n));
		
		
		
		if(d1.size()*d2.size()==0)
			return 0.0;
		
		double average1=0.0;
		for(double s:d1)
			average1+=returnClosestSim(s,d2);
		
		average1/=d1.size();
		
		double average2=0.0;
		for(double s:d2)
			average2+=returnClosestSim(s,d1);
		average2/=d2.size();
		
		return (average1+average2)/2;
	}
	
	private static double returnClosestSim(double a, ArrayList<Double> b){
		double max=Double.MAX_VALUE;
		
		for(double b1:b)
			if(Math.abs(b1-a)/((b1+a)/2)<max)
				max=Math.abs(b1-a)/((b1+a)/2);
		max=1.0-max;
		
		if(max>1.0)
			return 1.0;
		else if(max<0.0)
			return 0.0;
		else
			return max;
	}
	
	private static boolean IsNumOnlyArray(String[] atts){
		for(String a:atts){
			if(a.trim().length()==0)
				continue;
			 try { 
			        Double.parseDouble(a); 
			    } catch(NumberFormatException e) { 
			        return false; 
			    }
		}
		return true;
	}
	
	
	
	private static HashSet<String> bigrams(String a){
		
		HashSet<String> res=new HashSet<String>();
		for(int i=1; i<a.length(); i++)
			res.add(a.substring(i-1,i+1));
		
		return res;
	}
	
	public static void main(String[] args){
		String a="(270) 358-3874";
		String[] b=a.split(Parameters.splitstring);
		System.out.println(IsNumOnlyArray(b));
		for(String b1:b)
			System.out.println(b1);
	}
	
}
