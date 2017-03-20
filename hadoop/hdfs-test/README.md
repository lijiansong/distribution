## Hdfs Test
This archieve is my first HDFS project. You can follow the steps below to build it.

## Enviroment
- Ubuntu 14.04
- Hadoop 2.7.3
- Eclipse Neon.1 Release (4.6.1)
I build the plugin from the source code of hadoop-2.7.3-src, you can follow BUILDING.txt in the src folder to build it. Then follow this [page](http://blog.csdn.net/young_kim1/article/details/50208837) to config Eclipse...

## Build
- Fisrtly, you need to put the local file to HDFS by taking use of `copyFromLocal` or `put`, here I use `put` to copy test.dat to root of HDFS.
```
$ ./bin/hdfs dfs -put /home/json-lee/Desktop/test.dat /
$ ./bin/hdfs dfs -ls /
```
- Secondly, type the following code
```
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

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

```
Just run it by Run AS -> Run on Hadoop, then you can observe the result from the console...
