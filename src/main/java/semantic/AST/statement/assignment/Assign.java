package semantic.AST.statement.assignment;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import semantic.AST.expression.Expression;
import semantic.AST.expression.variable.Variable;
import semantic.DSCPs.DSCP;

public class Assign extends  Assignment {
    public Assign(Expression expression, Variable variable) {
        super(expression, variable);
    }
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        expression.codegen(mv, cw);


    }
}
