import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import generated.SPLLexer;
import generated.SPLParser;
import symbolTable.*;
import translator.Translator;
import typeChecker.*;
import basic.BasicCodeGenerator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Check if input file is provided
            if (args.length < 1) {
                System.out.println("\nNo input file specified :(");
                System.out.println("Running with example input instead...\n");
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
            System.out.println("Source: " + sourceName);
            System.out.println("=================================================\n");
        }
        System.out.println("=================================================");
        System.out.println("SPL COMPILER: Front-End Phase");
        System.out.println("=================================================\n");
        
        // Step 1: Lexical Analysis
        if (!basicOnly) System.out.println("Phase 1:(Lexer) Lexical Analysis...");
        SPLLexer lexer = new SPLLexer(input);
        lexer.removeErrorListeners();
        final boolean finalBasicOnly = basicOnly;
        final boolean[] hasLexicalError = {false};
        lexer.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer,
                                  Object offendingSymbol,
                                  int line, int charPositionInLine,
                                  String msg,
                                  RecognitionException e) {
                if (!finalBasicOnly) {
                    System.err.println("Lexical Error: at line " + line + ":" + 
                                     charPositionInLine + " -> " + msg);
                    hasLexicalError[0] = true;
                }
            }
        });
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();
        if (!hasLexicalError[0])
        {
            System.out.println("Tokens accepted");
        }else{
            return;
        }
        
        // Step 2: Syntax Analysis (Parsing)
        if (!basicOnly) System.out.println("\nPhase 2:(Parser) Syntax Analysis...");
        SPLParser parser = new SPLParser(tokens);
        // Error handling for parser
        parser.removeErrorListeners();
        List<String> syntaxErrors = new ArrayList<>();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer,
                                  Object offendingSymbol,
                                  int line, int charPositionInLine,
                                  String msg,
                                  RecognitionException e) {
                syntaxErrors.add("Line " + line + ":" + charPositionInLine + " -> " + msg);
            }
        }); 
        ParseTree tree = parser.spl_prog();
        
        // Check if parsing was successful
        if (parser.getNumberOfSyntaxErrors() > 0) {
            if (!basicOnly) System.out.println("Syntax error: ");
            syntaxErrors.forEach(System.err::println);
            return;
        }
        
        if (!basicOnly) System.out.println("Syntax accepted");
        
        System.out.println("\n=================================================");
        System.out.println("Semantic Analysis Phase");
        System.out.println("=================================================\n");
        
        // Step 3: Semantic Analysis
        if (!basicOnly) System.out.println("Phase 3: Semantic Analysis (Name-Scope Analyser)...");
        ParseTreeWalker walker = new ParseTreeWalker();
        SPLSemanticAnalyzer analyzer = new SPLSemanticAnalyzer();
        
        // Walk the parse tree
        walker.walk(analyzer, tree);
        
        // Get results
        SymbolTable symbolTable = analyzer.getSymbolTable();
        
        // Step 4: Report Results
        if (!basicOnly) {
            if (symbolTable.hasErrors()) {
                System.out.println("Type error:");
                int errorNum = 1;
                for (String error : symbolTable.getErrors()) {
                    System.out.println(errorNum + ". " + error);
                    errorNum++;
                }
                System.out.println("Total errors: " + symbolTable.getErrors().size() + "\n");
            } else {
                System.out.println("Variable Naming and Function Naming accepted");
            }
        }
        
        // Exit early if there are errors
        if (symbolTable.hasErrors()) {
            return;
        }
    
        // Step 5: Type Checking
        if (!basicOnly) System.out.println("\nPhase 4: Semantic Analysis (Type-Checker)...");
        
        TypeErrorListener typeErrorListener = basicOnly ? new SilentTypeErrorListener() : new ConsoleTypeErrorListener();
        TypeChecker typeChecker = new TypeChecker(symbolTable, typeErrorListener);
        Boolean tc_ok = typeChecker.visit((SPLParser.Spl_progContext) tree);
        if (tc_ok) {
            if (!basicOnly) System.out.println("Types accepted");
        }
        else {
            return;
        }

        // Step 6: Code Generation (Intermediate Code)
        if (!basicOnly) {
            System.out.println("\n=================================================");
            System.out.println(" Intermediate Code Generation Phase");
            System.out.println("=================================================");
        }
        if (!basicOnly) System.out.println("\nPhase 5: Intermediate Code Generation...");
        
        Translator translator = new Translator(symbolTable,tree);
        translator.generateIntermediateCode();
        //if (!basicOnly) translator.printIntermediateCode();
        
        // Step 7: BASIC Code Generation
        if (!basicOnly) {
            System.out.println("\n=================================================");
            System.out.println(" BASIC Code Generation Phase...");
            System.out.println("=================================================");
        }
        if (!basicOnly) System.out.println("\nPhase 6: Executable BASIC Code...");
        
        BasicCodeGenerator basicGenerator = new BasicCodeGenerator(translator.getIntermediateCode());
        String basicCode = basicGenerator.generateBasicCode();
        
        if (basicOnly) {
            // Only print the BASIC code, nothing else
            System.out.print(basicCode);
            try {
                Files.writeString(Path.of("output.txt"), basicCode);
                System.out.println("output.txt created: " + Path.of("output.txt"));
            } catch (Exception e) {
                System.err.println("Error writing BASIC code to output.txt: " + e.getMessage());
            }
        } else {
            //System.out.print(basicCode);
            try {
                Files.writeString(Path.of("output.txt"), basicCode);
                System.out.println("output.txt created ");
            } catch (Exception e) {
                System.err.println("Error writing BASIC code to output.txt: " + e.getMessage());
            }

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