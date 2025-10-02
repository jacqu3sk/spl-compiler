package symbolTable;

/**
 * Represents a symbol in the SPL symbol table
 */
public class Symbol {
    private final String name;
    private final SymbolType type;
    private final ScopeType scope;
    private final int declarationLine;
    private final int declarationColumn;
    
    /**
     * Constructor for Symbol
     * @param name The name/identifier of the symbol
     * @param type The type of symbol (VARIABLE, FUNCTION, etc.)
     * @param scope The scope where this symbol is declared
     * @param declarationLine Line number where symbol is declared
     * @param declarationColumn Column number where symbol is declared
     */
    public Symbol(String name, SymbolType type, ScopeType scope, int declarationLine, int declarationColumn) {
        this.name = name;
        this.type = type;
        this.scope = scope;
        this.declarationLine = declarationLine;
        this.declarationColumn = declarationColumn;
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public SymbolType getType() {
        return type;
    }
    
    public ScopeType getScope() {
        return scope;
    }
    
    public int getDeclarationLine() {
        return declarationLine;
    }
    
    public int getDeclarationColumn() {
        return declarationColumn;
    }
    
    @Override
    public String toString() {
        return String.format("Symbol{name='%s', type=%s, scope=%s, line=%d, col=%d}", 
                           name, type, scope, declarationLine, declarationColumn);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Symbol symbol = (Symbol) obj;
        return name.equals(symbol.name) && type == symbol.type && scope == symbol.scope;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
