package semantic.AST.expression.conditional;


import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import semantic.AST.expression.BaseExp;
import semantic.AST.expression.Expression;

import static org.objectweb.asm.Opcodes.IOR;

public class ORBit extends BaseExp {

    public ORBit(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        firstop.codegen(mv, cw);
        secondop.codegen(mv, cw);
        if(!firstop.getType().equals(secondop.getType()))
            throw new RuntimeException("types not match for " + this.getClass().getName());
        type = firstop.getType();
        mv.visitInsn(type.getOpcode(IOR));
    }
}
