package semantic.AST.expression.conditional;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import semantic.AST.expression.Expression;

import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.IF_ICMPNE;

public class Equal extends CondExp {

    public Equal(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        cmp(IFNE, IF_ICMPNE, mv, cw);
    }
}
