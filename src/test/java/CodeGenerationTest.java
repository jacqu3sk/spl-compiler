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

}
