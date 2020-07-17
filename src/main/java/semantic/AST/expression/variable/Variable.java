package semantic.AST.expression.variable;

import org.objectweb.asm.Type;
import semantic.AST.expression.Expression;
import semantic.symbolTable.SymbolTableHandler;

public abstract class Variable extends Expression {

    String name;

    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return getDSCP().getType();
    }

    public String getDSCP() {
        return SymbolTableHandler.getInstance().getDescriptor(name);
    }
}

