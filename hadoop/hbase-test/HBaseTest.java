import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;

public class HBaseTest {

	public static void main(String[] args) throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
		// TODO Auto-generated method stub
		String tableName="mytable";
		HTableDescriptor htd=new HTableDescriptor(TableName.valueOf(tableName));
		HColumnDescriptor cf=new HColumnDescriptor("mycf");
		htd.addFamily(cf);
		
		Configuration configuration=HBaseConfiguration.create();
		HBaseAdmin hAdmin=new HBaseAdmin(configuration);
		hAdmin.createTable(htd);
		hAdmin.close();
		
		// put "mytable","abc","mycf:a","789"
		HTable table=new HTable(configuration, tableName);
		Put put=new Put("abc".getBytes());
		put.add("mycf".getBytes(),"a".getBytes(),"789".getBytes());
		table.put(put);
		table.close();
		System.out.println("put successfully!");
	}
}
