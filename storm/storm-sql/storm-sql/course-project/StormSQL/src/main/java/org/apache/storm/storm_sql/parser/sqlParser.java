// Generated from sql.g4 by ANTLR 4.7

package org.apache.storm.storm_sql.parser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class sqlParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, ID=24, NUM=25, 
		WS=26;
	public static final int
		RULE_root = 0, RULE_select_list = 1, RULE_select_list_elem = 2, RULE_table_sources = 3, 
		RULE_table_source = 4, RULE_expression = 5, RULE_table_name = 6, RULE_column_name = 7, 
		RULE_aggregate_function = 8, RULE_search_condition = 9, RULE_search_condition_and = 10, 
		RULE_comparison_operator = 11, RULE_group_by_item = 12, RULE_within_time = 13;
	public static final String[] ruleNames = {
		"root", "select_list", "select_list_elem", "table_sources", "table_source", 
		"expression", "table_name", "column_name", "aggregate_function", "search_condition", 
		"search_condition_and", "comparison_operator", "group_by_item", "within_time"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'select'", "'from'", "'where'", "'group'", "'by'", "','", "'having'", 
		"'within'", "'('", "')'", "'.'", "'avg'", "'max'", "'min'", "'sum'", "'count'", 
		"'and'", "'='", "'>'", "'<'", "'<='", "'>='", "'!='"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		"ID", "NUM", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "sql.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public sqlParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class RootContext extends ParserRuleContext {
		public Select_listContext select_list() {
			return getRuleContext(Select_listContext.class,0);
		}
		public Table_sourcesContext table_sources() {
			return getRuleContext(Table_sourcesContext.class,0);
		}
		public List<Search_conditionContext> search_condition() {
			return getRuleContexts(Search_conditionContext.class);
		}
		public Search_conditionContext search_condition(int i) {
			return getRuleContext(Search_conditionContext.class,i);
		}
		public List<Group_by_itemContext> group_by_item() {
			return getRuleContexts(Group_by_itemContext.class);
		}
		public Group_by_itemContext group_by_item(int i) {
			return getRuleContext(Group_by_itemContext.class,i);
		}
		public Within_timeContext within_time() {
			return getRuleContext(Within_timeContext.class,0);
		}
		public RootContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_root; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitRoot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RootContext root() throws RecognitionException {
		RootContext _localctx = new RootContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_root);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			match(T__0);
			setState(29);
			select_list();
			setState(30);
			match(T__1);
			setState(31);
			table_sources();
			setState(34);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(32);
				match(T__2);
				setState(33);
				search_condition();
				}
			}

			setState(46);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(36);
				match(T__3);
				setState(37);
				match(T__4);
				setState(38);
				group_by_item();
				setState(43);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(39);
					match(T__5);
					setState(40);
					group_by_item();
					}
					}
					setState(45);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(50);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(48);
				match(T__6);
				setState(49);
				search_condition();
				}
			}

			setState(54);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(52);
				match(T__7);
				setState(53);
				within_time();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Select_listContext extends ParserRuleContext {
		public Select_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_list; }
	 
		public Select_listContext() { }
		public void copyFrom(Select_listContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PrintSelectListContext extends Select_listContext {
		public List<Select_list_elemContext> select_list_elem() {
			return getRuleContexts(Select_list_elemContext.class);
		}
		public Select_list_elemContext select_list_elem(int i) {
			return getRuleContext(Select_list_elemContext.class,i);
		}
		public PrintSelectListContext(Select_listContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitPrintSelectList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_listContext select_list() throws RecognitionException {
		Select_listContext _localctx = new Select_listContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_select_list);
		int _la;
		try {
			_localctx = new PrintSelectListContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			select_list_elem();
			setState(61);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(57);
				match(T__5);
				setState(58);
				select_list_elem();
				}
				}
				setState(63);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Select_list_elemContext extends ParserRuleContext {
		public Select_list_elemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_list_elem; }
	 
		public Select_list_elemContext() { }
		public void copyFrom(Select_list_elemContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PrintSelectListElemContext extends Select_list_elemContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public PrintSelectListElemContext(Select_list_elemContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitPrintSelectListElem(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SelectAggregateFunctionContext extends Select_list_elemContext {
		public Aggregate_functionContext aggregate_function() {
			return getRuleContext(Aggregate_functionContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public SelectAggregateFunctionContext(Select_list_elemContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitSelectAggregateFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Select_list_elemContext select_list_elem() throws RecognitionException {
		Select_list_elemContext _localctx = new Select_list_elemContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_select_list_elem);
		try {
			setState(70);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				_localctx = new PrintSelectListElemContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(64);
				expression();
				}
				break;
			case 2:
				_localctx = new SelectAggregateFunctionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(65);
				aggregate_function();
				setState(66);
				match(T__8);
				setState(67);
				expression();
				setState(68);
				match(T__9);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Table_sourcesContext extends ParserRuleContext {
		public Table_sourcesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_sources; }
	 
		public Table_sourcesContext() { }
		public void copyFrom(Table_sourcesContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TableSourcesContext extends Table_sourcesContext {
		public List<Table_sourceContext> table_source() {
			return getRuleContexts(Table_sourceContext.class);
		}
		public Table_sourceContext table_source(int i) {
			return getRuleContext(Table_sourceContext.class,i);
		}
		public TableSourcesContext(Table_sourcesContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitTableSources(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Table_sourcesContext table_sources() throws RecognitionException {
		Table_sourcesContext _localctx = new Table_sourcesContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_table_sources);
		int _la;
		try {
			_localctx = new TableSourcesContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			table_source();
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(73);
				match(T__5);
				setState(74);
				table_source();
				}
				}
				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Table_sourceContext extends ParserRuleContext {
		public Table_sourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_source; }
	 
		public Table_sourceContext() { }
		public void copyFrom(Table_sourceContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TableSourceContext extends Table_sourceContext {
		public TerminalNode ID() { return getToken(sqlParser.ID, 0); }
		public TableSourceContext(Table_sourceContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitTableSource(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Table_sourceContext table_source() throws RecognitionException {
		Table_sourceContext _localctx = new Table_sourceContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_table_source);
		try {
			_localctx = new TableSourceContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class IdContext extends ExpressionContext {
		public TerminalNode ID() { return getToken(sqlParser.ID, 0); }
		public IdContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitId(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprAggrFuncContext extends ExpressionContext {
		public Aggregate_functionContext aggregate_function() {
			return getRuleContext(Aggregate_functionContext.class,0);
		}
		public ExprAggrFuncContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitExprAggrFunc(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumContext extends ExpressionContext {
		public TerminalNode NUM() { return getToken(sqlParser.NUM, 0); }
		public NumContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitNum(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprContext extends ExpressionContext {
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public Column_nameContext column_name() {
			return getRuleContext(Column_nameContext.class,0);
		}
		public ExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_expression);
		try {
			setState(89);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				_localctx = new ExprAggrFuncContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(82);
				aggregate_function();
				}
				break;
			case 2:
				_localctx = new ExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(83);
				table_name();
				setState(84);
				match(T__10);
				setState(85);
				column_name();
				}
				}
				break;
			case 3:
				_localctx = new NumContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(87);
				match(NUM);
				}
				break;
			case 4:
				_localctx = new IdContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(88);
				match(ID);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Table_nameContext extends ParserRuleContext {
		public Table_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_name; }
	 
		public Table_nameContext() { }
		public void copyFrom(Table_nameContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TableNameContext extends Table_nameContext {
		public TerminalNode ID() { return getToken(sqlParser.ID, 0); }
		public TableNameContext(Table_nameContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitTableName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Table_nameContext table_name() throws RecognitionException {
		Table_nameContext _localctx = new Table_nameContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_table_name);
		try {
			_localctx = new TableNameContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Column_nameContext extends ParserRuleContext {
		public Column_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column_name; }
	 
		public Column_nameContext() { }
		public void copyFrom(Column_nameContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ColumnNameContext extends Column_nameContext {
		public TerminalNode ID() { return getToken(sqlParser.ID, 0); }
		public ColumnNameContext(Column_nameContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitColumnName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Column_nameContext column_name() throws RecognitionException {
		Column_nameContext _localctx = new Column_nameContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_column_name);
		try {
			_localctx = new ColumnNameContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Aggregate_functionContext extends ParserRuleContext {
		public Aggregate_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregate_function; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitAggregate_function(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Aggregate_functionContext aggregate_function() throws RecognitionException {
		Aggregate_functionContext _localctx = new Aggregate_functionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_aggregate_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Search_conditionContext extends ParserRuleContext {
		public Search_conditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_search_condition; }
	 
		public Search_conditionContext() { }
		public void copyFrom(Search_conditionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PrintSearchConditionContext extends Search_conditionContext {
		public List<Search_condition_andContext> search_condition_and() {
			return getRuleContexts(Search_condition_andContext.class);
		}
		public Search_condition_andContext search_condition_and(int i) {
			return getRuleContext(Search_condition_andContext.class,i);
		}
		public PrintSearchConditionContext(Search_conditionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitPrintSearchCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Search_conditionContext search_condition() throws RecognitionException {
		Search_conditionContext _localctx = new Search_conditionContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_search_condition);
		int _la;
		try {
			_localctx = new PrintSearchConditionContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
			search_condition_and();
			setState(102);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__16) {
				{
				{
				setState(98);
				match(T__16);
				setState(99);
				search_condition_and();
				}
				}
				setState(104);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Search_condition_andContext extends ParserRuleContext {
		public Search_condition_andContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_search_condition_and; }
	 
		public Search_condition_andContext() { }
		public void copyFrom(Search_condition_andContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PrintSearchConditionAndContext extends Search_condition_andContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public Comparison_operatorContext comparison_operator() {
			return getRuleContext(Comparison_operatorContext.class,0);
		}
		public PrintSearchConditionAndContext(Search_condition_andContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitPrintSearchConditionAnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Search_condition_andContext search_condition_and() throws RecognitionException {
		Search_condition_andContext _localctx = new Search_condition_andContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_search_condition_and);
		try {
			_localctx = new PrintSearchConditionAndContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(105);
			expression();
			setState(106);
			comparison_operator();
			setState(107);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Comparison_operatorContext extends ParserRuleContext {
		public Comparison_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison_operator; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitComparison_operator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comparison_operatorContext comparison_operator() throws RecognitionException {
		Comparison_operatorContext _localctx = new Comparison_operatorContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_comparison_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Group_by_itemContext extends ParserRuleContext {
		public Group_by_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_group_by_item; }
	 
		public Group_by_itemContext() { }
		public void copyFrom(Group_by_itemContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class GroupByItemContext extends Group_by_itemContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public GroupByItemContext(Group_by_itemContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitGroupByItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Group_by_itemContext group_by_item() throws RecognitionException {
		Group_by_itemContext _localctx = new Group_by_itemContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_group_by_item);
		try {
			_localctx = new GroupByItemContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Within_timeContext extends ParserRuleContext {
		public Within_timeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_within_time; }
	 
		public Within_timeContext() { }
		public void copyFrom(Within_timeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class WithinTimeContext extends Within_timeContext {
		public TerminalNode NUM() { return getToken(sqlParser.NUM, 0); }
		public WithinTimeContext(Within_timeContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof sqlVisitor ) return ((sqlVisitor<? extends T>)visitor).visitWithinTime(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Within_timeContext within_time() throws RecognitionException {
		Within_timeContext _localctx = new Within_timeContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_within_time);
		try {
			_localctx = new WithinTimeContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(113);
			match(NUM);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\34v\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\3\2\3\2\3\2\3\2\3\2\5\2%\n\2\3"+
		"\2\3\2\3\2\3\2\3\2\7\2,\n\2\f\2\16\2/\13\2\5\2\61\n\2\3\2\3\2\5\2\65\n"+
		"\2\3\2\3\2\5\29\n\2\3\3\3\3\3\3\7\3>\n\3\f\3\16\3A\13\3\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\5\4I\n\4\3\5\3\5\3\5\7\5N\n\5\f\5\16\5Q\13\5\3\6\3\6\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\5\7\\\n\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13"+
		"\7\13g\n\13\f\13\16\13j\13\13\3\f\3\f\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3"+
		"\17\3\17\2\2\20\2\4\6\b\n\f\16\20\22\24\26\30\32\34\2\4\3\2\16\22\3\2"+
		"\24\31\2s\2\36\3\2\2\2\4:\3\2\2\2\6H\3\2\2\2\bJ\3\2\2\2\nR\3\2\2\2\f["+
		"\3\2\2\2\16]\3\2\2\2\20_\3\2\2\2\22a\3\2\2\2\24c\3\2\2\2\26k\3\2\2\2\30"+
		"o\3\2\2\2\32q\3\2\2\2\34s\3\2\2\2\36\37\7\3\2\2\37 \5\4\3\2 !\7\4\2\2"+
		"!$\5\b\5\2\"#\7\5\2\2#%\5\24\13\2$\"\3\2\2\2$%\3\2\2\2%\60\3\2\2\2&\'"+
		"\7\6\2\2\'(\7\7\2\2(-\5\32\16\2)*\7\b\2\2*,\5\32\16\2+)\3\2\2\2,/\3\2"+
		"\2\2-+\3\2\2\2-.\3\2\2\2.\61\3\2\2\2/-\3\2\2\2\60&\3\2\2\2\60\61\3\2\2"+
		"\2\61\64\3\2\2\2\62\63\7\t\2\2\63\65\5\24\13\2\64\62\3\2\2\2\64\65\3\2"+
		"\2\2\658\3\2\2\2\66\67\7\n\2\2\679\5\34\17\28\66\3\2\2\289\3\2\2\29\3"+
		"\3\2\2\2:?\5\6\4\2;<\7\b\2\2<>\5\6\4\2=;\3\2\2\2>A\3\2\2\2?=\3\2\2\2?"+
		"@\3\2\2\2@\5\3\2\2\2A?\3\2\2\2BI\5\f\7\2CD\5\22\n\2DE\7\13\2\2EF\5\f\7"+
		"\2FG\7\f\2\2GI\3\2\2\2HB\3\2\2\2HC\3\2\2\2I\7\3\2\2\2JO\5\n\6\2KL\7\b"+
		"\2\2LN\5\n\6\2MK\3\2\2\2NQ\3\2\2\2OM\3\2\2\2OP\3\2\2\2P\t\3\2\2\2QO\3"+
		"\2\2\2RS\7\32\2\2S\13\3\2\2\2T\\\5\22\n\2UV\5\16\b\2VW\7\r\2\2WX\5\20"+
		"\t\2X\\\3\2\2\2Y\\\7\33\2\2Z\\\7\32\2\2[T\3\2\2\2[U\3\2\2\2[Y\3\2\2\2"+
		"[Z\3\2\2\2\\\r\3\2\2\2]^\7\32\2\2^\17\3\2\2\2_`\7\32\2\2`\21\3\2\2\2a"+
		"b\t\2\2\2b\23\3\2\2\2ch\5\26\f\2de\7\23\2\2eg\5\26\f\2fd\3\2\2\2gj\3\2"+
		"\2\2hf\3\2\2\2hi\3\2\2\2i\25\3\2\2\2jh\3\2\2\2kl\5\f\7\2lm\5\30\r\2mn"+
		"\5\f\7\2n\27\3\2\2\2op\t\3\2\2p\31\3\2\2\2qr\5\f\7\2r\33\3\2\2\2st\7\33"+
		"\2\2t\35\3\2\2\2\f$-\60\648?HO[h";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}