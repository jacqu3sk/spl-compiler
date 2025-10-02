package symbolTable;

public enum ScopeType {
    EVERYWHERE,  // Top-level scope
    GLOBAL,      // Global variables
    PROCEDURE,   // Procedure definitions scope
    FUNCTION,    // Function definitions scope
    MAIN,        // Main program scope
    LOCAL        // Local scope within procedure/function
}