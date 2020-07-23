package src.semantic.AST.statement;

import semantic.AST.declaration.function.FunctionDcl;
import semantic.AST.expression.Expression;
import semantic.AST.expression.unary.Cast;
import semantic.symbolTable.SymbolTable;
import semantic.symbolTable.SymbolTableHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class FuncReturn extends Statement {

    private Expression expression;
    private SymbolTable scope;

    public FuncReturn(Expression expression,FunctionDcl funcDcl) {
        this.expression = expression;
        funcDcl.addReturn(this);
        if((expression == null && !funcDcl.getType().equals(Type.VOID_TYPE)) ||
                (expression != null && (funcDcl.getType().equals(Type.VOID_TYPE) ||
                        !funcDcl.getType().equals(expression.getType()) )))
            throw new RuntimeException("Return type mismatch");
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        FunctionDcl functionDcl = SymbolTableHandler.getInstance().getLastFunction();
        scope = SymbolTableHandler.getInstance().getLastScope();
        int index = functionDcl.getReturns().indexOf(this);
        for (int i = 0; i < index; i++)  {
            FuncReturn funcReturn = functionDcl.getReturns().get(i);
            if(funcReturn.scope.equals(scope)) {
                throw new RuntimeException("more than one return in single scope -__-");
            }
        }
        if(expression == null) {
            mv.visitInsn(RETURN);
        }
        else {
            expression.codegen(mv, cw);
            //mv.visitInsn(Cast.getOpcode(expression.getType(),functionDcl.getType()));
            if(!expression.getType().equals(functionDcl.getType()))
                throw new RuntimeException("Return types don't match");
            mv.visitInsn(expression.getType().getOpcode(IRETURN));
        }

    }
}
