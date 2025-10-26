package typeChecker;

import generated.*;

public class TypeGetter extends SPLBaseVisitor<String> {
    
    @Override
    public String visitVar(SPLParser.VarContext ctx) {
        return "numeric";
    }

    @Override
    public String visitAtom(SPLParser.AtomContext ctx) {
        if (ctx.var() == null && ctx.NUMBER() == null) {
            return "unknown";
        }
        return "numeric";
    }

    @Override
    public String visitTerm(SPLParser.TermContext ctx) {
        if (ctx.atom() != null) {
            return "numeric";
        }

        if (ctx.unop() != null) {
            switch (visit(ctx.unop())) {
                case "numeric":
                    return ((visit(ctx.term(0)).equals("numeric")) ? "numeric" : "unknown");
                case "boolean":
                    return ((visit(ctx.term(0)).equals("boolean")) ? "boolean" : "unknown");
                default: 
                    return "unknown";
            }
        }

        if (ctx.binop() != null) {
            switch (visit(ctx.binop())) {
                case "numeric":
                    return ((visit(ctx.term(0)).equals("numeric") && visit(ctx.term(1)).equals("numeric")) ? "numeric" : "unknown");
                case "boolean":
                    return ((visit(ctx.term(0)).equals("boolean") && visit(ctx.term(1)).equals("boolean")) ? "boolean" : "unknown");
                case "comparison":
                    return ((visit(ctx.term(0)).equals("numeric") && visit(ctx.term(1)).equals("numeric")) ? "boolean" : "unknown");
                default: return "unknown";
            }
        }

        return "unknown";
    }

    @Override
    public String visitUnop(SPLParser.UnopContext ctx) {
        if (ctx.getText().equals("neg")) {
            return "numeric";
        }
        else if (ctx.getText().equals("not")){
            return "boolean";
        }
        return "unknown";
    }

    @Override
    public String visitBinop(SPLParser.BinopContext ctx) {
        String op = ctx.getText();
        if (op.equals("or") || op.equals("and")) {
            return "boolean";
        }
        else if (op.equals("plus") || op.equals("minus") || op.equals("mult") || op.equals("div")) {
            return "numeric";
        }
        else {
            return "comparison";
        }
    }
        
}
