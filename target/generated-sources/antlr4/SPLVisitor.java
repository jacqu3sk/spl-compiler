// Generated from SPL.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SPLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SPLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SPLParser#spl_prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpl_prog(SPLParser.Spl_progContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#variables}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariables(SPLParser.VariablesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar(SPLParser.VarContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#procdefs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcdefs(SPLParser.ProcdefsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#pdef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPdef(SPLParser.PdefContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#funcdefs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncdefs(SPLParser.FuncdefsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#fdef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFdef(SPLParser.FdefContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBody(SPLParser.BodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(SPLParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#maxthree}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMaxthree(SPLParser.MaxthreeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#mainprog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMainprog(SPLParser.MainprogContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(SPLParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#algo}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlgo(SPLParser.AlgoContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#instr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstr(SPLParser.InstrContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#assign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssign(SPLParser.AssignContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoop(SPLParser.LoopContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#branch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBranch(SPLParser.BranchContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#output}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutput(SPLParser.OutputContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#input}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput(SPLParser.InputContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(SPLParser.TermContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#unop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnop(SPLParser.UnopContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPLParser#binop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinop(SPLParser.BinopContext ctx);
}