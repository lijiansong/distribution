package org.apache.storm.storm_sql.bolt;


import org.apache.storm.storm_sql.planner.OpCode;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by json-lee on 17-6-5.
 */
public class SelectionBolt extends BaseBasicBolt{
    //Operator opNode;
    OpCode opCode=OpCode.SELECTION;
    Fields spoutFields;
    //ArrayList<String> spoutFields;
    Fields param_fields;
    //ArrayList<String> selection_param;

    public SelectionBolt(Fields param_fields,Fields spoutFields) {

        //for(String str:param_fields)
        //selection_param=param_fields.toList();
        this.param_fields=param_fields;
        this.spoutFields = spoutFields;
        System.out.println(opCode);

    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
       // System.out.println("++++++++++++++++++++++++++++++++++pap"+context.getThisSources().size());
//        //int numSources=context.getThisSources().size();
//        for(GlobalStreamId source:context.getThisSources().keySet()){
//            spoutFields=context.getComponentOutputFields(source.get_componentId(),source.get_streamId());
//        }
//        //if(numSources==1){
//            //GlobalStreamId source=context.getThisSources().keySet().iterator().next();
//            //spoutFields=context.getComponentOutputFields(source.get_componentId(),source.get_streamId());
////            for(GlobalStreamId source:context.getThisSources().keySet()){
////                spoutFields=context.getComponentOutputFields(source.get_componentId(),source.get_streamId());
////                //spoutFields=new HashSet<>()
////            }
//            //source.get
//       // }
//        //selection_param=opNode.getParam();
//        //for(String str:param_fields){}
    }




    boolean flag;
    //TODO: only support integer fields now, "and" relation in this order, e.g. <= s1.r1 100
    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        //System.out.println("++++++++++++exe"+param_fields);
        flag=false;
        for(int i=0;i<param_fields.size()/3;++i){
            String op_symbol=param_fields.get(i*3);
           // System.out.println("++++++++++++symbel"+op_symbol);
            String column_name=param_fields.get(i*3+1).split("\\.")[1];
           // System.out.println("++++++++++++c n"+column_name);
            String column_value=input.getStringByField(column_name);
           // System.out.println("++++++++++++c v"+column_value);
            String limit=param_fields.get(i*3+2);
           // System.out.println("++++++++++++limit"+limit);
            if(column_value.matches("-?\\d+(\\.\\d+)?")){
                //is a number
                switch (op_symbol){
                    case "=":
                        if(Integer.valueOf(column_value).equals(Integer.valueOf(limit))) flag=true;
                        else { return;}
                        break;
                    case ">":
                        if(Integer.valueOf(column_value)>Integer.valueOf(limit)) flag=true;
                        else {return;}
                        break;
                    case "<":
                        if(Integer.valueOf(column_value)<Integer.valueOf(limit)) flag=true;
                        else {return;}
                        break;
                    case ">=":
                        if(Integer.valueOf(column_value)>=Integer.valueOf(limit)) flag=true;
                        else {return;}
                        break;
                    case "<=":
                        if(Integer.valueOf(column_value)<=Integer.valueOf(limit)) flag=true;
                        else {return;}
                        break;
                    case "!=":
                        if(!Integer.valueOf(column_value).equals(Integer.valueOf(limit))) flag=true;
                        else {return;}
                        break;
                }
            }else {
                //string
                //System.out.println("else__________________");
                switch (op_symbol){
                    case "=":
                        if(column_value.equals(limit)) flag=true;
                        else {return;}
                        break;
                    case "!=":
                        if(!column_value.equals(limit)) flag=true;
                        else {return;}
                        break;
                }
            }
        }
        //System.out.println(input.toString());
        //meet the condition
        ArrayList<Object> tup=new ArrayList<>();
        for(String field:spoutFields){
            //System.out.println(field);
            tup.add(input.getValueByField(field));
        }
        collector.emit(tup);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(spoutFields);
    }
}

