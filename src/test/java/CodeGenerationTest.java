import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import generated.SPLLexer;
import generated.SPLParser;
import symbolTable.SPLSemanticAnalyzer;
import symbolTable.SymbolTable;
import translator.Translator;
import typeChecker.ConsoleTypeErrorListener;
import typeChecker.TypeChecker;
import typeChecker.TypeErrorListener;

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
        TypeChecker typeChecker = new TypeChecker(symbolTable, typeErrorListener);
        typeChecker.visit((SPLParser.Spl_progContext) tree);

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
                x = gx(10);
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
        TypeChecker typeChecker = new TypeChecker(symbolTable, typeErrorListener);
        typeChecker.visit((SPLParser.Spl_progContext) tree);

        Translator translator = new Translator(symbolTable,tree);
        translator.generateIntermediateCode();

        String expected_code = """
t1 = 10
t2 = 5
t3 = t1+t2
v1 = t3
PRINT v1
t4 = 10
v5 = t4
STOP
t5 = v5
v1 = t5
STOP
""";
        assertEquals(expected_code, translator.getIntermediateCode());
    }

    @Test
    void CodeGeneration_ProcCall_Success() {
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
                hx();
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
        TypeChecker typeChecker = new TypeChecker(symbolTable, typeErrorListener);
        typeChecker.visit((SPLParser.Spl_progContext) tree);

        Translator translator = new Translator(symbolTable,tree);
        translator.generateIntermediateCode();

        String expected_code = """
t1 = 10
t2 = 5
t3 = t1+t2
v1 = t3
PRINT v1
STOP
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
        TypeChecker typeChecker = new TypeChecker(symbolTable, typeErrorListener);
        typeChecker.visit((SPLParser.Spl_progContext) tree);

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
t4 = 10
v5 = t4
STOP
t5 = v5
v2 = t5
GOTO L3
REM L2
REM L3
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
        TypeChecker typeChecker = new TypeChecker(symbolTable, typeErrorListener);
        typeChecker.visit((SPLParser.Spl_progContext) tree);

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
t4 = 10
v5 = t4
STOP
t6 = v5
v2 = t6
GOTO L3
REM L2
t5 = 20
v5 = t5
STOP
t7 = v5
v2 = t7
REM L3
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
        TypeChecker typeChecker = new TypeChecker(symbolTable, typeErrorListener);
        typeChecker.visit((SPLParser.Spl_progContext) tree);

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
t4 = 20
v5 = t4
STOP
t6 = v5
v2 = t6
GOTO L3
REM L2
t5 = 10
v5 = t5
STOP
t7 = v5
v2 = t7
REM L3
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
        TypeChecker typeChecker = new TypeChecker(symbolTable, typeErrorListener);
        typeChecker.visit((SPLParser.Spl_progContext) tree);

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
IF t3=t4 THEN L4
GOTO L2
REM L4
IF t5=t6 THEN L1
GOTO L2
REM L1
t7 = 10
v5 = t7
STOP
t9 = v5
v2 = t9
GOTO L3
REM L2
t8 = 20
v5 = t8
STOP
t10 = v5
v2 = t10
REM L3
STOP
""";
        assertEquals(expected_code, translator.getIntermediateCode());
    }

    @Test
    void CodeGeneration_BinopOr_Success() {
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
                if ((x eq 10) or (a eq 2)) {
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
        TypeChecker typeChecker = new TypeChecker(symbolTable, typeErrorListener);
        typeChecker.visit((SPLParser.Spl_progContext) tree);

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
IF t5=t6 THEN L1
GOTO L2
REM L1
t7 = 10
v5 = t7
STOP
t9 = v5
v2 = t9
GOTO L3
REM L2
t8 = 20
v5 = t8
STOP
t10 = v5
v2 = t10
REM L3
STOP
""";
        assertEquals(expected_code, translator.getIntermediateCode());
    }

    @Test
    void CodeGeneration_WhileLoop_Success() {
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
                x = 5;
                print x;
                a = 10;
                while (a > 5) {
                    a = (a minus 1)
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
        TypeChecker typeChecker = new TypeChecker(symbolTable, typeErrorListener);
        typeChecker.visit((SPLParser.Spl_progContext) tree);

        Translator translator = new Translator(symbolTable,tree);
        translator.generateIntermediateCode();

        String expected_code = """
t1 = 5
v1 = t1
PRINT v1
t2 = 10
v7 = t2
REM L1
t3 = v7
t4 = 5
IF t3>t4 THEN L2
GOTO L3
REM L2
t5 = v7
t6 = 1
t7 = t5-t6
v7 = t7
GOTO L1
REM L3
STOP
""";
        assertEquals(expected_code, translator.getIntermediateCode());
    }

    @Test
    void CodeGeneration_DoUntilLoop_Success() {
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
                x = 5;
                print x;
                a = 10;
                do {
                    a = (a minus 1)
                } until ( a eq 0);
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
        TypeChecker typeChecker = new TypeChecker(symbolTable, typeErrorListener);
        typeChecker.visit((SPLParser.Spl_progContext) tree);

        Translator translator = new Translator(symbolTable,tree);
        translator.generateIntermediateCode();

        String expected_code = """
t1 = 5
v1 = t1
PRINT v1
t2 = 10
v7 = t2
REM L1
t3 = v7
t4 = 1
t5 = t3-t4
v7 = t5
t6 = v7
t7 = 0
IF t6=t7 THEN L2
GOTO L1
REM L2
STOP
""";
        assertEquals(expected_code, translator.getIntermediateCode());
    }

}
