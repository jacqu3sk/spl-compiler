import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // Example SPL program
        String input = """
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
        
        CharStream charStream = CharStreams.fromString(input);
        SPLLexer lexer = new SPLLexer(charStream);
        
        // Create custom error listener
        SyntaxErrorListener errorListener = new SyntaxErrorListener();
        
        // Remove default error listeners and add our custom one
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SPLParser parser = new SPLParser(tokens);
        
        // Add error listener to parser as well
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        ParseTree tree = parser.spl_prog(); // Start parsing from the 'spl_prog' rule

        // Check if there were any syntax errors
        if (errorListener.hasErrors()) {
            System.err.println("\nParsing failed due to syntax errors.");
            return;
        }

        System.out.println("Parsing completed successfully!\n");

        // Print the parse tree
        System.out.println("Parse Tree:");
        System.out.println(tree.toStringTree(parser));
        
        // You can then traverse the parse tree using a Listener
        // Example with a Listener:
        ParseTreeWalker walker = new ParseTreeWalker();
        
        // Create and use the symbol table builder
        SPLSymbolTableBuilder symbolTableBuilder = new SPLSymbolTableBuilder();
        walker.walk(symbolTableBuilder, tree);
        
        // Print symbol table results
        symbolTableBuilder.printSymbolTable();
        
        // Check for semantic errors
        if (symbolTableBuilder.hasErrors()) {
            symbolTableBuilder.printErrors();
        } else {
            System.out.println("No semantic errors found!");
        }
        
        // Original listener example
        SPLBaseListener listener = new SPLBaseListener() {
            @Override
            public void enterSpl_prog(SPLParser.Spl_progContext ctx) {
                System.out.println("Entering SPL program");
            }
            
            @Override
            public void exitSpl_prog(SPLParser.Spl_progContext ctx) {
                System.out.println("Exiting SPL program");
            }

            
        };
        walker.walk(listener, tree);
    }
}