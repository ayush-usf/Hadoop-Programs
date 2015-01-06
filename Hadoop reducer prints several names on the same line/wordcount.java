import java.io.IOException;
import java.util.LinkedList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;



public class wordcount {

	public static class TokenizerMapper extends Mapper<Object, Text, Text, Text>{

	    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

	    	String line = value.toString();
	    	String[] elements = line.split(" "); 
    		Text position = new Text();
    		Text name = new Text();
	    	if(!elements[1].equals(null)){
	    		name.set(elements[1]);;
	    	}else{
	    		name.set("null");
	    	}
	    	
	    	if(!elements[2].equals(null)){
	    		position.set(elements[2]);
	    	}else{
	    		position.set("null");
	    	}
	        context.write(new Text(position), new Text(name));
	      
	    }
	  }

	  public static class IntSumReducer extends Reducer<Text,Text,Text,Text> {


	    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
	    	String name = ""; 
	    	LinkedList<String> names = new LinkedList<String>();
	    	for ( Text value : values){
	    		names.add(value.toString());
	    	}
	    	name = "";
	    	for( int i = 0; i < names.size(); i++){
	    		name = name + ", " + names.get(i);
	    	}
	    	
	      context.write(key, new Text(name));
	    }
	  }

	  public static void main(String[] args) throws Exception {
	    Configuration conf = new Configuration();
	    //Job.getInstance provides the config and the job name
	    Job job = Job.getInstance(conf, "word count");
	    // set jar by finding where the given class name came from 
	    job.setJarByClass(wordcount.class);

	    //set the mapper, reducer, and the combiner 
	    //with the above classes we defined
	    job.setMapperClass(TokenizerMapper.class);
	    //job.setCombinerClass(IntSumReducer.class);
	    job.setReducerClass(IntSumReducer.class);

	    //set the data type for the key in the output
	    job.setOutputKeyClass(Text.class);

	    //set the data type for the value in the output
	    job.setOutputValueClass(Text.class);

	    //provide the location of the input and output directory 
	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));
	    System.exit(job.waitForCompletion(true) ? 0 : 1);
	  }

}
