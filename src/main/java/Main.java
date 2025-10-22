import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import generated.SPLLexer;
import generated.SPLParser;
import symbolTable.*;
import translator.Translator;
import typeChecker.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Check if input file is provided
            if (args.length < 1) {
                System.out.println("\nRunning with example input instead...\n");
                runWithExampleInput();
                return;
            }
            
            // Read input from file
            String inputFile = args[0];
            CharStream input = CharStreams.fromFileName(inputFile);
            
            // Run the compiler
            compile(input, inputFile);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Compile the SPL program
     */
    public static void compile(CharStream input, String sourceName) {
        System.out.println("=================================================");
        System.out.println("SPL COMPILER - Semantic Analysis Phase");
        System.out.println("=================================================");
        System.out.println("Source: " + sourceName);
        System.out.println("=================================================\n");
        
        // Step 1: Lexical Analysis
        System.out.println("Phase 1: Lexical Analysis...");
        SPLLexer lexer = new SPLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Step 2: Syntax Analysis (Parsing)
        System.out.println("Phase 2: Syntax Analysis...");
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
                System.err.println("Syntax Error at line " + line + ":" + 
                                 charPositionInLine + " - " + msg);
            }
        });
        
        ParseTree tree = parser.spl_prog();
        
        // Check if parsing was successful
        if (parser.getNumberOfSyntaxErrors() > 0) {
            System.out.println("\n✗ Compilation failed due to syntax errors.");
            return;
        }
        
        System.out.println("✓ Parsing successful");
        
        // Step 3: Semantic Analysis
        System.out.println("\nPhase 3: Semantic Analysis...");
        ParseTreeWalker walker = new ParseTreeWalker();
        SPLSemanticAnalyzer analyzer = new SPLSemanticAnalyzer();
        
        // Walk the parse tree
        walker.walk(analyzer, tree);
        
        // Get results
        SymbolTable symbolTable = analyzer.getSymbolTable();
        
        // Step 4: Report Results
        System.out.println("\n=================================================");
        System.out.println("SEMANTIC ANALYSIS RESULTS");
        System.out.println("=================================================");
        
        if (symbolTable.hasErrors()) {
            System.out.println("\n✗ Semantic errors found:\n");
            int errorNum = 1;
            for (String error : symbolTable.getErrors()) {
                System.out.println(errorNum + ". " + error);
                errorNum++;
            }
            System.out.println("\nTotal errors: " + symbolTable.getErrors().size());
        } else {
            System.out.println("\n✓ No semantic errors found!");
            System.out.println("✓ All name-scope rules are satisfied.");
        }
        
        // Print symbol table
        symbolTable.printTable();
        
        System.out.println("\n=================================================");
        if (!symbolTable.hasErrors()) {
            System.out.println("Compilation Status: SUCCESS");
            System.out.println("Ready to proceed to next phase (code generation)");
        } else {
            System.out.println("Compilation Status: FAILED");
            System.out.println("Fix semantic errors before proceeding");
        }
        System.out.println("=================================================\n");

        TypeErrorListener typeErrorListener = new ConsoleTypeErrorListener();
        TypeChecker typeChecker = new TypeChecker(typeErrorListener);
        Boolean tc_ok = typeChecker.visit((SPLParser.Spl_progContext) tree);
        if (tc_ok) {
            System.out.println("Type Checking Completed Successfully!");
        }
        else {
            System.out.println("Type Checking failed");
        }

        Translator translator = new Translator(symbolTable,tree);
        translator.generateIntermediateCode();
    }
    
    /**
     * Run with example SPL program for demonstration
     */
    public static void runWithExampleInput() {
        // Example SPL program with various semantic scenarios
        String exampleProgram = 
            """
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
        
        System.out.println("Example SPL Program:");
        System.out.println("-------------------");
        System.out.println(exampleProgram);
        System.out.println("-------------------\n");
        
        CharStream input = CharStreams.fromString(exampleProgram);
        compile(input, "example.spl");
    }
}