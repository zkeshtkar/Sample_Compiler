package semantic.AST.expression.arithmetic;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import semantic.AST.expression.BaseExp;
import semantic.AST.expression.Expression;

import static jdk.internal.org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.IDIV;


public class Divide extends BaseExp {

    public Divide(Expression firstop, Expression secondop) {
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
            mv.visitInsn(type.getOpcode(DDIV));
        }
        else if(type == Type.FLOAT_TYPE)
        {
            mv.visitInsn(type.getOpcode(FDIV));
        }
        else if(type == Type.LONG_TYPE)
        {
            mv.visitInsn(type.getOpcode(LDIV));
        }
        else if(type == Type.INT_TYPE)
        {
            mv.visitInsn(type.getOpcode(IDIV));
        }
    }
}
