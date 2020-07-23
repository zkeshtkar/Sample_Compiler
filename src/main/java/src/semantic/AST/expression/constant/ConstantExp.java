package src.semantic.AST.expression.constant;

import semantic.AST.expression.Expression;

public abstract class ConstantExp extends Expression {
    public abstract Object getValue();
}
