package symbolTable;

import java.util.*;

/**
 * Represents a symbol table for a specific scope in SPL
 */
public class SymbolTable {
    private final Map<String, Symbol> symbols;
    private final SymbolTable parent;
    private final String scopeName;
    private final ScopeType scopeType;
    
    /**
     * Constructor for SymbolTable
     * @param scopeName Name of this scope (e.g., "Global", "function_add", "main")
     * @param scopeType Type of this scope
     * @param parent Parent symbol table (null for global scope)
     */
    public SymbolTable(String scopeName, ScopeType scopeType, SymbolTable parent) {
        this.symbols = new HashMap<>();
        this.scopeName = scopeName;
        this.scopeType = scopeType;
        this.parent = parent;
    }
    
    /**
     * Add a symbol to this symbol table
     * @param symbol The symbol to add
     * @return true if added successfully, false if symbol already exists in this scope
     */
    public boolean addSymbol(Symbol symbol) {
        if (symbols.containsKey(symbol.getName())) {
            return false; // Symbol already exists in this scope
        }
        symbols.put(symbol.getName(), symbol);
        return true;
    }
    
    /**
     * Check if a symbol exists in this scope only (not parent scopes)
     * @param name Name of the symbol to check
     * @return true if symbol exists in this scope
     */
    public boolean containsSymbol(String name) {
        return symbols.containsKey(name);
    }
    
    /**
     * Look up a symbol in this scope only (not parent scopes)
     * @param name Name of the symbol to look up
     * @return Symbol if found, null otherwise
     */
    public Symbol getSymbol(String name) {
        return symbols.get(name);
    }
    
    /**
     * Look up a symbol in this scope and all parent scopes
     * @param name Name of the symbol to look up
     * @return Symbol if found anywhere in scope chain, null otherwise
     */
    public Symbol lookupSymbol(String name) {
        Symbol symbol = symbols.get(name);
        if (symbol != null) {
            return symbol;
        }
        
        // If not found in current scope, check parent scope
        if (parent != null) {
            return parent.lookupSymbol(name);
        }
        
        return null; // Not found anywhere
    }
    
    /**
     * Get all symbols in this scope
     * @return Collection of all symbols in this scope
     */
    public Collection<Symbol> getAllSymbols() {
        return symbols.values();
    }
    
    /**
     * Get the parent symbol table
     * @return Parent symbol table or null if this is the global scope
     */
    public SymbolTable getParent() {
        return parent;
    }
    
    /**
     * Get the scope name
     * @return Name of this scope
     */
    public String getScopeName() {
        return scopeName;
    }
    
    /**
     * Get the scope type
     * @return Type of this scope
     */
    public ScopeType getScopeType() {
        return scopeType;
    }
    
    /**
     * Get the number of symbols in this scope
     * @return Number of symbols
     */
    public int size() {
        return symbols.size();
    }
    
    /**
     * Check if this scope is empty
     * @return true if no symbols in this scope
     */
    public boolean isEmpty() {
        return symbols.isEmpty();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("SymbolTable[%s]:\n", scopeName));
        if (symbols.isEmpty()) {
            sb.append("  (empty)\n");
        } else {
            for (Symbol symbol : symbols.values()) {
                sb.append("  ").append(symbol).append("\n");
            }
        }
        return sb.toString();
    }
}
