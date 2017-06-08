package org.apache.storm.storm_sql.spout;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.Map;
import java.util.Random;


//
//import org.apache.storm.testing.FeederSpout;
//import org.apache.storm.tuple.Fields;
//import org.apache.storm.tuple.Values;
//import org.apache.storm.utils.Utils;
//
//import java.util.Random;
//
///**
// * Created by json-lee on 17-6-5.
// */
////class TakeCourseSpout extends FeederSpout{
////
////    //static String studentId;
////    private static String[] courseName = {"大数据系统","体系结构","算法设计","网络安全导论","操作系统","数据挖掘","高级算法设计","推荐系统","英语口语","英语写作"};
////    private static String[] courseTime = {"周一","周二","周三","周四","周五"};
////    private static String[] courseTeacher = {"张飞","赵云","马超","诸葛亮","曹操","刘备","孙权","黄月英","陆逊"};
////    private static String[] location = {"教1","教2"};
////
//////    public static String getStudentId() {
//////        return studentId;
//////    }
////
////    public static String[] getCourseName() {
////        return courseName;
////    }
////
////    public static String[] getCourseTime() {
////        return courseTime;
////    }
////
////    public static String[] getCourseTeacher() {
////        return courseTeacher;
////    }
////
////    public static String[] getLocation() {
////        return location;
////    }
////
////
////    public TakeCourseSpout(Fields outFields) {
////        super(outFields);
////    }
////}
//
//public class StudentSpout extends FeederSpout{
//    private static String []stuName={"ZhangSan","LiSi","WangWu","LiuLiu","ChenShiMin","LiuGang","JiaLiChen","SaoZhu"};
//    private static String []stuMajor={"CS","CA","DBMS","DM","ML","DL","AI"};
//    private static int stuGpa=4;
//    private static  String studentId;
//
//    //static TakeCourseSpout takeCourseSpout=new TakeCourseSpout(new Fields("id","course_name","course_time","course_teacher","location"));
//
//    private static Random random=new Random();
//
//    public StudentSpout(Fields outFields) {
//        super(outFields);
//    }
//
//    //student:id name gender major year gpa
//    public static void generateStudentData(FeederSpout studentSpout){
////        String[] courseName=takeCourseSpout.getCourseName();
////        String[] courseTime=takeCourseSpout.getCourseTime();
////        String[] courseTeacher=takeCourseSpout.getCourseTeacher();
////        String[] location=takeCourseSpout.getLocation();
//
//        for (int i=10;i>=0;--i){
//            Utils.sleep(500);
//            String gender;
//            if (i % 2 == 0) {
//                gender = "male";
//            }
//            else {
//                gender = "female";
//            }
//            studentId=random.nextInt(i+100) +"201610";
//            studentSpout.feed(new Values(studentId,
//                    stuName[random.nextInt(stuName.length)]+random.nextInt(5),
//                    gender,
//                    stuMajor[random.nextInt(stuMajor.length)],
//                    2000+random.nextInt(16),
//                    random.nextInt(stuGpa)+0.1));
////            takeCourseSpout.feed(new Values(studentId,courseName[random.nextInt(courseName.length)],
////                    courseTime[random.nextInt(courseTime.length)],
////                    courseTeacher[random.nextInt(courseTime.length)],
////                    location[random.nextInt(location.length)]));
//        }
//    }
//}
public class StudentSpout extends BaseRichSpout {

    SpoutOutputCollector _collector;
    Random _rand;
    int studentId=0;
    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        _collector=spoutOutputCollector;
        _rand=new Random();
        studentId=0;
    }

    @Override
    public void nextTuple() {
        Utils.sleep(100);
        String []stuName={"ZhangSan","LiSi","WangWu","LiuLiu","ChenShiMin","LiuGang","JiaLiChen","SaoZhu"};
        String []stuMajor={"CS","CA","DBMS","DM","ML","DL","AI"};
        int stuGpa=100;

        String gender;
        if (studentId % 2 == 0) {
            gender = "male";
        }
        else {
            gender = "female";
        }
        _collector.emit(new Values(studentId,
                stuName[_rand.nextInt(stuName.length)]+_rand.nextInt(5),
                gender,
                stuMajor[_rand.nextInt(stuMajor.length)],
                2000+_rand.nextInt(16),
                _rand.nextInt(stuGpa/2-10)+60));
        ++studentId;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("id","name","gender","major","year","gpa"));
    }
}
