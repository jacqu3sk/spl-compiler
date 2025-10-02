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
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SPLParser parser = new SPLParser(tokens);

        ParseTree tree = parser.spl_prog(); // Start parsing from the 'spl_prog' rule

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