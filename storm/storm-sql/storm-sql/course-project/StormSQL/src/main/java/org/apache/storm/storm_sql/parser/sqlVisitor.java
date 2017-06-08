// Generated from sql.g4 by ANTLR 4.7

package org.apache.storm.storm_sql.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link sqlParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface sqlVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link sqlParser#root}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRoot(sqlParser.RootContext ctx);
	/**
	 * Visit a parse tree produced by the {@code printSelectList}
	 * labeled alternative in {@link sqlParser#select_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintSelectList(sqlParser.PrintSelectListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code printSelectListElem}
	 * labeled alternative in {@link sqlParser#select_list_elem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintSelectListElem(sqlParser.PrintSelectListElemContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectAggregateFunction}
	 * labeled alternative in {@link sqlParser#select_list_elem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectAggregateFunction(sqlParser.SelectAggregateFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tableSources}
	 * labeled alternative in {@link sqlParser#table_sources}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTableSources(sqlParser.TableSourcesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tableSource}
	 * labeled alternative in {@link sqlParser#table_source}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTableSource(sqlParser.TableSourceContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprAggrFunc}
	 * labeled alternative in {@link sqlParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprAggrFunc(sqlParser.ExprAggrFuncContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expr}
	 * labeled alternative in {@link sqlParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(sqlParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code num}
	 * labeled alternative in {@link sqlParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNum(sqlParser.NumContext ctx);
	/**
	 * Visit a parse tree produced by the {@code id}
	 * labeled alternative in {@link sqlParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(sqlParser.IdContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tableName}
	 * labeled alternative in {@link sqlParser#table_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTableName(sqlParser.TableNameContext ctx);
	/**
	 * Visit a parse tree produced by the {@code columnName}
	 * labeled alternative in {@link sqlParser#column_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColumnName(sqlParser.ColumnNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link sqlParser#aggregate_function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggregate_function(sqlParser.Aggregate_functionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code printSearchCondition}
	 * labeled alternative in {@link sqlParser#search_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintSearchCondition(sqlParser.PrintSearchConditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code printSearchConditionAnd}
	 * labeled alternative in {@link sqlParser#search_condition_and}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintSearchConditionAnd(sqlParser.PrintSearchConditionAndContext ctx);
	/**
	 * Visit a parse tree produced by {@link sqlParser#comparison_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_operator(sqlParser.Comparison_operatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code groupByItem}
	 * labeled alternative in {@link sqlParser#group_by_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupByItem(sqlParser.GroupByItemContext ctx);
	/**
	 * Visit a parse tree produced by the {@code withinTime}
	 * labeled alternative in {@link sqlParser#within_time}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWithinTime(sqlParser.WithinTimeContext ctx);
}