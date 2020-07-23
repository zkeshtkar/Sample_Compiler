package src.semantic.AST.statement.assignment;

import semantic.AST.expression.Expression;
import semantic.AST.expression.variable.SimpleVar;
import semantic.AST.expression.variable.Variable;
import semantic.AST.statement.Statement;
import semantic.AST.statement.loop.InitExp;
import semantic.AST.statement.loop.StepExp;
import semantic.symbolTable.DSCPs.DSCP;
import semantic.symbolTable.DSCPs.GlobalVarDSCP;
import semantic.symbolTable.DSCPs.LocalVarDSCP;
import semantic.symbolTable.SymbolTableHandler;



public abstract class Assignment extends Statement implements InitExp, StepExp {
    protected Expression expression;
    protected Variable variable;

    Assignment(Expression expression, Variable variable) {
        this.expression = expression;
        this.variable = variable;
    }

    protected void checkConst() {
        boolean isConst = false;
        if (variable instanceof SimpleVar) {
            DSCP dscp = SymbolTableHandler.getInstance().getDescriptor(variable.getName());
            isConst = (dscp instanceof GlobalVarDSCP) ? ((GlobalVarDSCP) dscp).isConstant() : ((LocalVarDSCP) dscp).isConstant();
        }
        if (isConst)
            throw new RuntimeException("Const variables can't assign");
    }

}
