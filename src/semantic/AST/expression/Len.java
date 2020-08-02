package semantic.AST.expression;

import semantic.AST.Operation;
import semantic.AST.expression.variable.ArrayVar;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static lexical.scanner.yyline;
import static org.objectweb.asm.Opcodes.*;

public class Len extends Expression implements Operation{

    private Expression expression;

    public Len(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        expression.codegen(mv, cw);
        type = expression.getType();
        if (expression instanceof ArrayVar | expression.getType().toString().equalsIgnoreCase("Ljava/lang/String;")) {
            mv.visitInsn(ARRAYLENGTH);
        }
        else
            throw new RuntimeException("input of len function is not iterable" + " in line" + yyline);
    }
}
