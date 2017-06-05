package storm_sql.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import storm_sql.planner.OperatorTree;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by json-lee on 17-6-4.
 */
public class TestParser {
    public static void run(String sql)throws Exception{
        ANTLRInputStream inputStream=new ANTLRInputStream(sql);
        sqlLexer lexer=new sqlLexer(inputStream);
        CommonTokenStream tokens=new CommonTokenStream(lexer);
        sqlParser parser=new sqlParser(tokens);
        ParseTree tree=parser.root();

        SqlVisitorParser visitor=new SqlVisitorParser();
        visitor.visit(tree);

        //get the necessary info
        Hashtable<String,String> select_list=visitor.getSelect_list();
        for(Map.Entry entry:select_list.entrySet()){
            System.out.println("select_list:    "+entry.getKey()+" "+entry.getValue());
        }
        Hashtable<Integer,ArrayList<String>> select_list_aggr=visitor.getSelect_list_aggr();
        for(Map.Entry entry:select_list_aggr.entrySet()){
            System.out.println("select_list_aggr:    "+entry.getKey()+" "+entry.getValue());
        }
        ArrayList<String> table_sources=visitor.getTable_sources();
        for(String str:table_sources){
            System.out.println("table_sources   "+str);
        }
        ArrayList<ArrayList<String>> where_cond_join=visitor.getWhere_cond_join();
        for(ArrayList<String> tmp:where_cond_join)
        {
            for (String str:tmp){
                System.out.println("  "+str);
            }
        }
        ArrayList<ArrayList<String>> where_cond_select=visitor.getWhere_cond_select();
        for(ArrayList<String> tmp:where_cond_select)
        {
            for (String str:tmp){
                System.out.println("  "+str);
            }
        }
        Hashtable<String,String> group_by_item=visitor.getGroup_by_item();
        for(Map.Entry entry:group_by_item.entrySet()){
            System.out.println("group_by_item:    "+entry.getKey()+" "+entry.getValue());
        }
        Integer within_time=visitor.getWithin_time();
        System.out.println("within_time   "+within_time);
        OperatorTree op_tree=new OperatorTree();
        op_tree.buildOpTree(visitor);

    }

    public static void main(String[] args) throws Exception{
        String []testStr={
                "select s1.r1,s2.r2, avg(s2.r2), count(s1.r5) from s1,s2 where s1.r1=s2.r1 and s1.r3>100 group by s1.r4 within 20",
                "select s1.r1,s2.r2 from s1,s2 where s1.r2=s2.r1",
                "select s1.r2,avg(s1.r1) from s1",
                "select s1.r1,s2.r2 from s1,s2 where s1.r2=s2.r1 group by s1.r3",
                "select s1.r1,avg(s1.r2), count(s1.r5) from s1 where s1.r3>100 group by s1.r4 within 20"};
        for(String str:testStr) run(str);
    }
}
