package semantic.AST.expression;

import semantic.AST.Operation;
import semantic.AST.declaration.function.FunctionDcl;
import semantic.AST.expression.variable.ArrayVar;
import semantic.symbolTable.DSCPs.DSCP;
import semantic.symbolTable.DSCPs.GlobalArrDSCP;
import semantic.symbolTable.DSCPs.LocalArrDSCP;
import semantic.symbolTable.SymbolTableHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class FuncCall extends Expression implements Operation {

    private String id;
    private List<Expression> parameters;

    private FunctionDcl func;

    public FuncCall(String id, ArrayList<Expression> parameters) {
        this.id = id;
        this.parameters = parameters;
    }

    public void addParam(Expression exp) {
        if (parameters == null)
            parameters = new ArrayList<>();
        parameters.add(exp);

    }


    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        for (Expression parameter : parameters) {
            if (parameter instanceof ArrayVar && ((ArrayVar) parameter).getDimensions().isEmpty()) {
                mv.visitVarInsn(ALOAD, ((LocalArrDSCP) (SymbolTableHandler.getInstance().getDescriptor(((ArrayVar) parameter).getName()))).getIndex());
            } else
                parameter.codegen(mv, cw);
        }
        ArrayList<Type> paramTypes = new ArrayList<>();
        for (Expression exp :
                parameters) {
            if (exp instanceof ArrayVar && ((ArrayVar) exp).getDimensions().isEmpty()) {
                String repeatedArray = "";
                DSCP dscp = ((ArrayVar) exp).getDSCP();
                if (dscp instanceof LocalArrDSCP) {
                    for (int i = 0; i < ((LocalArrDSCP) (dscp)).getDimNum(); i++) {
                        repeatedArray += "[";
                    }
                } else if (dscp instanceof GlobalArrDSCP) {
                    for (int i = 0; i < ((GlobalArrDSCP) (dscp)).getDimNum(); i++) {
                        repeatedArray += "[";
                    }
                }
                paramTypes.add(Type.getType(repeatedArray + exp.getType()));
            } else
                paramTypes.add(exp.getType());
        }
        this.func = SymbolTableHandler.getInstance().getFunction(id, paramTypes);
        this.type = func.getType();
        if (parameters.size() != func.getParameters().size())
            throw new RuntimeException("error in func parameter");
        mv.visitMethodInsn(INVOKESTATIC, "Main", func.getName(), func.getSignature(), false);
    }
}
