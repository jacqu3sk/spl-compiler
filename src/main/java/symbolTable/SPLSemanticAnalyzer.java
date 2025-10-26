package symbolTable;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;

import generated.SPLBaseListener;
import generated.SPLParser;

public class SPLSemanticAnalyzer extends SPLBaseListener {
    private SymbolTable symbolTable;
    private Stack<ScopeInfo> scopeStack;
    private String currentProcFunc;  // Current procedure/function being processed
    private Set<String> currentParams;  // Parameters of current proc/func
    private Set<String> currentLocals;  // Local vars of current proc/func
    
    public SPLSemanticAnalyzer() {
        this.symbolTable = new SymbolTable();
        this.scopeStack = new Stack<>();
        this.currentParams = new HashSet<>();
        this.currentLocals = new HashSet<>();
    }
    
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
    
    // ==================== SPL_PROG ====================
    
    @Override
    public void enterSpl_prog(SPLParser.Spl_progContext ctx) {
        // Establish the EVERYWHERE scope
        scopeStack.push(new ScopeInfo(ScopeType.EVERYWHERE, null));
    }
    
    @Override
    public void exitSpl_prog(SPLParser.Spl_progContext ctx) {
        // Check for conflicts at EVERYWHERE scope level
        symbolTable.checkEverywhereConflicts();
        scopeStack.pop();
    }
    
    // ==================== GLOBAL VARIABLES ====================
    
    @Override
    public void enterVariables(SPLParser.VariablesContext ctx) {
        // Check if this is global variables (parent is spl_prog)
        if (ctx.parent instanceof SPLParser.Spl_progContext) {
            scopeStack.push(new ScopeInfo(ScopeType.GLOBAL, null));
        }
        // If parent is mainprog, scope is handled in enterMainprog
    }
    
    @Override
    public void exitVariables(SPLParser.VariablesContext ctx) {
        // Only pop if we pushed (i.e., if parent is spl_prog)
        if (ctx.parent instanceof SPLParser.Spl_progContext) {
            scopeStack.pop();
        }
    }
    
    // ==================== VARIABLE DECLARATIONS ====================
    
    @Override
    public void enterVar(SPLParser.VarContext ctx) {
        String varName = ctx.NAME().getText();
        int nodeId = symbolTable.getNextNodeId();
        
        if (scopeStack.isEmpty()) {
            return; // Should not happen, but safety check
        }
        
        ScopeInfo currentScope = scopeStack.peek();
        ParserRuleContext parent = (ParserRuleContext) ctx.parent;
        
        // Determine if this is a declaration or usage
        // Declaration contexts: variables, maxthree
        if (parent instanceof SPLParser.VariablesContext || 
            parent instanceof SPLParser.MaxthreeContext) {
            
            boolean isParam = isParameterContext(parent);
            
            SymbolEntry entry = new SymbolEntry(
                varName,
                SymbolType.VARIABLE,
                currentScope.getType(),
                nodeId,
                currentScope.getOwner(),
                isParam
            );
            
            symbolTable.addSymbol(entry);
            
            // Track parameters and locals for shadowing check
            if (currentScope.getType() == ScopeType.LOCAL) {
                if (isParam) {
                    currentParams.add(varName);
                } else {
                    // This is a local variable in MAXTHREE under BODY
                    ParserRuleContext grandparent = (ParserRuleContext) parent.parent;
                    if (grandparent instanceof SPLParser.BodyContext) {
                        // Check for shadowing
                        if (currentParams.contains(varName)) {
                            symbolTable.getErrors().add(
                                "Error: Local variable '" + varName + 
                                "' shadows parameter in " + currentScope.getOwner() +
                                " at line " + ctx.getStart().getLine()
                            );
                        }
                        currentLocals.add(varName);
                    }
                }
            }
        }
        // If it's not a declaration context, it's usage - handled in atom/assign
    }
    
    /**
     * Check if maxthree is a parameter context (under param) or local context (under body)
     */
    private boolean isParameterContext(ParserRuleContext ctx) {
        if (ctx instanceof SPLParser.MaxthreeContext) {
            return ctx.parent instanceof SPLParser.ParamContext;
        }
        return false;
    }
    
    // ==================== PROCEDURES ====================
    
    @Override
    public void enterProcdefs(SPLParser.ProcdefsContext ctx) {
        scopeStack.push(new ScopeInfo(ScopeType.PROCEDURE, null));
    }
    
    @Override
    public void exitProcdefs(SPLParser.ProcdefsContext ctx) {
        scopeStack.pop();
    }
    
    @Override
    public void enterPdef(SPLParser.PdefContext ctx) {
        String procName = ctx.NAME().getText();
        int nodeId = symbolTable.getNextNodeId();
        
        // Set current procedure context
        currentProcFunc = procName;
        currentParams.clear();
        currentLocals.clear();
        
        // Add procedure to symbol table
        SymbolEntry entry = new SymbolEntry(
            procName,
            SymbolType.PROCEDURE,
            ScopeType.PROCEDURE,
            nodeId,
            null,
            false
        );

        entry.setNumParams(ctx.param().maxthree().var().size());
        
        symbolTable.addSymbol(entry);
        
        // Push local scope for this procedure
        scopeStack.push(new ScopeInfo(ScopeType.LOCAL, procName));
    }
    
    @Override
    public void exitPdef(SPLParser.PdefContext ctx) {
        scopeStack.pop();
        currentProcFunc = null;
        currentParams.clear();
        currentLocals.clear();
    }
    
    // ==================== FUNCTIONS ====================
    
    @Override
    public void enterFuncdefs(SPLParser.FuncdefsContext ctx) {
        scopeStack.push(new ScopeInfo(ScopeType.FUNCTION, null));
    }
    
    @Override
    public void exitFuncdefs(SPLParser.FuncdefsContext ctx) {
        scopeStack.pop();
    }
    
    @Override
    public void enterFdef(SPLParser.FdefContext ctx) {
        String funcName = ctx.NAME().getText();
        int nodeId = symbolTable.getNextNodeId();
        
        // Set current function context
        currentProcFunc = funcName;
        currentParams.clear();
        currentLocals.clear();
        
        // Add function to symbol table
        SymbolEntry entry = new SymbolEntry(
            funcName,
            SymbolType.FUNCTION,
            ScopeType.FUNCTION,
            nodeId,
            null,
            false
        );

        entry.setNumParams(ctx.param().maxthree().var().size());
        
        symbolTable.addSymbol(entry);
        
        // Push local scope for this function
        scopeStack.push(new ScopeInfo(ScopeType.LOCAL, funcName));
    }
    
    @Override
    public void exitFdef(SPLParser.FdefContext ctx) {
        scopeStack.pop();
        currentProcFunc = null;
        currentParams.clear();
        currentLocals.clear();
    }
    
    // ==================== MAIN PROGRAM ====================
    
    @Override
    public void enterMainprog(SPLParser.MainprogContext ctx) {
        scopeStack.push(new ScopeInfo(ScopeType.MAIN, null));
    }
    
    @Override
    public void exitMainprog(SPLParser.MainprogContext ctx) {
        scopeStack.pop();
    }
    
    // ==================== VARIABLE USAGE VERIFICATION ====================
    
    @Override
    public void enterAtom(SPLParser.AtomContext ctx) {
        // Only check if it's a variable (has var child, not NUMBER)
        if (ctx.var() != null) {
            String varName = ctx.var().NAME().getText();
            
            if (scopeStack.isEmpty()) {
                return; // Safety check
            }
            
            ScopeInfo currentScope = scopeStack.peek();
            
            // Lookup variable in appropriate scope chain
            SymbolEntry entry = symbolTable.lookupVariable(
                varName,
                currentScope.getOwner(),
                currentScope.getType()
            );
            
            if (entry == null) {
                symbolTable.getErrors().add(
                    "Error: Undeclared variable '" + varName + "' at line " + 
                    ctx.getStart().getLine()
                );
            }
        }
    }
    
    @Override
    public void enterAssign(SPLParser.AssignContext ctx) {
        String varName = ctx.var().NAME().getText();
        
        if (scopeStack.isEmpty()) {
            return; // Safety check
        }
        
        ScopeInfo currentScope = scopeStack.peek();
        
        // Verify the variable being assigned to is declared
        SymbolEntry entry = symbolTable.lookupVariable(
            varName,
            currentScope.getOwner(),
            currentScope.getType()
        );
        
        if (entry == null) {
            symbolTable.getErrors().add(
                "Error: Undeclared variable '" + varName + "' at line " + 
                ctx.getStart().getLine()
            );
        }
        
        // If it's a function call assignment, verify function exists
        if (ctx.NAME() != null) {
            String funcName = ctx.NAME().getText();
            if (!symbolTable.functionExists(funcName)) {
                symbolTable.getErrors().add(
                    "Error: Undeclared function '" + funcName + "' at line " + 
                    ctx.getStart().getLine()
                );
            }
        }
    }
    
    @Override
    public void enterInstr(SPLParser.InstrContext ctx) {
        // Check procedure calls (NAME '(' input ')')
        if (ctx.NAME() != null) {
            String procName = ctx.NAME().getText();
            if (!symbolTable.procedureExists(procName)) {
                symbolTable.getErrors().add(
                    "Error: Undeclared procedure '" + procName + "' at line " + 
                    ctx.getStart().getLine()
                );
            }
        }
    }
}