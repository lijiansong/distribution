import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;

/**
 * lijiansong, 201618013229011, for more info, see [here](http://lijiansong.github.io)<br>
 * Group3, sort based group by
 * @author Json Lee,
 * @version V1.0.0
 */
public class Hw1Grp3 {

	/** Hbase table name */
	private static final String TABLENAME="Result";
	private static String mColumnFamily;//member variable, column family
	private static HTable mHTable;//member variable, Hbase table
	private static int mCount;//the number of each group by key
	private static Map<Integer,Integer> mOpMap;//operation map, key is operation number, e.g. count 1, avg 2, max 3, value is column number
	private static Map<Integer, Integer> mNewOpMap;//new operation map, key is operation number, e.g. count 1, avg 2, max 3, value is new column number, start from 0
	private static double []result;//group by result, result[0] count, result[1] avg, result[2] max
	/*
	 * this method is the main entrance
	 * @param args command line string
	 * @return void
	 * @exception IOException throw when something is wrong with I/O 
     * @exception URISyntaxException throw when something is wrong with URI
     * @exception MasterNotRunningException throw when the master is not running
     * @exception ZooKeeperConnectionException throw when the client can't connect to zookeeper
	 * */
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
//			System.out.println("OpMap: ");
//			for(Map.Entry<Integer, Integer> entry:mOpMap.entrySet()){
//				System.out.println(entry.getKey()+" "+entry.getValue());
//			}
			
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
			
			/**read the file data into memory,here we use a 2 dimension matrix to store the data.<br> To save the memory space, we only read the necessary data into the memory*/
			FileSystem hdfs=FileSystem.get(URI.create(hdfsFilePath),new Configuration());
			FSDataInputStream inputStream=hdfs.open(new Path(hdfsFilePath));
			BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
			mNewOpMap=new HashMap<Integer,Integer>();//new map key-value : operation-new column
			
			String line;
			final ArrayList<ArrayList<String> > dataList=new ArrayList<ArrayList<String> >();
			while((line=reader.readLine())!=null){
				ArrayList<String> lineTmp=new ArrayList<String>();
				String []oneLine=line.split("\\|");
				lineTmp.add(oneLine[groupByNo]);//group by key column
				//mNewOpMap.put(0, 0);
				int opCounter=1;
				for(Map.Entry<Integer, Integer> entry:mOpMap.entrySet()){
					if(entry.getKey()==1){
						mNewOpMap.put(1, 0);
						continue;
					}
					lineTmp.add(oneLine[entry.getValue()]);
					if(!mNewOpMap.containsKey(entry.getKey())){
						mNewOpMap.put(entry.getKey(), opCounter);
						++opCounter;
					}
				}
				dataList.add(lineTmp);
			}
			inputStream.close();
			hdfs.close();
			System.out.println("newOpMap: ");
			for(Map.Entry<Integer, Integer> entry:mNewOpMap.entrySet()){
				System.out.println(entry.getKey()+" "+entry.getValue());
			}
			
			/**process the data by sorting them then calculate the max and average*/
			Collections.sort(dataList,new Comparator<ArrayList<String> >() {
				@Override
				public int compare(ArrayList<String> o1, ArrayList<String> o2) {
					//return o1.get(groupByNo).compareTo(o2.get(groupByNo));
					return o1.get(0).compareTo(o2.get(0));
				}
			});
//			System.out.println("sorted dataList: ");
//			for(ArrayList<String> list : dataList){
//				for(String data:list){
//					System.out.print(data+" ");
//				}
//				System.out.println();
//			}
			
			String groupByStr=dataList.get(0).get(0);
			mCount=0;
			//result=new double[mNewOpMap.size()];
			result=new double[3];result[2]=Double.MIN_VALUE;
			Arrays.fill(result, 0);
			for(ArrayList<String> record:dataList){
				if(!groupByStr.equals(record.get(0))){
					hbaseInsert(groupByStr);
					mCount=0;
					groupByStr=record.get(0);
					Arrays.fill(result, 0);
				}
				++mCount;
				for(Map.Entry<Integer, Integer> entry:mNewOpMap.entrySet()){
					switch (entry.getKey()) {
					case 2:
						result[1]+=Double.valueOf(record.get(entry.getValue()));
						break;
					case 3:
						if(result[2] - Double.valueOf(record.get(entry.getValue())) < 1e-6)
							result[2]=Double.valueOf(record.get(entry.getValue()));
						break;
					default:
						break;
					}
				}
			}//end for
			hbaseInsert(groupByStr);
			mCount=0;
			Arrays.fill(result, 0);
			mHTable.close();
			System.out.println("succeed!");
		}
		else{
			//args error
		}
	}
	/**
     * this method is mainly about inserting record into the Hbase table
     * @param key is the row key, i.e. the group by key
     * @return void
     * @exception RetriesExhaustedWithDetailsException throw when retries are exhausted with details
     * @exception InterruptedIOException throw when something is wrong with the interrupted I/O
     */
	private static void hbaseInsert(String key) throws RetriesExhaustedWithDetailsException, InterruptedIOException{
		Put put=new Put(key.getBytes());
		for(Map.Entry<Integer, Integer> entry:mNewOpMap.entrySet()){
			switch(entry.getKey()){
			case 1:
				put.add(mColumnFamily.getBytes(),("count").getBytes(),(mCount+"").getBytes());
				break;
			case 2:
				put.add(mColumnFamily.getBytes(),("avg(R"+mOpMap.get(entry.getKey())+")").getBytes(),(new DecimalFormat("##0.00").format(result[1]/mCount).getBytes()));
				break;
			case 3:
				put.add(mColumnFamily.getBytes(),("max(R"+mOpMap.get(entry.getKey())+")").getBytes(),(result[2]+"").getBytes());
				break;
			}
		}
		mHTable.put(put);
	}
}
