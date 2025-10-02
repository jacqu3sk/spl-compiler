package symbolTable;

import generated.SPLBaseListener;
import generated.SPLParser;

/**
 * SPL Symbol Table Builder using ANTLR Listener pattern
 * Builds symbol tables and performs semantic analysis for SPL programs
 */
public class SPLSymbolTableBuilder extends SPLBaseListener {
    private final SymbolTableManager manager;
    private boolean inGlobalVars = false;
    private boolean inMainVars = false;
    private boolean inLocalVars = false;
    private boolean inParams = false;
    private String currentFunctionName = null;
    
    /**
     * Constructor
     */
    public SPLSymbolTableBuilder() {
        this.manager = new SymbolTableManager();
    }
    
    // ===== SPL Program Structure =====
    
    @Override
    public void enterSpl_prog(SPLParser.Spl_progContext ctx) {
        // Global scope is already initialized in SymbolTableManager
        System.out.println("Starting SPL program analysis...");
    }
    
    @Override
    public void exitSpl_prog(SPLParser.Spl_progContext ctx) {
        System.out.println("SPL program analysis completed.");
    }
    
    // ===== Global Variables =====
    
    @Override
    public void enterVariables(SPLParser.VariablesContext ctx) {
        // Determine context: are we in glob, main, or local section?
        if (isInGlobalSection(ctx)) {
            inGlobalVars = true;
        } else if (isInMainSection(ctx)) {
            inMainVars = true;
        }
    }
    
    @Override
    public void exitVariables(SPLParser.VariablesContext ctx) {
        inGlobalVars = false;
        inMainVars = false;
    }
    
    @Override
    public void enterVar(SPLParser.VarContext ctx) {
        String varName = ctx.NAME().getText();
        int line = ctx.NAME().getSymbol().getLine();
        int column = ctx.NAME().getSymbol().getCharPositionInLine();
        
        if (inGlobalVars) {
            // Global variable declaration
            manager.addSymbol(varName, SymbolType.VARIABLE, line, column);
        } else if (inMainVars) {
            // Main program variable declaration
            manager.addSymbol(varName, SymbolType.VARIABLE, line, column);
        } else if (inLocalVars) {
            // Local variable in function/procedure
            manager.addSymbol(varName, SymbolType.VARIABLE, line, column);
        } else if (inParams) {
            // Parameter declaration
            manager.addSymbol(varName, SymbolType.PARAMETER, line, column);
        } else {
            // Variable usage - check if it's declared
            manager.checkSymbolDeclared(varName, line, column);
        }
    }
    
    // ===== Function Definitions =====
    
    @Override
    public void enterFdef(SPLParser.FdefContext ctx) {
        String funcName = ctx.NAME().getText();
        int line = ctx.NAME().getSymbol().getLine();
        int column = ctx.NAME().getSymbol().getCharPositionInLine();
        
        // Add function to current scope (should be global)
        manager.addSymbol(funcName, SymbolType.FUNCTION, line, column);
        
        // Enter function scope
        String scopeName = manager.generateScopeName(funcName, ScopeType.FUNCTION);
        manager.enterScope(scopeName, ScopeType.FUNCTION);
        currentFunctionName = funcName;
    }
    
    @Override
    public void exitFdef(SPLParser.FdefContext ctx) {
        // Exit function scope
        manager.exitScope();
        currentFunctionName = null;
    }
    
    // ===== Procedure Definitions =====
    
    @Override
    public void enterPdef(SPLParser.PdefContext ctx) {
        String procName = ctx.NAME().getText();
        int line = ctx.NAME().getSymbol().getLine();
        int column = ctx.NAME().getSymbol().getCharPositionInLine();
        
        // Add procedure to current scope (should be global)
        manager.addSymbol(procName, SymbolType.PROCEDURE, line, column);
        
        // Enter procedure scope
        String scopeName = manager.generateScopeName(procName, ScopeType.PROCEDURE);
        manager.enterScope(scopeName, ScopeType.PROCEDURE);
        currentFunctionName = procName;
    }
    
    @Override
    public void exitPdef(SPLParser.PdefContext ctx) {
        // Exit procedure scope
        manager.exitScope();
        currentFunctionName = null;
    }
    
    // ===== Parameters =====
    
    @Override
    public void enterParam(SPLParser.ParamContext ctx) {
        inParams = true;
    }
    
    @Override
    public void exitParam(SPLParser.ParamContext ctx) {
        inParams = false;
    }
    
    // ===== Local Variables (in body) =====
    
    @Override
    public void enterBody(SPLParser.BodyContext ctx) {
        // Body contains local variables and algorithm
    }
    
    @Override
    public void enterMaxthree(SPLParser.MaxthreeContext ctx) {
        // Check if this maxthree is in a local context (inside body)
        if (isInLocalContext(ctx)) {
            inLocalVars = true;
        } else if (isInParamContext(ctx)) {
            inParams = true;
        }
    }
    
    @Override
    public void exitMaxthree(SPLParser.MaxthreeContext ctx) {
        inLocalVars = false;
        inParams = false;
    }
    
    // ===== Main Program =====
    
    @Override
    public void enterMainprog(SPLParser.MainprogContext ctx) {
        // Enter main scope
        manager.enterScope("Main", ScopeType.MAIN);
    }
    
    @Override
    public void exitMainprog(SPLParser.MainprogContext ctx) {
        // Exit main scope
        manager.exitScope();
    }
    
    // ===== Function/Procedure Calls =====
    
    @Override
    public void enterInstr(SPLParser.InstrContext ctx) {
        // Check for function/procedure calls: NAME '(' input ')'
        if (ctx.NAME() != null) {
            String callName = ctx.NAME().getText();
            int line = ctx.NAME().getSymbol().getLine();
            int column = ctx.NAME().getSymbol().getCharPositionInLine();
            
            // Check if function/procedure is declared
            Symbol symbol = manager.checkSymbolDeclared(callName, line, column);
            if (symbol != null && symbol.getType() != SymbolType.FUNCTION && symbol.getType() != SymbolType.PROCEDURE) {
                manager.addError(String.format("Line %d:%d: '%s' is not a function or procedure", 
                                              line, column, callName));
            }
        }
    }
    
    @Override
    public void enterAssign(SPLParser.AssignContext ctx) {
        // Check assignment target and function calls in assignment
        if (ctx.NAME() != null) {
            // This is an assignment with function call: var = NAME '(' input ')'
            String funcName = ctx.NAME().getText();
            int line = ctx.NAME().getSymbol().getLine();
            int column = ctx.NAME().getSymbol().getCharPositionInLine();
            
            // Check if function is declared
            Symbol symbol = manager.checkSymbolDeclared(funcName, line, column);
            if (symbol != null && symbol.getType() != SymbolType.FUNCTION) {
                manager.addError(String.format("Line %d:%d: '%s' is not a function", 
                                              line, column, funcName));
            }
        }
    }
    
    // ===== Helper Methods =====
    
    /**
     * Check if we're in the global variables section
     */
    private boolean isInGlobalSection(SPLParser.VariablesContext ctx) {
        // Check if parent context indicates we're in global section
        return ctx.getParent() instanceof SPLParser.Spl_progContext;
    }
    
    /**
     * Check if we're in the main variables section
     */
    private boolean isInMainSection(SPLParser.VariablesContext ctx) {
        // Check if parent context is mainprog
        return ctx.getParent() instanceof SPLParser.MainprogContext;
    }
    
    /**
     * Check if maxthree context is for local variables
     */
    private boolean isInLocalContext(SPLParser.MaxthreeContext ctx) {
        // Check if we're inside a body context (local variables)
        return ctx.getParent() instanceof SPLParser.BodyContext;
    }
    
    /**
     * Check if maxthree context is for parameters
     */
    private boolean isInParamContext(SPLParser.MaxthreeContext ctx) {
        // Check if we're inside a param context
        return ctx.getParent() instanceof SPLParser.ParamContext;
    }
    
    // ===== Public Interface =====
    
    /**
     * Check if there are any semantic errors
     * @return true if errors exist
     */
    public boolean hasErrors() {
        return manager.hasErrors();
    }
    
    /**
     * Print all semantic errors
     */
    public void printErrors() {
        manager.printErrors();
    }
    
    /**
     * Print the symbol table
     */
    public void printSymbolTable() {
        manager.printSymbolTable();
    }
    
    /**
     * Get the symbol table manager (for testing)
     * @return SymbolTableManager instance
     */
    public SymbolTableManager getManager() {
        return manager;
    }
    
    /**
     * Get all errors as a list
     * @return List of error messages
     */
    public java.util.List<String> getErrors() {
        return manager.getErrors();
    }
}
