package typeChecker;

import org.antlr.v4.runtime.ParserRuleContext;

public interface TypeErrorListener {

    void onTypeError(ParserRuleContext ctx, String message);

}
