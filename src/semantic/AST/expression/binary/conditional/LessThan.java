package semantic.AST.expression.binary.conditional;

import semantic.AST.expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LessThan extends CondExp {

    public LessThan(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        cmp(Opcodes.IFGE, Opcodes.IF_ICMPGE, mv, cw);
    }
}
