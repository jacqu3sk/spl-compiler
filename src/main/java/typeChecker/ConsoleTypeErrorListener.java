package typeChecker;

import org.antlr.v4.runtime.ParserRuleContext;

public class ConsoleTypeErrorListener implements TypeErrorListener {
    @Override
    public void onTypeError(ParserRuleContext ctx, String message) {
        System.out.printf("Type Error: at line %d:%d - %s%n",
                ctx.start.getLine(),
                ctx.start.getCharPositionInLine(),
                message);
    }

}
