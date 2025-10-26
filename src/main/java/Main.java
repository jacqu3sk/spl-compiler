import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import generated.SPLLexer;
import generated.SPLParser;
import symbolTable.*;
import translator.Translator;
import typeChecker.*;
import basic.BasicCodeGenerator;

public class Main {
    public static void main(String[] args) {
        try {
            // Check if input file is provided
            if (args.length < 1) {
                System.out.println("\nRunning with example input instead...\n");
                runWithExampleInput();
                return;
            }
            
            // Check for --basic-only flag
            boolean basicOnly = false;
            String inputFile = args[0];
            
            if (args.length > 1 && args[1].equals("--basic-only")) {
                basicOnly = true;
            } else if (args[0].equals("--basic-only") && args.length > 1) {
                basicOnly = true;
                inputFile = args[1];
            }
            
            // Read input from file
            CharStream input = CharStreams.fromFileName(inputFile);
            
            // Run the compiler
            compile(input, inputFile, basicOnly);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Compile the SPL program
     */
    public static void compile(CharStream input, String sourceName, boolean basicOnly) {
        if (!basicOnly) {
            System.out.println("=================================================");
            System.out.println("SPL COMPILER - Semantic Analysis Phase");
            System.out.println("=================================================");
            System.out.println("Source: " + sourceName);
            System.out.println("=================================================\n");
        }
        
        // Step 1: Lexical Analysis
        if (!basicOnly) System.out.println("Phase 1: Lexical Analysis...");
        SPLLexer lexer = new SPLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Step 2: Syntax Analysis (Parsing)
        if (!basicOnly) System.out.println("Phase 2: Syntax Analysis...");
        SPLParser parser = new SPLParser(tokens);
        
        // Error handling for parser
        parser.removeErrorListeners();
        final boolean finalBasicOnly = basicOnly;
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer,
                                  Object offendingSymbol,
                                  int line, int charPositionInLine,
                                  String msg,
                                  RecognitionException e) {
                if (!finalBasicOnly) {
                    System.err.println("Syntax Error at line " + line + ":" + 
                                     charPositionInLine + " - " + msg);
                }
            }
        });
        
        ParseTree tree = parser.spl_prog();
        
        // Check if parsing was successful
        if (parser.getNumberOfSyntaxErrors() > 0) {
            if (!basicOnly) System.out.println("\n✗ Compilation failed due to syntax errors.");
            return;
        }
        
        if (!basicOnly) System.out.println("✓ Parsing successful");
        
        // Step 3: Semantic Analysis
        if (!basicOnly) System.out.println("\nPhase 3: Semantic Analysis...");
        ParseTreeWalker walker = new ParseTreeWalker();
        SPLSemanticAnalyzer analyzer = new SPLSemanticAnalyzer();
        
        // Walk the parse tree
        walker.walk(analyzer, tree);
        
        // Get results
        SymbolTable symbolTable = analyzer.getSymbolTable();
        
        // Step 4: Report Results
        if (!basicOnly) {
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
        }
        
        // Exit early if there are errors
        if (symbolTable.hasErrors()) {
            return;
        }

        // Step 5: Type Checking
        if (!basicOnly) {
            System.out.println("\n=================================================");
            System.out.println("Phase 4: Type Checking...");
            System.out.println("=================================================");
        }
        
        TypeErrorListener typeErrorListener = basicOnly ? new SilentTypeErrorListener() : new ConsoleTypeErrorListener();
        TypeChecker typeChecker = new TypeChecker(symbolTable, typeErrorListener);
        Boolean tc_ok = typeChecker.visit((SPLParser.Spl_progContext) tree);
        if (tc_ok) {
            if (!basicOnly) System.out.println("✓ Type Checking Completed Successfully!");
        }
        else {
            if (!basicOnly) System.out.println("✗ Type Checking failed");
            return;
        }

        // Step 6: Code Generation (Intermediate Code)
        if (!basicOnly) {
            System.out.println("\n=================================================");
            System.out.println("Phase 5: Intermediate Code Generation...");
            System.out.println("=================================================");
        }
        
        Translator translator = new Translator(symbolTable,tree);
        translator.generateIntermediateCode();
        if (!basicOnly) translator.printIntermediateCode();
        
        // Step 7: BASIC Code Generation
        if (!basicOnly) {
            System.out.println("\n=================================================");
            System.out.println("Phase 6: BASIC Code Generation...");
            System.out.println("=================================================");
        }
        
        BasicCodeGenerator basicGenerator = new BasicCodeGenerator(translator.getIntermediateCode());
        String basicCode = basicGenerator.generateBasicCode();
        
        if (basicOnly) {
            // Only print the BASIC code, nothing else
            System.out.print(basicCode);
        } else {
            basicGenerator.printBasicCode();
            System.out.println("\n=================================================");
            System.out.println("COMPILATION COMPLETE!");
            System.out.println("=================================================\n");
        }
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
                    local{
                        newvalue
                    }
                    newvalue = (value plus 1);
                    return newvalue
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
                    y = gx(25)
                };
                print y;
                halt
            }
            """;
        
        System.out.println("Example SPL Program:");
        System.out.println("-------------------");
        System.out.println(exampleProgram);
        System.out.println("-------------------\n");
        
        CharStream input = CharStreams.fromString(exampleProgram);
        compile(input, "example.spl", false);
    }
    
    /**
     * Silent type error listener for --basic-only mode
     */
    static class SilentTypeErrorListener implements TypeErrorListener {
        @Override
        public void onTypeError(ParserRuleContext ctx, String message) {
            // Silently ignore errors in basic-only mode
        }
    }
}