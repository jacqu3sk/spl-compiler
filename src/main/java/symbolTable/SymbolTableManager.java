package symbolTable;

import java.util.*;

/**
 * Manages symbol tables and scopes for SPL
 */
public class SymbolTableManager {
    private final Stack<SymbolTable> scopeStack;
    private final SymbolTable globalScope;
    private final List<String> errors;
    private final List<SymbolTable> allScopes; // Keep track of all created scopes
    private int scopeCounter;
    
    /**
     * Constructor for SymbolTableManager
     */
    public SymbolTableManager() {
        this.scopeStack = new Stack<>();
        this.globalScope = new SymbolTable("Global", ScopeType.GLOBAL, null);
        this.errors = new ArrayList<>();
        this.allScopes = new ArrayList<>();
        this.scopeCounter = 0;
        
        // Start with global scope
        scopeStack.push(globalScope);
        allScopes.add(globalScope); // Track global scope
    }
    
    /**
     * Enter a new scope
     * @param scopeName Name of the new scope
     * @param scopeType Type of the new scope
     * @return The newly created symbol table
     */
    public SymbolTable enterScope(String scopeName, ScopeType scopeType) {
        SymbolTable currentScope = getCurrentScope();
        SymbolTable newScope = new SymbolTable(scopeName, scopeType, currentScope);
        scopeStack.push(newScope);
        allScopes.add(newScope); // Track all created scopes
        return newScope;
    }
    
    /**
     * Exit the current scope
     * @return The symbol table that was exited, or null if trying to exit global scope
     */
    public SymbolTable exitScope() {
        if (scopeStack.size() <= 1) {
            // Don't exit global scope
            addError("Cannot exit global scope");
            return null;
        }
        return scopeStack.pop();
    }
    
    /**
     * Get the current (top) scope
     * @return Current symbol table
     */
    public SymbolTable getCurrentScope() {
        return scopeStack.peek();
    }
    
    /**
     * Get the global scope
     * @return Global symbol table
     */
    public SymbolTable getGlobalScope() {
        return globalScope;
    }
    
    /**
     * Add a symbol to the current scope
     * @param name Symbol name
     * @param type Symbol type
     * @param line Declaration line
     * @param column Declaration column
     * @return true if added successfully, false if duplicate in current scope
     */
    public boolean addSymbol(String name, SymbolType type, int line, int column) {
        SymbolTable currentScope = getCurrentScope();
        ScopeType scopeType = currentScope.getScopeType();
        
        Symbol symbol = new Symbol(name, type, scopeType, line, column);
        
        if (!currentScope.addSymbol(symbol)) {
            addError(String.format("Line %d:%d: Duplicate declaration of '%s' in %s scope", 
                                 line, column, name, scopeType.toString().toLowerCase()));
            return false;
        }
        
        return true;
    }
    
    /**
     * Look up a symbol in the current scope chain
     * @param name Symbol name to look up
     * @return Symbol if found, null otherwise
     */
    public Symbol lookupSymbol(String name) {
        return getCurrentScope().lookupSymbol(name);
    }
    
    /**
     * Check if a symbol is declared (for error reporting)
     * @param name Symbol name
     * @param line Line where symbol is used
     * @param column Column where symbol is used
     * @return Symbol if found, null if not found (and error is added)
     */
    public Symbol checkSymbolDeclared(String name, int line, int column) {
        Symbol symbol = lookupSymbol(name);
        if (symbol == null) {
            addError(String.format("Line %d:%d: Undeclared variable '%s'", line, column, name));
        }
        return symbol;
    }
    
    /**
     * Generate a unique scope name for functions/procedures
     * @param baseName Base name (function/procedure name)
     * @param scopeType Type of scope
     * @return Unique scope name
     */
    public String generateScopeName(String baseName, ScopeType scopeType) {
        scopeCounter++;
        return String.format("%s_%s_%d", scopeType.toString().toLowerCase(), baseName, scopeCounter);
    }
    
    /**
     * Add an error message
     * @param error Error message to add
     */
    public void addError(String error) {
        errors.add(error);
    }
    
    /**
     * Check if there are any errors
     * @return true if errors exist
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Get all error messages
     * @return List of error messages
     */
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * Print all errors to System.err
     */
    public void printErrors() {
        if (hasErrors()) {
            System.err.println("\n=== SEMANTIC ERRORS ===");
            for (String error : errors) {
                System.err.println(error);
            }
            System.err.println();
        }
    }
    
    /**
     * Print the complete symbol table structure
     */
    public void printSymbolTable() {
        System.out.println("\n=== SYMBOL TABLE ===");
        
        // Print all scopes that were created during parsing
        for (SymbolTable table : allScopes) {
            printSingleSymbolTable(table);
            System.out.println(); // Add spacing between scopes
        }
    }
    
    /**
     * Print a single symbol table
     * @param table Symbol table to print
     */
    private void printSingleSymbolTable(SymbolTable table) {
        System.out.printf("%s:\n", table.getScopeName());
        
        if (table.isEmpty()) {
            System.out.println("  (empty)");
        } else {
            for (Symbol symbol : table.getAllSymbols()) {
                System.out.printf("  %s: %s (line %d)\n", 
                                symbol.getName(), symbol.getType(), 
                                symbol.getDeclarationLine());
            }
        }
    }
    
    /**
     * Get current scope depth
     * @return Number of nested scopes (1 = global only)
     */
    public int getScopeDepth() {
        return scopeStack.size();
    }
    
    /**
     * Clear all errors (useful for testing)
     */
    public void clearErrors() {
        errors.clear();
    }
}
