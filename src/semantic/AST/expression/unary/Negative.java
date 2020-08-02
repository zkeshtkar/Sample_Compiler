package semantic.AST.expression.unary;


import semantic.AST.expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static lexical.scanner.yyline;
import static org.objectweb.asm.Opcodes.INEG;

public class Negative extends UnaryExp { //-


    public Negative(Expression operand) {
        super(operand);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        operand.codegen(mv, cw);
        if(type != Type.BOOLEAN_TYPE)
            throw new RuntimeException("It's not number!" + " in line " + yyline);
        type = operand.getType();
        mv.visitInsn(type.getOpcode(INEG));
    }
}


