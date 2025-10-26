import generated.SPLLexer;
import generated.SPLParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;
import parser.SyntaxErrorListener;

import static org.junit.jupiter.api.Assertions.*;

class SyntaxTest {

    @Test
    void variablesAndProceduresWithParametersParse() {
        String program = """
                glob {
                    globalOne
                    globalTwo
                }
                proc {
                    compute(alpha beta) {
                        local {
                            temp
                            scratch
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

        SyntaxErrorListener listener = parseProgram(program);

        assertFalse(listener.hasErrors(), "Procedure with parameters and locals should parse");
    }

    @Test
    void wellFormedProgramParses() {
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
                    adder() {
                        local { }
                        halt;
                        return 0
                    }
                }
                main {
                    var {
                        result
                    }
                    result = (5 plus 3);
                    halt
                }
                """;

        SyntaxErrorListener listener = parseProgram(program);

        assertFalse(listener.hasErrors(), "Valid SPL program should parse without syntax errors");
    }

    @Test
    void functionWithParametersAndReturnParses() {
        String program = """
                glob { }
                proc { }
                func {
                    combine(a b c) {
                        local {
                            temp
                        }
                        temp = (a plus b);
                        halt;
                        return c
                    }
                }
                main {
                    var {
                        output
                    }
                    output = combine(output output output);
                    halt
                }
                """;

        SyntaxErrorListener listener = parseProgram(program);

        assertFalse(listener.hasErrors(), "Function with three parameters and return expression should parse");
    }

    @Test
    void complexMainConstructsParse() {
        String program = """
                glob {
                    seed
                }
                proc {
                    ping() {
                        local { }
                        halt
                    }
                }
                func {
                    double(value) {
                        local {
                            temp
                        }
                        temp = (value plus value);
                        halt;
                        return temp
                    }
                }
                main {
                    var {
                        counter
                    }
                    while ( counter eq counter ) {
                        counter = (counter plus 1);
                        if ( counter eq counter ) {
                            print counter
                        } else {
                            ping()
                        };
                        do {
                            ping()
                        } until ( counter eq counter );
                        counter = double(counter);
                        halt
                    };
                    halt
                }
                """;

        SyntaxErrorListener listener = parseProgram(program);

        assertFalse(listener.hasErrors(), "Loops, branches, and calls combined should parse");
    }

    @Test
    void missingReturnExpressionInFunctionProducesSyntaxError() {
        String program = """
                glob { }
                proc { }
                func {
                    faulty() {
                        local { }
                        halt;
                        return
                    }
                }
                main {
                    var { }
                    halt
                }
                """;

        SyntaxErrorListener listener = parseProgram(program);

        assertTrue(listener.hasErrors(), "Function without return atom should trigger syntax error");
    }

    @Test
    void functionCallWithTooManyArgumentsFails() {
        String program = """
                glob { }
                proc { }
                func {
                    sum(a b c) {
                        local { }
                        halt;
                        return a
                    }
                }
                main {
                    var {
                        value
                    }
                    value = sum(value value value value);
                    halt
                }
                """;

        SyntaxErrorListener listener = parseProgram(program);

        assertTrue(listener.hasErrors(), "Function call with four arguments must fail parsing");
    }

    @Test
    void tooManyLocalDeclarationsFailsParsing() {
        String program = """
                glob { }
                proc {
                    worker() {
                        local {
                            a
                            b
                            c
                            d
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

        SyntaxErrorListener listener = parseProgram(program);

        assertTrue(listener.hasErrors(), "MAXTHREE with more than three identifiers should not parse");
    }

    @Test
    void missingSemicolonBetweenInstructionsFailsParsing() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var {
                        counter
                    }
                    print counter
                    halt
                }
                """;

        SyntaxErrorListener listener = parseProgram(program);

        assertTrue(listener.hasErrors(), "Consecutive instructions without semicolon should be a syntax error");
    }

    @Test
    void procedureBodyWithoutLocalSectionFailsParsing() {
        String program = """
                glob { }
                proc {
                    worker() {
                        halt
                    }
                }
                func { }
                main {
                    var { }
                    halt
                }
                """;

        SyntaxErrorListener listener = parseProgram(program);

        assertTrue(listener.hasErrors(), "Procedure body must contain local declaration section");
    }

    @Test
    void missingMainBlockIsSyntaxError() {
        String program = """
                glob { }
                proc { }
                func { }
                """;

        SyntaxErrorListener listener = parseProgram(program);

        assertTrue(listener.hasErrors(), "Programs missing main block must fail parsing");
    }

    @Test
    void commentsAreIgnoredByParser() {
        String program = """
                glob {
                    alpha
                    // a comment
                    beta
                }
                proc {
                    /* block comment */
                    worker() {
                        local {
                            // inside local
                            temp
                        }
                        halt
                    }
                }
                func {
                    noop() {
                        local { }
                        halt;
                        return 0
                    }
                }
                main {
                    var {
                        // comment in main vars
                        value
                    }
                    print "ok";
                    halt
                }
                """;

        SyntaxErrorListener listener = parseProgram(program);

        assertFalse(listener.hasErrors(), "Comments should not interfere with parsing");
    }

    @Test
    void identifierStartingWithDigitFails() {
        String program = """
                glob {
                    1bad
                }
                proc { }
                func { }
                main {
                    var { }
                    halt
                }
                """;

        SyntaxErrorListener listener = parseProgram(program);

        assertTrue(listener.hasErrors(), "Identifiers may not start with a digit");
    }

    @Test
    void unmatchedStringLiteralFails() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var { }
                    print "unterminated;
                    halt
                }
                """;

        SyntaxErrorListener listener = parseProgram(program);

        assertTrue(listener.hasErrors(), "Unterminated string literal should produce syntax error");
    }

    @Test
    void zeroArgumentProcedureCallParses() {
        String program = """
                glob { }
                proc {
                    ping() {
                        local { }
                        halt
                    }
                }
                func { }
                main {
                    var { }
                    ping();
                    halt
                }
                """;

        SyntaxErrorListener listener = parseProgram(program);

        assertFalse(listener.hasErrors(), "Zero argument procedure call should parse");
    }

    @Test
    void nestedBinaryOperationsParse() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var {
                        value
                    }
                    value = ( ( (1 plus 2) mult 3 ) minus 4 );
                    halt
                }
                """;

        SyntaxErrorListener listener = parseProgram(program);

        assertFalse(listener.hasErrors(), "Nested binary operations should be valid syntax");
    }

    private SyntaxErrorListener parseProgram(String source) {
        try {
            CharStream charStream = CharStreams.fromString(source);
            SPLLexer lexer = new SPLLexer(charStream);
            SyntaxErrorListener syntaxErrors = new SyntaxErrorListener();
            lexer.removeErrorListeners();
            lexer.addErrorListener(syntaxErrors);

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            SPLParser parser = new SPLParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(syntaxErrors);

            ParseTree tree = parser.spl_prog();
            assertNotNull(tree, "Parser should produce a parse tree even when syntax errors occur");
            return syntaxErrors;
        } catch (Exception e) {
            fail("Parsing failed unexpectedly: " + e.getMessage());
            return null;
        }
    }
}
