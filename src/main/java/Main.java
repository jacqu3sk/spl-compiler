import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

class SyntaxErrorListener extends BaseErrorListener {
    private boolean hasErrors = false;
    
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                           int line, int charPositionInLine, String msg,
                           RecognitionException e) {
        hasErrors = true;
        System.err.println("Syntax Error at line " + line + ", position " + charPositionInLine + ": " + msg);
        
        if (offendingSymbol instanceof Token) {
            Token token = (Token) offendingSymbol;
            System.err.println("Offending token: '" + token.getText() + "'");
        }
    }
    
    public boolean hasErrors() {
        return hasErrors;
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        // Example SPL program
        String input = """
            glob {
                x
                y
            }
            proc {
            }
            func {
            }
            main {
                var {
                    x
                    y
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