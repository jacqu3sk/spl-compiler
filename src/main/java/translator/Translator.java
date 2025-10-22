package translator;
/*
 * visitlates source code to intermediate code
 */


import org.antlr.v4.runtime.tree.*;

import generated.SPLBaseVisitor;
import generated.SPLParser;
import generated.SPLParser.AtomContext;
import symbolTable.*;

public class Translator extends SPLBaseVisitor<String> {
    private StringBuilder intermediateCode;
    private Label label;
    private TempVariable tempVariable;
    private final SymbolTable symbolTable;
    private final ParseTree tree;

    // constructor
    public Translator(SymbolTable symbolTable, ParseTree tree) {
        intermediateCode = new StringBuilder();
        label = new Label();
        tempVariable = new TempVariable();
        this.symbolTable = symbolTable;
        this.tree = tree;
    }

    // getter
    public String getIntermediateCode()
    {
        return intermediateCode.toString();
    }
    
    // Generate intermediate code
    public void generateIntermediateCode()
    {
        visit(tree);

        System.out.println("\nGenerated Intermediate Code:");
        System.out.println(intermediateCode);
    }

    // semantic attributes for visitlating code

    public String visitSpl_prog(SPLParser.Spl_progContext ctx) {
        if (ctx != null) {
            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree ch = ctx.getChild(i);
                if (ch instanceof SPLParser.MainprogContext) {
                    visit(ch);
                    break;
                }
            }
        }
        return null;
    }

    public String visitMainprog(SPLParser.MainprogContext ctx) {
        if (ctx.algo() != null) visit(ctx.algo());
        return null;
    }

    // ATOM ::= VAR | number
    public String visitAtom(SPLParser.AtomContext ctx) {
        if (ctx.NUMBER() != null)
        {
            tempVariable.newTemp();
            String code = tempVariable.printTemp() + " = " + ctx.NUMBER().getText();
            intermediateCode.append(code);
            intermediateCode.append("\n");
            return tempVariable.printTemp();
        }else{
            SymbolEntry symbol = symbolTable.lookupVariable(ctx.var().getText(), getIntermediateCode(), null);
            tempVariable.newTemp();
            String code = tempVariable.printTemp() + " = " + symbol.getRenamedVariable();
            intermediateCode.append(code);
            intermediateCode.append("\n");
           
            symbolTable.updateVariable(symbol.getName(), symbol.getScopeOwner(), symbol.getScope(),tempVariable.printTemp() );
            return tempVariable.printTemp();

        }
    }

    // ALGO ::= INSTR ; ALGO
    public String visitAlgo(SPLParser.AlgoContext ctx) {

        if (ctx.instr() != null) {
            visitInstr(ctx.instr());
        }
        if (ctx.algo() != null) {
            visitAlgo(ctx.algo());
        }
        return null;
    }

    // INSTR ::= halt || print OUTPUT || ASSIGN || BRANCH || LOOP || NAME(INPUT)
    public String visitInstr(SPLParser.InstrContext ctx) {
        if (ctx.getText().startsWith("halt")) {
            intermediateCode.append("STOP\n");

        } else if (ctx.getText().startsWith("print")) {
            intermediateCode.append("PRINT ");
            visitOutput(ctx.output());

        }else if (ctx.assign()!=null) {
           visitAssign(ctx.assign());

        }else if (ctx.branch() !=null) {
            visitBranch(ctx.branch());

        }else if (ctx.loop()!=null) {
            visitLoop(ctx.loop());

        }else { //  NAME(INPUT)

            
            String code = "CALL ";
            System.out.println(ctx.NAME().getText());
            SymbolEntry function = symbolTable.lookupFunction(ctx.NAME().getText(), null, null);
            code += function.getName() + '(';
            for (int i=0; i<ctx.input().atom().size();i++)
            {
                String var = visitAtom(ctx.input().atom(i));
                if (i>0){
                    code += ", ";
                }
                SymbolEntry symbol = symbolTable.lookupVariable(ctx.input().atom(i).getText(), getIntermediateCode(), null);
                if (symbol!=null)
                {
                    code +=symbol.getTempVariable();
                }else{
                    code += var;
                }
            }

            intermediateCode.append(code + ") \n");
        }
        return null;
    }

    public String visitOutput(SPLParser.OutputContext ctx)
    {
    
        if (ctx.STRING() != null)
        {
            String code = ctx.STRING().getText();
            intermediateCode.append(code + "\n");
        }else if (ctx.atom() !=null) {
            if (ctx.atom().NUMBER() !=null)
            {
                visitAtom(ctx.atom());
            }else{
                SymbolEntry symbol = symbolTable.lookupVariable(ctx.atom().var().getText(), getIntermediateCode(), null);
                intermediateCode.append(symbol.getRenamedVariable() + "\n");
            }
            
        }
        return null;
    }

    // ASSIGN ::= VAR=TERM|| VAR=NAME(INPUT)
    public String visitAssign(SPLParser.AssignContext ctx)
    {
        if (ctx.term()!= null)
        {
            SymbolEntry symbol = symbolTable.lookupVariable(ctx.var().getText(), getIntermediateCode(), null);
            /*tempVariable.newTemp();
            symbolTable.updateVariable(symbol.getName(), symbol.getScopeOwner(), symbol.getScope(),tempVariable.printTemp() );
            String t = tempVariable.printTemp();*/

            label.newLabel();
            String l1 = label.printLabel();
            label.newLabel();
            String l2 = label.printLabel();

            String t1 =visitTerm(ctx.term(), l1, l2);

            String varCode = symbol.getRenamedVariable() + " = " + t1;
            intermediateCode.append(varCode);
            intermediateCode.append("\n");

        }else{

            // under construction

        }

        return null;
    }

    public String visitBranch(SPLParser.BranchContext ctx)
    {
        StringBuilder code = new StringBuilder();

        label.newLabel();
        String l1 = label.printLabel();
        label.newLabel();
        String l2 = label.printLabel();
        
        intermediateCode.append("IF ");
        visitTerm(ctx.term(),l1,l2);
        intermediateCode.append(" THEN " + l1 + "\n");
        if (ctx.algo(1)!=null)
        {
            visitAlgo(ctx.algo(1));
        }
        code.append("GOTO "+l2+"\n");
        code.append("REM "+l1+"\n");
        visitAlgo(ctx.algo(0));
        code.append("REM "+l2+"\n");

        intermediateCode.append(code.toString());
        return null;
    }

    public String visitLoop(SPLParser.LoopContext ctx)
    {

        if (ctx.getText().startsWith("while")) {
            label.newLabel();
            String l1 = label.printLabel();
            label.newLabel();
            String l2 = label.printLabel();
            label.newLabel();
            String l3 = label.printLabel();

            intermediateCode.append("REM " + l1);
            visitTerm(ctx.term(), l2,l3);
            intermediateCode.append("REM " + l2);
            visitAlgo(ctx.algo());
            intermediateCode.append(" GOTO " + l1 + "REM " + l3);
        }else{
            label.newLabel();
            String l1 = label.printLabel();
            label.newLabel();
            String l2 = label.printLabel();

            intermediateCode.append("REM " + l1);
            visitAlgo(ctx.algo());
            visitTerm(ctx.term(), l2,l1);
            intermediateCode.append("REM " + l2);
        }

        return null;
    }

    // TERM ::= ATOM || (UNOP TERM) || (TERM BINOP TERM)
    public String visitTerm(SPLParser.TermContext ctx,String l1, String l2)
    {
        if (ctx.atom() != null)
        {
            String t1 = visitAtom(ctx.atom());
            return t1;

        } else if (ctx.unop()!=null) {
            String unopCode = visitUnop(ctx.unop());

            tempVariable.newTemp();
            String t1 = tempVariable.printTemp();
            visitTerm(ctx.term(0),l1,l2);
            tempVariable.newTemp();
            String t2 = tempVariable.printTemp();
            intermediateCode.append(t2 + " = " + unopCode + t1 + "\n");
            return t2;

        }else{
            String t1 = visitTerm(ctx.term(0),l1,l2);
            String t2 = visitTerm(ctx.term(1),l1,l2);
            String binopCode = visitBinop(ctx.binop());

            if (t1==null)
            {   
                tempVariable.newTemp();
                t1 = tempVariable.printTemp();
            }
            if (t2==null)
            {   
                tempVariable.newTemp();
                t2 = tempVariable.printTemp();
            }
            tempVariable.newTemp();
            String t3 = tempVariable.printTemp();
            intermediateCode.append(t3 + " = " + t1 + binopCode + t2 + "\n");
            return t3;
        }
    }

   

    public String visitUnop(SPLParser.UnopContext ctx)
    {
        if (ctx.getText().equals("neg")) {
            return "-";
        }else {
            return "";
        }
    }

    public String visitBinop(SPLParser.BinopContext ctx)
    {
        if (ctx.getText().equals("eq")) {
            return "=";
        }else if (ctx.getText().equals("plus")) {
            return "+";
        }else if (ctx.getText().equals("minus")) {
            return "-";
        }else if (ctx.getText().equals("mult")) {
            return "*";
        }else if (ctx.getText().equals("div")) {
            return "/";
        }else{
            return ctx.getText();
        }
    }
}
