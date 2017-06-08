package org.apache.storm.storm_sql.bolt;

import org.apache.storm.generated.GlobalStreamId;
import org.apache.storm.storm_sql.planner.OpCode;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.windowing.TupleWindow;

import java.util.*;

/**
 *
 * Hash Join
 * Created by JZH on 17-6-5.
 */
public class JoinBolt extends BaseWindowedBolt{
    private OpCode opCode= OpCode.JOIN;
    private Fields _outputFileds;
    private ArrayList<String> joinParam;
    private OutputCollector _collector;
    private String join_key;
    private Map<String,Fields> inputFields;
    private ArrayList<String> componentIdList;
    private Map<String,Integer> fieldPosition;
    public JoinBolt(Fields outputFileds) {
        System.out.println(opCode);
        _outputFileds = outputFileds;
    }
    public void setJoinParam(ArrayList<String> joinParam) {
        this.joinParam = joinParam;
    }
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        //super.prepare(stormConf, context, collector);
        //System.out.println("outputFields num"+_outputFileds);
        this._collector = collector;
        join_key = joinParam.get(1).split("\\.")[1];
        //System.out.println("join_key:"+join_key);
        inputFields = new HashMap<>();
        componentIdList = new ArrayList<>();
        for(GlobalStreamId source : context.getThisSources().keySet()){
            Fields fields = context.getComponentOutputFields(source.get_componentId(), source.get_streamId());
            inputFields.put(source.get_componentId(),fields);
            componentIdList.add(source.get_componentId());
            //System.out.println("streaming"+fields+"componentId"+source.get_componentId()+"steamId"+source.get_streamId());
        }
        //System.out.println("param:join_param"+joinParam.toString());
        //System.out.println("outputFields"+_outputFileds);
        fieldPosition = new HashMap<>();
        for(int i=0;i<_outputFileds.size();i++){
            fieldPosition.put(_outputFileds.get(i),i);
        }

    }

    @Override
    public void execute(TupleWindow inputWindow) {
        System.out.println("------------------------------");
        List<Tuple> tuplesInWindow = inputWindow.get();
        List<Tuple> newTuples = inputWindow.getNew();
        List<Tuple> expiredTuples = inputWindow.getExpired();
//        for(Tuple tuple: tuplesInWindow){
//            System.out.println("++++++++++++++++++TupleInWindow++++++++++++++++");
//            System.out.println(tuple.toString());
//        }
//        for(Tuple tuple: newTuples){
//            System.out.println("++++++++++++++++++NewTuple++++++++++++++++");
//            System.out.println(tuple.toString());
//        }
//        for(Tuple tuple: expiredTuples){
//            System.out.println("++++++++++++++++++expiredTuple++++++++++++++++");
//            System.out.println(tuple.toString());
//        }
       // System.out.println(tuplesInWindow.size()+" "+newTuples.size()+" "+expiredTuples.size());
        Hashtable<String,ArrayList<Tuple>> source1 = new Hashtable<>();
        ArrayList<Tuple> source2 = new ArrayList<>();
        for(Tuple tuple: tuplesInWindow){
            GlobalStreamId id = tuple.getSourceGlobalStreamId();
            String key = String.valueOf(tuple.getValueByField(join_key));
            //System.out.println("key++++++++"+key);
            if(id.get_componentId().equals(componentIdList.get(0))){
               if(source1.containsKey(key)){
                   ArrayList<Tuple> arrayList = source1.get(key);
                   arrayList.add(tuple);
               }
               else{
                   ArrayList<Tuple> arrayList = new ArrayList<>();
                   arrayList.add(tuple);
                   source1.put(key,arrayList);
               }
            }
            else {
               source2.add(tuple);
            }

        }
//        Enumeration e = source1.elements();
//        System.out.println("++++++++++++++++++++++++source1");
//        while (e.hasMoreElements()){
//            System.out.println(e.nextElement());
//        }
//        System.out.println("++++++++++++++++++++++++source2");
//        for(Tuple tuple:source2){
//            System.out.println(tuple.toString());
//        }
        for(Tuple tuple:source2){
            List<Object> emit_results = new ArrayList<>(_outputFileds.size());
            Object[] join_result = new Object[_outputFileds.size()];
            String key = String.valueOf(tuple.getValueByField(join_key));
            if(source1.containsKey(key)){
                Fields inputFields2 = inputFields.get(tuple.getSourceGlobalStreamId().get_componentId());
                //System.out.println("inputFields2"+inputFields2);
                //ArrayList<String> outputValue2 = new ArrayList<>();
                for(String field:inputFields2){
                    //outputValue2.add(String.valueOf(tuple.getValueByField(field)));
                    //emit_results.add(tuple.getValueByField(field));
                    join_result[fieldPosition.get(field)]=tuple.getValueByField(field);
                }
                ArrayList<Tuple> arrayList = source1.get(key);
                for(Tuple tuple1:arrayList){
                    Fields inputFields1 = inputFields.get(tuple1.getSourceGlobalStreamId().get_componentId());
                   // System.out.println("inputFields1"+inputFields1);
                    //ArrayList<String> outputValue1 = new ArrayList<>();
                    for(String field:inputFields1){
                        if(!field.equals(join_key)) {
                            //outputValue1.add(String.valueOf(tuple1.getValueByField(field)));
                            //emit_results.add(tuple1.getValueByField(field));
                            join_result[fieldPosition.get(field)]=tuple1.getValueByField(field);
                        }
                    }
                    _collector.emit(Arrays.asList(join_result));
                   // System.out.println("join_result:"+join_result);
//                    for(String str: outputValue1){
//                        results.add(str);
//                    }
//                    for(String str: outputValue2){
//                        results.add(str);
//                    }
                    //System.out.println(results);
                    //_collector.emit(results);
                }
            }
        }//[LiuGang4, female, DM, 2000, 3.1][91, Writing, Mon, zhangfei, Building1]
//        _collector.emit(results);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(_outputFileds);
    }
}
