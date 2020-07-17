package semantic.AST.expression;

public abstract class BaseExp extends Expression {
    protected Expression firstop;
    protected Expression secondop;

    public BaseExp(Expression firstop, Expression secondop) {
        this.firstop = firstop;
        this.secondop = secondop;
    }
}
