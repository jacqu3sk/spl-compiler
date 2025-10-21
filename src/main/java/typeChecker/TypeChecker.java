package typeChecker;

import java.util.ListIterator;

import org.antlr.v4.runtime.ParserRuleContext;

import generated.SPLBaseVisitor;
import generated.SPLParser;

public class TypeChecker extends SPLBaseVisitor<Boolean> {

    private final TypeGetter tg = new TypeGetter();
    private final TypeErrorListener errorListener;

    public TypeChecker(TypeErrorListener listener) {
        this.errorListener = listener;
    }

    private void error(ParserRuleContext ctx, String message) {
        if (errorListener != null) {
            errorListener.onTypeError(ctx, message);
        }
    }

    @Override
    public Boolean visitSpl_prog(SPLParser.Spl_progContext ctx) {
        return (visit(ctx.variables()) && visit(ctx.procdefs()) && visit(ctx.funcdefs()) && visit(ctx.mainprog()));
    }

    @Override
    public Boolean visitVariables(SPLParser.VariablesContext ctx) {
        if (ctx.var() == null) {
            return true;
        }

        return (tg.visitVar(ctx.var()).equals("numeric") && visit(ctx.variables()));
    }

    @Override
    public Boolean visitProcdefs(SPLParser.ProcdefsContext ctx) {
        if (ctx.pdef() == null) {
            return true;
        }
        return (visit(ctx.pdef()) && visit(ctx.procdefs()));
    }

    @Override
    public Boolean visitPdef(SPLParser.PdefContext ctx) {
        return (visit(ctx.param()) && visit(ctx.body()));
    }

    @Override
    public Boolean visitFdef(SPLParser.FdefContext ctx) {
        return (tg.visit(ctx.atom()).equals("numeric") && visit(ctx.param()) && visit(ctx.body()));
    }

    @Override
    public Boolean visitFuncdefs(SPLParser.FuncdefsContext ctx) {
        if (ctx.fdef() == null) {
            return true;
        }

        return (visit(ctx.fdef()) && visit(ctx.funcdefs()));
    }

    @Override
    public Boolean visitBody(SPLParser.BodyContext ctx) {
        return (visit(ctx.maxthree()) && visit(ctx.algo()));
    }

    @Override
    public Boolean visitParam(SPLParser.ParamContext ctx) {
        return visit(ctx.maxthree());
    }

    @Override
    public Boolean visitMaxthree(SPLParser.MaxthreeContext ctx) {
        if (ctx.var().isEmpty()) {
            return true;
        }
        if (ctx.var().size() > 3) {
            return false;
        }

        ListIterator<SPLParser.VarContext> it = ctx.var().listIterator();
        while (it.hasNext()) {
            if (!tg.visit(it.next()).equals("numeric")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean visitMainprog(SPLParser.MainprogContext ctx) {
        switch (ctx.variables()) {
            case null:
                return visit(ctx.algo());
            default:
                return (visit(ctx.algo()) && visit(ctx.variables()));
        }
    }

    @Override
    public Boolean visitAlgo(SPLParser.AlgoContext ctx) {
        Boolean instr_t = visit(ctx.instr());
        if (ctx.algo() == null) {
            return instr_t;
        }
        else {
            return (visit(ctx.algo()) && instr_t);
        }
    }

    @Override
    public Boolean visitInstr(SPLParser.InstrContext ctx) {
        if (ctx.getText().equals("halt")) {
            return true;
        }

        if (ctx.output() != null) {
            return visit(ctx.output());
        }

        if (ctx.input() != null) {
            return visit(ctx.input());
        }

        if (ctx.assign() != null) {
            return visit(ctx.assign());
        }

        if (ctx.loop() != null) {
            return visit(ctx.loop());
        }

        if (ctx.branch() != null) {
            return visit(ctx.branch());
        }
        
        return false;
    }

    @Override
    public Boolean visitAssign(SPLParser.AssignContext ctx) {
        if (ctx.NAME() != null) {
            Boolean input_t = (ctx.input() == null) || visit(ctx.input());
            Boolean var_t = tg.visit(ctx.var()).equals("numeric");
            return input_t && var_t;
        }

        if (ctx.term() != null) {
            return (tg.visit(ctx.term()).equals("numeric") && tg.visit(ctx.var()).equals("numeric"));
        }
        
        return false;

    }

    @Override
    public Boolean visitLoop(SPLParser.LoopContext ctx) {
        Boolean term_t = tg.visit(ctx.term()).equals("boolean");
        if (!term_t) {
            error(ctx, "Condition in loop must be boolean");
            return false;
        }
        return (visit(ctx.algo()));
    }

    @Override
    public Boolean visitBranch(SPLParser.BranchContext ctx) {
        if (!tg.visit(ctx.term()).equals("boolean")) {
            error(ctx, "Condition in branch must be boolean");
            return false;
        }
        ListIterator<SPLParser.AlgoContext> it = ctx.algo().listIterator();
        while (it.hasNext()) {
            if (!visit(it.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean visitOutput(SPLParser.OutputContext ctx) {
        if (ctx.STRING() != null) {
            return true;
        }

        return tg.visit(ctx.atom()).equals("numeric");
    }

    @Override
    public Boolean visitInput(SPLParser.InputContext ctx) {
        if (ctx.atom().isEmpty()) {
            return true;
        }
        ListIterator<SPLParser.AtomContext> it = ctx.atom().listIterator();
        while (it.hasNext()) {
            if (!tg.visit(it.next()).equals("numeric")) {
                return false;
            }
        }
        return true;
    }

   
    
}
