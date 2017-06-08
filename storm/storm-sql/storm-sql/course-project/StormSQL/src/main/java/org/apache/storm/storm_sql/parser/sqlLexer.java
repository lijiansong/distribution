// Generated from sql.g4 by ANTLR 4.7

package org.apache.storm.storm_sql.parser;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class sqlLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, ID=24, NUM=25, 
		WS=26;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "ID", "NUM", "WS"
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


	public sqlLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "sql.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\34\u00b1\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3"+
		"\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\7\3"+
		"\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\13"+
		"\3\13\3\f\3\f\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17"+
		"\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22"+
		"\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\3\30\3\30"+
		"\3\30\3\31\3\31\7\31\u0094\n\31\f\31\16\31\u0097\13\31\3\32\3\32\3\32"+
		"\7\32\u009c\n\32\f\32\16\32\u009f\13\32\3\32\3\32\3\32\7\32\u00a4\n\32"+
		"\f\32\16\32\u00a7\13\32\5\32\u00a9\n\32\3\33\6\33\u00ac\n\33\r\33\16\33"+
		"\u00ad\3\33\3\33\2\2\34\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f"+
		"\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63"+
		"\33\65\34\3\2\7\5\2C\\aac|\6\2\62;C\\aac|\3\2\63;\3\2\62;\5\2\13\f\17"+
		"\17\"\"\2\u00b6\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3"+
		"\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2"+
		"\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3"+
		"\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2"+
		"\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\3\67\3\2\2\2\5"+
		">\3\2\2\2\7C\3\2\2\2\tI\3\2\2\2\13O\3\2\2\2\rR\3\2\2\2\17T\3\2\2\2\21"+
		"[\3\2\2\2\23b\3\2\2\2\25d\3\2\2\2\27f\3\2\2\2\31h\3\2\2\2\33l\3\2\2\2"+
		"\35p\3\2\2\2\37t\3\2\2\2!x\3\2\2\2#~\3\2\2\2%\u0082\3\2\2\2\'\u0084\3"+
		"\2\2\2)\u0086\3\2\2\2+\u0088\3\2\2\2-\u008b\3\2\2\2/\u008e\3\2\2\2\61"+
		"\u0091\3\2\2\2\63\u00a8\3\2\2\2\65\u00ab\3\2\2\2\678\7u\2\289\7g\2\29"+
		":\7n\2\2:;\7g\2\2;<\7e\2\2<=\7v\2\2=\4\3\2\2\2>?\7h\2\2?@\7t\2\2@A\7q"+
		"\2\2AB\7o\2\2B\6\3\2\2\2CD\7y\2\2DE\7j\2\2EF\7g\2\2FG\7t\2\2GH\7g\2\2"+
		"H\b\3\2\2\2IJ\7i\2\2JK\7t\2\2KL\7q\2\2LM\7w\2\2MN\7r\2\2N\n\3\2\2\2OP"+
		"\7d\2\2PQ\7{\2\2Q\f\3\2\2\2RS\7.\2\2S\16\3\2\2\2TU\7j\2\2UV\7c\2\2VW\7"+
		"x\2\2WX\7k\2\2XY\7p\2\2YZ\7i\2\2Z\20\3\2\2\2[\\\7y\2\2\\]\7k\2\2]^\7v"+
		"\2\2^_\7j\2\2_`\7k\2\2`a\7p\2\2a\22\3\2\2\2bc\7*\2\2c\24\3\2\2\2de\7+"+
		"\2\2e\26\3\2\2\2fg\7\60\2\2g\30\3\2\2\2hi\7c\2\2ij\7x\2\2jk\7i\2\2k\32"+
		"\3\2\2\2lm\7o\2\2mn\7c\2\2no\7z\2\2o\34\3\2\2\2pq\7o\2\2qr\7k\2\2rs\7"+
		"p\2\2s\36\3\2\2\2tu\7u\2\2uv\7w\2\2vw\7o\2\2w \3\2\2\2xy\7e\2\2yz\7q\2"+
		"\2z{\7w\2\2{|\7p\2\2|}\7v\2\2}\"\3\2\2\2~\177\7c\2\2\177\u0080\7p\2\2"+
		"\u0080\u0081\7f\2\2\u0081$\3\2\2\2\u0082\u0083\7?\2\2\u0083&\3\2\2\2\u0084"+
		"\u0085\7@\2\2\u0085(\3\2\2\2\u0086\u0087\7>\2\2\u0087*\3\2\2\2\u0088\u0089"+
		"\7>\2\2\u0089\u008a\7?\2\2\u008a,\3\2\2\2\u008b\u008c\7@\2\2\u008c\u008d"+
		"\7?\2\2\u008d.\3\2\2\2\u008e\u008f\7#\2\2\u008f\u0090\7?\2\2\u0090\60"+
		"\3\2\2\2\u0091\u0095\t\2\2\2\u0092\u0094\t\3\2\2\u0093\u0092\3\2\2\2\u0094"+
		"\u0097\3\2\2\2\u0095\u0093\3\2\2\2\u0095\u0096\3\2\2\2\u0096\62\3\2\2"+
		"\2\u0097\u0095\3\2\2\2\u0098\u0099\7/\2\2\u0099\u009d\t\4\2\2\u009a\u009c"+
		"\t\5\2\2\u009b\u009a\3\2\2\2\u009c\u009f\3\2\2\2\u009d\u009b\3\2\2\2\u009d"+
		"\u009e\3\2\2\2\u009e\u00a9\3\2\2\2\u009f\u009d\3\2\2\2\u00a0\u00a9\7\62"+
		"\2\2\u00a1\u00a5\t\4\2\2\u00a2\u00a4\t\5\2\2\u00a3\u00a2\3\2\2\2\u00a4"+
		"\u00a7\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00a9\3\2"+
		"\2\2\u00a7\u00a5\3\2\2\2\u00a8\u0098\3\2\2\2\u00a8\u00a0\3\2\2\2\u00a8"+
		"\u00a1\3\2\2\2\u00a9\64\3\2\2\2\u00aa\u00ac\t\6\2\2\u00ab\u00aa\3\2\2"+
		"\2\u00ac\u00ad\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\u00af"+
		"\3\2\2\2\u00af\u00b0\b\33\2\2\u00b0\66\3\2\2\2\b\2\u0095\u009d\u00a5\u00a8"+
		"\u00ad\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}