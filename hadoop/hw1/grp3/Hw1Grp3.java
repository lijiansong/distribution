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
	//private static Map<Integer,Integer> mOpMap;//operation map, key is operation number, e.g. count 1, avg 2, max 3, value is column number
	private static ArrayList<int[]> mOpMap;
	//private static Map<Integer, Integer> mNewOpMap;//new operation map, key is operation number, e.g. count 1, avg 2, max 3, value is new column number, start from 0
	private static ArrayList<int[]> mNewOpMap;
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
			//java Hw1Grp3 R=/test.txt groupby:R2 res:avg(R3),count,avg(R0),max(R4),max(R0)
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
			//mOpMap =new HashMap<Integer, Integer>();//key is count avg or max, value is the columnNo
			mOpMap=new ArrayList<int[]>();
			int []map=null;
			for(String op:operation){
				map=new int[2];
				if(op.substring(0, 5).equals("count")){
					map[0]=1;
					//map[1]=0;
				}else if(op.substring(0,3).equals("avg")){
					map[0]=2;
					map[1]=Integer.parseInt(op.substring(op.indexOf("R")+1, op.indexOf(")")));
				}else if(op.substring(0,3).equals("max")){
					map[0]=3;
					map[1]=Integer.parseInt(op.substring(op.indexOf("R")+1, op.indexOf(")")));
				}else{
					//error
				}
				mOpMap.add(map);
			}
			System.out.println("mOpMap: ");
			for(int[] _map:mOpMap){
				System.out.println(_map[0]+" "+_map[1]);
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
			
			/**read the file data into memory,here we use a 2 dimension matrix to store the data.<br> To save the memory space, we only read the necessary data into the memory*/
			FileSystem hdfs=FileSystem.get(URI.create(hdfsFilePath),new Configuration());
			FSDataInputStream inputStream=hdfs.open(new Path(hdfsFilePath));
			BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
			//mNewOpMap=new HashMap<Integer,Integer>();//new map key-value : operation-new column
			
			String line;
			final ArrayList<ArrayList<String> > dataList=new ArrayList<ArrayList<String> >();
			while((line=reader.readLine())!=null){
				ArrayList<String> lineTmp=new ArrayList<String>();
				String []oneLine=line.split("\\|");
				lineTmp.add(oneLine[groupByNo]);//group by key column
				for(int []tmp:mOpMap){
					if(tmp[0]!=1) lineTmp.add(oneLine[tmp[1]]);//skip count
				}
				dataList.add(lineTmp);
			}
			inputStream.close();
			hdfs.close();
			
			//dataList[0] is group by key, dataList[1..n] is the data need to be stored, so we have to update the index
			mNewOpMap=new ArrayList<int[]>();
			int opCounter=1;
			for(int[]tmp:mOpMap){
				map=new int[2];
				if(tmp[0]==1){//for count
					map[0]=1;
					map[1]=0;
					mNewOpMap.add(map);
				}else{//for avg and max
					map[0]=tmp[0];
					map[1]=opCounter;
					mNewOpMap.add(map);
					++opCounter;
				}
			}
			System.out.println("newOpMap: ");
			for(int[] tmp:mNewOpMap){
				System.out.println(tmp[0]+" "+tmp[1]);
			}
			/**process the data by sorting them then calculate the max and average*/
			Collections.sort(dataList,new Comparator<ArrayList<String> >() {
				@Override
				public int compare(ArrayList<String> o1, ArrayList<String> o2) {
					//dataList[0] is group by key
					return o1.get(0).compareTo(o2.get(0));
				}
			});
			System.out.println("sorted dataList: ");
			for(ArrayList<String> list : dataList){
				for(String data:list){
					System.out.print(data+" ");
				}
				System.out.println();
			}
			
			String groupByStr=dataList.get(0).get(0);
			mCount=0;
			//result=new double[mNewOpMap.size()];
			result=new double[mOpMap.size()];
			Arrays.fill(result, 0);
			//initialize max result to minimum
			for(int[] tmp:mNewOpMap){
				if(tmp[0]==3){//for max
					result[tmp[1]]=Double.MIN_VALUE;
				}
			}
			for(ArrayList<String> record:dataList){
				if(!groupByStr.equals(record.get(0))){
					hbaseInsert(groupByStr);
					mCount=0;
					groupByStr=record.get(0);
					Arrays.fill(result, 0);
					for(int[] tmp:mNewOpMap){
						if(tmp[0]==3){//for max result initialization
							result[tmp[1]]=Double.MIN_VALUE;
						}
					}
				}
				++mCount;
				for(int i=0;i<mOpMap.size();++i){
					map=mNewOpMap.get(i);
					switch (map[0]) {
					case 2://avg
						result[map[1]]+=Double.valueOf(record.get(map[1]));
						break;
					case 3://max
						result[map[1]]=Math.max(result[map[1]], Double.valueOf(record.get(map[1])));
						break;
					default:
						break;
					}
				}
			}//end for
			hbaseInsert(groupByStr);
			mCount=0;
			Arrays.fill(result, 0);
			for(int[] tmp:mNewOpMap){
				if(tmp[0]==3){//for max
					result[tmp[1]]=Double.MIN_VALUE;
				}
			}
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
		int []tmp=null;
		for(int i=0;i<mOpMap.size();++i){
			tmp=mNewOpMap.get(i);
			switch(tmp[0]){
			case 1://count
				put.add(mColumnFamily.getBytes(),("count").getBytes(),(mCount+"").getBytes());
				break;
			case 2://avg
				put.add(mColumnFamily.getBytes(),("avg(R"+mOpMap.get(i)[1]+")").getBytes(),(new DecimalFormat("##0.00").format(result[tmp[1]]/mCount).getBytes()));
				break;
			case 3://max
				put.add(mColumnFamily.getBytes(),("max(R"+mOpMap.get(i)[1]+")").getBytes(),(result[tmp[1]]+"").getBytes());
				break;
			}
		}
		mHTable.put(put);
	}
}
