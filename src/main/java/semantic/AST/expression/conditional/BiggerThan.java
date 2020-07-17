package semantic.AST.expression.conditional;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import semantic.AST.expression.Expression;

public class BiggerThan extends CondExp{

    public BiggerThan(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        cmp(Opcodes.IFLE, Opcodes.IF_ICMPLE, mv, cw);
    }
}
