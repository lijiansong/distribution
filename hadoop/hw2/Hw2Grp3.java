/**
 *count and average time by modifying the word count example,
 *lijiansong, 201618013229011
 *@author Json Lee
 *@version V1.0.0
 */

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Hw2Grp3 {
	private static class Value implements Writable{
		int count;
		double sum;
		@Override
		public void readFields(DataInput arg0) throws IOException {
			count=arg0.readInt();
			sum=arg0.readDouble();
		}

		@Override
		public void write(DataOutput arg0) throws IOException {
			arg0.writeInt(count);
			arg0.writeDouble(sum);
		}
	}

  public static class TokenizerMapper extends Mapper<Object, Text, Text, Value>{
	  Value val=new Value();
	  public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		  String []token=value.toString().split("\\s+");
		  if(token.length==3){
			  String tmp=token[0]+" "+token[1];//src dst
			  try {
				val.sum=Double.parseDouble(token[2]);//time
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			val.count=1;
			context.write(new Text(tmp), val);
		  }
	  }
  }
  
  public static class IntSumCombiner extends Reducer<Text,Value,Text,Value> {
	  Value result=new Value();
	  public void reduce(Text key, Iterable<Value> values,Context context) throws IOException, InterruptedException {
		  result.count=0;
		  result.sum=0.0;
		  for(Value val:values){
			  result.count+=val.count;
			  result.sum+=val.sum;
		  }
		  context.write(key, result);
	  }
  }

  public static class IntSumReducer extends Reducer<Text,Value,Text,Text> {
	  public void reduce(Text key, Iterable<Value> values, Context context) throws IOException, InterruptedException {
		  int count=0;
		  double sum=0.0;
		  for(Value val:values){
			  count+=val.count;
			  sum+=val.sum;
		  }
		  String avg=new DecimalFormat("##0.000").format(sum/count);
		  context.write(key, new Text(count+" "+avg));
	  }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
    if (otherArgs.length < 2) {
      System.err.println("Usage: wordcount <in> [<in>...] <out>");
      System.exit(2);
    }

    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(Hw2Grp3.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumCombiner.class);
    job.setReducerClass(IntSumReducer.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Value.class);//Value
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    // add the input paths as given by command line
    for (int i = 0; i < otherArgs.length - 1; ++i) {
      FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
    }
    // add the output path as given by the command line
    FileOutputFormat.setOutputPath(job,new Path(otherArgs[otherArgs.length - 1]));
    System.out.println("succeed!");
    System.exit(job.waitForCompletion(true) ? 0 : 1);
    
  }
}
