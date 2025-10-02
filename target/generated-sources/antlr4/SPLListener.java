// Generated from SPL.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SPLParser}.
 */
public interface SPLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SPLParser#spl_prog}.
	 * @param ctx the parse tree
	 */
	void enterSpl_prog(SPLParser.Spl_progContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#spl_prog}.
	 * @param ctx the parse tree
	 */
	void exitSpl_prog(SPLParser.Spl_progContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#variables}.
	 * @param ctx the parse tree
	 */
	void enterVariables(SPLParser.VariablesContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#variables}.
	 * @param ctx the parse tree
	 */
	void exitVariables(SPLParser.VariablesContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#var}.
	 * @param ctx the parse tree
	 */
	void enterVar(SPLParser.VarContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#var}.
	 * @param ctx the parse tree
	 */
	void exitVar(SPLParser.VarContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#procdefs}.
	 * @param ctx the parse tree
	 */
	void enterProcdefs(SPLParser.ProcdefsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#procdefs}.
	 * @param ctx the parse tree
	 */
	void exitProcdefs(SPLParser.ProcdefsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#pdef}.
	 * @param ctx the parse tree
	 */
	void enterPdef(SPLParser.PdefContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#pdef}.
	 * @param ctx the parse tree
	 */
	void exitPdef(SPLParser.PdefContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#funcdefs}.
	 * @param ctx the parse tree
	 */
	void enterFuncdefs(SPLParser.FuncdefsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#funcdefs}.
	 * @param ctx the parse tree
	 */
	void exitFuncdefs(SPLParser.FuncdefsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#fdef}.
	 * @param ctx the parse tree
	 */
	void enterFdef(SPLParser.FdefContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#fdef}.
	 * @param ctx the parse tree
	 */
	void exitFdef(SPLParser.FdefContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#body}.
	 * @param ctx the parse tree
	 */
	void enterBody(SPLParser.BodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#body}.
	 * @param ctx the parse tree
	 */
	void exitBody(SPLParser.BodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(SPLParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(SPLParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#maxthree}.
	 * @param ctx the parse tree
	 */
	void enterMaxthree(SPLParser.MaxthreeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#maxthree}.
	 * @param ctx the parse tree
	 */
	void exitMaxthree(SPLParser.MaxthreeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#mainprog}.
	 * @param ctx the parse tree
	 */
	void enterMainprog(SPLParser.MainprogContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#mainprog}.
	 * @param ctx the parse tree
	 */
	void exitMainprog(SPLParser.MainprogContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(SPLParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(SPLParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#algo}.
	 * @param ctx the parse tree
	 */
	void enterAlgo(SPLParser.AlgoContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#algo}.
	 * @param ctx the parse tree
	 */
	void exitAlgo(SPLParser.AlgoContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#instr}.
	 * @param ctx the parse tree
	 */
	void enterInstr(SPLParser.InstrContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#instr}.
	 * @param ctx the parse tree
	 */
	void exitInstr(SPLParser.InstrContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#assign}.
	 * @param ctx the parse tree
	 */
	void enterAssign(SPLParser.AssignContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#assign}.
	 * @param ctx the parse tree
	 */
	void exitAssign(SPLParser.AssignContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#loop}.
	 * @param ctx the parse tree
	 */
	void enterLoop(SPLParser.LoopContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#loop}.
	 * @param ctx the parse tree
	 */
	void exitLoop(SPLParser.LoopContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#branch}.
	 * @param ctx the parse tree
	 */
	void enterBranch(SPLParser.BranchContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#branch}.
	 * @param ctx the parse tree
	 */
	void exitBranch(SPLParser.BranchContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#output}.
	 * @param ctx the parse tree
	 */
	void enterOutput(SPLParser.OutputContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#output}.
	 * @param ctx the parse tree
	 */
	void exitOutput(SPLParser.OutputContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#input}.
	 * @param ctx the parse tree
	 */
	void enterInput(SPLParser.InputContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#input}.
	 * @param ctx the parse tree
	 */
	void exitInput(SPLParser.InputContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(SPLParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(SPLParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#unop}.
	 * @param ctx the parse tree
	 */
	void enterUnop(SPLParser.UnopContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#unop}.
	 * @param ctx the parse tree
	 */
	void exitUnop(SPLParser.UnopContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPLParser#binop}.
	 * @param ctx the parse tree
	 */
	void enterBinop(SPLParser.BinopContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPLParser#binop}.
	 * @param ctx the parse tree
	 */
	void exitBinop(SPLParser.BinopContext ctx);
}