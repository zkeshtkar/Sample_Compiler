package semantic.AST.expression.conditional;


import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import semantic.AST.expression.Expression;

public class OR extends CondExp {

    public OR(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        AndOr(false, mv, cw);
    }
}
