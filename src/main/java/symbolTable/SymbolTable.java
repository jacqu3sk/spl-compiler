package symbolTable;

import java.util.*;

/**
 * Symbol table for the SPL compiler
 * Manages all symbols and their scope information
 */
public class SymbolTable {
    // Main symbol storage: maps node ID to symbol entry
    private Map<Integer, SymbolEntry> table;
    
    // Quick lookup maps for different scopes
    private Map<String, SymbolEntry> globalVariables;
    private Map<String, SymbolEntry> procedures;
    private Map<String, SymbolEntry> functions;
    private Map<String, SymbolEntry> mainVariables;
    
    // Local scope storage: maps scopeOwner to local symbols
    // Key: procedure/function name, Value: map of variable names to entries
    private Map<String, Map<String, SymbolEntry>> localScopes;
    
    // Node ID counter
    private int nextNodeId;
    
    // Error messages
    private List<String> errors;
    
    public SymbolTable() {
        this.table = new HashMap<>();
        this.globalVariables = new HashMap<>();
        this.procedures = new HashMap<>();
        this.functions = new HashMap<>();
        this.mainVariables = new HashMap<>();
        this.localScopes = new HashMap<>();
        this.nextNodeId = 1;
        this.errors = new ArrayList<>();
    }
    
    /**
     * Get next available node ID
     */
    public int getNextNodeId() {
        return nextNodeId++;
    }
    
    /**
     * Add a symbol to the table
     * @return true if successful, false if duplicate
     */
    public boolean addSymbol(SymbolEntry entry) {
        table.put(entry.getNodeId(), entry);
        
        switch (entry.getScope()) {
            case GLOBAL:
                if (globalVariables.containsKey(entry.getName())) {
                    errors.add("Error: Variable '" + entry.getName() + 
                              "' already declared in global scope");
                    return false;
                }
                globalVariables.put(entry.getName(), entry);
                break;
                
            case PROCEDURE:
                if (procedures.containsKey(entry.getName())) {
                    errors.add("Error: Procedure '" + entry.getName() + 
                              "' already declared");
                    return false;
                }
                procedures.put(entry.getName(), entry);
                break;
                
            case FUNCTION:
                if (functions.containsKey(entry.getName())) {
                    errors.add("Error: Function '" + entry.getName() + 
                              "' already declared");
                    return false;
                }
                functions.put(entry.getName(), entry);
                break;
                
            case MAIN:
                if (mainVariables.containsKey(entry.getName())) {
                    errors.add("Error: Variable '" + entry.getName() + 
                              "' already declared in main scope");
                    return false;
                }
                mainVariables.put(entry.getName(), entry);
                break;
                
            case LOCAL:
                String owner = entry.getScopeOwner();
                localScopes.putIfAbsent(owner, new HashMap<>());
                Map<String, SymbolEntry> localScope = localScopes.get(owner);
                
                if (localScope.containsKey(entry.getName())) {
                    errors.add("Error: Variable '" + entry.getName() + 
                              "' already declared in local scope of " + owner);
                    return false;
                }
                localScope.put(entry.getName(), entry);
                break;
        }
        
        return true;
    }
    
    /**
     * Check for name conflicts at EVERYWHERE scope level
     */
    public void checkEverywhereConflicts() {
        // Check: no variable name = function name
        for (String varName : globalVariables.keySet()) {
            if (functions.containsKey(varName)) {
                errors.add("Error: Global variable '" + varName + 
                          "' conflicts with function name");
            }
            if (procedures.containsKey(varName)) {
                errors.add("Error: Global variable '" + varName + 
                          "' conflicts with procedure name");
            }
        }
        
        // Check: no function name = procedure name
        for (String funcName : functions.keySet()) {
            if (procedures.containsKey(funcName)) {
                errors.add("Error: Function '" + funcName + 
                          "' conflicts with procedure name");
            }
        }
    }
    
    /**
     * Lookup a variable in the appropriate scope chain
     * @param name Variable name to look up
     * @param currentScope Current procedure/function name (null if in main/global)
     * @param scopeType Current scope type
     * @return SymbolEntry if found, null otherwise
     */
    public SymbolEntry lookupVariable(String name, String currentScope, 
                                     ScopeType scopeType) {
        // First, check local scope if applicable (for PROCEDURE/FUNCTION local scopes)
        if (scopeType == ScopeType.LOCAL && currentScope != null) {
            Map<String, SymbolEntry> localScope = localScopes.get(currentScope);
            if (localScope != null && localScope.containsKey(name)) {
                return localScope.get(name);
            }
        }
        
        // Then check main scope if applicable
        if (scopeType == ScopeType.MAIN) {
            if (mainVariables.containsKey(name)) {
                return mainVariables.get(name);
            }
        }
        
        // Finally, check global scope (accessible from everywhere)
        if (globalVariables.containsKey(name)) {
            return globalVariables.get(name);
        }
        
        return null; // Not found
    }
    
    /**
     * Check if a procedure exists
     */
    public boolean procedureExists(String name) {
        return procedures.containsKey(name);
    }
    
    /**
     * Check if a function exists
     */
    public boolean functionExists(String name) {
        return functions.containsKey(name);
    }
    
    /**
     * Get all errors
     */
    public List<String> getErrors() {
        return errors;
    }
    
    /**
     * Check if there are any errors
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Get symbol entry by node ID
     */
    public SymbolEntry getSymbolByNodeId(int nodeId) {
        return table.get(nodeId);
    }
    
    /**
     * Print the symbol table in a readable format
     */
    public void printTable() {
        System.out.println("\n========== SYMBOL TABLE ==========");
        
        System.out.println("\n--- Global Variables ---");
        if (globalVariables.isEmpty()) {
            System.out.println("  (none)");
        } else {
            globalVariables.values().forEach(e -> 
                System.out.println("  " + e.getName() + " (NodeID: " + e.getNodeId() + ")"));
        }
        
        System.out.println("\n--- Procedures ---");
        if (procedures.isEmpty()) {
            System.out.println("  (none)");
        } else {
            procedures.values().forEach(e -> 
                System.out.println("  " + e.getName() + " (NodeID: " + e.getNodeId() + ")"));
        }
        
        System.out.println("\n--- Functions ---");
        if (functions.isEmpty()) {
            System.out.println("  (none)");
        } else {
            functions.values().forEach(e -> 
                System.out.println("  " + e.getName() + " (NodeID: " + e.getNodeId() + ")"));
        }
        
        System.out.println("\n--- Main Variables ---");
        if (mainVariables.isEmpty()) {
            System.out.println("  (none)");
        } else {
            mainVariables.values().forEach(e -> 
                System.out.println("  " + e.getName() + " (NodeID: " + e.getNodeId() + ")"));
        }
        
        System.out.println("\n--- Local Scopes ---");
        if (localScopes.isEmpty()) {
            System.out.println("  (none)");
        } else {
            localScopes.forEach((owner, locals) -> {
                System.out.println("  In " + owner + ":");
                if (locals.isEmpty()) {
                    System.out.println("    (none)");
                } else {
                    locals.values().forEach(e -> {
                        String marker = e.isParameter() ? " [parameter]" : " [local]";
                        System.out.println("    " + e.getName() + marker + 
                            " (NodeID: " + e.getNodeId() + ")");
                    });
                }
            });
        }
        
        System.out.println("\n==================================");
    }
}