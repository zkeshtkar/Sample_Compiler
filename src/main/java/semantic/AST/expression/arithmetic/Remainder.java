package semantic.AST.expression.arithmetic;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import semantic.AST.expression.BaseExp;
import semantic.AST.expression.Expression;

import static jdk.internal.org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.IREM;

public class Remainder extends BaseExp {

    public Remainder(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        firstop.codegen(mv, cw);
        secondop.codegen(mv, cw);
        if(!firstop.getType().equals(secondop.getType()))
            throw new RuntimeException("types not match for " + this.getClass().getName());
        type = firstop.getType();
        if(type == Type.DOUBLE_TYPE)
        {
            mv.visitInsn(type.getOpcode(DREM));
        }
        else if(type == Type.FLOAT_TYPE)
        {
            mv.visitInsn(type.getOpcode(FREM));
        }
        else if(type == Type.LONG_TYPE)
        {
            mv.visitInsn(type.getOpcode(LREM));
        }
        else if(type == Type.INT_TYPE)
        {
            mv.visitInsn(type.getOpcode(IREM));
        }
    }
}
