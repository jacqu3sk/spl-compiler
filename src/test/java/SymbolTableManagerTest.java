import org.junit.jupiter.api.Test;
import symbolTable.ScopeType;
import symbolTable.SymbolEntry;
import symbolTable.SymbolTable;
import symbolTable.SymbolType;

import static org.junit.jupiter.api.Assertions.*;

class SymbolTableManagerTest {

    @Test
    void addGlobalVariableAllowsLookupAcrossScopes() {
        SymbolTable table = new SymbolTable();
        SymbolEntry globalVar = new SymbolEntry(
                "value",
                SymbolType.VARIABLE,
                ScopeType.GLOBAL,
                table.getNextNodeId(),
                null,
                false
        );

        assertTrue(table.addSymbol(globalVar), "Global variable should be added");

        SymbolEntry resolved = table.lookupVariable("value", "anyOwner", ScopeType.LOCAL);
        assertNotNull(resolved, "Global variable should resolve when queried from local scope");
        assertSame(globalVar, resolved, "Lookup should return the same symbol entry that was stored");
    }

    @Test
    void duplicateGlobalVariableReportsError() {
        SymbolTable table = new SymbolTable();
        SymbolEntry first = new SymbolEntry(
                "dup",
                SymbolType.VARIABLE,
                ScopeType.GLOBAL,
                table.getNextNodeId(),
                null,
                false
        );
        SymbolEntry duplicate = new SymbolEntry(
                "dup",
                SymbolType.VARIABLE,
                ScopeType.GLOBAL,
                table.getNextNodeId(),
                null,
                false
        );

        assertTrue(table.addSymbol(first), "First declaration should succeed");
        assertFalse(table.addSymbol(duplicate), "Duplicate declaration should be rejected");
        assertTrue(table.hasErrors(), "Duplicate declaration should be recorded as an error");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("Variable 'dup'") && message.contains("global")),
                "Error list should mention the duplicate global variable");
    }

    @Test
    void lookupPrefersLocalVariableOverGlobal() {
        SymbolTable table = new SymbolTable();
        String scopeOwner = "compute";
        SymbolEntry globalVar = new SymbolEntry(
                "shared",
                SymbolType.VARIABLE,
                ScopeType.GLOBAL,
                table.getNextNodeId(),
                null,
                false
        );
        SymbolEntry localVar = new SymbolEntry(
                "shared",
                SymbolType.VARIABLE,
                ScopeType.LOCAL,
                table.getNextNodeId(),
                scopeOwner,
                false
        );

        assertTrue(table.addSymbol(globalVar), "Global variable should be added");
        assertTrue(table.addSymbol(localVar), "Local variable should be added");

        SymbolEntry resolved = table.lookupVariable("shared", scopeOwner, ScopeType.LOCAL);
        assertNotNull(resolved, "Lookup should find the symbol");
        assertSame(localVar, resolved, "Local definition should shadow the global one");
    }

    @Test
    void checkEverywhereConflictsDetectsNameCollisions() {
        SymbolTable table = new SymbolTable();
        SymbolEntry globalVar = new SymbolEntry(
                "compute",
                SymbolType.VARIABLE,
                ScopeType.GLOBAL,
                table.getNextNodeId(),
                null,
                false
        );
        SymbolEntry function = new SymbolEntry(
                "compute",
                SymbolType.FUNCTION,
                ScopeType.FUNCTION,
                table.getNextNodeId(),
                null,
                false
        );

        assertTrue(table.addSymbol(globalVar), "Global variable should be added");
        assertTrue(table.addSymbol(function), "Function should be added");

        table.checkEverywhereConflicts();

        assertTrue(table.hasErrors(), "Conflicting names should be recorded as errors");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("Global variable 'compute' conflicts with function name")),
                "Error list should mention the conflict between variable and function");
    }

    @Test
    void procedureAndFunctionExistenceMatchesRegisteredEntries() {
        SymbolTable table = new SymbolTable();
        SymbolEntry procedure = new SymbolEntry(
                "proc",
                SymbolType.PROCEDURE,
                ScopeType.PROCEDURE,
                table.getNextNodeId(),
                null,
                false
        );
        SymbolEntry function = new SymbolEntry(
                "func",
                SymbolType.FUNCTION,
                ScopeType.FUNCTION,
                table.getNextNodeId(),
                null,
                false
        );

        assertTrue(table.addSymbol(procedure), "Procedure entry should be added");
        assertTrue(table.addSymbol(function), "Function entry should be added");

        assertTrue(table.procedureExists("proc"), "Procedure should be registered");
        assertFalse(table.procedureExists("missing"), "Unknown procedure should not be reported as existing");
        assertTrue(table.functionExists("func"), "Function should be registered");
        assertFalse(table.functionExists("other"), "Unknown function should not be reported as existing");
    }

    @Test
    void duplicateLocalDeclarationReportsError() {
        SymbolTable table = new SymbolTable();
        String owner = "worker";
        SymbolEntry first = new SymbolEntry(
                "temp",
                SymbolType.VARIABLE,
                ScopeType.LOCAL,
                table.getNextNodeId(),
                owner,
                false
        );
        SymbolEntry duplicate = new SymbolEntry(
                "temp",
                SymbolType.VARIABLE,
                ScopeType.LOCAL,
                table.getNextNodeId(),
                owner,
                false
        );

        assertTrue(table.addSymbol(first), "Initial local variable should be accepted");
        assertFalse(table.addSymbol(duplicate), "Duplicate local variable must be rejected");
        assertTrue(table.hasErrors(), "Duplicate local declaration should record an error");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("local scope of worker")),
                "Error message should mention the owning scope");
    }

    @Test
    void duplicateParameterDeclarationReportsError() {
        SymbolTable table = new SymbolTable();
        String owner = "worker";
        SymbolEntry first = new SymbolEntry(
                "param",
                SymbolType.VARIABLE,
                ScopeType.LOCAL,
                table.getNextNodeId(),
                owner,
                true
        );
        SymbolEntry duplicate = new SymbolEntry(
                "param",
                SymbolType.VARIABLE,
                ScopeType.LOCAL,
                table.getNextNodeId(),
                owner,
                true
        );

        assertTrue(table.addSymbol(first), "First parameter should be recorded");
        assertFalse(table.addSymbol(duplicate), "Duplicate parameter must be rejected");
        assertTrue(table.hasErrors(), "Duplicate parameter should produce an error");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("local scope of worker")),
                "Error message should mention the owning scope");
    }

    @Test
    void localScopesAreIsolatedByOwner() {
        SymbolTable table = new SymbolTable();
        SymbolEntry localOne = new SymbolEntry(
                "temp",
                SymbolType.VARIABLE,
                ScopeType.LOCAL,
                table.getNextNodeId(),
                "worker",
                false
        );
        SymbolEntry localTwo = new SymbolEntry(
                "temp",
                SymbolType.VARIABLE,
                ScopeType.LOCAL,
                table.getNextNodeId(),
                "helper",
                false
        );

        assertTrue(table.addSymbol(localOne), "First local declaration should succeed");
        assertTrue(table.addSymbol(localTwo), "Same name in different owner should be allowed");
        assertFalse(table.hasErrors(), "No error expected for independent scopes");
    }

    @Test
    void checkEverywhereConflictsDetectsVariableProcedureNameClash() {
        SymbolTable table = new SymbolTable();
        SymbolEntry globalVar = new SymbolEntry(
                "shared",
                SymbolType.VARIABLE,
                ScopeType.GLOBAL,
                table.getNextNodeId(),
                null,
                false
        );
        SymbolEntry procedure = new SymbolEntry(
                "shared",
                SymbolType.PROCEDURE,
                ScopeType.PROCEDURE,
                table.getNextNodeId(),
                null,
                false
        );

        assertTrue(table.addSymbol(globalVar));
        assertTrue(table.addSymbol(procedure));

        table.checkEverywhereConflicts();

        assertTrue(table.hasErrors(), "Conflict between global variable and procedure should be flagged");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("Global variable 'shared' conflicts with procedure name")));
    }

    @Test
    void checkEverywhereConflictsDetectsFunctionProcedureClash() {
        SymbolTable table = new SymbolTable();
        SymbolEntry procedure = new SymbolEntry(
                "shared",
                SymbolType.PROCEDURE,
                ScopeType.PROCEDURE,
                table.getNextNodeId(),
                null,
                false
        );
        SymbolEntry function = new SymbolEntry(
                "shared",
                SymbolType.FUNCTION,
                ScopeType.FUNCTION,
                table.getNextNodeId(),
                null,
                false
        );

        assertTrue(table.addSymbol(procedure));
        assertTrue(table.addSymbol(function));

        table.checkEverywhereConflicts();

        assertTrue(table.hasErrors(), "Procedure and function with same name should conflict");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("Function 'shared' conflicts with procedure name")));
    }

    @Test
    void duplicateMainVariableReportsError() {
        SymbolTable table = new SymbolTable();
        SymbolEntry first = new SymbolEntry(
                "result",
                SymbolType.VARIABLE,
                ScopeType.MAIN,
                table.getNextNodeId(),
                null,
                false
        );
        SymbolEntry duplicate = new SymbolEntry(
                "result",
                SymbolType.VARIABLE,
                ScopeType.MAIN,
                table.getNextNodeId(),
                null,
                false
        );

        assertTrue(table.addSymbol(first), "First main-scoped variable should succeed");
        assertFalse(table.addSymbol(duplicate), "Duplicate main-scoped variable must be rejected");
        assertTrue(table.hasErrors(), "Duplicate in main scope should produce an error");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("main scope")),
                "Error message should mention the main scope");
    }

    @Test
    void lookupFromMainPrefersMainVariableOverGlobal() {
        SymbolTable table = new SymbolTable();
        SymbolEntry globalVar = new SymbolEntry(
                "shared",
                SymbolType.VARIABLE,
                ScopeType.GLOBAL,
                table.getNextNodeId(),
                null,
                false
        );
        SymbolEntry mainVar = new SymbolEntry(
                "shared",
                SymbolType.VARIABLE,
                ScopeType.MAIN,
                table.getNextNodeId(),
                null,
                false
        );

        assertTrue(table.addSymbol(globalVar));
        assertTrue(table.addSymbol(mainVar));

        SymbolEntry resolved = table.lookupVariable("shared", null, ScopeType.MAIN);
        assertNotNull(resolved);
        assertSame(mainVar, resolved, "Main scope variable should shadow global variable with same name");
    }

    @Test
    void lookupFromMainFallsBackToGlobalWhenMissing() {
        SymbolTable table = new SymbolTable();
        SymbolEntry globalVar = new SymbolEntry(
                "counter",
                SymbolType.VARIABLE,
                ScopeType.GLOBAL,
                table.getNextNodeId(),
                null,
                false
        );

        assertTrue(table.addSymbol(globalVar));

        SymbolEntry resolved = table.lookupVariable("counter", null, ScopeType.MAIN);
        assertNotNull(resolved, "Main lookup should fall back to global declaration when absent locally");
        assertSame(globalVar, resolved);
    }
}
