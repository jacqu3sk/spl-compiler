import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import generated.SPLLexer;
import generated.SPLParser;
import parser.SyntaxErrorListener;
import symbolTable.SPLSemanticAnalyzer;
import symbolTable.SymbolTable;
import typeChecker.TypeChecker;
import typeChecker.TypeErrorListener;

class TypeCheckerTest {

    private static final class CollectingTypeErrorListener implements TypeErrorListener {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void onTypeError(ParserRuleContext ctx, String message) {
            messages.add(message);
        }
    }

    private static final class TypeCheckResult {
        final boolean success;
        final List<String> errors;

        TypeCheckResult(boolean success, List<String> errors) {
            this.success = success;
            this.errors = errors;
        }
    }

    @Test
    void wellTypedProgramPasses() {
        String program = """
                glob {
                    counter
                }
                proc {
                    worker(input) {
                        local {
                            temp
                        }
                        temp = (input plus 1);
                        halt
                    }
                }
                func {
                    adder(value) {
                        local {
                            scratch
                        }
                        scratch = (value plus 2);
                        halt;
                        return value
                    }
                }
                main {
                    var {
                        result
                    }
                    result = (5 plus 3);
                    if ( result eq 8 ) {
                        print result
                    } else {
                        halt
                    };
                    while ( result eq result ) {
                        halt
                    };
                    halt
                }
                """;

        TypeCheckResult result = runTypeChecker(program);

        assertTrue(result.success, () -> "Expected program to type check but errors were: " + result.errors);
        assertTrue(result.errors.isEmpty(), "Well-typed program should not report type errors");
    }

    @Test
    void loopWithNumericConditionFails() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var {
                        counter
                    }
                    while ( 1 plus 2 ) {
                        halt
                    };
                    halt
                }
                """;

        TypeCheckResult result = runTypeChecker(program);

        assertFalse(result.success, "Numeric loop condition must fail type checking");
        assertTrue(result.errors.stream().anyMatch(msg -> msg.contains("Condition in loop must be boolean")),
                () -> "Expected loop condition error, got: " + result.errors);
    }

    @Test
    void branchWithNumericConditionFails() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var {
                        value
                    }
                    if ( 1 plus 2 ) {
                        halt
                    } else {
                        halt
                    };
                    halt
                }
                """;

        TypeCheckResult result = runTypeChecker(program);

        assertFalse(result.success, "Numeric branch condition must fail type checking");
        assertTrue(result.errors.stream().anyMatch(msg -> msg.contains("Condition in branch must be boolean")),
                () -> "Expected branch condition error, got: " + result.errors);
    }

    @Test
    void assignmentFromBooleanExpressionFails() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var {
                        result
                    }
                    result = ( ( 1 eq 2 ) plus 3 );
                    halt
                }
                """;

        TypeCheckResult result = runTypeChecker(program);

        assertFalse(result.success, "Assignment from mismatched term should fail");
        assertTrue(result.errors.isEmpty(), "Assignment mismatch does not emit explicit error message");
    }

    @Test
    void logicalOperatorsRequireBooleanTerms() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var {
                        flag
                    }
                    if ( 1 or 2 ) {
                        halt
                    } else {
                        halt
                    };
                    halt
                }
                """;

        TypeCheckResult result = runTypeChecker(program);

        assertFalse(result.success, "Logical operations with numeric operands should fail typing");
    }

    @Test
    void stringOutputIsAllowed() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var { }
                    print "ok";
                    halt
                }
                """;

        TypeCheckResult result = runTypeChecker(program);

        assertTrue(result.success, "Printing a string literal should type check");
    }

    @Test
    void negAppliedToBooleanTermFails() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var {
                        value
                    }
                    value = ( neg ( 1 eq 2 ) );
                    halt
                }
                """;

        TypeCheckResult result = runTypeChecker(program);

        assertFalse(result.success, "Applying numeric negation to boolean term must fail");
    }

    @Test
    void notAppliedToNumericTermFails() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var {
                        value
                    }
                    if ( not ( 1 plus 2 ) ) {
                        halt
                    } else {
                        halt
                    };
                    halt
                }
                """;

        TypeCheckResult result = runTypeChecker(program);

        assertFalse(result.success, "Applying boolean not to numeric term must fail");
    }

    @Test
    void doUntilWithNumericConditionFails() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var { }
                    do {
                        halt
                    } until ( 1 plus 2 );
                    halt
                }
                """;

        TypeCheckResult result = runTypeChecker(program);

        assertFalse(result.success, "do-until must require boolean condition");
        assertTrue(result.errors.stream().anyMatch(msg -> msg.contains("Condition in loop must be boolean")),
                () -> "Expected loop condition error, got: " + result.errors);
    }

    @Test
    void doUntilWithBooleanConditionPasses() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var {
                        counter
                    }
                    do {
                        counter = (counter plus 1);
                        halt
                    } until ( counter eq counter );
                    halt
                }
                """;

        TypeCheckResult result = runTypeChecker(program);

        assertTrue(result.success, () -> "Boolean do-until condition should type check but errors were: " + result.errors);
    }

    @Test
    void booleanConjunctionWithComparisonsPasses() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var {
                        flag
                    }
                    if ( ( 1 eq 2 ) and ( 2 eq 3 ) ) {
                        halt
                    } else {
                        halt
                    };
                    halt
                }
                """;

        TypeCheckResult result = runTypeChecker(program);

        assertTrue(result.success, () -> "Boolean conjunction of comparisons should type check but errors were: " + result.errors);
    }

    @Test
    void whileWithBooleanConditionPasses() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var {
                        counter
                    }
                    while ( counter eq counter ) {
                        halt
                    };
                    halt
                }
                """;

        TypeCheckResult result = runTypeChecker(program);

        assertTrue(result.success, () -> "While with boolean condition should type check but errors were: " + result.errors);
    }

    @Test
    void branchWithBooleanConditionPasses() {
        String program = """
                glob { }
                proc { }
                func { }
                main {
                    var {
                        counter
                    }
                    if ( counter eq counter ) {
                        halt
                    } else {
                        halt
                    };
                    halt
                }
                """;

        TypeCheckResult result = runTypeChecker(program);

        assertTrue(result.success, () -> "Branch with boolean condition should type check but errors were: " + result.errors);
    }

    @Test
    void functionCallAssignmentPasses() {
        String program = """
                glob {
                    seed
                }
                proc { }
                func {
                    sum(a b) {
                        local {
                            temp
                        }
                        temp = (a plus b);
                        halt;
                        return temp
                    }
                }
                main {
                    var {
                        result
                    }
                    result = sum(result seed);
                    halt
                }
                """;

        TypeCheckResult result = runTypeChecker(program);

        assertTrue(result.success, () -> "Function call assignment should type check but errors were: " + result.errors);
    }

    @Test
    void functionCallWithThreeArgumentsPasses() {
        String program = """
                glob { }
                proc { }
                func {
                    combine(a b c) {
                        local { }
                        halt;
                        return a
                    }
                }
                main {
                    var {
                        value
                    }
                    value = combine(value value value);
                    halt
                }
                """;

        TypeCheckResult result = runTypeChecker(program);

        assertTrue(result.success, () -> "Function call with three numeric arguments should succeed but errors were: " + result.errors);
    }

    @Test
    void functionCallWithoutArgumentsPasses() {
        String program = """
                glob { }
                proc { }
                func {
                    zero() {
                        local { }
                        halt;
                        return 0
                    }
                }
                main {
                    var {
                        value
                    }
                    value = zero();
                    halt
                }
                """;

        TypeCheckResult result = runTypeChecker(program);

        assertTrue(result.success, () -> "Zero-argument function call should type check but errors were: " + result.errors);
    }

    @Test
    void procedureCallWithArgumentPasses() {
        String program = """
                glob {
                    shared
                }
                proc {
                    worker(item) {
                        local { }
                        halt
                    }
                }
                func { }
                main {
                    var {
                        localVar
                    }
                    localVar = (shared plus 1);
                    worker(localVar);
                    halt
                }
                """;

        TypeCheckResult result = runTypeChecker(program);

        assertTrue(result.success, () -> "Procedure call with numeric argument should type check but errors were: " + result.errors);
    }

    @Test
    void procedureCallWithoutArgumentsPasses() {
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

        TypeCheckResult result = runTypeChecker(program);

        assertTrue(result.success, () -> "Zero-argument procedure call should type check but errors were: " + result.errors);
    }

    private TypeCheckResult runTypeChecker(String source) {
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
            assertFalse(syntaxErrors.hasErrors(), "Provided SPL program contains syntax errors");

            ParseTreeWalker walker = new ParseTreeWalker();
            SPLSemanticAnalyzer analyzer = new SPLSemanticAnalyzer();
            
            // Walk the parse tree
            walker.walk(analyzer, tree);
            
            // Get results
            SymbolTable symbolTable = analyzer.getSymbolTable();
            
            assertFalse(symbolTable.hasErrors(), "Provided SPL program contains semantic errors");

            CollectingTypeErrorListener listener = new CollectingTypeErrorListener();
            TypeChecker checker = new TypeChecker(symbolTable, listener);
            boolean success = checker.visit(tree);
            return new TypeCheckResult(success, listener.messages);
        } catch (Exception e) {
            fail("Failed to parse or type check SPL program: " + e.getMessage());
            return new TypeCheckResult(false, List.of());
        }
    }
}
