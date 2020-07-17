package semantic.AST.statement.assignment;


import semantic.AST.expression.Expression;
import semantic.AST.expression.variable.Variable;
import semantic.AST.statement.Statement;

public abstract class Assignment {
    protected Expression expression;
    protected Variable variable;

    Assignment(Expression expression, Variable variable) {
        this.expression = expression;
        this.variable = variable;
    }



}
