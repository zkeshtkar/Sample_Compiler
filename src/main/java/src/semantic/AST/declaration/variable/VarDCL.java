package src.semantic.AST.declaration.variable;

import semantic.AST.Operation;
import semantic.AST.declaration.Declaration;
import semantic.AST.statement.loop.InitExp;
import lombok.Data;
import org.objectweb.asm.Type;

@Data
public abstract class VarDCL implements Operation, InitExp, Declaration {
    protected String name;
    protected Type type = null;
    protected boolean global = true;

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
