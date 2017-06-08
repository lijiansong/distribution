package org.apache.storm.storm_sql;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.storm_sql.bolt.GroupByBolt;
import org.apache.storm.storm_sql.bolt.JoinBolt;
import org.apache.storm.storm_sql.bolt.ProjectionBolt;
import org.apache.storm.storm_sql.bolt.SelectionBolt;
import org.apache.storm.storm_sql.parser.SqlVisitorParser;
import org.apache.storm.storm_sql.parser.sqlLexer;
import org.apache.storm.storm_sql.parser.sqlParser;
import org.apache.storm.storm_sql.planner.Operator;
import org.apache.storm.storm_sql.planner.OperatorTree;
import org.apache.storm.storm_sql.spout.StudentSpout;
import org.apache.storm.storm_sql.spout.TCSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * Created by jzh on 05/06/2017.
 */
public class Main {
    private StudentSpout studentSpout ;
    private TCSpout tcSpout;
    private HashMap<Integer,String> stream_id = new HashMap<>();
    private static ArrayList<String> tc_fileds = new ArrayList<>();
    private static ArrayList<String> stu_fileds = new ArrayList<>();
    private int withinTime;
    public int getWithinTime() {
        return withinTime;
    }

    public void setWithinTime(int withinTime) {
        this.withinTime = withinTime;
    }

    public static void main(String[] args) throws Exception{
        //TakeCourseSpout tcSpout = new TakeCourseSpout(new Fields("studentId","courseName","courseTime","courseTeacher","location"));
        //StudentSpout studentSpout=new StudentSpout(new Fields("id","name","gender","major","year","gpa"));
//        TCSpout tcSpout = new TCSpout();
//        StudentSpout studentSpout = new StudentSpout();
//        TopologyBuilder builder=new TopologyBuilder();

//        builder.setBolt("print",new PrinterBolt()).shuffleGrouping("tc").shuffleGrouping("student");
        initTcFields(tc_fileds);
        initStuFields(stu_fileds);
        TopologyBuilder tp ;
        String testStr0=
                "select tc.id, tc.courseName, tc.courseTeacher, tc.courseTime, tc.location from tc ";
        String testStr1=
                "select tc.id, tc.courseName, tc.courseTeacher, tc.courseTime, tc.location from tc where tc.courseName=OS";
        String testStr2=
                "select student.id, student.name, tc.courseName, tc.location from tc,student where student.id=tc.id within 5000";
        String testStr3=
                "select student.gender, avg(student.gpa) from student group by student.gender within 2000 ";
        String testStr4=
                "select tc.courseName, avg(student.gpa) from tc,student where tc.id=student.id and tc.location=Building1 group by tc.courseName within 10000 ";
        tp = new Main().run(testStr4);
        Config conf = new Config();

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("test",conf,tp.createTopology() );

        Utils.sleep(100000);
        cluster.shutdown();
    }//[CA, Building1, 90, male]
    private static void initTcFields(ArrayList<String> fieldsList){
        fieldsList.add("id");
        fieldsList.add("courseName");
        fieldsList.add("courseTime");
        fieldsList.add("courseTeacher");
        fieldsList.add("location");
    }
    private static void initStuFields(ArrayList<String> fieldsList){
        //id","name","gender","major","year","gpa
        fieldsList.add("id");
        fieldsList.add("name");
        fieldsList.add("gender");
        fieldsList.add("major");
        fieldsList.add("year");
        fieldsList.add("gpa");
    }
    private TopologyBuilder run(String sql)throws Exception{

        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        ANTLRInputStream inputStream=new ANTLRInputStream(sql);
        sqlLexer lexer=new sqlLexer(inputStream);
        CommonTokenStream tokens=new CommonTokenStream(lexer);
        sqlParser parser=new sqlParser(tokens);
        ParseTree tree=parser.root();

        SqlVisitorParser visitor=new SqlVisitorParser();
        visitor.visit(tree);

        //get the necessary info
        ArrayList<ArrayList<String>> select_list=visitor.getSelect_list();
        System.out.println("select_list:      "+select_list.toString());
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
        setWithinTime(within_time);
        OperatorTree op_tree=new OperatorTree();
        op_tree.buildOpTree(visitor);
        DirectedAcyclicGraph<Operator, DefaultEdge> opDag = op_tree.getOpDag();
        return(createTopo(opDag));
    }
    //private static BoltDeclarer boltDeclarer ;
    private TopologyBuilder createTopo(DirectedAcyclicGraph<Operator, DefaultEdge> opDag){
        TopologyBuilder tpBuilder = new TopologyBuilder();
        Iterator<Operator> iter = opDag.iterator();
        while (iter.hasNext()){
            Operator vertex=iter.next();
           // System.out.println(vertex.toString());
            System.out.println("boltId   "+vertex.getId());
            switch (vertex.getOpcode()){
                case SOURCE:
                    //TODO: suppose input streams are "tc" and "student"
                    tcSpout = new TCSpout();
                    studentSpout = new StudentSpout();
                    if(vertex.getParam().get(0).equals("tc")){
                        tpBuilder.setSpout(vertex.getParam().get(0),tcSpout);
                        stream_id.put(vertex.getId(),vertex.getParam().get(0));
                    }
                    else if(vertex.getParam().get(0).equals("student")){
                        tpBuilder.setSpout(vertex.getParam().get(0),studentSpout);
                        stream_id.put(vertex.getId(),vertex.getParam().get(0));
                    }
                    break;
                case SELECTION:
                    //selection must be from source
                   // ArrayList<String> param=vertex.getParam();
                    //SelectionBolt selection=new SelectionBolt();
                    //System.out.println("+++++++++++++++++++++12"+vertex.getParam().get(0));
                    System.out.println();
                    String tableName = stream_id.get(vertex.getFather().get(0));
                    if (tableName.equals("tc"))
                        tpBuilder.setBolt(vertex.getId() + "", new SelectionBolt(findSelectionCond(vertex), new Fields(tc_fileds)))
                                .shuffleGrouping(stream_id.get(vertex.getFather().get(0)));
                    else
                        tpBuilder.setBolt(vertex.getId() + "", new SelectionBolt(findSelectionCond(vertex), new Fields(stu_fileds)))
                                .shuffleGrouping(stream_id.get(vertex.getFather().get(0)));
                    //  tpBuilder.setBolt(vertex.getId()+"",new SelectionBolt(findSelectionCond(vertex),new Fields("studentId","courseName","courseTime","courseTeacher","location")));

                    break;
                case JOIN:
                    //boltDeclarer = tpBuilder.setBolt(vertex.getId()+"",new JoinBolt())；
                    JoinBolt jb =(JoinBolt)new JoinBolt(findJoinOutPut()).withTumblingWindow(new BaseWindowedBolt.Duration(getWithinTime(), TimeUnit.MILLISECONDS));
                    jb.setJoinParam(vertex.getParam());
                    Fields join_key = new Fields(vertex.getParam().get(1).split("\\.")[1]);
//                    System.out.println("join_key :"+join_key);
//                    System.out.println(vertex.getFather().get(0)+" "+vertex.getFather().get(1));
                    if ((vertex.getFather().get(0) == 0 && vertex.getFather().get(1) == 1)||(vertex.getFather().get(0) == 1 && vertex.getFather().get(1) == 0))
                        tpBuilder.setBolt(vertex.getId() + "", jb).fieldsGrouping(stream_id.get(vertex.getFather().get(0)), join_key).
                                fieldsGrouping(stream_id.get(vertex.getFather().get(1)), join_key);
//                        tpBuilder.setBolt(vertex.getId() + "", jb).fieldsGrouping("tc", join_key).
//                                fieldsGrouping("student", join_key);
                    else if ((vertex.getFather().get(0) == 0 && vertex.getFather().get(1) > 1)||(vertex.getFather().get(0) == 1 && vertex.getFather().get(1) > 0))
                        tpBuilder.setBolt(vertex.getId() + "", jb).fieldsGrouping(stream_id.get(vertex.getFather().get(0)), join_key).
                                fieldsGrouping(vertex.getFather().get(1) + "", join_key);
                    else if ((vertex.getFather().get(0) >1 && vertex.getFather().get(1) == 1)||(vertex.getFather().get(0) > 1 && vertex.getFather().get(1) == 0))
                        tpBuilder.setBolt(vertex.getId() + "", jb).fieldsGrouping(vertex.getFather().get(1) + "", join_key).
                                fieldsGrouping(stream_id.get(vertex.getFather().get(0)) + "", join_key);
                    else
                        tpBuilder.setBolt(vertex.getId() + "", jb).fieldsGrouping(vertex.getFather().get(0) + "", join_key).
                                fieldsGrouping(vertex.getFather().get(0) + "" + "", join_key);
                    break;
                case GROUP_BY:
                    System.out.println("---------GROUP_BY output fields: "+findOutGroupByFields(vertex));
                    GroupByBolt grpByBolt=(GroupByBolt) new GroupByBolt(findOutGroupByFields(vertex)).
                            withTumblingWindow(new BaseWindowedBolt.Duration(getWithinTime(), TimeUnit.MILLISECONDS));
                    grpByBolt.setAggrParam(vertex.getGroupByParam().get(1));
                    System.out.println("---------GROUP_BY father id: "+vertex.getFather().get(0));
                    if(stream_id.containsKey(vertex.getFather().get(0))){
                        System.out.println("---------GROUP_BY father table name:"+stream_id.get(vertex.getFather().get(0)));
                        System.out.println("---------GROUP_BY fieldsGrouping :"+getGrpByColumn(vertex).toString());
//                        tpBuilder.setBolt(vertex.getId()+"",grpByBolt)
//                                .fieldsGrouping(stream_id.get(vertex.getFather().get(0)),getGrpByColumn(vertex));
                        tpBuilder.setBolt(vertex.getId()+"",grpByBolt)
                                .shuffleGrouping(stream_id.get(vertex.getFather().get(0)));
//                        tpBuilder.setBolt(vertex.getId()+"",grpByBolt)
//                                .shuffleGrouping("student");

                    }else {
                        //not source
                        tpBuilder.setBolt(vertex.getId()+"",grpByBolt)
                                .fieldsGrouping(vertex.getFather().get(0)+"",getGrpByColumn(vertex));
                    }
                    break;
                case PROJECTION:
//                    for (int i=0;i<vertex.getFather().size();++i){
//                        if((vertex.getFather().get(i)==0) || (vertex.getFather().get(i)==1)){
//                            boltDeclarer = boltDeclarer.shuffleGrouping(stream_id.get(vertex.getFather().get(i)));
//                        }else{
//                            boltDeclarer = boltDeclarer.shuffleGrouping(vertex.getFather().get(i)+"");
//                        }
//                        //boltDeclarer.shuffleGrouping(stream_id)
//                    }
                    if(stream_id.size()==2) {
                        System.out.println("prj father"+vertex.getFather().get(0));
                        if ((vertex.getFather().get(0) == 0) || (vertex.getFather().get(0) == 1)) {
                            tpBuilder.setBolt(vertex.getId() + "", new ProjectionBolt(findOutPutFields(vertex))).shuffleGrouping(stream_id.get(vertex.getFather().get(0)));
                        } else {

                            tpBuilder.setBolt(vertex.getId() + "", new ProjectionBolt(findOutPutFields(vertex))).shuffleGrouping(vertex.getFather().get(0) + "");
                        }
                    }
                    else {
                        if ((vertex.getFather().get(0) == 0)) {
                            tpBuilder.setBolt(vertex.getId() + "", new ProjectionBolt(findOutPutFields(vertex))).shuffleGrouping(stream_id.get(vertex.getFather().get(0)));
                        } else {
                            tpBuilder.setBolt(vertex.getId() + "", new ProjectionBolt(findOutPutFields(vertex))).shuffleGrouping(vertex.getFather().get(0) + "");
                        }
                    }
                    break;
            }
        }
        return tpBuilder;
    }
    private Fields findOutPutFields(Operator vtx){
        ArrayList<String> fields = new ArrayList<>();
        ArrayList<ArrayList<String>> arrayLists = vtx.getProj_param();
        for(ArrayList<String> arrayList:arrayLists){
            if (arrayList.size() == 3) {
                fields.add(arrayList.get(2));
            } else {
                fields.add(arrayList.get(1));
            }
        }
        return new Fields(fields);
    }
    private Fields findSelectionCond(Operator vertex){
        //ArrayList<String> param=vertex.getParam();
        return new Fields(vertex.getParam());
    }
    private Fields findJoinOutPut(){
        Set<String> joinOutPutFields = new HashSet<>();
        ArrayList<String> outPutFileds = new ArrayList<>();
        for(String tc_str:tc_fileds)
            joinOutPutFields.add(tc_str);
        for(String stu_str:stu_fileds)
            joinOutPutFields.add(stu_str);
        for(String join_outField: joinOutPutFields) {
            outPutFileds.add(join_outField);
        }
        return  new Fields(outPutFileds);
    }
    //get output fields, for declaring output fields
    private Fields findOutGroupByFields(Operator vertex){
        ArrayList<String> fields=new ArrayList<>();
        for(ArrayList<String> _param:vertex.getGroupByParam()){
            if(_param.size()==2){
                //s1 r1
                fields.add(_param.get(1));
            }
            else if(_param.size()==3){
                //avg(code) s1 r1
                fields.add(_param.get(2));
            }
        }
        return new Fields(fields);
    }
    //get group by column, for fieldsGrouping
    private Fields getGrpByColumn(Operator vertex){
        //getGroupByParam return arraylist<arraylist<string>> [[],[]]
        return new Fields(vertex.getGroupByParam().get(0).get(1));
    }
}
