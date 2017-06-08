package org.apache.storm.storm_sql.bolt;

import org.apache.storm.storm_sql.planner.OpCode;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.windowing.TupleWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by json-lee on 17-6-5.
 */
public class GroupByBolt extends BaseWindowedBolt {
    OpCode opCode=OpCode.GROUP_BY;
    //Operator operator;
    Fields outputFields;
    private OutputCollector collector;
    ArrayList<String> aggrParam;
    HashMap<String,ArrayList<String>> groupByMap;//key: group by key,value: aggr list

    public void setAggrParam(ArrayList<String> aggrParam) {
        this.aggrParam = aggrParam;
    }

    public ArrayList<String> getAggrParam() {
        return aggrParam;
    }



    public GroupByBolt(Fields outFields) {
        //this.operator = operator;
        this.outputFields=outFields;
        System.out.println(opCode);
    }


//    public void setOutputFields(Fields outputFields) {
//        this.outputFields = outputFields;
//    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);
        this.collector=collector;
        this.groupByMap=new HashMap<>();
    }

    @Override
    public void execute(TupleWindow tupleWindow) {
        List<Tuple> newTuples=tupleWindow.getNew();
        //System.out.println("++++++++++++++newTuples: "+newTuples.toString());
        this.groupByMap.clear();
        for(Tuple input:newTuples){
           // System.out.println("------------------------Group By Tuple: "+input.toString());
            String key=String.valueOf(input.getValueByField(outputFields.get(0)));

            if(groupByMap.containsKey(key)){
                ArrayList<String> values=groupByMap.get(key);
                values.add( String.valueOf( input.getValueByField( outputFields.get(1) ) ));
                //System.out.println("-----------------------------current values:"+values.toString());
            }
            else{
                ArrayList<String> values=new ArrayList<>();
                values.add(String.valueOf(input.getValueByField(outputFields.get(1))));
                groupByMap.put(key,values);
                //System.out.println("-----------------------------current values:"+values.toString());
            }
        }

        //send tuple
        switch (getAggrParam().get(0)){
            case "0":
                //avg
                for(Map.Entry<String,ArrayList<String>> entry:this.groupByMap.entrySet()){
                    ArrayList<String> values=entry.getValue();
                    int sum=0;
                    for(String val:values){
                        //whether is a number
                        if(val.matches("-?\\d+(\\.\\d+)?"))  sum+=Integer.valueOf(val);
                        else {
                            System.out.println("Error: This column cannot compute avg!");
                            return;
                        }
                    }
                    double avg=sum/values.size();
                    ArrayList<Object> tuple=new ArrayList<>();
                    tuple.add(entry.getKey());
                    tuple.add(avg+"");
                    collector.emit(tuple);
                }
                break;
            case "1":
                //max
                for(Map.Entry<String,ArrayList<String>> entry:this.groupByMap.entrySet()){
                    ArrayList<String> values=entry.getValue();
                    int max=Integer.MAX_VALUE;
                    for(String val:values){
                        //whether is a number
                        if(val.matches("-?\\d+(\\.\\d+)?"))
                            max=Math.max(Integer.valueOf(val),max);
                        else {
                            System.out.println("Error: This column cannot compute max!");
                            return;
                        }
                    }
                    ArrayList<Object> tuple=new ArrayList<>();
                    tuple.add(entry.getKey());
                    tuple.add(max+"");
                    collector.emit(tuple);
                }

                break;
            case "2":
                //min
                for(Map.Entry<String,ArrayList<String>> entry:this.groupByMap.entrySet()){
                    ArrayList<String> values=entry.getValue();
                    int min=Integer.MIN_VALUE;
                    for(String val:values){
                        //whether is a number
                        if(val.matches("-?\\d+(\\.\\d+)?"))
                            min=Math.min(Integer.valueOf(val),min);
                        else {
                            System.out.println("Error: This column cannot compute min!");
                            return;
                        }
                    }
                    ArrayList<Object> tuple=new ArrayList<>();
                    tuple.add(entry.getKey());
                    tuple.add(min+"");
                    collector.emit(tuple);
                }
                break;
            case "3":
                //sum
                for(Map.Entry<String,ArrayList<String>> entry:this.groupByMap.entrySet()){
                    ArrayList<String> values=entry.getValue();
                    int sum=0;
                    for(String val:values){
                        //whether is a number
                        if(val.matches("-?\\d+(\\.\\d+)?"))
                            sum+=Integer.valueOf(val);
                        else {
                            System.out.println("Error: This column cannot compute sum!");
                            return;
                        }
                    }
                    ArrayList<Object> tuple=new ArrayList<>();
                    tuple.add(entry.getKey());
                    tuple.add(sum+"");
                    collector.emit(tuple);
                }
                break;
            case "4":
                //count
                for(Map.Entry<String,ArrayList<String>> entry:this.groupByMap.entrySet()){
                    ArrayList<String> values=entry.getValue();
                    int count=values.size();
                    ArrayList<Object> tuple=new ArrayList<>();
                    tuple.add(entry.getKey());
                    tuple.add(count+"");
                    collector.emit(tuple);
                }
                break;
        }

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        //super.declareOutputFields(declarer);
        declarer.declare(outputFields);
    }
}

