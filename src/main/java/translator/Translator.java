package translator;
/*
 * translates source code to intermediate code
 */


import java.util.HashMap;

import org.antlr.v4.runtime.tree.*;

import generated.SPLBaseVisitor;
import generated.SPLParser;
import symbolTable.*;

/**
 * Intermediate code generator for the SPL compiler
 * Translates the AST to intermediate code
 */
public class Translator extends SPLBaseVisitor<String> {
    // Final intermediate code
    private StringBuilder intermediateCode;

    // Object to manage labels
    private Label label;
    // Object to manage temporary variables
    private TempVariable tempVariable;

    // AST from semantic analysis phase
    private final SymbolTable symbolTable;
    // AST from parser phase
    private final ParseTree tree;

    // Hashmap to store function name and sub-tree of AST
    private HashMap<String, SPLParser.FdefContext> funcMap ;
    // Hashmap to store procedure name and sub-tree of AST
    private HashMap<String, SPLParser.PdefContext> procMap ;

    /**
     * Constructor
     * @param symbolTable symbol table from semantic analysis phase
     * @param tree AST from parser phase
     */
    public Translator(SymbolTable symbolTable, ParseTree tree) {
        intermediateCode = new StringBuilder();
        label = new Label();
        tempVariable = new TempVariable();
        this.symbolTable = symbolTable;
        this.tree = tree;
        funcMap = new HashMap<>();
        procMap = new HashMap<>();
    }

    /**
     * Getter
     * @param name Variable name to look up
     * @return intermediate code as string
     */
    public String getIntermediateCode()
    {
        return intermediateCode.toString();
    }
    
     /**
     * Start intermediate code generation
     */
    public void generateIntermediateCode()
    {
        visit(tree);
    }

     /**
     * print intermediate code to terminal
     */
    public void printIntermediateCode()
    {
        System.out.println("\nGenerated Intermediate Code:");
        System.out.println(intermediateCode);
    }

    // translation rules

     /**
     * visit SPL_PROG node in AST
     * @param ctx SPL program context
     * @return string
     */
    public String visitSpl_prog(SPLParser.Spl_progContext ctx) {
        if (ctx != null) {
            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree child = ctx.getChild(i);
                // visit nodes in MAINPROG 
                if (child instanceof SPLParser.MainprogContext) {
                    visit(child);
                    break;
                }
                // Add all functions to funcmap
                if (child instanceof SPLParser.FuncdefsContext) {
                    visit(child);
                }
                // Add all procedures to procmap
                if (child instanceof SPLParser.ProcdefsContext) {
                    visit(child);
                }
            }
        }
        return null;
    }

    /**
     * visit Funcdefs node in AST
     * FUNCDEFS ::= FDEF FUNCDEFS || NULL
     * @param ctx Funcdefs context
     * @return string
     */
    public String visitFuncdefs(SPLParser.FuncdefsContext ctx)
    {
        if (ctx.fdef() !=null)
        {
            visitFdef(ctx.fdef());
        }
        if (ctx.funcdefs()!=null)
        {
            visitFuncdefs(ctx.funcdefs());
        }
        return null;
    }

    /**
     * visit Fdef node in AST
     * FDEF ::= NAME ( PARAM ) {} BODY ; return ATOM }
     * @param ctx Fdef context
     * @return string
     */
    public String visitFdef(SPLParser.FdefContext ctx)
    {
        String funcName = ctx.NAME().getText();
        funcMap.put(funcName, ctx);
        return null;
    }

    /**
     * start searching function subtree stored in hashmap
     * @param functionName name of the function
     * @param ctx Fdef context
     * @return string of temporary variable used to store the return value
     */
    public String inlineFunction(String functionName, SPLParser.FdefContext ctx)
    {
        visitBody(ctx.body());
        return visitAtom(ctx.atom(), functionName);
    }

     /**
     * start searching procedure subtree stored in hashmap
     * @param ctx Pdef context
     */
    public void inlineProc(SPLParser.PdefContext ctx)
    {
        visitBody(ctx.body());
    }

    /**
     * visit Body node in AST
     * Body ::= ALGO
     * @param ctx Body context
     * @return string
     */
    public String visitBody(SPLParser.BodyContext ctx)
    {
        visitAlgo(ctx.algo());
        return null;
    }

    /**
     * visit Procdefs node in AST
     * PROCDEFS ::= PDEF PROCDEFS || null
     * @param ctx Procdefs context
     * @return string
     */
    public String visitProcdefs(SPLParser.ProcdefsContext ctx)
    {
        if (ctx.pdef() !=null)
        {
            visitPdef(ctx.pdef());
        }
        if (ctx.procdefs()!=null)
        {
            visitProcdefs(ctx.procdefs());
        }
        return null;
    }

    /**
     * visit Pdef node in AST
     * PDEF ::= NAME ( PARAM ) { BODY }
     * @param ctx Pdef context
     * @return string
     */
    public String visitPdef(SPLParser.PdefContext ctx)
    {
        String funcName = ctx.NAME().getText();
        procMap.put(funcName, ctx);
        return null;
    }

    /**
     * visit Mainprog node in AST
     * MAINPROG ::= var { VARIABLES } ALGO
     * @param ctx Mainprog context
     * @return string
     */
    public String visitMainprog(SPLParser.MainprogContext ctx) {
        if (ctx.algo() != null) visit(ctx.algo());
        return null;
    }

    /**
     * visit Atom node in AST
     * ATOM ::= VAR | number
     * @param ctx Atom context
     * @return string
     */
    public String visitAtom(SPLParser.AtomContext ctx, String currentScope) {
        
        if (ctx.NUMBER() != null) // ATOM ::= number
        {
            tempVariable.newTemp();
            String code = tempVariable.printTemp() + " = " + ctx.NUMBER().getText();
            intermediateCode.append(code);
            intermediateCode.append("\n");
            return tempVariable.printTemp();
        }else { // ATOM ::= VAR
            if (currentScope==null)
            {
                currentScope = getIntermediateCode();
            }
            SymbolEntry symbol = symbolTable.lookupVariable(ctx.var().getText(),currentScope , ScopeType.LOCAL);
            if (symbol==null)
            {
                symbol = symbolTable.lookupVariable(ctx.var().getText(), currentScope, ScopeType.MAIN);
            }
            if (symbol==null)
            {
                symbol = symbolTable.lookupVariable(ctx.var().getText(), currentScope, null);
            }
            tempVariable.newTemp();
            String code = tempVariable.printTemp() + " = " + symbol.getRenamedVariable();
            intermediateCode.append(code);
            intermediateCode.append("\n");
           
            symbolTable.updateVariable(symbol.getName(), symbol.getScopeOwner(), symbol.getScope(),tempVariable.printTemp() );
            return tempVariable.printTemp();

        }
    }

    /**
     * visit Algo node in AST
     * ALGO ::= INSTR ; ALGO
     * @param ctx Algo context
     * @return string
     */
    public String visitAlgo(SPLParser.AlgoContext ctx) {

        if (ctx.instr() != null) {
            visitInstr(ctx.instr());
        }
        if (ctx.algo() != null) {
            visitAlgo(ctx.algo());
        }
        return null;
    }

    /**
     * visit Instr node in AST
     * INSTR ::= halt || print OUTPUT || ASSIGN || BRANCH || LOOP || NAME(INPUT)
     * @param ctx Instr context
     * @return string
     */
    public String visitInstr(SPLParser.InstrContext ctx) {
        if (ctx.getText().startsWith("halt")) { // INSTR ::= halt 
            intermediateCode.append("STOP\n");

        } else if (ctx.getText().startsWith("print")) { // INSTR ::= print OUTPUT 
            intermediateCode.append("PRINT ");
            visitOutput(ctx.output());

        }else if (ctx.assign()!=null) { // INSTR ::= ASSIGN 
           visitAssign(ctx.assign());

        }else if (ctx.branch() !=null) { // INSTR ::= BRANCH 
            visitBranch(ctx.branch());

        }else if (ctx.loop()!=null) { // INSTR ::= LOOP 
            visitLoop(ctx.loop());

        }else { //  INSTR ::= NAME(INPUT)

            String function_code = "";
            SymbolEntry function = symbolTable.lookupProcedure(ctx.NAME().getText(), null, null);
            SPLParser.ParamContext functionParameters = procMap.get(function.getName()).param();

            if (ctx.input().atom().size() != functionParameters.maxthree().var().size())
            {
                System.out.println("Error: Number of arguments does not match number of parameters in procedure: " + function.getName());
                return null;
            }

            for (int i=0; i<ctx.input().atom().size();i++)
            {

                String t1 = visitAtom(ctx.input().atom(i),null);

                SymbolEntry symbol = symbolTable.lookupVariable(functionParameters.maxthree().var(i).getText(), function.getName(), ScopeType.LOCAL);
                if (symbol==null)
                {
                    symbol = symbolTable.lookupVariable(functionParameters.maxthree().var(i).getText(), function.getName(), ScopeType.MAIN);
                }
                if (symbol==null)
                {
                    symbol = symbolTable.lookupVariable(functionParameters.maxthree().var(i).getText(), function.getName(), null);
                }
                if (symbol!=null)
                {
                    function_code += symbol.getRenamedVariable() + " = " + t1 + "\n";
                }
            }

            intermediateCode.append(function_code);
            inlineProc(procMap.get(function.getName()));
            
        }
        return null;
    }

    /**
     * visit Output node in AST
     * OUTPUT ::= ATOM || string
     * @param ctx Output context
     * @return string
     */
    public String visitOutput(SPLParser.OutputContext ctx)
    {
        if (ctx.STRING() != null) // OUTPUT ::= string
        {
            String code = ctx.STRING().getText();
            intermediateCode.append(code + "\n");
        }else if (ctx.atom() !=null) {
            if (ctx.atom().NUMBER() !=null) // OUTPUT ::= ATOM
            {
                visitAtom(ctx.atom(),null);
            }else{
                SymbolEntry symbol = symbolTable.lookupVariable(ctx.atom().var().getText(), getIntermediateCode(), ScopeType.MAIN);
                if (symbol==null)
                {
                    symbol = symbolTable.lookupVariable(ctx.atom().var().getText(), getIntermediateCode(), ScopeType.LOCAL);
                }
                if (symbol==null)
                {
                    symbol = symbolTable.lookupVariable(ctx.atom().var().getText(), getIntermediateCode(), null);
                }
                intermediateCode.append(symbol.getRenamedVariable() + "\n");
            }
            
        }
        return null;
    }

    /**
     * visit Assign node in AST
     * ASSIGN ::= VAR=TERM || VAR=NAME(INPUT)
     * @param ctx Assign context
     * @return string
     */
    public String visitAssign(SPLParser.AssignContext ctx)
    {
        if (ctx.term()!= null) // ASSIGN ::= VAR=TERM
        {
            SymbolEntry symbol = symbolTable.lookupVariable(ctx.var().getText(), getIntermediateCode(), ScopeType.MAIN);
            if (symbol==null)
            {
                symbol = symbolTable.lookupVariable(ctx.var().getText(), getIntermediateCode(), ScopeType.LOCAL);
            }
            if (symbol==null)
            {
                symbol = symbolTable.lookupVariable(ctx.var().getText(), getIntermediateCode(), null);
            }
            String t1 = visitTerm(ctx.term(), null, null);

            String varCode = symbol.getRenamedVariable() + " = " + t1;
            intermediateCode.append(varCode);
            intermediateCode.append("\n");

        }else{ // ASSIGN ::= VAR=NAME(INPUT)

            String function_code = "";
            SymbolEntry function = symbolTable.lookupFunction(ctx.NAME().getText(), null, null);
            SPLParser.ParamContext functionParameters = funcMap.get(function.getName()).param();

            for (int i=0; i<ctx.input().atom().size();i++)
            {
                String t1 = visitAtom(ctx.input().atom(i),null);

                SymbolEntry symbol = symbolTable.lookupVariable(functionParameters.maxthree().var(i).getText(), function.getName(), ScopeType.LOCAL);
                if (symbol==null)
                {
                    symbol = symbolTable.lookupVariable(functionParameters.maxthree().var(i).getText(), function.getName(), ScopeType.MAIN);
                }
                if (symbol==null)
                {
                    symbol = symbolTable.lookupVariable(functionParameters.maxthree().var(i).getText(), function.getName(), null);
                }
                if (symbol!=null)
                {
                    function_code += symbol.getRenamedVariable() + " = " + t1 + "\n";
                }
            }

            intermediateCode.append(function_code);
            String returnT = inlineFunction(function.getName(),funcMap.get(function.getName()));

            SymbolEntry symbol = symbolTable.lookupVariable(ctx.var().getText(), getIntermediateCode(), null);
            String varCode = symbol.getRenamedVariable() + " = " + returnT;
            intermediateCode.append(varCode);
            intermediateCode.append("\n");
        }

        return null;
    }

    /**
     * visit Branch node in AST
     * BRANCH ::= if TERM { ALGO } || if TERM { ALGO } else { ALGO }
     * @param ctx Branch context
     * @return string
     */
    public String visitBranch(SPLParser.BranchContext ctx)
    {
        label.newLabel();
        String l1 = label.printLabel();
        label.newLabel();
        String l2 = label.printLabel();
        
        String condition = visitTerm(ctx.term(),l1,l2);
        if (condition.contains(","))
        {
            label.newLabel();
            String l3 = label.printLabel();
            String[] conditions = condition.split(",");

            intermediateCode.append("IF " + conditions[0] + " THEN " + l1 + "\n");
            if (ctx.term().unop()!=null && ctx.term().unop().getText().equals("not"))
            {
                visitAlgo(ctx.algo(0));
            }else{
                if (ctx.algo(1)!=null)
                {
                    visitAlgo(ctx.algo(1));
                }
            }
            intermediateCode.append("GOTO "+l3+"\n");
            intermediateCode.append("REM "+l1+"\n");
            intermediateCode.append("IF " + conditions[1] + " THEN " + l2 + "\n");
            if (ctx.term().unop()!=null && ctx.term().unop().getText().equals("not"))
            {
                visitAlgo(ctx.algo(0));
            }else{
                if (ctx.algo(1)!=null)
                {
                    visitAlgo(ctx.algo(1));
                }
            }
            intermediateCode.append("GOTO "+l2+"\n");
            
            intermediateCode.append("REM "+l2+"\n");
            if (ctx.term().unop()!=null && ctx.term().unop().getText().equals("not"))
            {
                if (ctx.algo(1)!=null)
                {
                    visitAlgo(ctx.algo(1));
                }
            }else{
                visitAlgo(ctx.algo(0));
            }

            intermediateCode.append("REM "+l3+"\n");

        }else if (condition.contains("#"))
        {
            label.newLabel();
            String l3 = label.printLabel();
            String[] conditions = condition.split("#");

            intermediateCode.append("IF " + conditions[0] + " THEN " + l2 + "\n");
            intermediateCode.append("GOTO "+l1+"\n");
            
            intermediateCode.append("REM "+l1+"\n");
            intermediateCode.append("IF " + conditions[1] + " THEN " + l2 + "\n");
            if (ctx.term().unop()!=null && ctx.term().unop().getText().equals("not"))
            {
                visitAlgo(ctx.algo(0));
            }else{
                if (ctx.algo(1)!=null)
                {
                    visitAlgo(ctx.algo(1));
                }
            }
            intermediateCode.append("GOTO "+l3+"\n");
            
            intermediateCode.append("REM "+l2+"\n");
            if (ctx.term().unop()!=null && ctx.term().unop().getText().equals("not"))
            {
                if (ctx.algo(1)!=null)
                {
                    visitAlgo(ctx.algo(1));
                }
            }else{
                visitAlgo(ctx.algo(0));
            }

            intermediateCode.append("REM "+l3+"\n");

        }else{
            intermediateCode.append("IF " + condition + " THEN " + l1 + "\n");
            if (ctx.term().unop()!=null && ctx.term().unop().getText().equals("not"))
            {
                visitAlgo(ctx.algo(0));
            }else{
                if (ctx.algo(1)!=null)
                {
                    visitAlgo(ctx.algo(1));
                }
            }
            intermediateCode.append("GOTO "+l2+"\n");
            intermediateCode.append("REM "+l1+"\n");
            if (ctx.term().unop()!=null && ctx.term().unop().getText().equals("not"))
            {
                if (ctx.algo(1)!=null)
                {
                    visitAlgo(ctx.algo(1));
                }
            }else{
                visitAlgo(ctx.algo(0));
            }
            
            intermediateCode.append("REM "+l2+"\n");
        }

        return null;
    }

    /**
     * visit Loop node in AST
     * LOOP ::= while TERM { ALGO } || do { ALGO } until TERM
     * @param ctx Loop context
     * @return string
     */
    public String visitLoop(SPLParser.LoopContext ctx)
    {
        if (ctx.getText().startsWith("while"))  // LOOP ::= while TERM { ALGO }
        { 
            label.newLabel();
            String l1 = label.printLabel();
            label.newLabel();
            String l2 = label.printLabel();
            label.newLabel();
            String l3 = label.printLabel();

            intermediateCode.append("REM " + l1 + "\n");
            String t1 = visitTerm(ctx.term(), l2,l3);
            intermediateCode.append("IF " + t1 + " THEN " +l2 + "\n");
            intermediateCode.append("GOTO " + l3 + "\n");
            intermediateCode.append("REM " + l2 + "\n");
            visitAlgo(ctx.algo());
            intermediateCode.append("GOTO " + l1 + "\n" + "REM " + l3+ "\n");
        } else { // LOOP ::=do { ALGO } until TERM
            label.newLabel();
            String l1 = label.printLabel();
            label.newLabel();
            String l2 = label.printLabel();

            intermediateCode.append("REM " + l1 + "\n");
            visitAlgo(ctx.algo());
            String t1 = visitTerm(ctx.term(), l2,l1);
            intermediateCode.append("IF " + t1 + " THEN " +l2 + "\n");
            intermediateCode.append("GOTO " + l1 + "\n");
            intermediateCode.append("REM " + l2 + "\n");
        }

        return null;
    }

    /**
     * visit Term node in AST
     * TERM ::= ATOM || (UNOP TERM) || (TERM BINOP TERM)
     * @param ctx Term context
     * @return string
     */
    public String visitTerm(SPLParser.TermContext ctx,String l1, String l2)
    {
        if (ctx.atom() != null) // TERM ::= ATOM 
        {
            String t1 = visitAtom(ctx.atom(),null);
            return t1;

        } else if (ctx.unop()!=null) { // TERM ::= (UNOP TERM) 
            String unopCode = visitUnop(ctx.unop());

            if (unopCode.equals("not"))
            {
                String term = visitTerm(ctx.term(0),l1,l2);
                return term;
            }

            tempVariable.newTemp();
            String t1 = tempVariable.printTemp();
            visitTerm(ctx.term(0),l1,l2);
            tempVariable.newTemp();
            String t2 = tempVariable.printTemp();
            intermediateCode.append(t2 + " = " + unopCode + t1 + "\n");
            return t2;

        } else { // TERM ::= (TERM BINOP TERM)
            String t1 = visitTerm(ctx.term(0),l1,l2);
            String t2 = visitTerm(ctx.term(1),l1,l2);
            String binopCode = visitBinop(ctx.binop());

            if (t1==null)
            {   
                tempVariable.newTemp();
                t1 = tempVariable.printTemp();
            }
            if (t2==null)
            {   
                tempVariable.newTemp();
                t2 = tempVariable.printTemp();
            }

            switch (binopCode)
            {
                case "=",">":
                    return t1 + binopCode + t2 ;
                case "and":
                    return t1 + ',' + t2 ;
                case "or":
                    return t1 + '#' + t2 ;
            }
            tempVariable.newTemp();
            String t3 = tempVariable.printTemp();
            intermediateCode.append(t3 + " = " + t1 + binopCode + t2 + "\n");
            return t3;
        }
    }

    /**
     * visit Unop node in AST
     * UNOP ::= neg || not
     * @param ctx Unop context
     * @return string
     */
    public String visitUnop(SPLParser.UnopContext ctx)
    {
        if (ctx.getText().equals("neg")) // UNOP ::= neg 
        {
            return "-";
        }else if (ctx.getText().equals("not")) // UNOP ::= not
        {
            return "not";
        }

        return null;
    }

    /**
     * visit Binop node in AST
     * BINOP ::= eq || > || or || and || plus || minus || mult || div
     * @param ctx Binop context
     * @return string
     */
    public String visitBinop(SPLParser.BinopContext ctx)
    {
        switch (ctx.getText())
        {
            case "eq":
                 return "=";
            case "plus":
                 return "+";
            case "minus":
                 return "-";
            case "mult":
                 return "*";
            case "div":
                 return "/";
            default:
                return ctx.getText();
        }
        
    }
}
