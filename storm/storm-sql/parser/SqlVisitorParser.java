package storm_sql.parser;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

/**
 * Created by json-lee on 17-6-4.
 */
public class SqlVisitorParser extends sqlBaseVisitor<String>{

    //@variable select_list, key: table name(stream name), value: column name(fields name)
    Hashtable<String,String> select_list=new Hashtable<String,String>();
    //@variable select_list_aggr, key: avg 0 max 1 min 2 sum 3, value: table name and column name
    Hashtable<Integer,ArrayList<String>> select_list_aggr=new Hashtable<Integer, ArrayList<String>>();
    //@variable table_sources, table name (stream name)
    ArrayList<String> table_sources=new ArrayList<String>();
    //@variable where_cond_, where condition,<operation, operand1, operand2>, e.g. <">=","s1.r1","100">,<"=","s1.r1","s2.r2">
    ArrayList<ArrayList<String>> where_cond_join=new ArrayList<ArrayList<String>>();

    ArrayList<ArrayList<String>> where_cond_select=new ArrayList<ArrayList<String>>();
    //@variable group_by_item, key: table name(stream name), value: column name
    Hashtable<String,String> group_by_item=new Hashtable<>();

    Integer within_time=0;


    public ArrayList<ArrayList<String>> getWhere_cond_join() {
        return where_cond_join;
    }

    public ArrayList<ArrayList<String>> getWhere_cond_select() {
        return where_cond_select;
    }



    public Hashtable<String, String> getSelect_list() {
        return select_list;
    }

    public Hashtable<Integer, ArrayList<String>> getSelect_list_aggr() {
        return select_list_aggr;
    }

    public ArrayList<String> getTable_sources() {
        return table_sources;
    }



    public Hashtable<String, String> getGroup_by_item() {
        return group_by_item;
    }

    public int getWithin_time() {
        return within_time;
    }

    @Override
    public String visitRoot(sqlParser.RootContext ctx) {
        return super.visitRoot(ctx);
    }

    //select s1.r1,s2.r2, avg(s2.r2) from s1,s2 where s1.r1=s2.r1 and s1.r3>100 group by s1.r4 within 20
    //select_list->select_list_elem (',' select_list_elem)*
    @Override
    public String visitPrintSelectList(sqlParser.PrintSelectListContext ctx) {
        //String first=visit(ctx.select_list_elem(0));
        //System.out.println("+++++++++++select_list_elem: "+ctx.select_list_elem().size());
        for(int i=0;i<ctx.select_list_elem().size();++i){
            String select_list_elem=visit(ctx.select_list_elem(i));
            //System.out.println("select_list_elem: "+select_list_elem);
            String []spilit=select_list_elem.split("\\.");
//            for(String str:spilit){
//                System.out.println(str);
//            }
            if(spilit.length!=0){
                //must be avg(s1.r3)

                if(spilit[0].contains("(")){
                    //avg s2
                    String []tmp=spilit[0].split("\\(");

                    String []table_column=spilit[1].split("\\)");
                    //System.out.println(table_column.length);
                    switch (tmp[0]){
                        case "avg":
                            select_list_aggr.put(0,
                                    new ArrayList<String>(
                                    Arrays.asList(tmp[1],table_column[0]) ));
                            //return "";
                            break;
                        case "max":
                            select_list_aggr.put(1,
                                    new ArrayList<String>(
                                            Arrays.asList(tmp[1],table_column[0])) );
                            //return "";
                            break;
                        case "min":
                            select_list_aggr.put(2,
                                    new ArrayList<String>(
                                            Arrays.asList(tmp[1],table_column[0]) ) );
                            //return "";
                            break;
                        case "sum":
                            select_list_aggr.put(3,
                                    new ArrayList<String>(
                                            Arrays.asList(tmp[1],table_column[0]) ));
                            //return "";
                            break;
                        case "count":
                            select_list_aggr.put(4,
                                    new ArrayList<String>(
                                            Arrays.asList(tmp[1],table_column[0]) ));
                            //return "";
                            break;
                    }
                }
                else {
                    //must be s1.r1
                    select_list.put(spilit[0],spilit[1]);
                }

            }

        }
        return "";
        //return first;
        //return super.visitPrintSelectList(ctx);
    }

    //select_list_elem -> expression
    @Override
    public String visitPrintSelectListElem(sqlParser.PrintSelectListElemContext ctx) {
        //return super.visitPrintSelectListElem(ctx);
        return visit(ctx.expression());
    }

    //select_list_elem -> aggregate_function'(' expression ')'
    @Override
    public String visitSelectAggregateFunction(sqlParser.SelectAggregateFunctionContext ctx) {
        String aggr_name=visit(ctx.aggregate_function());
        String left="(",right=")";
        String expr=visit(ctx.expression());
        return aggr_name+left+expr+right;
        //return super.visitSelectAggregateFunction(ctx);
    }

    //table_source (',' table_source)*
    @Override
    public String visitTableSources(sqlParser.TableSourcesContext ctx) {
        //return super.visitTableSources(ctx);
        //System.out.println("++++++++++++=table_source: "+ctx.table_source().size());
        for(int i=0;i<ctx.table_source().size();++i){
            table_sources.add(visit(ctx.table_source(i)));
        }
        return "";
        //return visit(ctx.table_source(0));
    }

    //table_source -> ID
    @Override
    public String visitTableSource(sqlParser.TableSourceContext ctx) {
        //return super.visitTableSource(ctx);
        //return visit(ctx.ID());
        return ctx.ID().getText();
    }

    //expression-> aggregate_function
    @Override
    public String visitExprAggrFunc(sqlParser.ExprAggrFuncContext ctx) {
        //return super.visitExprAggrFunc(ctx);
        return visit(ctx.aggregate_function());
    }

    //expression-> (table_name '.' column_name)
    @Override
    public String visitExpr(sqlParser.ExprContext ctx) {
        //return super.visitExpr(ctx);
        return visit(ctx.table_name())+"."+visit(ctx.column_name());
    }

    //expression->NUM
    @Override
    public String visitNum(sqlParser.NumContext ctx) {
        //return super.visitNum(ctx);
        //return visit(ctx.NUM());
        return ctx.NUM().getText();
    }

    //table_name->ID
    @Override
    public String visitTableName(sqlParser.TableNameContext ctx) {
        //return super.visitTableName(ctx);
        //return visit(ctx.ID());
        return ctx.ID().getText();
    }

    //column_name ->ID
    @Override
    public String visitColumnName(sqlParser.ColumnNameContext ctx) {
        //return super.visitColumnName(ctx);
        //return visit(ctx.ID());
        return ctx.ID().getText();
    }

    //aggregate_function ->'avg' | 'max' | 'min' | 'sum'
    @Override
    public String visitAggregate_function(sqlParser.Aggregate_functionContext ctx) {
        //return super.visitAggregate_function(ctx);
        return ctx.getText();
    }

    //search_condition->search_condition_and ('and' search_condition_and)*
    @Override
    public String visitPrintSearchCondition(sqlParser.PrintSearchConditionContext ctx) {
        //return super.visitPrintSearchCondition(ctx);
        System.out.println("----------------"+ctx.search_condition_and().size());
        for(int i=0;i<ctx.search_condition_and().size();++i){
            ArrayList<String> tmp=new ArrayList<String>();
            String operand1=visit(ctx.search_condition_and(i).getChild(0));
            String comp_symbol=visit(ctx.search_condition_and(i).getChild(1));
            String operand2=visit(ctx.search_condition_and(i).getChild(2));
            if(comp_symbol.equals("=")&&(operand1.split("\\.").length==2) && (operand2.split("\\.").length==2) ){
                tmp.add(comp_symbol);
                tmp.add(operand1);
                tmp.add(operand2);
                where_cond_join.add(tmp);
                //return "";
            }
            else{
                tmp.add(comp_symbol);
                tmp.add(operand1);
                tmp.add(operand2);
                where_cond_select.add(tmp);
                //return "";
            }
        }
        return "";
    }

    //search_condition_and->expression comparison_operator expression
    @Override
    public String visitPrintSearchConditionAnd(sqlParser.PrintSearchConditionAndContext ctx) {
        //return super.visitPrintSearchConditionAnd(ctx);
        visit(ctx.expression(0));
        visit(ctx.comparison_operator());
        visit(ctx.expression(1));
        //TODO:it may be wrong
        return "";
    }

    //comparison_operator->'=' | '>' | '<' | '<=' | '>=' | '!='
    @Override
    public String visitComparison_operator(sqlParser.Comparison_operatorContext ctx) {
        //return super.visitComparison_operator(ctx);
        return ctx.getText();
    }

    //group_by_item->expression
    @Override
    public String visitGroupByItem(sqlParser.GroupByItemContext ctx) {
        //return super.visitGroupByItem(ctx);
        String groupby=visit(ctx.expression());
        String []tmp=groupby.split("\\.");
        if(tmp.length!=0){
            group_by_item.put(tmp[0],tmp[1]);
        }
        return "";
    }

    //within_time-> (NUM)
    @Override
    public String visitWithinTime(sqlParser.WithinTimeContext ctx) {
        //return visit(ctx.NUM());
        //TODO: within_time should > 0
        within_time=Integer.valueOf(ctx.NUM().getText());
        return ctx.NUM().getText();
        //return super.visitWithinTime(ctx);
    }

    @Override
    public String visit(ParseTree tree) {
        return super.visit(tree);
    }

    @Override
    public String visitChildren(RuleNode node) {
        return super.visitChildren(node);
    }

    @Override
    public String visitTerminal(TerminalNode node) {
        return super.visitTerminal(node);
    }

    @Override
    public String visitErrorNode(ErrorNode node) {
        return super.visitErrorNode(node);
    }

    @Override
    protected String defaultResult() {
        return super.defaultResult();
    }

    @Override
    protected String aggregateResult(String aggregate, String nextResult) {
        return super.aggregateResult(aggregate, nextResult);
    }

    @Override
    protected boolean shouldVisitNextChild(RuleNode node, String currentResult) {
        return super.shouldVisitNextChild(node, currentResult);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

}
