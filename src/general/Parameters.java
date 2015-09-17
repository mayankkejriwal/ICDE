package general;


public class Parameters {
	
	public static String splitstring="://|\\[|\\]|\\(|\\)|: |\t| |,|:|/|-|_|\\. |\\.|\\&|\"|#|;|; ";
	public static int num_feats=28; 
	public static int svm_num_feats=38;
	public static int maxBucketPairs=2000;
	public static double recall=0.8; //epsilon: more is better, the less noisy the sample
	public static double eta=0.2; //less the better, less noisy the sample
	public static String[] forbiddenwords={"null"}; //interpreted as case insensitive
	public static boolean DNF=false;
	public static int maxpairs=2000;	//try to keep all pairs parameters equal
}
