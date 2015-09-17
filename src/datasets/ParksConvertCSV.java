package datasets;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import utils.CSVParser;

public class ParksConvertCSV {
	
	public static void main(String[] args)throws IOException{
		String prefix="/host/heteroDatasets/icde_experiments/parks2/headers/";
		String file="1966-2012_LI_RN.csv";
		String output=prefix+"parks.csv";
		PrintWriter out=new PrintWriter(new File(output));
		Scanner in=new Scanner(new FileReader(prefix+file));
		while(in.hasNextLine()){
			String line=in.nextLine();
			String[] fields=(new CSVParser()).parseLine(line);
			for(int i=0; i<fields.length; i++)
				fields[i]=fields[i].trim();
			String e=new String("");
			for(String field:fields)
				e+=("\""+field+"\",");
			e=e.substring(0,e.length()-1);
			out.println(e);
		}
		in.close();
		out.close();
	}

}
