package parser;

import org.antlr.v4.runtime.*;

public class SyntaxErrorListener extends BaseErrorListener {
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