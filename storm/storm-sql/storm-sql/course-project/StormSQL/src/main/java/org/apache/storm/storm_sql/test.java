package org.apache.storm.storm_sql;

/**
 * Created by json-lee on 17-6-1.
 */
public class test {
    //select * from S1
    public static void main(String[] args) throws Exception{
        String str="s1.r2";
        System.out.println(str.split("\\.")[0]+" "+str.split("\\.")[1]);
    }
}
