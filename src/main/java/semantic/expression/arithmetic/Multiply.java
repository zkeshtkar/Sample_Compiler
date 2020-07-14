package semantic.expression.arithmetic;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import semantic.expression.BaseExp;
import semantic.expression.Expression;

import static org.objectweb.asm.Opcodes.IMUL;

public class Multiply extends BaseExp {

    public Multiply(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        firstop.codegen(mv, cw);
        secondop.codegen(mv, cw);
        if(!firstop.getType().equals(secondop.getType()))
            throw new RuntimeException("types not match for " + this.getClass().getName());
        type = firstop.getType();
        mv.visitInsn(type.getOpcode(IMUL));
    }
}
