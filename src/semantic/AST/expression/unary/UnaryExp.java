package semantic.AST.expression.unary;

import semantic.AST.expression.Expression;
import semantic.AST.expression.variable.SimpleVar;
import semantic.AST.expression.variable.Variable;
import semantic.symbolTable.DSCPs.DSCP;
import semantic.symbolTable.DSCPs.GlobalVarDSCP;
import semantic.symbolTable.DSCPs.LocalVarDSCP;
import semantic.symbolTable.SymbolTableHandler;

import static lexical.scanner.yyline;


abstract public class UnaryExp extends Expression {
    protected Expression operand;

    UnaryExp(Expression operand){
        this.operand = operand;
    }

    //This is just for postpp,prepp,...
    protected void checkConst(Variable variable) {
        boolean isConst = false;
        if (variable instanceof SimpleVar) {
            DSCP dscp = SymbolTableHandler.getInstance().getDescriptor(variable.getName());
            isConst = (dscp instanceof GlobalVarDSCP) ? ((GlobalVarDSCP) dscp).isConstant() : ((LocalVarDSCP) dscp).isConstant();
        }
        if (isConst)
            throw new RuntimeException("Const variables can't assign" + " in line" + yyline);
    }
}
