package org.apache.storm.storm_sql.spout;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;

import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * Created by jzh on 05/06/2017.
 */
public class TCSpout extends BaseRichSpout {
    private ArrayList<String> fieldsList = new ArrayList<>();
    private static int studentId;
    private SpoutOutputCollector _collector;
    private static Random _random ;
    private static String[] courseName = {"BigData","ComputerArchitecture","Algorithm","OS","DataMining","AdvancedAlgorithm","RecommendationSystem","OralEnglish","Writing"};
    private static String[] courseTime = {"Mon","Tue","Wed","Thu","Fri"};
    private static String[] courseTeacher = {"zhangfei","zhaoyun","machao","chenshimin","csm","shiminchen","jasonlee","saozhu","lee"};
    private static String[] location = {"Building1","Building2"};

    public ArrayList<String> getFieldsList() {
//        fieldsList.add("id");
//        fieldsList.add("courseName");
//        fieldsList.add("courseTime");
//        fieldsList.add("courseTeacher");
//        fieldsList.add("location");
        return fieldsList;
    }

    @Override
    public void ack(Object msgId) {
        super.ack(msgId);
    }

    @Override
    public void fail(Object msgId) {
        super.fail(msgId);
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        _collector = collector;
        _random = new Random();
        studentId = 0;
    }

    @Override
    public void nextTuple() {
        Utils.sleep(100);
        studentId++;
        _collector.emit(new Values(studentId,courseName[_random.nextInt(courseName.length)],
                courseTime[_random.nextInt(courseTime.length)],
                courseTeacher[_random.nextInt(courseTime.length)],
                location[_random.nextInt(location.length)]));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
//        fieldsList = new ArrayList<>();
//        fieldsList.add("studentId");
//        fieldsList.add("courseName");
//        fieldsList.add("courseTime");
//        fieldsList.add("courseTeacher");
//        fieldsList.add("location");
        declarer.declare(new Fields("id","courseName","courseTime","courseTeacher","location"));
    }

}
