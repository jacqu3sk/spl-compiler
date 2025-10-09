import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SymbolTableManagerTest {

    @Test
    void addSymbolStoresEntryInCurrentScope() {
        SymbolTableManager manager = new SymbolTableManager();

        boolean added = manager.addSymbol("value", SymbolType.VARIABLE, 1, 0);

        assertTrue(added, "Symbol should be added to the current scope");
        Symbol resolved = manager.lookupSymbol("value");
        assertNotNull(resolved, "Symbol must be resolvable from the current scope");
        assertEquals("value", resolved.getName());
        assertEquals(SymbolType.VARIABLE, resolved.getType());
        assertEquals(ScopeType.GLOBAL, resolved.getScope());
    }

    @Test
    void duplicateDeclarationsReportError() {
        SymbolTableManager manager = new SymbolTableManager();

        assertTrue(manager.addSymbol("dup", SymbolType.VARIABLE, 2, 1));
        assertFalse(manager.addSymbol("dup", SymbolType.VARIABLE, 3, 5),
                "Duplicate symbol should be rejected");

        assertTrue(manager.hasErrors(), "Manager should record duplicate declaration error");
        assertTrue(manager.getErrors().stream()
                .anyMatch(message -> message.contains("Duplicate declaration of 'dup'")),
                "Duplicate declaration must be mentioned in errors");
    }

    @Test
    void symbolsAreVisibleAcrossNestedScopes() {
        SymbolTableManager manager = new SymbolTableManager();
        manager.addSymbol("globalVar", SymbolType.VARIABLE, 1, 0);

        manager.enterScope("function_fn_1", ScopeType.FUNCTION);
        manager.addSymbol("localVar", SymbolType.VARIABLE, 5, 2);

        assertNotNull(manager.lookupSymbol("localVar"), "Inner symbol should resolve inside nested scope");
        assertNotNull(manager.lookupSymbol("globalVar"), "Global symbol should be visible inside nested scope");

        manager.exitScope();
        assertEquals(ScopeType.GLOBAL, manager.getCurrentScope().getScopeType(),
                "Exiting scope must restore the global scope");
        assertNull(manager.lookupSymbol("localVar"),
                "Local symbol must not be visible after exiting its scope");
    }

    @Test
    void cannotExitGlobalScope() {
        SymbolTableManager manager = new SymbolTableManager();

        SymbolTable exited = manager.exitScope();

        assertNull(exited, "Attempt to exit global scope should fail");
        assertTrue(manager.hasErrors(), "Manager should report the invalid scope exit");
        assertTrue(manager.getErrors().stream()
                .anyMatch(message -> message.contains("Cannot exit global scope")),
                "Error message must mention the global scope restriction");
    }

    @Test
    void generateScopeNameProducesIncrementalIdentifiers() {
        SymbolTableManager manager = new SymbolTableManager();

        String first = manager.generateScopeName("compute", ScopeType.FUNCTION);
        String second = manager.generateScopeName("compute", ScopeType.FUNCTION);

        assertNotEquals(first, second, "Generated scope names must be unique");
        assertTrue(first.startsWith("function_compute_"));
        assertTrue(second.startsWith("function_compute_"));
        assertNotNull(manager.enterScope("function_one", ScopeType.FUNCTION));
    }
}
