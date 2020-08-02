package semantic.AST.statement;

import semantic.AST.expression.Expression;
import semantic.AST.expression.unary.Cast;
import semantic.symbolTable.SymbolTableHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ICONST_2;

public class Println extends Statement {

    private Expression expression;

    public Println(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        if(expression == null){
            mv.visitLdcInsn("\n");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",
                    "(Ljava/lang/String;)V", false);

        }else{
            expression.codegen(mv, cw);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",
                    "("+ expression.getType() +")V", false);
        }
    }
}
