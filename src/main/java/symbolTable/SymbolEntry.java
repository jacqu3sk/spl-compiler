package symbolTable;

public class SymbolEntry {
    private String name;
    private SymbolType type;  // VARIABLE, PROCEDURE, FUNCTION
    private ScopeType scope;  // EVERYWHERE, GLOBAL, PROCEDURE, FUNCTION, MAIN, LOCAL
    private int nodeId;       // Unique identifier for the parse tree node
    private String scopeOwner; // Name of procedure/function if in local scope
    private boolean isParameter; // True if this is a parameter
    private String tempVariable; //Temporary variable name for intermediate code generation
    
    // Constructor
    public SymbolEntry(String name, SymbolType type, ScopeType scope, 
                       int nodeId, String scopeOwner, boolean isParameter) {
        this.name = name;
        this.type = type;
        this.scope = scope;
        this.nodeId = nodeId;
        this.scopeOwner = scopeOwner;
        this.isParameter = isParameter;
        this.tempVariable = "";
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String newName) {
        name = newName;
    }
    
        
    public String getScopeOwner() {
        return scopeOwner;
    }
    
    public void setScopeOwner(String newOwner) {
        scopeOwner = newOwner;
    }
    
        
    public SymbolType getSymbolType() {
        return type;
    }
    
    public void setSymbolType(SymbolType newType) {
        type = newType;
    }
    
    public ScopeType getScope() {
        return scope;
    }
    
    public void setScope(ScopeType newType) {
        scope = newType;
    }
    
    public int getNodeId() {
        return nodeId;
    }

    public String getRenamedVariable() {
        return "v" + nodeId;
    }

    public String getTempVariable() {
        return tempVariable;
    }
    
    public void setNodeId(int newId) {
        nodeId = newId;
    }
    
    public boolean isParameter() {
        return isParameter;
    }
    
    public void setParameter(boolean newValue) {
        isParameter = newValue;
    }

    public void setTempVariable(String temp) {
        tempVariable = temp;
    }
}