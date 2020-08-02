package semantic.AST.expression.binary.conditional;

import semantic.AST.expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class OR extends CondExp {

    public OR(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        AndOr(false, mv, cw);
    }
}
