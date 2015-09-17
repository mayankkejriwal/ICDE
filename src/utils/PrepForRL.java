package utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class PrepForRL {

	
	public static void prep(String file1, String file2, String output1, String output2)throws IOException{
		Scanner in=new Scanner(new FileReader(file1));
		PrintWriter out=new PrintWriter(new File(output1));
		int count=0;
		while(in.hasNextLine()){
			String line=in.nextLine();
			out.println("A"+count+","+line);
			count++;
		}
		out.close();
		in.close();
		
		in=new Scanner(new FileReader(file2));
		out=new PrintWriter(new File(output2));
		count=0;
		while(in.hasNextLine()){
			String line=in.nextLine();
			out.println("B"+count+","+line);
			count++;
		}
		out.close();
		in.close();
	}
	
	public static void main(String[] args)throws IOException{
		String prefix="/host/heteroDatasets/icde_experiments/game/";
		
		String infile1=prefix+"vgchartz.csv";
		String infile2=prefix+"dbpedia.csv";
		prep(infile1,infile2,prefix+"file1.csv",prefix+"file2.csv");
		
	}
}
