package semantic.AST.expression;

import semantic.AST.Node;
import lombok.Data;
import org.objectweb.asm.Type;

import static lexical.scanner.yyline;

@Data
abstract public class Expression implements Node {
    protected Type type;


    public Type getType() {
        if(type == null)
            throw new RuntimeException("The type of expression is not set!"+ " in line" + yyline);
        return type;
    }
}
