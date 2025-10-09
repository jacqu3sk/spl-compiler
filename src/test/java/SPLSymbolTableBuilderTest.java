import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SPLSymbolTableBuilderTest {

    @Test
    void wellFormedProgramProducesSymbolTable() {
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

        SPLSymbolTableBuilder builder = buildSymbolTable(program);

        assertFalse(builder.hasErrors(), () -> "Semantic errors found: " + builder.getErrors());

        SymbolTableManager manager = builder.getManager();
        SymbolTable global = manager.getGlobalScope();

        Symbol counter = global.getSymbol("counter");
        assertNotNull(counter, "Global variable should be registered");
        assertEquals(SymbolType.VARIABLE, counter.getType());

        Symbol adder = global.getSymbol("adder");
        assertNotNull(adder, "Function should be registered in global scope");
        assertEquals(SymbolType.FUNCTION, adder.getType());

        Symbol worker = global.getSymbol("worker");
        assertNotNull(worker, "Procedure should be registered in global scope");
        assertEquals(SymbolType.PROCEDURE, worker.getType());

        assertNull(manager.lookupSymbol("scratch"),
                "Local symbols must not leak outside their defining scope");
        assertEquals(ScopeType.GLOBAL, manager.getCurrentScope().getScopeType(),
                "Symbol table traversal should return to the global scope");
    }

    @Test
    void duplicateVariableDeclarationReportsError() {
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

        SPLSymbolTableBuilder builder = buildSymbolTable(program);

        assertTrue(builder.hasErrors(), "Duplicate variable should be reported as an error");
        assertTrue(builder.getErrors().stream()
                .anyMatch(message -> message.contains("Duplicate declaration of 'reused'")),
                () -> "Expected duplicate declaration error, but got: " + builder.getErrors());
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

        SPLSymbolTableBuilder builder = buildSymbolTable(program);

        assertTrue(builder.hasErrors(), "Using an undeclared identifier must be reported");
        List<String> errors = builder.getErrors();
        assertTrue(errors.stream().anyMatch(message -> message.contains("Undeclared variable 'mystery'")),
                () -> "Expected undeclared variable error, but got: " + errors);
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

        SPLSymbolTableBuilder builder = buildSymbolTable(program);

        assertTrue(builder.hasErrors(), "Calling an undeclared procedure should report an error");
        assertTrue(builder.getErrors().stream()
                .anyMatch(message -> message.contains("Undeclared variable 'helper'")),
                () -> "Expected undeclared helper error, but got: " + builder.getErrors());
    }

    @Test
    void assignmentWithProcedureReportsError() {
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

        SPLSymbolTableBuilder builder = buildSymbolTable(program);

        assertTrue(builder.hasErrors(), "Assigning from a procedure should report an error");
        assertTrue(builder.getErrors().stream()
                .anyMatch(message -> message.contains("'task' is not a function")),
                () -> "Expected procedure assignment error, but got: " + builder.getErrors());
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

        SPLSymbolTableBuilder builder = buildSymbolTable(program);

        assertTrue(builder.hasErrors(), "Local variable shadowing a parameter must be rejected");
        assertTrue(builder.getErrors().stream()
                .anyMatch(message -> message.contains("Duplicate declaration of 'input'")),
                () -> "Expected duplicate declaration error, but got: " + builder.getErrors());
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

        SPLSymbolTableBuilder builder = buildSymbolTable(program);

        assertFalse(builder.hasErrors(), () -> "Main variable reusing global name should be allowed: " + builder.getErrors());
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

        SPLSymbolTableBuilder builder = buildSymbolTable(program);

        assertTrue(builder.hasErrors(), "Using an undeclared name inside procedure must be reported");
        assertTrue(builder.getErrors().stream()
                .anyMatch(message -> message.contains("Undeclared variable 'counter'")),
                () -> "Expected undeclared counter error, but got: " + builder.getErrors());
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

        SPLSymbolTableBuilder builder = buildSymbolTable(program);

        assertTrue(builder.hasErrors(), "Global identifiers must not clash across kinds");
        assertTrue(builder.getErrors().stream()
                .anyMatch(message -> message.contains("Duplicate declaration of 'calc'")),
                () -> "Expected duplicate calc declaration error, but got: " + builder.getErrors());
    }

    private SPLSymbolTableBuilder buildSymbolTable(String source) {
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

            SPLSymbolTableBuilder builder = new SPLSymbolTableBuilder();
            ParseTreeWalker.DEFAULT.walk(builder, tree);
            return builder;
        } catch (Exception e) {
            fail("Failed to parse SPL program: " + e.getMessage());
            return null; // Unreachable because fail throws, but keeps compiler happy
        }
    }
}
