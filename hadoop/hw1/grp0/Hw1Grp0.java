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
import java.util.Hashtable;
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

//hash join
public class Hw1Grp0 {

	/** Hbase table name */
	private static final String TABLENAME="Result";
	private static String mColumnFamily;//member variable, column family
	private static HTable mHTable;//member variable, Hbase table
	private static ArrayList<int[]> mOperation;//table name and column key,1:R table,2:S table
	
	public static void main(String[] args) throws IOException,URISyntaxException,MasterNotRunningException,ZooKeeperConnectionException {
		/**the argument's length must be 4, so the first step is to parse the argument string*/
		if(args.length==4){
			//java Hw1GrpX R=/test1.txt S=/test2.txt join:R2=S3 res:R4,S5
			String rFilePath="hdfs://localhost:9000"+args[0].substring(2);
			String sFilePath="hdfs://localhost:9000"+args[1].substring(2);
			int rJoinKey=Integer.valueOf(args[2].substring(args[2].indexOf("R")+1, args[2].indexOf("=")));
			int sJoinKey=Integer.valueOf(args[2].substring(args[2].indexOf("S")+1));
			mColumnFamily=args[3].split(":")[0];//res
			String []resKeys=args[3].split(":")[1].split(",");
			mOperation=new ArrayList<int[]>();
			int []map=null;
			for(String key:resKeys){
				map=new int[2];//map[0]:table name,map[1]:column key
				if(key.substring(0, 1).equals("R")){
					map[0]=1;//R table id
				}
				else if(key.substring(0,1).equals("S")){
					map[0]=2;//S table id
				}else{
					//error
				}
				map[1]=Integer.valueOf(key.substring(1));
				mOperation.add(map);
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
			
			Configuration hdfsConf=new Configuration();
			FileSystem rHdfs = FileSystem.get(URI.create(rFilePath), hdfsConf);
	        Path rPath = new Path(rFilePath);
	        FSDataInputStream rInputStream = rHdfs.open(rPath);
	        BufferedReader rReader = new BufferedReader(new InputStreamReader(rInputStream));
	        String rLine;

	        FileSystem sHdfs= FileSystem.get(URI.create(sFilePath), hdfsConf);
	        Path sPath = new Path(sFilePath);
	        FSDataInputStream sInputStream = sHdfs.open(sPath);
	        BufferedReader sReader = new BufferedReader(new InputStreamReader(sInputStream));
	        String sLine;
			
	        //read R table into hash table
	        Hashtable<String, ArrayList<ArrayList<String>>> rHashtable=new Hashtable<String,ArrayList<ArrayList<String>>>();
	        ArrayList<ArrayList<String>> value=null;
	        ArrayList<String> valueTuple=null;//data to be stored in one line
	        while((rLine=rReader.readLine())!=null){
	        	String []oneLine=rLine.split("\\|");
	        	valueTuple=new ArrayList<String>();
	        	for(int[] keymap:mOperation){
	        		if(keymap[0]==1){
	        			valueTuple.add(oneLine[keymap[1]]);
	        		}
	        	}
	        	if (rHashtable.containsKey(oneLine[rJoinKey])){
	        		rHashtable.get(oneLine[rJoinKey]).add(valueTuple);
	        	}else{
	        		value=new ArrayList<ArrayList<String>>();
		        	value.add(valueTuple);
		        	rHashtable.put(oneLine[rJoinKey], value);
	        	}
	        }
	        
	        int count=0;
	        while((sLine=sReader.readLine())!=null){
	        	String []oneLine=sLine.split("\\|");
	        	if(rHashtable.containsKey(oneLine[sJoinKey])){
	        		Put put=new Put(oneLine[sJoinKey].getBytes());
	        		
	        	}
	        }
	        
		}
		else{
			//args error
		}
	}
	
}
