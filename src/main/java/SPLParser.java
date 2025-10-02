// Generated from SPL.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SPLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, NAME=31, NUMBER=32, 
		STRING=33, WS=34, COMMENT=35, BLOCK_COMMENT=36;
	public static final int
		RULE_spl_prog = 0, RULE_variables = 1, RULE_var = 2, RULE_procdefs = 3, 
		RULE_pdef = 4, RULE_funcdefs = 5, RULE_fdef = 6, RULE_body = 7, RULE_param = 8, 
		RULE_maxthree = 9, RULE_mainprog = 10, RULE_atom = 11, RULE_algo = 12, 
		RULE_instr = 13, RULE_assign = 14, RULE_loop = 15, RULE_branch = 16, RULE_output = 17, 
		RULE_input = 18, RULE_term = 19, RULE_unop = 20, RULE_binop = 21;
	private static String[] makeRuleNames() {
		return new String[] {
			"spl_prog", "variables", "var", "procdefs", "pdef", "funcdefs", "fdef", 
			"body", "param", "maxthree", "mainprog", "atom", "algo", "instr", "assign", 
			"loop", "branch", "output", "input", "term", "unop", "binop"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'glob'", "'{'", "'}'", "'proc'", "'func'", "'main'", "'('", "')'", 
			"';'", "'return'", "'local'", "'var'", "'halt'", "'print'", "'='", "'while'", 
			"'do'", "'until'", "'if'", "'else'", "'neg'", "'not'", "'eq'", "'>'", 
			"'or'", "'and'", "'plus'", "'minus'", "'mult'", "'div'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, "NAME", "NUMBER", "STRING", 
			"WS", "COMMENT", "BLOCK_COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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
	public String getGrammarFileName() { return "SPL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SPLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Spl_progContext extends ParserRuleContext {
		public VariablesContext variables() {
			return getRuleContext(VariablesContext.class,0);
		}
		public ProcdefsContext procdefs() {
			return getRuleContext(ProcdefsContext.class,0);
		}
		public FuncdefsContext funcdefs() {
			return getRuleContext(FuncdefsContext.class,0);
		}
		public MainprogContext mainprog() {
			return getRuleContext(MainprogContext.class,0);
		}
		public Spl_progContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_spl_prog; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterSpl_prog(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitSpl_prog(this);
		}
	}

	public final Spl_progContext spl_prog() throws RecognitionException {
		Spl_progContext _localctx = new Spl_progContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_spl_prog);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(T__0);
			setState(45);
			match(T__1);
			setState(46);
			variables();
			setState(47);
			match(T__2);
			setState(48);
			match(T__3);
			setState(49);
			match(T__1);
			setState(50);
			procdefs();
			setState(51);
			match(T__2);
			setState(52);
			match(T__4);
			setState(53);
			match(T__1);
			setState(54);
			funcdefs();
			setState(55);
			match(T__2);
			setState(56);
			match(T__5);
			setState(57);
			match(T__1);
			setState(58);
			mainprog();
			setState(59);
			match(T__2);
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

	@SuppressWarnings("CheckReturnValue")
	public static class VariablesContext extends ParserRuleContext {
		public VarContext var() {
			return getRuleContext(VarContext.class,0);
		}
		public VariablesContext variables() {
			return getRuleContext(VariablesContext.class,0);
		}
		public VariablesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variables; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterVariables(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitVariables(this);
		}
	}

	public final VariablesContext variables() throws RecognitionException {
		VariablesContext _localctx = new VariablesContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_variables);
		try {
			setState(65);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(62);
				var();
				setState(63);
				variables();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	@SuppressWarnings("CheckReturnValue")
	public static class VarContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(SPLParser.NAME, 0); }
		public VarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitVar(this);
		}
	}

	public final VarContext var() throws RecognitionException {
		VarContext _localctx = new VarContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_var);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			match(NAME);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ProcdefsContext extends ParserRuleContext {
		public PdefContext pdef() {
			return getRuleContext(PdefContext.class,0);
		}
		public ProcdefsContext procdefs() {
			return getRuleContext(ProcdefsContext.class,0);
		}
		public ProcdefsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_procdefs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterProcdefs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitProcdefs(this);
		}
	}

	public final ProcdefsContext procdefs() throws RecognitionException {
		ProcdefsContext _localctx = new ProcdefsContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_procdefs);
		try {
			setState(73);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(70);
				pdef();
				setState(71);
				procdefs();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	@SuppressWarnings("CheckReturnValue")
	public static class PdefContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(SPLParser.NAME, 0); }
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public BodyContext body() {
			return getRuleContext(BodyContext.class,0);
		}
		public PdefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pdef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterPdef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitPdef(this);
		}
	}

	public final PdefContext pdef() throws RecognitionException {
		PdefContext _localctx = new PdefContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_pdef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(75);
			match(NAME);
			setState(76);
			match(T__6);
			setState(77);
			param();
			setState(78);
			match(T__7);
			setState(79);
			match(T__1);
			setState(80);
			body();
			setState(81);
			match(T__2);
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

	@SuppressWarnings("CheckReturnValue")
	public static class FuncdefsContext extends ParserRuleContext {
		public FdefContext fdef() {
			return getRuleContext(FdefContext.class,0);
		}
		public FuncdefsContext funcdefs() {
			return getRuleContext(FuncdefsContext.class,0);
		}
		public FuncdefsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcdefs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterFuncdefs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitFuncdefs(this);
		}
	}

	public final FuncdefsContext funcdefs() throws RecognitionException {
		FuncdefsContext _localctx = new FuncdefsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_funcdefs);
		try {
			setState(87);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case NAME:
				enterOuterAlt(_localctx, 2);
				{
				setState(84);
				fdef();
				setState(85);
				funcdefs();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	@SuppressWarnings("CheckReturnValue")
	public static class FdefContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(SPLParser.NAME, 0); }
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public BodyContext body() {
			return getRuleContext(BodyContext.class,0);
		}
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public FdefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fdef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterFdef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitFdef(this);
		}
	}

	public final FdefContext fdef() throws RecognitionException {
		FdefContext _localctx = new FdefContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_fdef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			match(NAME);
			setState(90);
			match(T__6);
			setState(91);
			param();
			setState(92);
			match(T__7);
			setState(93);
			match(T__1);
			setState(94);
			body();
			setState(95);
			match(T__8);
			setState(96);
			match(T__9);
			setState(97);
			atom();
			setState(98);
			match(T__2);
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

	@SuppressWarnings("CheckReturnValue")
	public static class BodyContext extends ParserRuleContext {
		public MaxthreeContext maxthree() {
			return getRuleContext(MaxthreeContext.class,0);
		}
		public AlgoContext algo() {
			return getRuleContext(AlgoContext.class,0);
		}
		public BodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_body; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitBody(this);
		}
	}

	public final BodyContext body() throws RecognitionException {
		BodyContext _localctx = new BodyContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_body);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100);
			match(T__10);
			setState(101);
			match(T__1);
			setState(102);
			maxthree();
			setState(103);
			match(T__2);
			setState(104);
			algo();
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

	@SuppressWarnings("CheckReturnValue")
	public static class ParamContext extends ParserRuleContext {
		public MaxthreeContext maxthree() {
			return getRuleContext(MaxthreeContext.class,0);
		}
		public ParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitParam(this);
		}
	}

	public final ParamContext param() throws RecognitionException {
		ParamContext _localctx = new ParamContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_param);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
			maxthree();
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

	@SuppressWarnings("CheckReturnValue")
	public static class MaxthreeContext extends ParserRuleContext {
		public List<VarContext> var() {
			return getRuleContexts(VarContext.class);
		}
		public VarContext var(int i) {
			return getRuleContext(VarContext.class,i);
		}
		public MaxthreeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_maxthree; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterMaxthree(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitMaxthree(this);
		}
	}

	public final MaxthreeContext maxthree() throws RecognitionException {
		MaxthreeContext _localctx = new MaxthreeContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_maxthree);
		try {
			setState(117);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(109);
				var();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(110);
				var();
				setState(111);
				var();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(113);
				var();
				setState(114);
				var();
				setState(115);
				var();
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

	@SuppressWarnings("CheckReturnValue")
	public static class MainprogContext extends ParserRuleContext {
		public VariablesContext variables() {
			return getRuleContext(VariablesContext.class,0);
		}
		public AlgoContext algo() {
			return getRuleContext(AlgoContext.class,0);
		}
		public MainprogContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mainprog; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterMainprog(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitMainprog(this);
		}
	}

	public final MainprogContext mainprog() throws RecognitionException {
		MainprogContext _localctx = new MainprogContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_mainprog);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			match(T__11);
			setState(120);
			match(T__1);
			setState(121);
			variables();
			setState(122);
			match(T__2);
			setState(123);
			algo();
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

	@SuppressWarnings("CheckReturnValue")
	public static class AtomContext extends ParserRuleContext {
		public VarContext var() {
			return getRuleContext(VarContext.class,0);
		}
		public TerminalNode NUMBER() { return getToken(SPLParser.NUMBER, 0); }
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitAtom(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_atom);
		try {
			setState(127);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NAME:
				enterOuterAlt(_localctx, 1);
				{
				setState(125);
				var();
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(126);
				match(NUMBER);
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	@SuppressWarnings("CheckReturnValue")
	public static class AlgoContext extends ParserRuleContext {
		public InstrContext instr() {
			return getRuleContext(InstrContext.class,0);
		}
		public AlgoContext algo() {
			return getRuleContext(AlgoContext.class,0);
		}
		public AlgoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_algo; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterAlgo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitAlgo(this);
		}
	}

	public final AlgoContext algo() throws RecognitionException {
		AlgoContext _localctx = new AlgoContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_algo);
		try {
			setState(134);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(129);
				instr();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(130);
				instr();
				setState(131);
				match(T__8);
				setState(132);
				algo();
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

	@SuppressWarnings("CheckReturnValue")
	public static class InstrContext extends ParserRuleContext {
		public OutputContext output() {
			return getRuleContext(OutputContext.class,0);
		}
		public TerminalNode NAME() { return getToken(SPLParser.NAME, 0); }
		public InputContext input() {
			return getRuleContext(InputContext.class,0);
		}
		public AssignContext assign() {
			return getRuleContext(AssignContext.class,0);
		}
		public LoopContext loop() {
			return getRuleContext(LoopContext.class,0);
		}
		public BranchContext branch() {
			return getRuleContext(BranchContext.class,0);
		}
		public InstrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterInstr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitInstr(this);
		}
	}

	public final InstrContext instr() throws RecognitionException {
		InstrContext _localctx = new InstrContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_instr);
		try {
			setState(147);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(136);
				match(T__12);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(137);
				match(T__13);
				setState(138);
				output();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(139);
				match(NAME);
				setState(140);
				match(T__6);
				setState(141);
				input();
				setState(142);
				match(T__7);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(144);
				assign();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(145);
				loop();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(146);
				branch();
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

	@SuppressWarnings("CheckReturnValue")
	public static class AssignContext extends ParserRuleContext {
		public VarContext var() {
			return getRuleContext(VarContext.class,0);
		}
		public TerminalNode NAME() { return getToken(SPLParser.NAME, 0); }
		public InputContext input() {
			return getRuleContext(InputContext.class,0);
		}
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public AssignContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterAssign(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitAssign(this);
		}
	}

	public final AssignContext assign() throws RecognitionException {
		AssignContext _localctx = new AssignContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_assign);
		try {
			setState(160);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(149);
				var();
				setState(150);
				match(T__14);
				setState(151);
				match(NAME);
				setState(152);
				match(T__6);
				setState(153);
				input();
				setState(154);
				match(T__7);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(156);
				var();
				setState(157);
				match(T__14);
				setState(158);
				term();
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

	@SuppressWarnings("CheckReturnValue")
	public static class LoopContext extends ParserRuleContext {
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public AlgoContext algo() {
			return getRuleContext(AlgoContext.class,0);
		}
		public LoopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterLoop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitLoop(this);
		}
	}

	public final LoopContext loop() throws RecognitionException {
		LoopContext _localctx = new LoopContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_loop);
		try {
			setState(175);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__15:
				enterOuterAlt(_localctx, 1);
				{
				setState(162);
				match(T__15);
				setState(163);
				term();
				setState(164);
				match(T__1);
				setState(165);
				algo();
				setState(166);
				match(T__2);
				}
				break;
			case T__16:
				enterOuterAlt(_localctx, 2);
				{
				setState(168);
				match(T__16);
				setState(169);
				match(T__1);
				setState(170);
				algo();
				setState(171);
				match(T__2);
				setState(172);
				match(T__17);
				setState(173);
				term();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	@SuppressWarnings("CheckReturnValue")
	public static class BranchContext extends ParserRuleContext {
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public List<AlgoContext> algo() {
			return getRuleContexts(AlgoContext.class);
		}
		public AlgoContext algo(int i) {
			return getRuleContext(AlgoContext.class,i);
		}
		public BranchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_branch; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterBranch(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitBranch(this);
		}
	}

	public final BranchContext branch() throws RecognitionException {
		BranchContext _localctx = new BranchContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_branch);
		try {
			setState(193);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(177);
				match(T__18);
				setState(178);
				term();
				setState(179);
				match(T__1);
				setState(180);
				algo();
				setState(181);
				match(T__2);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(183);
				match(T__18);
				setState(184);
				term();
				setState(185);
				match(T__1);
				setState(186);
				algo();
				setState(187);
				match(T__2);
				setState(188);
				match(T__19);
				setState(189);
				match(T__1);
				setState(190);
				algo();
				setState(191);
				match(T__2);
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

	@SuppressWarnings("CheckReturnValue")
	public static class OutputContext extends ParserRuleContext {
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public TerminalNode STRING() { return getToken(SPLParser.STRING, 0); }
		public OutputContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_output; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterOutput(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitOutput(this);
		}
	}

	public final OutputContext output() throws RecognitionException {
		OutputContext _localctx = new OutputContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_output);
		try {
			setState(197);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NAME:
			case NUMBER:
				enterOuterAlt(_localctx, 1);
				{
				setState(195);
				atom();
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 2);
				{
				setState(196);
				match(STRING);
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	@SuppressWarnings("CheckReturnValue")
	public static class InputContext extends ParserRuleContext {
		public List<AtomContext> atom() {
			return getRuleContexts(AtomContext.class);
		}
		public AtomContext atom(int i) {
			return getRuleContext(AtomContext.class,i);
		}
		public InputContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_input; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterInput(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitInput(this);
		}
	}

	public final InputContext input() throws RecognitionException {
		InputContext _localctx = new InputContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_input);
		try {
			setState(208);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(200);
				atom();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(201);
				atom();
				setState(202);
				atom();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(204);
				atom();
				setState(205);
				atom();
				setState(206);
				atom();
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

	@SuppressWarnings("CheckReturnValue")
	public static class TermContext extends ParserRuleContext {
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public UnopContext unop() {
			return getRuleContext(UnopContext.class,0);
		}
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public BinopContext binop() {
			return getRuleContext(BinopContext.class,0);
		}
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitTerm(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_term);
		try {
			setState(222);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(210);
				atom();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(211);
				match(T__6);
				setState(212);
				unop();
				setState(213);
				term();
				setState(214);
				match(T__7);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(216);
				match(T__6);
				setState(217);
				term();
				setState(218);
				binop();
				setState(219);
				term();
				setState(220);
				match(T__7);
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

	@SuppressWarnings("CheckReturnValue")
	public static class UnopContext extends ParserRuleContext {
		public UnopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterUnop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitUnop(this);
		}
	}

	public final UnopContext unop() throws RecognitionException {
		UnopContext _localctx = new UnopContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_unop);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(224);
			_la = _input.LA(1);
			if ( !(_la==T__20 || _la==T__21) ) {
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

	@SuppressWarnings("CheckReturnValue")
	public static class BinopContext extends ParserRuleContext {
		public BinopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_binop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).enterBinop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPLListener ) ((SPLListener)listener).exitBinop(this);
		}
	}

	public final BinopContext binop() throws RecognitionException {
		BinopContext _localctx = new BinopContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_binop);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(226);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 2139095040L) != 0)) ) {
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

	public static final String _serializedATN =
		"\u0004\u0001$\u00e5\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001B\b\u0001\u0001\u0002"+
		"\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003"+
		"J\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0003\u0005X\b\u0005\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0003\tv\b\t\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0003\u000b"+
		"\u0080\b\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003\f\u0087\b"+
		"\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0003\r\u0094\b\r\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0003\u000e\u00a1\b\u000e\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f"+
		"\u00b0\b\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010"+
		"\u00c2\b\u0010\u0001\u0011\u0001\u0011\u0003\u0011\u00c6\b\u0011\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u00d1\b\u0012\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u00df"+
		"\b\u0013\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0000"+
		"\u0000\u0016\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016"+
		"\u0018\u001a\u001c\u001e \"$&(*\u0000\u0002\u0001\u0000\u0015\u0016\u0001"+
		"\u0000\u0017\u001e\u00e4\u0000,\u0001\u0000\u0000\u0000\u0002A\u0001\u0000"+
		"\u0000\u0000\u0004C\u0001\u0000\u0000\u0000\u0006I\u0001\u0000\u0000\u0000"+
		"\bK\u0001\u0000\u0000\u0000\nW\u0001\u0000\u0000\u0000\fY\u0001\u0000"+
		"\u0000\u0000\u000ed\u0001\u0000\u0000\u0000\u0010j\u0001\u0000\u0000\u0000"+
		"\u0012u\u0001\u0000\u0000\u0000\u0014w\u0001\u0000\u0000\u0000\u0016\u007f"+
		"\u0001\u0000\u0000\u0000\u0018\u0086\u0001\u0000\u0000\u0000\u001a\u0093"+
		"\u0001\u0000\u0000\u0000\u001c\u00a0\u0001\u0000\u0000\u0000\u001e\u00af"+
		"\u0001\u0000\u0000\u0000 \u00c1\u0001\u0000\u0000\u0000\"\u00c5\u0001"+
		"\u0000\u0000\u0000$\u00d0\u0001\u0000\u0000\u0000&\u00de\u0001\u0000\u0000"+
		"\u0000(\u00e0\u0001\u0000\u0000\u0000*\u00e2\u0001\u0000\u0000\u0000,"+
		"-\u0005\u0001\u0000\u0000-.\u0005\u0002\u0000\u0000./\u0003\u0002\u0001"+
		"\u0000/0\u0005\u0003\u0000\u000001\u0005\u0004\u0000\u000012\u0005\u0002"+
		"\u0000\u000023\u0003\u0006\u0003\u000034\u0005\u0003\u0000\u000045\u0005"+
		"\u0005\u0000\u000056\u0005\u0002\u0000\u000067\u0003\n\u0005\u000078\u0005"+
		"\u0003\u0000\u000089\u0005\u0006\u0000\u00009:\u0005\u0002\u0000\u0000"+
		":;\u0003\u0014\n\u0000;<\u0005\u0003\u0000\u0000<\u0001\u0001\u0000\u0000"+
		"\u0000=B\u0001\u0000\u0000\u0000>?\u0003\u0004\u0002\u0000?@\u0003\u0002"+
		"\u0001\u0000@B\u0001\u0000\u0000\u0000A=\u0001\u0000\u0000\u0000A>\u0001"+
		"\u0000\u0000\u0000B\u0003\u0001\u0000\u0000\u0000CD\u0005\u001f\u0000"+
		"\u0000D\u0005\u0001\u0000\u0000\u0000EJ\u0001\u0000\u0000\u0000FG\u0003"+
		"\b\u0004\u0000GH\u0003\u0006\u0003\u0000HJ\u0001\u0000\u0000\u0000IE\u0001"+
		"\u0000\u0000\u0000IF\u0001\u0000\u0000\u0000J\u0007\u0001\u0000\u0000"+
		"\u0000KL\u0005\u001f\u0000\u0000LM\u0005\u0007\u0000\u0000MN\u0003\u0010"+
		"\b\u0000NO\u0005\b\u0000\u0000OP\u0005\u0002\u0000\u0000PQ\u0003\u000e"+
		"\u0007\u0000QR\u0005\u0003\u0000\u0000R\t\u0001\u0000\u0000\u0000SX\u0001"+
		"\u0000\u0000\u0000TU\u0003\f\u0006\u0000UV\u0003\n\u0005\u0000VX\u0001"+
		"\u0000\u0000\u0000WS\u0001\u0000\u0000\u0000WT\u0001\u0000\u0000\u0000"+
		"X\u000b\u0001\u0000\u0000\u0000YZ\u0005\u001f\u0000\u0000Z[\u0005\u0007"+
		"\u0000\u0000[\\\u0003\u0010\b\u0000\\]\u0005\b\u0000\u0000]^\u0005\u0002"+
		"\u0000\u0000^_\u0003\u000e\u0007\u0000_`\u0005\t\u0000\u0000`a\u0005\n"+
		"\u0000\u0000ab\u0003\u0016\u000b\u0000bc\u0005\u0003\u0000\u0000c\r\u0001"+
		"\u0000\u0000\u0000de\u0005\u000b\u0000\u0000ef\u0005\u0002\u0000\u0000"+
		"fg\u0003\u0012\t\u0000gh\u0005\u0003\u0000\u0000hi\u0003\u0018\f\u0000"+
		"i\u000f\u0001\u0000\u0000\u0000jk\u0003\u0012\t\u0000k\u0011\u0001\u0000"+
		"\u0000\u0000lv\u0001\u0000\u0000\u0000mv\u0003\u0004\u0002\u0000no\u0003"+
		"\u0004\u0002\u0000op\u0003\u0004\u0002\u0000pv\u0001\u0000\u0000\u0000"+
		"qr\u0003\u0004\u0002\u0000rs\u0003\u0004\u0002\u0000st\u0003\u0004\u0002"+
		"\u0000tv\u0001\u0000\u0000\u0000ul\u0001\u0000\u0000\u0000um\u0001\u0000"+
		"\u0000\u0000un\u0001\u0000\u0000\u0000uq\u0001\u0000\u0000\u0000v\u0013"+
		"\u0001\u0000\u0000\u0000wx\u0005\f\u0000\u0000xy\u0005\u0002\u0000\u0000"+
		"yz\u0003\u0002\u0001\u0000z{\u0005\u0003\u0000\u0000{|\u0003\u0018\f\u0000"+
		"|\u0015\u0001\u0000\u0000\u0000}\u0080\u0003\u0004\u0002\u0000~\u0080"+
		"\u0005 \u0000\u0000\u007f}\u0001\u0000\u0000\u0000\u007f~\u0001\u0000"+
		"\u0000\u0000\u0080\u0017\u0001\u0000\u0000\u0000\u0081\u0087\u0003\u001a"+
		"\r\u0000\u0082\u0083\u0003\u001a\r\u0000\u0083\u0084\u0005\t\u0000\u0000"+
		"\u0084\u0085\u0003\u0018\f\u0000\u0085\u0087\u0001\u0000\u0000\u0000\u0086"+
		"\u0081\u0001\u0000\u0000\u0000\u0086\u0082\u0001\u0000\u0000\u0000\u0087"+
		"\u0019\u0001\u0000\u0000\u0000\u0088\u0094\u0005\r\u0000\u0000\u0089\u008a"+
		"\u0005\u000e\u0000\u0000\u008a\u0094\u0003\"\u0011\u0000\u008b\u008c\u0005"+
		"\u001f\u0000\u0000\u008c\u008d\u0005\u0007\u0000\u0000\u008d\u008e\u0003"+
		"$\u0012\u0000\u008e\u008f\u0005\b\u0000\u0000\u008f\u0094\u0001\u0000"+
		"\u0000\u0000\u0090\u0094\u0003\u001c\u000e\u0000\u0091\u0094\u0003\u001e"+
		"\u000f\u0000\u0092\u0094\u0003 \u0010\u0000\u0093\u0088\u0001\u0000\u0000"+
		"\u0000\u0093\u0089\u0001\u0000\u0000\u0000\u0093\u008b\u0001\u0000\u0000"+
		"\u0000\u0093\u0090\u0001\u0000\u0000\u0000\u0093\u0091\u0001\u0000\u0000"+
		"\u0000\u0093\u0092\u0001\u0000\u0000\u0000\u0094\u001b\u0001\u0000\u0000"+
		"\u0000\u0095\u0096\u0003\u0004\u0002\u0000\u0096\u0097\u0005\u000f\u0000"+
		"\u0000\u0097\u0098\u0005\u001f\u0000\u0000\u0098\u0099\u0005\u0007\u0000"+
		"\u0000\u0099\u009a\u0003$\u0012\u0000\u009a\u009b\u0005\b\u0000\u0000"+
		"\u009b\u00a1\u0001\u0000\u0000\u0000\u009c\u009d\u0003\u0004\u0002\u0000"+
		"\u009d\u009e\u0005\u000f\u0000\u0000\u009e\u009f\u0003&\u0013\u0000\u009f"+
		"\u00a1\u0001\u0000\u0000\u0000\u00a0\u0095\u0001\u0000\u0000\u0000\u00a0"+
		"\u009c\u0001\u0000\u0000\u0000\u00a1\u001d\u0001\u0000\u0000\u0000\u00a2"+
		"\u00a3\u0005\u0010\u0000\u0000\u00a3\u00a4\u0003&\u0013\u0000\u00a4\u00a5"+
		"\u0005\u0002\u0000\u0000\u00a5\u00a6\u0003\u0018\f\u0000\u00a6\u00a7\u0005"+
		"\u0003\u0000\u0000\u00a7\u00b0\u0001\u0000\u0000\u0000\u00a8\u00a9\u0005"+
		"\u0011\u0000\u0000\u00a9\u00aa\u0005\u0002\u0000\u0000\u00aa\u00ab\u0003"+
		"\u0018\f\u0000\u00ab\u00ac\u0005\u0003\u0000\u0000\u00ac\u00ad\u0005\u0012"+
		"\u0000\u0000\u00ad\u00ae\u0003&\u0013\u0000\u00ae\u00b0\u0001\u0000\u0000"+
		"\u0000\u00af\u00a2\u0001\u0000\u0000\u0000\u00af\u00a8\u0001\u0000\u0000"+
		"\u0000\u00b0\u001f\u0001\u0000\u0000\u0000\u00b1\u00b2\u0005\u0013\u0000"+
		"\u0000\u00b2\u00b3\u0003&\u0013\u0000\u00b3\u00b4\u0005\u0002\u0000\u0000"+
		"\u00b4\u00b5\u0003\u0018\f\u0000\u00b5\u00b6\u0005\u0003\u0000\u0000\u00b6"+
		"\u00c2\u0001\u0000\u0000\u0000\u00b7\u00b8\u0005\u0013\u0000\u0000\u00b8"+
		"\u00b9\u0003&\u0013\u0000\u00b9\u00ba\u0005\u0002\u0000\u0000\u00ba\u00bb"+
		"\u0003\u0018\f\u0000\u00bb\u00bc\u0005\u0003\u0000\u0000\u00bc\u00bd\u0005"+
		"\u0014\u0000\u0000\u00bd\u00be\u0005\u0002\u0000\u0000\u00be\u00bf\u0003"+
		"\u0018\f\u0000\u00bf\u00c0\u0005\u0003\u0000\u0000\u00c0\u00c2\u0001\u0000"+
		"\u0000\u0000\u00c1\u00b1\u0001\u0000\u0000\u0000\u00c1\u00b7\u0001\u0000"+
		"\u0000\u0000\u00c2!\u0001\u0000\u0000\u0000\u00c3\u00c6\u0003\u0016\u000b"+
		"\u0000\u00c4\u00c6\u0005!\u0000\u0000\u00c5\u00c3\u0001\u0000\u0000\u0000"+
		"\u00c5\u00c4\u0001\u0000\u0000\u0000\u00c6#\u0001\u0000\u0000\u0000\u00c7"+
		"\u00d1\u0001\u0000\u0000\u0000\u00c8\u00d1\u0003\u0016\u000b\u0000\u00c9"+
		"\u00ca\u0003\u0016\u000b\u0000\u00ca\u00cb\u0003\u0016\u000b\u0000\u00cb"+
		"\u00d1\u0001\u0000\u0000\u0000\u00cc\u00cd\u0003\u0016\u000b\u0000\u00cd"+
		"\u00ce\u0003\u0016\u000b\u0000\u00ce\u00cf\u0003\u0016\u000b\u0000\u00cf"+
		"\u00d1\u0001\u0000\u0000\u0000\u00d0\u00c7\u0001\u0000\u0000\u0000\u00d0"+
		"\u00c8\u0001\u0000\u0000\u0000\u00d0\u00c9\u0001\u0000\u0000\u0000\u00d0"+
		"\u00cc\u0001\u0000\u0000\u0000\u00d1%\u0001\u0000\u0000\u0000\u00d2\u00df"+
		"\u0003\u0016\u000b\u0000\u00d3\u00d4\u0005\u0007\u0000\u0000\u00d4\u00d5"+
		"\u0003(\u0014\u0000\u00d5\u00d6\u0003&\u0013\u0000\u00d6\u00d7\u0005\b"+
		"\u0000\u0000\u00d7\u00df\u0001\u0000\u0000\u0000\u00d8\u00d9\u0005\u0007"+
		"\u0000\u0000\u00d9\u00da\u0003&\u0013\u0000\u00da\u00db\u0003*\u0015\u0000"+
		"\u00db\u00dc\u0003&\u0013\u0000\u00dc\u00dd\u0005\b\u0000\u0000\u00dd"+
		"\u00df\u0001\u0000\u0000\u0000\u00de\u00d2\u0001\u0000\u0000\u0000\u00de"+
		"\u00d3\u0001\u0000\u0000\u0000\u00de\u00d8\u0001\u0000\u0000\u0000\u00df"+
		"\'\u0001\u0000\u0000\u0000\u00e0\u00e1\u0007\u0000\u0000\u0000\u00e1)"+
		"\u0001\u0000\u0000\u0000\u00e2\u00e3\u0007\u0001\u0000\u0000\u00e3+\u0001"+
		"\u0000\u0000\u0000\rAIWu\u007f\u0086\u0093\u00a0\u00af\u00c1\u00c5\u00d0"+
		"\u00de";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}