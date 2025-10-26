package translator;
/*
 * translates source code to intermediate code
 */


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
    
    // Track current function/procedure scope for inlining
    private String currentFunctionScope = null;

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
        addInlining();

        createHTML();
    }

    /**
     * Creates HTML file to display intermediate code
     */
    public void createHTML()
    {
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title> Intermediate Code </title>
                </head>
                <body>
                    <h2> Generated Intermediate Code </h2>
                    <p>
                """;
        String lines[] = getIntermediateCode().split("\n");
        for (int i = 0; i < lines.length; i++) {
            html += lines[i];
            html += "<br>";
        }
        html += """
                    </p>
                </body>
                </html>
                """;

        Path filePath = Path.of("intermediateCode.html");
        try{
            Files.writeString(filePath, html, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("HTML file created: " + filePath.toAbsolutePath());
        } catch (IOException e) {
            System.out.println(e);
        }
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
    public String inlineFunction(String functionName, SPLParser.FdefContext ctx, String[] parameters)
    {
        String previousScope = currentFunctionScope;
        currentFunctionScope = functionName;
        SPLParser.ParamContext params = ctx.param();
        for (int i=0; i< params.maxthree().var().size(); i++)
        {
            SymbolEntry symbol = symbolTable.lookupVariable(params.maxthree().var(i).getText(),functionName , ScopeType.LOCAL);
            if (symbol==null)
            {
                symbol = symbolTable.lookupVariable(params.maxthree().var(i).getText(), functionName, ScopeType.MAIN);
            }
            if (symbol==null)
            {
                symbol = symbolTable.lookupVariable(params.maxthree().var(i).getText(), functionName, null);
            }
            intermediateCode.append( symbol.getRenamedVariable() + " = " + parameters[i] + "\n");
        }
        visitBody(ctx.body());
        String result = visitAtom(ctx.atom(), functionName);
        currentFunctionScope = previousScope;
        return result;
    }

     /**
     * start searching procedure subtree stored in hashmap
     * @param ctx Pdef context
     */
    public void inlineProc(String procName, SPLParser.PdefContext ctx, String[] parameters)
    {
        String previousScope = currentFunctionScope;
        currentFunctionScope = procName;
        SPLParser.ParamContext params = ctx.param();
        for (int i=0; i< params.maxthree().var().size(); i++)
        {
            SymbolEntry symbol = symbolTable.lookupVariable(params.maxthree().var(i).getText(),procName , ScopeType.LOCAL);
            if (symbol==null)
            {
                symbol = symbolTable.lookupVariable(params.maxthree().var(i).getText(), procName, ScopeType.MAIN);
            }
            if (symbol==null)
            {
                symbol = symbolTable.lookupVariable(params.maxthree().var(i).getText(), procName, null);
            }
            intermediateCode.append( symbol.getRenamedVariable() + " = " + parameters[i] + "\n");
        }
        visitBody(ctx.body());
        currentFunctionScope = previousScope;
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
                currentScope = currentFunctionScope != null ? currentFunctionScope : getIntermediateCode();
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

        } else if (ctx.output() != null) { // INSTR ::= print OUTPUT 
            if (ctx.output().atom() != null && ctx.output().atom().NUMBER() != null) {
                // For numbers, generate the temp assignment first, then print it
                String temp = visitAtom(ctx.output().atom(), null);
                intermediateCode.append("PRINT " + temp + "\n");
            } else {
                intermediateCode.append("PRINT ");
                visitOutput(ctx.output());
            }

        }else if (ctx.assign()!=null) { // INSTR ::= ASSIGN 
           visitAssign(ctx.assign());

        }else if (ctx.branch() !=null) { // INSTR ::= BRANCH 
            visitBranch(ctx.branch());

        }else if (ctx.loop()!=null) { // INSTR ::= LOOP 
            visitLoop(ctx.loop());

        }else { //  INSTR ::= NAME(INPUT)

            SymbolEntry function = symbolTable.lookupProcedure(ctx.NAME().getText(), null, null);
            SPLParser.ParamContext functionParameters = procMap.get(function.getName()).param();

            String code = "CALL_" + function.getName() + "(";

            if (ctx.input().atom().size() != functionParameters.maxthree().var().size())
            {
                System.out.println("Error: Number of arguments does not match number of parameters in procedure: " + function.getName());
                return null;
            }

            for (int i=0; i<ctx.input().atom().size();i++)
            {
                if (i>0)
                {
                    code += ",";
                }

                String t = visitAtom(ctx.input().atom(i),null);
                code += t;

            }

            intermediateCode.append(code + ")\n");
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
            // For variables
            String scopeToUse = currentFunctionScope != null ? currentFunctionScope : getIntermediateCode();
            SymbolEntry symbol = symbolTable.lookupVariable(ctx.atom().var().getText(), scopeToUse, ScopeType.LOCAL);
            if (symbol==null)
            {
                symbol = symbolTable.lookupVariable(ctx.atom().var().getText(), scopeToUse, ScopeType.MAIN);
            }
            if (symbol==null)
            {
                symbol = symbolTable.lookupVariable(ctx.atom().var().getText(), scopeToUse, null);
            }
            intermediateCode.append(symbol.getRenamedVariable() + "\n");
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
            String scopeToUse = currentFunctionScope != null ? currentFunctionScope : getIntermediateCode();
            SymbolEntry symbol = symbolTable.lookupVariable(ctx.var().getText(), scopeToUse, ScopeType.LOCAL);
            if (symbol==null)
            {
                symbol = symbolTable.lookupVariable(ctx.var().getText(), scopeToUse, ScopeType.MAIN);
            }
            if (symbol==null)
            {
                symbol = symbolTable.lookupVariable(ctx.var().getText(), scopeToUse, null);
            }
            String t1 = visitTerm(ctx.term(), null, null);

            String varCode = symbol.getRenamedVariable() + " = " + t1;
            intermediateCode.append(varCode);
            intermediateCode.append("\n");

        }else{ // ASSIGN ::= VAR=NAME(INPUT)

            SymbolEntry function = symbolTable.lookupFunction(ctx.NAME().getText(), null, null);
            SPLParser.ParamContext functionParameters = funcMap.get(function.getName()).param();
            String code = "CALL_" + function.getName() + "(";

            if (ctx.input().atom().size() != functionParameters.maxthree().var().size())
            {
                System.out.println("Error: Number of arguments does not match number of parameters in procedure: " + function.getName());
                return null;
            }

            for (int i=0; i<ctx.input().atom().size();i++)
            {
                if (i>0)
                {
                    code += ",";
                }
                String t = visitAtom(ctx.input().atom(i),null);
                code += t;
            }


            String scopeToUse = currentFunctionScope != null ? currentFunctionScope : getIntermediateCode();
            SymbolEntry symbol = symbolTable.lookupVariable(ctx.var().getText(), scopeToUse, ScopeType.LOCAL);
            if (symbol==null)
            {
                symbol = symbolTable.lookupVariable(ctx.var().getText(), scopeToUse, ScopeType.MAIN);
            }
            if (symbol==null)
            {
                symbol = symbolTable.lookupVariable(ctx.var().getText(), scopeToUse, null);
            }
            String varCode = symbol.getRenamedVariable() + " = " + code + ")\n";
            intermediateCode.append(varCode);
        }

        return null;
    }

    /**
     * Generate conditional jump code for a condition
     * Handles OR (delimited by #), AND (delimited by ,), and simple conditions
     * @param condition The condition string from visitTerm
     * @param trueLabel Label to jump to if condition is true
     * @param falseLabel Label to jump to if condition is false
     */
    private void generateConditionalJump(String condition, String trueLabel, String falseLabel) {
        if (condition.contains("#")) {
            // Handle OR conditions: if ANY is true, jump to trueLabel
            String[] orConditions = condition.split("#");
            for (String cond : orConditions) {
                intermediateCode.append("IF " + cond + " THEN " + trueLabel + "\n");
            }
            intermediateCode.append("GOTO " + falseLabel + "\n");
            
        } else if (condition.contains(",")) {
            // Handle AND conditions: ALL must be true for trueLabel
            String[] andConditions = condition.split(",");
            for (int i = 0; i < andConditions.length - 1; i++) {
                label.newLabel();
                String lNext = label.printLabel();
                intermediateCode.append("IF " + andConditions[i] + " THEN " + lNext + "\n");
                intermediateCode.append("GOTO " + falseLabel + "\n");
                intermediateCode.append("REM " + lNext + "\n");
            }
            // Last condition
            intermediateCode.append("IF " + andConditions[andConditions.length - 1] + " THEN " + trueLabel + "\n");
            intermediateCode.append("GOTO " + falseLabel + "\n");
            
        } else {
            // Simple condition
            intermediateCode.append("IF " + condition + " THEN " + trueLabel + "\n");
            intermediateCode.append("GOTO " + falseLabel + "\n");
        }
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
        String lThen = label.printLabel(); // label for then-branch
        label.newLabel();
        String lElse = label.printLabel(); // label for else-branch
        label.newLabel();
        String lEnd = label.printLabel(); // label for end of if statement
        
        String condition = visitTerm(ctx.term(), lThen, lElse);
        boolean hasElse = ctx.algo(1) != null;
        boolean hasNot = ctx.term().unop() != null && ctx.term().unop().getText().equals("not");
        
        // Generate conditional jump
        generateConditionalJump(condition, lThen, lElse);
        
        // Then-branch
        intermediateCode.append("REM " + lThen + "\n");
        if (hasNot) {
            if (hasElse) visitAlgo(ctx.algo(1));
        } else {
            visitAlgo(ctx.algo(0));
        }
        intermediateCode.append("GOTO " + lEnd + "\n");
        
        // Else-branch
        intermediateCode.append("REM " + lElse + "\n");
        if (hasNot) {
            visitAlgo(ctx.algo(0));
        } else {
            if (hasElse) visitAlgo(ctx.algo(1));
        }
        
        intermediateCode.append("REM " + lEnd + "\n");
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
            String lStart = label.printLabel();
            label.newLabel();
            String lBody = label.printLabel();
            label.newLabel();
            String lEnd = label.printLabel();

            intermediateCode.append("REM " + lStart + "\n");
            String condition = visitTerm(ctx.term(), lBody, lEnd);
            
            // Generate conditional jump using helper method
            generateConditionalJump(condition, lBody, lEnd);
            
            intermediateCode.append("REM " + lBody + "\n");
            visitAlgo(ctx.algo());
            intermediateCode.append("GOTO " + lStart + "\n");
            intermediateCode.append("REM " + lEnd + "\n");
            
        } else { // LOOP ::= do { ALGO } until TERM
            label.newLabel();
            String lStart = label.printLabel();
            label.newLabel();
            String lEnd = label.printLabel();

            intermediateCode.append("REM " + lStart + "\n");
            visitAlgo(ctx.algo());
            String condition = visitTerm(ctx.term(), lEnd, lStart);
            
            // Generate conditional jump using helper method
            generateConditionalJump(condition, lEnd, lStart);
            
            intermediateCode.append("REM " + lEnd + "\n");
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

            String t1 = visitTerm(ctx.term(0),l1,l2);
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

    public String addInlining()
    {
        String copyCode = getIntermediateCode();
        String[] lines = copyCode.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            
            if (line.isEmpty()) {
                continue; // Skip empty lines
            }
            
            // If this line is a call(CALL_)
            if (line.startsWith("CALL_")) {
                String functionName = extractFunctionName(line);
                String allParameters = line.substring(line.indexOf('(')+1,line.indexOf(')'));
                String[] parameters = allParameters.split(",");
                if (parameters.length==0)
                {
                    parameters[0] = allParameters;
                }
                if (functionName!=null)
                {
                    SPLParser.PdefContext proc= procMap.get(functionName);
                    intermediateCode.setLength(0);

                    if (proc!=null)
                    {
                        inlineProc(functionName, proc, parameters);
                    }
                    
                    lines[i] = intermediateCode.toString();
                }
            }else if (line.contains("CALL_")) {
                String variableName = extractVariableName(line);
                String functionName = extractFunctionName(line);
                String allParameters = line.substring(line.indexOf('(')+1,line.indexOf(')'));
                String[] parameters = allParameters.split(",");
                if (parameters.length==0)
                {
                    parameters[0] = allParameters;
                }
                if (functionName!=null)
                {
                    SPLParser.FdefContext function = funcMap.get(functionName);
                    intermediateCode.setLength(0);
                    String returnVar="";
                    if (function!=null)
                    {
                        returnVar = inlineFunction(functionName, function, parameters);
                    }

                    variableName = variableName.trim();
                    intermediateCode.append(variableName + " = " + returnVar + "\n");
                    
                    lines[i] = intermediateCode.toString();
                }
            }
            
        }

        intermediateCode.setLength(0);
        for (int i = 0; i < lines.length; i++) {
            intermediateCode.append(lines[i]);
            if (!lines[i].contains("\n"))
            {
                intermediateCode.append("\n");
            }
        }

        return null;
    }

     /**
     * Extract function/procedure name from CALL statement
     * @param line the line containing CALL_
     * @return the function/procedure name or null if not found
     */
    private String extractFunctionName(String line) {
        // Line format: "CALL_<functionName>()"
        int underscorePos = line.indexOf('_');
        int bracketPos = line.indexOf('(');
        String name = line.substring(underscorePos+1, bracketPos);
        return name;
    }
     /**
     * Extract variable name from CALL statement
     * @param line the line containing CALL_
     * @return the variable name or null if not found
     */
    private String extractVariableName(String line) {
        // Line format: "<variableName> = CALL_<functionName>()"
        int eqPos = line.indexOf('=');
        String name = line.substring(0,eqPos);
        return name;
    }
}
