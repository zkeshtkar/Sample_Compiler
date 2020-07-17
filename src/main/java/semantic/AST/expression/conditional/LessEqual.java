package semantic.AST.expression.conditional;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import semantic.AST.expression.BaseExp;
import semantic.AST.expression.Expression;

public class LessEqual extends CondExp {
    public LessEqual(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        cmp(Opcodes.IFGT, Opcodes.IF_ICMPGT, mv, cw);
    }
}
