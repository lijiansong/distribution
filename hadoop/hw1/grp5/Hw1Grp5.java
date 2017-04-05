import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;


public class Hw1Grp5 {

	/** Hbase table name */
	private static final String TABLENAME="Result";
	private static final String COLUMN_FAMILY="res";//res
	private static String mOpt;//operation: > gt; >= ge; == eq; != ne; le <=; lt <
	private static double mLimit;
	private static int[] mDistinctRowNo;
	private static HTable mHTable;
	
	
	
	public static void main(String[] args) throws IOException,URISyntaxException,MasterNotRunningException,ZooKeeperConnectionException {
		/**the argument's length must be 3, so the first step is to parse the argument string*/
		if(args.length==3){
			//java Hw1Grp5 R=/lineitem.tbl select:R1,gt,5.1 distinct:R2,R3,R5
			//> gt; >= ge; == eq; != ne; le <=; lt <
			//parse the argument string
			String hdfsFilePath="hdfs://localhost:9000"+args[0].substring(2);
			String []select=args[1].split(":")[1].split(",");//R1,gt,5.1
			int selectRowNo=Integer.parseInt(select[0].substring(select[0].indexOf("R")+1));
			mOpt=select[1];
			mLimit=Double.parseDouble(select[2]);//5.1
			String []distinct=args[2].split(":")[1].split(",");
			mDistinctRowNo=new int[distinct.length];
			for(int i=0;i<distinct.length;++i){
				mDistinctRowNo[i]=Integer.parseInt(distinct[i].substring(distinct[i].indexOf("R")+1));
				System.out.println("distinct row: "+mDistinctRowNo[i]+" ");
			}
			
			//create hbase table
			HTableDescriptor hTableDescriptor=new HTableDescriptor(TableName.valueOf(TABLENAME));
			hTableDescriptor.addFamily(new HColumnDescriptor(COLUMN_FAMILY));
			Configuration conf=HBaseConfiguration.create();
			HBaseAdmin hBaseAdmin=new HBaseAdmin(conf);
			/**judge whether the table exists, if it does exist,<br> drop it and create the table named "Result"*/
			if(hBaseAdmin.tableExists(TABLENAME)){
				hBaseAdmin.disableTable(TABLENAME);
				hBaseAdmin.deleteTable(TABLENAME);
			}
			hBaseAdmin.createTable(hTableDescriptor);
			hBaseAdmin.close();
			mHTable=new HTable(conf, TABLENAME);
			
			//hdfs
			FileSystem hdfs=FileSystem.get(URI.create(hdfsFilePath),new Configuration());
			FSDataInputStream inputStream=hdfs.open(new Path(hdfsFilePath));
			BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
			
			String line;
			//ArrayList<ArrayList<String> > dataList=new ArrayList<ArrayList<String> >();
			ArrayList<String> dataList=new ArrayList<String>();
			while((line=reader.readLine())!=null){
				String []oneLine=line.split("\\|");
				//judge
				double distinctKey=Double.parseDouble(oneLine[selectRowNo]);
				if(judge(distinctKey)){
					String result="";
					for(int row:mDistinctRowNo){
						result+=oneLine[row]+'|';
						//dataList.add(oneLine[row]);
					}
					dataList.add(result);
				}
			}
			inputStream.close();
			hdfs.close();
			
			//sort
			Object []res=dataList.toArray();
			Arrays.sort(res);
			dataList.clear();
			dataList.add(res[0].toString());
			//distinct
			for(int i=1;i<res.length;++i){
				if(!res[i].toString().equals(res[i-1].toString())){
					dataList.add(res[i].toString());
				}
			}
			
			//insert into hbase
			int rowKey=0;
			for(String ans:dataList){
				Put put=new Put((rowKey+"").getBytes());
				String []result=ans.split("\\|");
				for(int i=0;i<result.length;++i){
					put.add(COLUMN_FAMILY.getBytes(),("R"+mDistinctRowNo[i]).getBytes(),result[i].getBytes());
				}
				mHTable.put(put);
				++rowKey;
			}
			mHTable.close();
			System.out.println("succeed!");
		}
		else{
			//args error
		}
	}
	
	private static boolean judge(double key){
		if(mOpt.equals("gt")) return key>mLimit;
		if(mOpt.equals("ge")) return key>=mLimit;
		if(mOpt.equals("eq")) return key==mLimit;
		if(mOpt.equals("ne")) return key!=mLimit;
		if(mOpt.equals("le")) return key<=mLimit;
		if(mOpt.equals("lt")) return key<mLimit;
		//default
		return false;
	}
	
}
