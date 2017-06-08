package org.apache.storm.storm_sql.bolt;


import org.apache.storm.storm_sql.planner.OpCode;
import org.apache.storm.storm_sql.planner.Operator;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

/**
 * Created by json-lee on 17-6-5.
 */
public class AggrBolt extends BaseBasicBolt
{
    OpCode opCode=OpCode.AGGR_FUNC;
    Operator opNode;

    public AggrBolt(Operator opNode) {
        this.opNode = opNode;
        System.out.println(opCode);
    }

    public AggrBolt() {
        super();
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
