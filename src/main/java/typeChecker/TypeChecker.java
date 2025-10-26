package typeChecker;

import java.util.ListIterator;

import org.antlr.v4.runtime.ParserRuleContext;

import generated.SPLBaseVisitor;
import generated.SPLParser;
import symbolTable.ScopeType;
import symbolTable.SymbolEntry;
import symbolTable.SymbolTable;

public class TypeChecker extends SPLBaseVisitor<Boolean> {

    private final TypeGetter tg = new TypeGetter();
    private final TypeErrorListener errorListener;
    private final SymbolTable st;

    public TypeChecker(SymbolTable st, TypeErrorListener listener) {
        this.errorListener = listener;
        this.st = st;
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
        int numParams = ctx.param().maxthree().var().size();
        String name = ctx.NAME().getText();
        int checkParams = st.lookupProcedure(name, "na", ScopeType.EVERYWHERE).getNumParams();
        if (numParams != checkParams) {
            error(ctx, "Procedure " + name + " called with incorrect number of parameters. Expected " + checkParams + " but found " + numParams);
            return false;
        }
        return (visit(ctx.param()) && visit(ctx.body()));
    }

    @Override
    public Boolean visitFdef(SPLParser.FdefContext ctx) {
        int numParams = ctx.param().maxthree().var().size();
        String name = ctx.NAME().getText();
        int checkParams = st.lookupFunction(name, "na", ScopeType.EVERYWHERE).getNumParams();
        if (numParams != checkParams) {
            error(ctx, "Function " + name + " called with incorrect number of parameters. Expected " + checkParams + " but found " + numParams);
            return false;
        }
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
            error(ctx, "A function/procedure can only have a maximum of three parameters");
            return false;
        }

        ListIterator<SPLParser.VarContext> it = ctx.var().listIterator();
        while (it.hasNext()) {
            SPLParser.VarContext varCtx = it.next();
            if (!tg.visit(varCtx).equals("numeric")) {
                error(ctx, "Function/procedure parameters must be of type numeric");
                return false;
            }
            Boolean def_var = (st.lookupAnyVariable(varCtx.getText()) != null);
            if (!def_var) {
                error(ctx, "Undefined variable: " + varCtx.getText());
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
        } else {
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
        error(ctx, "Unknown instruction");
        return false;
    }

    @Override
    public Boolean visitAssign(SPLParser.AssignContext ctx) {
        if (ctx.NAME() != null) {
            int numParams = 0;
            if (ctx.input() != null) {
                numParams = ctx.input().atom().size();
            }
            SymbolEntry entry = st.lookupProcedure(ctx.NAME().getText(), "na", ScopeType.EVERYWHERE);
            entry = (entry != null) ? entry : st.lookupFunction(ctx.NAME().getText(), "na", ScopeType.EVERYWHERE);
            if (entry == null) {
                error(ctx, "Undefined procedure/function: " + ctx.NAME().getText());
                return false;
            }
            if (numParams != entry.getNumParams()) {
                error(ctx, "Procedure/function " + ctx.NAME().getText() + " called with incorrect number of parameters. Expected " + entry.getNumParams() + " but found " + numParams);
                return false;
            }
            Boolean input_t = (ctx.input() == null) || visit(ctx.input());
            Boolean var_t = tg.visit(ctx.var()).equals("numeric");
            Boolean def_var = (st.lookupAnyVariable(ctx.var().getText()) != null);
            if (!def_var) {
                error(ctx, "Undefined variable: " + ctx.var().getText());
                return false;
            }
            return input_t && var_t;
        }

        if (ctx.term() != null) {
            Boolean def_var = (st.lookupAnyVariable(ctx.var().getText()) != null);
            return (tg.visit(ctx.term()).equals("numeric") && tg.visit(ctx.var()).equals("numeric") && def_var);
        }

        error(ctx, "Invalid assignment");
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
        if (ctx.atom() != null) {
            if (ctx.atom().var() != null) {
                Boolean def_var = (st.lookupAnyVariable(ctx.atom().var().getText()) != null);
                if (!def_var) {
                    error(ctx, "Undefined variable: " + ctx.atom().var().getText());
                    return false;
                }
            }
            return tg.visit(ctx.atom()).equals("numeric");
        }
        error(ctx, "Invalid output statement");
        return false;
    }

    @Override
    public Boolean visitInput(SPLParser.InputContext ctx) {
        if (ctx.atom().isEmpty()) {
            return true;
        }
        if (ctx.atom().size() > 3) {
            error(ctx, "Input can only have a maximum of three parameters");
            return false;
        }
        ListIterator<SPLParser.AtomContext> it = ctx.atom().listIterator();
        while (it.hasNext()) {
            SPLParser.AtomContext atomCtx = it.next();
            if (!tg.visit(atomCtx).equals("numeric")) {
                error(ctx, "Input parameters must be of type numeric");
                return false;
            }
            if (atomCtx.var() != null) {
                Boolean def_var = (st.lookupAnyVariable(atomCtx.var().getText()) != null);
                if (!def_var) {
                    error(ctx, "Undefined variable: " + atomCtx.var().getText());
                    return false;
                }
            }
            
        }
        return true;
    }

}
