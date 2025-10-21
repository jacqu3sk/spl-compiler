import generated.SPLLexer;
import generated.SPLParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;
import parser.SyntaxErrorListener;
import symbolTable.ScopeType;
import symbolTable.SPLSemanticAnalyzer;
import symbolTable.SymbolEntry;
import symbolTable.SymbolTable;
import symbolTable.SymbolType;

import static org.junit.jupiter.api.Assertions.*;

class SPLSemanticAnalyzerTest {

    @Test
    void wellFormedProgramProducesExpectedSymbols() {
        String program = """
                glob {
                    counter
                }
                proc {
                    worker() {
                        local {
                            temp
                        }
                        halt
                    }
                }
                func {
                    adder(input) {
                        local {
                            scratch
                        }
                        print scratch;
                        halt;
                        return input
                    }
                }
                main {
                    var {
                        result
                    }
                    result = (5 plus 3);
                    result = adder(result);
                    print result;
                    halt
                }
                """;

        SPLSemanticAnalyzer analyzer = analyzeProgram(program);
        SymbolTable table = analyzer.getSymbolTable();

        assertFalse(table.hasErrors(), () -> "Semantic errors found: " + table.getErrors());

        SymbolEntry counter = table.lookupVariable("counter", null, ScopeType.GLOBAL);
        assertNotNull(counter, "Global variable should be registered");
        assertEquals(SymbolType.VARIABLE, counter.getSymbolType());

        assertTrue(table.functionExists("adder"), "Function should be registered");
        assertTrue(table.procedureExists("worker"), "Procedure should be registered");

        SymbolEntry scratchLocal = table.lookupVariable("scratch", "adder", ScopeType.LOCAL);
        assertNotNull(scratchLocal, "Local variable should be visible inside its function");
        assertNull(table.lookupVariable("scratch", null, ScopeType.GLOBAL),
                "Local variable should not leak into the global scope");
    }

    @Test
    void duplicateGlobalVariableReportsError() {
        String program = """
                glob {
                    reused
                    reused
                }
                proc { }
                func { }
                main {
                    var { }
                    halt
                }
                """;

        SymbolTable table = analyzeProgram(program).getSymbolTable();

        assertTrue(table.hasErrors(), "Duplicate global variable should produce an error");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("Variable 'reused'") &&
                        message.contains("already declared in global scope")),
                () -> "Expected duplicate declaration error, got: " + table.getErrors());
    }

    @Test
    void undeclaredVariableUsageTriggersError() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var {
                        counter
                    }
                    counter = (1 plus 2);
                    print counter;
                    counter = (counter plus mystery);
                    halt
                }
                """;

        SymbolTable table = analyzeProgram(program).getSymbolTable();

        assertTrue(table.hasErrors(), "Using an undeclared identifier must be reported");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("Undeclared variable 'mystery'")),
                () -> "Expected undeclared variable error, got: " + table.getErrors());
    }

    @Test
    void procedureCallMustBeDeclared() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var {
                        result
                    }
                    print result;
                    helper();
                    halt
                }
                """;

        SymbolTable table = analyzeProgram(program).getSymbolTable();

        assertTrue(table.hasErrors(), "Calling an undeclared procedure should report an error");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("Undeclared procedure 'helper'")),
                () -> "Expected undeclared procedure error, got: " + table.getErrors());
    }

    @Test
    void assignmentWithUnknownFunctionReportsError() {
        String program = """
                glob { }
                proc {
                    task() {
                        local { }
                        halt
                    }
                }
                func { }
                main {
                    var {
                        result
                    }
                    result = task();
                    halt
                }
                """;

        SymbolTable table = analyzeProgram(program).getSymbolTable();

        assertTrue(table.hasErrors(), "Assigning from a procedure should report an error");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("Undeclared function 'task'")),
                () -> "Expected function lookup error, got: " + table.getErrors());
    }

    @Test
    void localVariableMayNotShadowParameter() {
        String program = """
                glob { }
                proc { }
                func {
                    transform(input) {
                        local {
                            input
                        }
                        halt;
                        return input
                    }
                }
                main {
                    var { }
                    halt
                }
                """;

        SymbolTable table = analyzeProgram(program).getSymbolTable();

        assertTrue(table.hasErrors(), "Local variable shadowing a parameter must be rejected");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("shadows parameter")),
                () -> "Expected parameter shadowing error, got: " + table.getErrors());
    }

    @Test
    void mainVariablesCanReuseGlobalNames() {
        String program = """
                glob {
                    value
                }
                proc { }
                func { }
                main {
                    var {
                        value
                    }
                    print value;
                    halt
                }
                """;

        SymbolTable table = analyzeProgram(program).getSymbolTable();

        assertFalse(table.hasErrors(),
                () -> "Main variable reusing global name should be allowed: " + table.getErrors());
    }

    @Test
    void undeclaredVariableInProcedureReportsError() {
        String program = """
                glob { }
                proc {
                    worker() {
                        local { }
                        print counter;
                        halt
                    }
                }
                func { }
                main {
                    var { }
                    halt
                }
                """;

        SymbolTable table = analyzeProgram(program).getSymbolTable();

        assertTrue(table.hasErrors(), "Using an undeclared name inside procedure must be reported");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("Undeclared variable 'counter'")),
                () -> "Expected undeclared counter error, got: " + table.getErrors());
    }

    @Test
    void globalVariableMayNotShareNameWithFunction() {
        String program = """
                glob {
                    calc
                }
                proc { }
                func {
                    calc(total) {
                        local { }
                        halt;
                        return total
                    }
                }
                main {
                    var {
                        result
                    }
                    halt
                }
                """;

        SymbolTable table = analyzeProgram(program).getSymbolTable();

        assertTrue(table.hasErrors(), "Global identifiers must not clash across kinds");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("conflicts with function name")),
                () -> "Expected global/function conflict error, got: " + table.getErrors());
    }

    @Test
    void duplicateParametersInProcedureReported() {
        String program = """
                glob { }
                proc {
                    worker(alpha alpha) {
                        local { }
                        halt
                    }
                }
                func { }
                main {
                    var { }
                    halt
                }
                """;

        SymbolTable table = analyzeProgram(program).getSymbolTable();

        assertTrue(table.hasErrors(), "Duplicate parameters should be rejected");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("already declared in local scope of worker")),
                () -> "Expected duplicate parameter error, got: " + table.getErrors());
    }

    @Test
    void duplicateLocalVariablesInsideProcedureReported() {
        String program = """
                glob { }
                proc {
                    worker() {
                        local {
                            temp
                            temp
                        }
                        halt
                    }
                }
                func { }
                main {
                    var { }
                    halt
                }
                """;

        SymbolTable table = analyzeProgram(program).getSymbolTable();

        assertTrue(table.hasErrors(), "Duplicate local variable declarations must be rejected");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("already declared in local scope of worker")),
                () -> "Expected duplicate local error, got: " + table.getErrors());
    }

    @Test
    void duplicateMainVariablesReported() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var {
                        result
                        result
                    }
                    halt
                }
                """;

        SymbolTable table = analyzeProgram(program).getSymbolTable();

        assertTrue(table.hasErrors(), "Duplicates in main scope should be reported");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("already declared in main scope")),
                () -> "Expected duplicate main variable error, got: " + table.getErrors());
    }

    @Test
    void globalVariableMayNotShareNameWithProcedure() {
        String program = """
                glob {
                    shared
                }
                proc {
                    shared() {
                        local { }
                        halt
                    }
                }
                func { }
                main {
                    var { }
                    halt
                }
                """;

        SymbolTable table = analyzeProgram(program).getSymbolTable();

        assertTrue(table.hasErrors(), "Global variable sharing a procedure name should conflict");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("Global variable 'shared' conflicts with procedure name")),
                () -> "Expected variable/procedure conflict error, got: " + table.getErrors());
    }

    @Test
    void functionAndProcedureNameConflictReported() {
        String program = """
                glob { }
                proc {
                    shared() {
                        local { }
                        halt
                    }
                }
                func {
                    shared(input) {
                        local { }
                        halt;
                        return input
                    }
                }
                main {
                    var { }
                    halt
                }
                """;

        SymbolTable table = analyzeProgram(program).getSymbolTable();

        assertTrue(table.hasErrors(), "Function and procedure sharing a name should conflict");
        assertTrue(table.getErrors().stream()
                .anyMatch(message -> message.contains("Function 'shared' conflicts with procedure name")),
                () -> "Expected function/procedure conflict error, got: " + table.getErrors());
    }

    @Test
    void globalVariableAccessibleInsideFunctionWithoutLocalDeclaration() {
        String program = """
                glob {
                    seed
                }
                proc { }
                func {
                    reader(input) {
                        local { }
                        print seed;
                        halt;
                        return input
                    }
                }
                main {
                    var { }
                    halt
                }
                """;

        SymbolTable table = analyzeProgram(program).getSymbolTable();

        assertFalse(table.hasErrors(),
                () -> "Reading a global inside a function should be allowed: " + table.getErrors());
        SymbolEntry resolved = table.lookupVariable("seed", "reader", ScopeType.LOCAL);
        assertNotNull(resolved, "Lookup should find the global variable from function scope");
        assertEquals(ScopeType.GLOBAL, resolved.getScope(), "Lookup should return the global declaration");
    }

    @Test
    void mainMayUseGlobalVariablesWithoutLocalDeclaration() {
        String program = """
                glob {
                    counter
                }
                proc { }
                func { }
                main {
                    var { }
                    counter = (counter plus 1);
                    halt
                }
                """;

        SymbolTable table = analyzeProgram(program).getSymbolTable();

        assertFalse(table.hasErrors(),
                () -> "Main scope should be able to use global variables without redeclaration: " + table.getErrors());
        SymbolEntry resolved = table.lookupVariable("counter", null, ScopeType.MAIN);
        assertNotNull(resolved, "Lookup from main should fall back to the global declaration");
        assertEquals(SymbolType.VARIABLE, resolved.getSymbolType());
        assertEquals(ScopeType.GLOBAL, resolved.getScope());
    }

    private SPLSemanticAnalyzer analyzeProgram(String source) {
        try {
            CharStream charStream = CharStreams.fromString(source);
            SPLLexer lexer = new SPLLexer(charStream);
            SyntaxErrorListener errorListener = new SyntaxErrorListener();
            lexer.removeErrorListeners();
            lexer.addErrorListener(errorListener);

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            SPLParser parser = new SPLParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(errorListener);

            ParseTree tree = parser.spl_prog();
            assertFalse(errorListener.hasErrors(), "Test input produced syntax errors");

            SPLSemanticAnalyzer analyzer = new SPLSemanticAnalyzer();
            ParseTreeWalker.DEFAULT.walk(analyzer, tree);
            return analyzer;
        } catch (Exception e) {
            fail("Failed to parse SPL program: " + e.getMessage());
            return null;
        }
    }
}
