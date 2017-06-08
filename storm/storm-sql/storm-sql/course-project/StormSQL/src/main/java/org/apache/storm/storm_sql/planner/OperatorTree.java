package org.apache.storm.storm_sql.planner;

import org.apache.storm.storm_sql.parser.SqlVisitorParser;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;


import java.util.*;

/**
 * Created by json-lee on 17-6-4.
 */


public class OperatorTree {
    //SqlVisitorParser visitor=new SqlVisitorParser();
    //int [][]tree=new int[getVertex_num()][2];
    //ArrayList<Operator> tree=new ArrayList<>();

    public DirectedAcyclicGraph<Operator, DefaultEdge> getOpDag() {
        return opDag;
    }

    DirectedAcyclicGraph<Operator,DefaultEdge> opDag;

    public int getVertex_num() {
        int vertex_num=0;
        return vertex_num;
    }
    public boolean intersection(Operator op1,Operator op2){
        for(String str1:op1.getTable_name()){
            for(String str2:op2.getTable_name()){
                if(str1.equals(str2)) return true;
            }
        }
        return false;
    }


    public void buildOpTree(SqlVisitorParser visitor){
        opDag=new DirectedAcyclicGraph(DefaultEdge.class);
        int id=0;
        ArrayList<Operator> queue=new ArrayList<Operator>();
        //Hashtable<String,String> select_list=visitor.getSelect_list();
        ArrayList<ArrayList<String>> select_list=visitor.getSelect_list();
        Hashtable<Integer,ArrayList<String>> select_list_aggr=visitor.getSelect_list_aggr();
        ArrayList<String> table_sources=visitor.getTable_sources();
        ArrayList<ArrayList<String>> where_cond_join=visitor.getWhere_cond_join();
        ArrayList<ArrayList<String>> where_cond_select=visitor.getWhere_cond_select();
        Hashtable<String,String> group_by_item=visitor.getGroup_by_item();

        //1.add vertex
        if(table_sources.size()!=0){
            for(String table_name:table_sources){

                Operator source=new Operator();
                source.setId(id);
                source.setOpcode(OpCode.SOURCE);
                source.setTable_name(new ArrayList<String>(Arrays.asList(table_name)));
                //source.setParents(new ArrayList<Integer>(Arrays.asList(-1)));
                source.setParam(new ArrayList<String>(Arrays.asList(table_name)));
                opDag.addVertex(source);
                queue.add(source);
                ++id;
            }
        }

        System.out.println("+++++++++++++++++++++++where_cond_select.size() "+where_cond_select.size());
        if(where_cond_select.size()!=0){
            for(ArrayList<String> select_cond:where_cond_select){
                ArrayList<String> _tmp = new ArrayList<>();
                Operator select =new Operator();
                select.setId(id);
                select.setOpcode(OpCode.SELECTION);

                for(String str:select_cond){
                    _tmp.add(str);
                }
                select.setParam(_tmp);

                //TODO: merge selection node
//                String table_name=select_cond.get(0).split("\\.")[0];
//                if(select.getSelect_parm().containsKey(table_name)){
//
//                }

                ArrayList<String> tmp=new ArrayList<String>();
                for(int i=1;i<select_cond.size();++i){
                    tmp.add(select_cond.get(i).split("\\.")[0]);
                }
                select.setTable_name(tmp);

                opDag.addVertex(select);
                queue.add(select);
                ++id;
            }
        }

        if(where_cond_join.size()!=0){
            for(ArrayList<String> join_cond:where_cond_join){
                ArrayList<String> _tmp = new ArrayList<>();
                Operator join=new Operator();
                join.setId(id);
                join.setOpcode(OpCode.JOIN);
                for(String str:join_cond){
                    _tmp.add(str);
                }
                join.setParam(_tmp);
                ArrayList<String> tmp=new ArrayList<String>();
                for(int i=1;i<join_cond.size();++i){
                    tmp.add(join_cond.get(i).split("\\.")[0]);
                }
                join.setTable_name(tmp);
                opDag.addVertex(join);
                queue.add(join);
                ++id;
            }
        }

        //groupByParam: <group by key> <aggr function param>
        if(group_by_item.entrySet().size()!=0){
            //group_by_item.size() should be 1
            ArrayList<ArrayList<String>> groupByParam=new ArrayList<>();
            ArrayList<String> tmp=new ArrayList<>();
            Operator group_by=new Operator();
            //for(Map.Entry entry:group_by_item.entrySet()){
            //group_by=new Operator();
            String key=group_by_item.keySet().iterator().next();
            group_by.setId(id);
            group_by.setOpcode(OpCode.GROUP_BY);
            group_by.setParam(new ArrayList<String>( Arrays.asList(key,group_by_item.get(key))));
            //tmp.add(entry.getKey()+"");
            //tmp.add(entry.getValue()+"");
            tmp.add(key);
            tmp.add(group_by_item.get(key));
            groupByParam.add(tmp);

            group_by.setTable_name(new ArrayList<String>(Arrays.asList(key/*entry.getKey()+""*/)));
            //opDag.addVertex(group_by);
            //queue.add(group_by);
            //++id;
            //}
            //TODO: merge and modify projection aggr enum
            if(select_list_aggr.entrySet().size()!=0){
                Iterator<Integer> it=select_list_aggr.keySet().iterator();
                while(it.hasNext()){
                    //for(Map.Entry entry:select_list_aggr.entrySet()){
                    //Operator aggr=new Operator();
                    //aggr.setId(id);
                    //aggr.setOpcode(OpCode.AGGR_FUNC);
                    ArrayList<String> _tmp=new ArrayList<String>();
                    Integer _key=it.next();
                    _tmp.add(_key+"");
                    ArrayList<String> values=select_list_aggr.get(_key);
                    System.out.println(values.size());
                    for(String _str:values){
                        _tmp.add(_str);
                    }
                    groupByParam.add(_tmp);
                    //aggr.setTable_name(new ArrayList<String>(Arrays.asList(values.get(0))));
                    //aggr.setParam(_tmp);
                    //opDag.addVertex(aggr);
                    //queue.add(aggr);
                    //++id;
                }
            }
            group_by.setGroupByParam(groupByParam);
            opDag.addVertex(group_by);
            queue.add(group_by);
            ++id;

        }

//        if(select_list_aggr.entrySet().size()!=0){
//            Iterator<Integer> it=select_list_aggr.keySet().iterator();
//            while(it.hasNext()){
//                //for(Map.Entry entry:select_list_aggr.entrySet()){
//                Operator aggr=new Operator();
//                aggr.setId(id);
//                aggr.setOpcode(OpCode.AGGR_FUNC);
//                ArrayList<String> tmp=new ArrayList<String>();
//                Integer key=it.next();
//                tmp.add(key+"");
//                ArrayList<String> values=select_list_aggr.get(key);
//                System.out.println(values.size());
//                for(String _str:values){
//                    tmp.add(_str);
//                }
//                aggr.setTable_name(new ArrayList<String>(Arrays.asList(values.get(0))));
//                aggr.setParam(tmp);
//                opDag.addVertex(aggr);
//                queue.add(aggr);
//                ++id;
//            }
//        }

        if(select_list.size()!=0){
            //Iterator<String> it=select_list.keySet().iterator();
            ArrayList<ArrayList<String>> proj_param = new ArrayList<ArrayList<String>>();
            ArrayList<String> table_name = new ArrayList<String>();
            Operator projection=new Operator();
            projection.setId(id);
            projection.setOpcode(OpCode.PROJECTION);
            //while(it.hasNext()){
            for(ArrayList<String> tmp:select_list){
                //ArrayList<String> tmp=new ArrayList<String>();
                //String key=it.next();
                //tmp.add(key);
                //tmp.add(select_list.get(key));
                //projection.setParam(tmp);
                proj_param.add(tmp);
                //projection.setTable_name(new ArrayList<String>(Arrays.asList(key)));
                table_name.add(tmp.get(0));
            }
            //++id;
            if(select_list_aggr.entrySet().size()!=0){
                Iterator<Integer> _it=select_list_aggr.keySet().iterator();
                while(_it.hasNext()){

                    ArrayList<String> tmp=new ArrayList<String>();
                    Integer key=_it.next();
                    tmp.add(key+"");
                    ArrayList<String> values=select_list_aggr.get(key);
                    //System.out.println(values.size());
                    for(String _str:values){
                        tmp.add(_str);
                    }
                    proj_param.add(tmp);
                    table_name.add(values.get(0));

                }
            }
            projection.setTable_name(table_name);
            projection.setProj_param(proj_param);
            opDag.addVertex(projection);
            queue.add(projection);
        }

        //2.add edge
        for(int i=0;i<queue.size();++i){
            Operator from=queue.get(i);
            for(int j=i+1;j<queue.size();++j){
                Operator to=queue.get(j);
                if(from.getOpcode()!=to.getOpcode()){
                    if(intersection(from,to)){
                        from.setChild(to.getId());
                        opDag.addEdge(from,to);
                        break;
                    }
                }
            }
        }

//        for(Operator vertex:opDag.vertexSet()){
//            //System.out.println("id: "+vertex.getId()+" param"+vertex.getParam());
//            if(vertex.getOpcode() == OpCode.SOURCE){
//                Operator source = vertex;
//            }
//
//        }
        //ArrayList<ArrayList<Integer>> find_father=new ArrayList<>();
        Hashtable<Integer,ArrayList<Integer>> find_father=new Hashtable<>();
        Iterator<Operator> iter=opDag.iterator();
        while (iter.hasNext()){
            Operator vertex=iter.next();

            if(find_father.containsKey(vertex.getChild())){
                ArrayList<Integer> val=find_father.get(vertex.getChild());
                val.add(vertex.getId());
            }
            else {
                ArrayList<Integer> value=new ArrayList<>();
                value.add(vertex.getId());
                find_father.put(vertex.getChild(),value);
            }
            //tmp.add(vertex.getChild());
            //System.out.println(vertex.toString());
            //System.out.println("param: "+vertex.getParam()+" opcode: "+vertex.getOpcode()+" id: "+vertex.getId()+" child: "+vertex.getChild());
        }

        iter=opDag.iterator();
        while (iter.hasNext()){
            Operator vtx=iter.next();
            if(find_father.containsKey(vtx.getId())){
                vtx.setFather(find_father.get(vtx.getId()));
            }
            System.out.println(vtx.toString());
        }


    }

}
