package semantic.AST.expression.variable;

import semantic.AST.expression.Expression;
import semantic.symbolTable.DSCPs.DSCP;
import semantic.symbolTable.SymbolTableHandler;
import org.objectweb.asm.Type;

public abstract class Variable extends Expression {

    String name;

    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return getDSCP().getType();
    }

    public DSCP getDSCP() {
        return SymbolTableHandler.getInstance().getDescriptor(name);
    }
}
