import generated.SPLLexer;
import generated.SPLParser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;
import parser.SyntaxErrorListener;
import symbolTable.ScopeType;
import symbolTable.SPLSemanticAnalyzer;
import symbolTable.SymbolEntry;
import symbolTable.SymbolTable;
import symbolTable.SymbolType;
import translator.Translator;
import typeChecker.ConsoleTypeErrorListener;
import typeChecker.TypeChecker;
import typeChecker.TypeErrorListener;

import static org.junit.jupiter.api.Assertions.*;

public class CodeGenerationTest {
    
    @Test
    void CodeGeneration_Success() {
        String exampleProgram =  """
            glob {
                x
                y
            }
            proc {
                hx() {
                    local{}
                    halt
                }
            }
            func {
                gx(value) {
                    local{}
                    halt;
                    return value
                }
            }
            main {
                var {
                    a
                    b
                }
                x = (10 plus 5);
                print x;
                halt
            }
            """;

        CharStream input = CharStreams.fromString(exampleProgram);
       
        SPLLexer lexer = new SPLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Step 2: Syntax Analysis (Parsing)
        SPLParser parser = new SPLParser(tokens);
        
        // Error handling for parser
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer,
                                  Object offendingSymbol,
                                  int line, int charPositionInLine,
                                  String msg,
                                  RecognitionException e) {
            }
        });
        
        ParseTree tree = parser.spl_prog();
        
        // Check if parsing was successful
        if (parser.getNumberOfSyntaxErrors() > 0) {
            return;
        }
        
        // Step 3: Semantic Analysis
        ParseTreeWalker walker = new ParseTreeWalker();
        SPLSemanticAnalyzer analyzer = new SPLSemanticAnalyzer();
        
        // Walk the parse tree
        walker.walk(analyzer, tree);
        
        // Get results
        SymbolTable symbolTable = analyzer.getSymbolTable();

        TypeErrorListener typeErrorListener = new ConsoleTypeErrorListener();
        TypeChecker typeChecker = new TypeChecker(typeErrorListener);
        Boolean tc_ok = typeChecker.visit((SPLParser.Spl_progContext) tree);

        Translator translator = new Translator(symbolTable,tree);
        translator.generateIntermediateCode();

        String expected_code = """
t1 = 10
t2 = 5
t3 = t1+t2
v1 = t3
PRINT v1
STOP
""";
        assertEquals(expected_code, translator.getIntermediateCode());
    }

    @Test
    void CodeGeneration_FunctionCall_Success() {
        String exampleProgram =  """
            glob {
                x
                y
            }
            proc {
                hx() {
                    local{}
                    halt
                }
            }
            func {
                gx(value) {
                    local{}
                    halt;
                    return value
                }
            }
            main {
                var {
                    a
                    b
                }
                x = (10 plus 5);
                print x;
                gx(10);
                halt
            }
            """;

        CharStream input = CharStreams.fromString(exampleProgram);
       
        SPLLexer lexer = new SPLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Step 2: Syntax Analysis (Parsing)
        SPLParser parser = new SPLParser(tokens);
        
        // Error handling for parser
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer,
                                  Object offendingSymbol,
                                  int line, int charPositionInLine,
                                  String msg,
                                  RecognitionException e) {
            }
        });
        
        ParseTree tree = parser.spl_prog();
        
        // Check if parsing was successful
        if (parser.getNumberOfSyntaxErrors() > 0) {
            return;
        }
        
        // Step 3: Semantic Analysis
        ParseTreeWalker walker = new ParseTreeWalker();
        SPLSemanticAnalyzer analyzer = new SPLSemanticAnalyzer();
        
        // Walk the parse tree
        walker.walk(analyzer, tree);
        
        // Get results
        SymbolTable symbolTable = analyzer.getSymbolTable();

        TypeErrorListener typeErrorListener = new ConsoleTypeErrorListener();
        TypeChecker typeChecker = new TypeChecker(typeErrorListener);
        Boolean tc_ok = typeChecker.visit((SPLParser.Spl_progContext) tree);

        Translator translator = new Translator(symbolTable,tree);
        translator.generateIntermediateCode();

        String expected_code = """
t1 = 10
t2 = 5
t3 = t1+t2
v1 = t3
PRINT v1
t4 = 10
CALL gx(t4)
STOP
""";
        assertEquals(expected_code, translator.getIntermediateCode());
    }

    @Test
    void CodeGeneration_FunctionCall_Success2() {
        String exampleProgram =  """
            glob {
                x
                y
            }
            proc {
                hx() {
                    local{}
                    halt
                }
            }
            func {
                gx(value) {
                    local{}
                    halt;
                    return value
                }
            }
            main {
                var {
                    a
                    b
                }
                x = (10 plus 5);
                print x;
                y = gx(10);
                halt
            }
            """;

        CharStream input = CharStreams.fromString(exampleProgram);
       
        SPLLexer lexer = new SPLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Step 2: Syntax Analysis (Parsing)
        SPLParser parser = new SPLParser(tokens);
        
        // Error handling for parser
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer,
                                  Object offendingSymbol,
                                  int line, int charPositionInLine,
                                  String msg,
                                  RecognitionException e) {
            }
        });
        
        ParseTree tree = parser.spl_prog();
        
        // Check if parsing was successful
        if (parser.getNumberOfSyntaxErrors() > 0) {
            return;
        }
        
        // Step 3: Semantic Analysis
        ParseTreeWalker walker = new ParseTreeWalker();
        SPLSemanticAnalyzer analyzer = new SPLSemanticAnalyzer();
        
        // Walk the parse tree
        walker.walk(analyzer, tree);
        
        // Get results
        SymbolTable symbolTable = analyzer.getSymbolTable();

        TypeErrorListener typeErrorListener = new ConsoleTypeErrorListener();
        TypeChecker typeChecker = new TypeChecker(typeErrorListener);
        Boolean tc_ok = typeChecker.visit((SPLParser.Spl_progContext) tree);

        Translator translator = new Translator(symbolTable,tree);
        translator.generateIntermediateCode();

        String expected_code = """
t1 = 10
t2 = 5
t3 = t1+t2
v1 = t3
PRINT v1
t5 = 10
t4 = CALL gx(t5)
v2 = t5
STOP
""";
        assertEquals(expected_code, translator.getIntermediateCode());
    }

    @Test
    void CodeGeneration_If_Success() {
        String exampleProgram =  """
            glob {
                x
                y
            }
            proc {
                hx() {
                    local{}
                    halt
                }
            }
            func {
                gx(value) {
                    local{}
                    halt;
                    return value
                }
            }
            main {
                var {
                    a
                    b
                }
                x = 10;
                print x;
                if (x eq 10) {
                    y = gx(10)
                };
                halt
            }
            """;

        CharStream input = CharStreams.fromString(exampleProgram);
       
        SPLLexer lexer = new SPLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Step 2: Syntax Analysis (Parsing)
        SPLParser parser = new SPLParser(tokens);
        
        // Error handling for parser
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer,
                                  Object offendingSymbol,
                                  int line, int charPositionInLine,
                                  String msg,
                                  RecognitionException e) {
            }
        });
        
        ParseTree tree = parser.spl_prog();
        
        // Check if parsing was successful
        if (parser.getNumberOfSyntaxErrors() > 0) {
            return;
        }
        
        // Step 3: Semantic Analysis
        ParseTreeWalker walker = new ParseTreeWalker();
        SPLSemanticAnalyzer analyzer = new SPLSemanticAnalyzer();
        
        // Walk the parse tree
        walker.walk(analyzer, tree);
        
        // Get results
        SymbolTable symbolTable = analyzer.getSymbolTable();

        TypeErrorListener typeErrorListener = new ConsoleTypeErrorListener();
        TypeChecker typeChecker = new TypeChecker(typeErrorListener);
        Boolean tc_ok = typeChecker.visit((SPLParser.Spl_progContext) tree);

        Translator translator = new Translator(symbolTable,tree);
        translator.generateIntermediateCode();

        String expected_code = """
t1 = 10
v1 = t1
PRINT v1
t2 = v1
t3 = 10
IF t2=t3 THEN L1
GOTO L2
REM L1
t5 = 10
t4 = CALL gx(t5)
v2 = t5
REM L2
STOP
""";
        assertEquals(expected_code, translator.getIntermediateCode());
    }

    @Test
    void CodeGeneration_IfElse_Success() {
        String exampleProgram =  """
            glob {
                x
                y
            }
            proc {
                hx() {
                    local{}
                    halt
                }
            }
            func {
                gx(value) {
                    local{}
                    halt;
                    return value
                }
            }
            main {
                var {
                    a
                    b
                }
                x = 10;
                print x;
                if (x eq 10) {
                    y = gx(10)
                }else{
                    y = gx(20)
                };
                halt
            }
            """;

        CharStream input = CharStreams.fromString(exampleProgram);
       
        SPLLexer lexer = new SPLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Step 2: Syntax Analysis (Parsing)
        SPLParser parser = new SPLParser(tokens);
        
        // Error handling for parser
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer,
                                  Object offendingSymbol,
                                  int line, int charPositionInLine,
                                  String msg,
                                  RecognitionException e) {
            }
        });
        
        ParseTree tree = parser.spl_prog();
        
        // Check if parsing was successful
        if (parser.getNumberOfSyntaxErrors() > 0) {
            return;
        }
        
        // Step 3: Semantic Analysis
        ParseTreeWalker walker = new ParseTreeWalker();
        SPLSemanticAnalyzer analyzer = new SPLSemanticAnalyzer();
        
        // Walk the parse tree
        walker.walk(analyzer, tree);
        
        // Get results
        SymbolTable symbolTable = analyzer.getSymbolTable();

        TypeErrorListener typeErrorListener = new ConsoleTypeErrorListener();
        TypeChecker typeChecker = new TypeChecker(typeErrorListener);
        Boolean tc_ok = typeChecker.visit((SPLParser.Spl_progContext) tree);

        Translator translator = new Translator(symbolTable,tree);
        translator.generateIntermediateCode();

        String expected_code = """
t1 = 10
v1 = t1
PRINT v1
t2 = v1
t3 = 10
IF t2=t3 THEN L1
t5 = 20
t4 = CALL gx(t5)
v2 = t5
GOTO L2
REM L1
t7 = 10
t6 = CALL gx(t7)
v2 = t7
REM L2
STOP
""";
        assertEquals(expected_code, translator.getIntermediateCode());
    }

    @Test
    void CodeGeneration_UnopNot_Success() {
        String exampleProgram =  """
            glob {
                x
                y
            }
            proc {
                hx() {
                    local{}
                    halt
                }
            }
            func {
                gx(value) {
                    local{}
                    halt;
                    return value
                }
            }
            main {
                var {
                    a
                    b
                }
                x = 10;
                print x;
                if (not (x eq 10)) {
                    y = gx(10)
                }else{
                    y = gx(20)
                };
                halt
            }
            """;

        CharStream input = CharStreams.fromString(exampleProgram);
       
        SPLLexer lexer = new SPLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Step 2: Syntax Analysis (Parsing)
        SPLParser parser = new SPLParser(tokens);
        
        // Error handling for parser
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer,
                                  Object offendingSymbol,
                                  int line, int charPositionInLine,
                                  String msg,
                                  RecognitionException e) {
            }
        });
        
        ParseTree tree = parser.spl_prog();
        
        // Check if parsing was successful
        if (parser.getNumberOfSyntaxErrors() > 0) {
            return;
        }
        
        // Step 3: Semantic Analysis
        ParseTreeWalker walker = new ParseTreeWalker();
        SPLSemanticAnalyzer analyzer = new SPLSemanticAnalyzer();
        
        // Walk the parse tree
        walker.walk(analyzer, tree);
        
        // Get results
        SymbolTable symbolTable = analyzer.getSymbolTable();

        TypeErrorListener typeErrorListener = new ConsoleTypeErrorListener();
        TypeChecker typeChecker = new TypeChecker(typeErrorListener);
        Boolean tc_ok = typeChecker.visit((SPLParser.Spl_progContext) tree);

        Translator translator = new Translator(symbolTable,tree);
        translator.generateIntermediateCode();

        String expected_code = """
t1 = 10
v1 = t1
PRINT v1
t2 = v1
t3 = 10
IF t2=t3 THEN L1
t5 = 10
t4 = CALL gx(t5)
v2 = t5
GOTO L2
REM L1
t7 = 20
t6 = CALL gx(t7)
v2 = t7
REM L2
STOP
""";
        assertEquals(expected_code, translator.getIntermediateCode());
    }

    @Test
    void CodeGeneration_BinopAnd_Success() {
        String exampleProgram =  """
            glob {
                x
                y
            }
            proc {
                hx() {
                    local{}
                    halt
                }
            }
            func {
                gx(value) {
                    local{}
                    halt;
                    return value
                }
            }
            main {
                var {
                    a
                    b
                }
                x = 10;
                print x;
                a = 2;
                if ((x eq 10) and (a eq 2)) {
                    y = gx(10)
                }else{
                    y = gx(20)
                };
                halt
            }
            """;

        CharStream input = CharStreams.fromString(exampleProgram);
       
        SPLLexer lexer = new SPLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Step 2: Syntax Analysis (Parsing)
        SPLParser parser = new SPLParser(tokens);
        
        // Error handling for parser
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer,
                                  Object offendingSymbol,
                                  int line, int charPositionInLine,
                                  String msg,
                                  RecognitionException e) {
            }
        });
        
        ParseTree tree = parser.spl_prog();
        
        // Check if parsing was successful
        if (parser.getNumberOfSyntaxErrors() > 0) {
            return;
        }
        
        // Step 3: Semantic Analysis
        ParseTreeWalker walker = new ParseTreeWalker();
        SPLSemanticAnalyzer analyzer = new SPLSemanticAnalyzer();
        
        // Walk the parse tree
        walker.walk(analyzer, tree);
        
        // Get results
        SymbolTable symbolTable = analyzer.getSymbolTable();

        TypeErrorListener typeErrorListener = new ConsoleTypeErrorListener();
        TypeChecker typeChecker = new TypeChecker(typeErrorListener);
        Boolean tc_ok = typeChecker.visit((SPLParser.Spl_progContext) tree);

        Translator translator = new Translator(symbolTable,tree);
        translator.generateIntermediateCode();

        String expected_code = """
t1 = 10
v1 = t1
PRINT v1
t2 = 2
v7 = t2
t3 = v1
t4 = 10
t5 = v7
t6 = 2
IF t3=t4 THEN L1
t8 = 20
t7 = CALL gx(t8)
v2 = t8
GOTO L2
REM L1
IF t5=t6 THEN L2
t10 = 20
t9 = CALL gx(t10)
v2 = t10
GOTO L2
REM L2
t12 = 10
t11 = CALL gx(t12)
v2 = t12
REM L3
STOP
""";
        assertEquals(expected_code, translator.getIntermediateCode());
    }

}
