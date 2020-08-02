package semantic.AST.expression.binary.arithmetic;

import semantic.AST.expression.Expression;
import semantic.AST.expression.binary.BinaryExp;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static lexical.scanner.yyline;
import static org.objectweb.asm.Opcodes.IMUL;

public class Multiply extends BinaryExp {

    public Multiply(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        firstop.codegen(mv, cw);
        secondop.codegen(mv, cw);
        if (!firstop.getType().equals(secondop.getType()))
            throw new RuntimeException("types not match for " + this.getClass().getName() + " in line " + yyline);
        type = firstop.getType();
        mv.visitInsn(type.getOpcode(IMUL));
    }
}
