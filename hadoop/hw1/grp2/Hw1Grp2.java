import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

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

public class Hw1Grp2 {

	private static class Value{
		int count;
		double []result=new double[3];
	}
	/** Hbase table name */
	private static final String TABLENAME="Result";
	private static String mColumnFamily;//member variable, column family
	private static HTable mHTable;//member variable, Hbase table
	//private static int mCount;//the number of each group by key
	private static Map<Integer,Integer> mOpMap;//operation map, key is operation number, e.g. count 1, avg 2, max 3, value is column number
	//private static Map<Integer, Integer> mNewOpMap;//new operation map, key is operation number, e.g. count 1, avg 2, max 3, value is new column number, start from 0
	//private static double []result;//group by result, result[0] count, result[1] avg, result[2] max
	private static Hashtable<String, Value> mHashtable;//hash table, key is group by key, value is the result, e.g. count:result[0], avg:result[1], max:result[2]
	
	public static void main(String[] args) throws IOException,URISyntaxException,MasterNotRunningException,ZooKeeperConnectionException {
		/**the argument's length must be 3, so the first step is to parse the argument string*/
		if(args.length==3){
			//parse the argument string
			String hdfsFilePath="hdfs://localhost:9000"+args[0].substring(2);
			int groupByNo=Integer.parseInt(args[1].substring(args[1].indexOf("R")+1));//group by number
			//System.out.println("groupByNo: "+groupByNo);
			mColumnFamily=args[2].split(":")[0];//column family
			//System.out.println("cloumnfamily: "+mColumnFamily);
			
			String []operation=args[2].split(":")[1].split(",");
//			for(String s:operation){
//				System.out.println(s);
//			}
			mOpMap =new HashMap<Integer, Integer>();//key is count avg or max, value is the columnNo
			for(int i=0;i<operation.length;++i){
				if(operation[i].substring(0, 5).equals("count")){
					//count
					mOpMap.put(1, 0);
				}
				else if(operation[i].substring(0, 3).equals("avg")){
					//avg(R3)
					int indexR=operation[i].indexOf("R");
					int indexRight=operation[i].indexOf(")");
					int colNum=Integer.parseInt(operation[i].substring(indexR+1, indexRight));
					mOpMap.put(2, colNum);
				}
				else if(operation[i].substring(0, 3).equals("max")){
					//max(R4)
					int indexR=operation[i].indexOf("R");
					int indexRight=operation[i].indexOf(")");
					int colNum=Integer.parseInt(operation[i].substring(indexR+1, indexRight));
					mOpMap.put(3, colNum);
				}//else argument error
			}
			
			//create hbase table
			HTableDescriptor hTableDescriptor=new HTableDescriptor(TableName.valueOf(TABLENAME));
			hTableDescriptor.addFamily(new HColumnDescriptor(mColumnFamily));
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
			
			/**read the file data into memory,here we use a hash table to store the data*/
			FileSystem hdfs=FileSystem.get(URI.create(hdfsFilePath),new Configuration());
			FSDataInputStream inputStream=hdfs.open(new Path(hdfsFilePath));
			BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
			//mNewOpMap=new HashMap<Integer,Integer>();//new map key-value : operation-new column
			
			String line;
			mHashtable=new Hashtable<String,Value>();
			
			while((line=reader.readLine())!=null){
				String []oneLine=line.split("\\|");
				Value val=new Value();
				val.count=0;
				Arrays.fill(val.result, 0);
				if(mHashtable.containsKey(oneLine[groupByNo])){
					val.count=mHashtable.get(oneLine[groupByNo]).count+1;
					for(Map.Entry<Integer, Integer> entry:mOpMap.entrySet()){
						switch (entry.getKey()) {
//						case 1://count
//							val.result[0]
//							break;
						case 2://avg
							val.result[1]=mHashtable.get(oneLine[groupByNo]).result[1]+Double.valueOf(oneLine[entry.getValue()]);
							break;
						case 3://max
							val.result[2]=Math.max(mHashtable.get(oneLine[groupByNo]).result[2], Double.valueOf(oneLine[entry.getValue()]));
							break;
						default:
							break;
						}
					}
				}else{//not contains
					val.count=1;
					for(Map.Entry<Integer, Integer> entry:mOpMap.entrySet()){
						switch (entry.getKey()) {
						case 2://avg
							val.result[1]=Double.valueOf(oneLine[entry.getValue()]);
							break;
						case 3://max
							val.result[2]=Double.valueOf(oneLine[entry.getValue()]);
							break;
						default:
							break;
						}
					}
				}
				mHashtable.put(oneLine[groupByNo], val);
			}
			inputStream.close();
			hdfs.close();
			//insert into hbase
			for(Entry<String, Value> entry:mHashtable.entrySet()){
				Put put=new Put(entry.getKey().getBytes());
				for(Map.Entry<Integer, Integer> mapEntry:mOpMap.entrySet()){
					switch (mapEntry.getKey()) {
					case 1:
						put.add(mColumnFamily.getBytes(),("count").getBytes(),(entry.getValue().count+"").getBytes());
						break;
					case 2:
						put.add(mColumnFamily.getBytes(),("avg(R"+mOpMap.get(mapEntry.getKey())+")").getBytes(),(new DecimalFormat("##0.00").format(entry.getValue().result[1]/entry.getValue().count).getBytes()));
						break;
					case 3:
						put.add(mColumnFamily.getBytes(),("max(R"+mOpMap.get(mapEntry.getKey())+")").getBytes(),(entry.getValue().result[2]+"").getBytes());
						break;
					default:
						break;
					}
				}
				mHTable.put(put);
			}
			mHTable.close();
			System.out.println("succeed!");
		}
		else{
			//args error
		}
	}
}
