package semantic.AST.expression.unary;

import semantic.AST.expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static lexical.scanner.yyline;
import static org.objectweb.asm.Opcodes.IXOR;
import static org.objectweb.asm.Opcodes.ICONST_M1;


public class BitwiseNot extends UnaryExp {  //~
    public BitwiseNot(Expression operand) {
        super(operand);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        operand.codegen(mv, cw);
        if(operand.getType() != Type.INT_TYPE && operand.getType() != Type.LONG_TYPE)
            throw new RuntimeException("It's not real or integer.so I can't not it!" + " in line " + yyline);
        mv.visitInsn(ICONST_M1);
        type = operand.getType();
        mv.visitInsn(type.getOpcode(IXOR));
    }
}
