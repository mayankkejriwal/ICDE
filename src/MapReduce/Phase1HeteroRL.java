package MapReduce;
import java.io.IOException;
import java.util.ArrayList;

import java.util.HashSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import general.Parameters;


//The file used for experiments. Only writes out composite and TF scores.
//Baseline file is separate

public class Phase1HeteroRL {

	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
	    
	    private Text k = new Text();
	    private Text v=new Text();
	    private int limit=3;	//no. of non-forbidden tokens each field is allowed to emit
	  
	    //checks for forbidden words so can be used with property tables
	    //first column must be ID column. Does not consider that for tokenizing
	    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
	        String line = value.toString().toLowerCase();
	        String[] tokens=new utils.CSVParser().parseLine(line);
	        v.set(line);
	        for(int i=1; i<tokens.length; i++){
	        	
	        	String[] splits=tokens[i].split(Parameters.splitstring);
	        	
	        	ArrayList<String> list=new ArrayList<String>(splits.length);
	        	for(String split:splits){
	        		for(String forb:Parameters.forbiddenwords)
	        			if(split.equals(forb)||split.length()==0)
	        				continue;
	        			else
	        				list.add(split.trim());
	        		
	        	}
	        	if(list.size()>=1){
	        		//Collections.sort(list);	//in ascending order
	        		int count=0;
	        		for(int j=0; j<list.size() && count<limit; j++, count++){
	        			k.set(list.get(j));
	        			context.write(k, v);
	        	
	        		}
	        	}
	        }
	    }
	 } 
	        
	 public static class Reduce extends Reducer<Text, Text, Text, Text> {

		 private Text k = new Text();
		 private Text v=new Text();
	    
		 public void reduce(Text key, Iterable<Text> values, Context context) 
	      throws IOException, InterruptedException {
			 HashSet<String> A=new HashSet<String>();
		    	HashSet<String> B=new HashSet<String>();
		       
		        for (Text val : values) {
		        	if((new utils.CSVParser()).parseLine(val.toString())[0].contains("a"))
		        		A.add(val.toString());
		        	else
		        		B.add(val.toString());
		        }
		        if(A.size()==0||B.size()==0||A.size()*B.size()>Parameters.maxBucketPairs)
		        	;
	        else
		     //  k.set(Integer.toString(A.size()));
		     //  v.set(Integer.toString(B.size()));
		      // context.write(k,v);
	        	for(String a:A)
	        		for(String b:B){
	        				//double tf=general.FeaturizeToken.TF(a,b);
	        				double jaccard=general.FeaturizeToken.Jaccard(a,b);
	        				//double dice=general.FeaturizeToken.Dice(a,b);
	        				
	        				String l1=new utils.CSVParser().parseLine(a)[0];
	        				String l2=new utils.CSVParser().parseLine(b)[0];
	        				
	        				l1=l1.substring(1,l1.length());
	        				l2=l2.substring(1,l2.length());
	        				
	        				k.set(l1+" "+l2);
	        				
	        				
	        				
	        				v.set(Double.toString(jaccard)+" J");
	        				context.write(k,v);
	        		}
	        
	    }
	 }
	        
	 public static void main(String[] args) throws Exception {
	    Configuration conf = new Configuration();
	        
	        Job job = new Job(conf, "phase1heterorl");
	        job.setJarByClass(Phase1HeteroRL.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(Text.class);
	        
	    job.setMapperClass(Map.class);
	    job.setReducerClass(Reduce.class);
	        
	    job.setInputFormatClass(TextInputFormat.class);
	    job.setOutputFormatClass(TextOutputFormat.class);
	        
	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileInputFormat.addInputPath(job, new Path(args[1]));
	    FileOutputFormat.setOutputPath(job, new Path(args[2]));
	        
	    job.waitForCompletion(true);
	 }
	        
}
