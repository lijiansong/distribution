package org.apache.storm.storm_sql.planner;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by json-lee on 17-6-5.
 */
public class Operator{
    int id;
    //int degree;
    OpCode opcode;
    int child=-1;

    //ArrayList<Integer> parents;
    ArrayList<String> param;//join within time


    ArrayList<ArrayList<String>> proj_param;
    ArrayList<ArrayList<String>> groupByParam;

    Hashtable<String,ArrayList<String>> select_parm;

    public ArrayList<Integer> getFather() {
        return father;
    }
    ArrayList<Integer> father;

    public Hashtable<String, ArrayList<String>> getSelect_parm() {
        return select_parm;
    }

    public void setSelect_parm(Hashtable<String, ArrayList<String>> select_parm) {
        this.select_parm = select_parm;
    }

    public ArrayList<ArrayList<String>> getGroupByParam() {
        return groupByParam;
    }

    public void setGroupByParam(ArrayList<ArrayList<String>> groupByParam) {
        this.groupByParam = groupByParam;
    }

    @Override
    public String toString() {
        return "Operator{" +
                "id=" + id +
                ", opcode=" + opcode +
                ", child=" + child +
                ", param=" + param +
                ", proj_param=" + proj_param +
                ", groupByParam=" + groupByParam +
                ", select_parm=" + select_parm +
                ", father=" + father +
                ", table_name=" + table_name +
                '}';
    }

    public void setFather(ArrayList<Integer> father) {
        this.father = father;
    }





    public ArrayList<ArrayList<String>> getProj_param() {
        return proj_param;
    }

    public void setProj_param(ArrayList<ArrayList<String>> proj_param) {
        this.proj_param = proj_param;
    }




    public ArrayList<String> getTable_name() {
        return table_name;
    }

    public void setTable_name(ArrayList<String> table_name) {
        this.table_name = table_name;
    }

    ArrayList<String> table_name;//for finding out intersection
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OpCode getOpcode() {
        return opcode;
    }

    public void setOpcode(OpCode opcode) {
        this.opcode = opcode;
    }

    public int getChild() {
        return child;
    }

    public void setChild(int child) {
        this.child = child;
    }

    public ArrayList<String> getParam() {
        return param;
    }

    public void setParam(ArrayList<String> param) {
        this.param = param;
    }
}