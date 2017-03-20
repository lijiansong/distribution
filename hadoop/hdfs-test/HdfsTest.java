import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

public class HdfsTest {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String file="hdfs://localhost:9000/test.dat";
		Configuration conf=new Configuration();
		FileSystem fs=FileSystem.get(URI.create(file),conf);
		Path path=new Path(file);
		FSDataInputStream in_stream=fs.open(path);
		BufferedReader in=new BufferedReader(new InputStreamReader(in_stream));
		String s;
		while((s=in.readLine())!=null){
			System.out.println(s);
		}
		in.close();
		fs.close();
	}

}
