package org.apache.storm.storm_sql.bolt;

import org.apache.storm.storm_sql.planner.OpCode;
import org.apache.storm.storm_sql.planner.Operator;
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
public class ProjectionBolt extends BaseBasicBolt {
    private OpCode opCode = OpCode.PROJECTION;
    //private Operator opNode;
    private Fields _outFields;
    private ArrayList<ArrayList<String>> paramList;

    public ProjectionBolt(Fields fields) {
        //this.opNode = opNode;
        this._outFields = fields;
        System.out.println(opCode);
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
//        this.paramList = opNode.getProj_param();
//        super.prepare(stormConf, context);
//        ArrayList<String> fields = new ArrayList<>();
//        for (ArrayList<String> arrayList : paramList) {
//            if (paramList.size() == 3) {
//                fields.add(arrayList.get(2));
//            } else {
//                fields.add(arrayList.get(1));
//            }
//        }
//        _outFields = new Fields(fields);

    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        ArrayList<String> values = new ArrayList<>();
        for (String _outField: _outFields ) {
            String value = String.valueOf(input.getValueByField(_outField));
            values.add(value);
        }
        System.out.println(values);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        //declarer.declare(new Fields());
    }
}
