package semantic.AST.expression.unary;

import semantic.AST.Operation;
import semantic.AST.expression.Expression;
import semantic.AST.expression.constant.IntegerConst;
import semantic.AST.expression.variable.SimpleVar;
import semantic.AST.expression.variable.Variable;
import semantic.AST.statement.assignment.SumAssign;
import semantic.AST.statement.loop.InitExp;
import semantic.AST.statement.loop.StepExp;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static lexical.scanner.yyline;


public class PrePP extends UnaryExp implements InitExp, StepExp, Operation {

    public PrePP(Expression operand) {
        super(operand);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        type = operand.getType();
        if (!(operand instanceof Variable) || (type != Type.INT_TYPE && type != Type.DOUBLE_TYPE && type != Type.LONG_TYPE && type != Type.FLOAT_TYPE))
            throw new RuntimeException("the operand is wrong" + " in line" + yyline);
        Variable var = (Variable)operand;
        checkConst(var);
        new SumAssign(new IntegerConst(1), var).codegen(mv, cw);
        new SimpleVar(var.getName(), var.getType()).codegen(mv, cw);
    }
}