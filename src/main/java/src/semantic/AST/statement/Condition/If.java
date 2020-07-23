package src.semantic.AST.statement.Condition;

import semantic.AST.block.Block;
import semantic.AST.expression.Expression;
import semantic.AST.expression.binary.conditional.NotEqual;
import semantic.AST.expression.constant.IntegerConst;
import semantic.AST.statement.Statement;
import semantic.symbolTable.Scope;
import semantic.symbolTable.SymbolTableHandler;
import lombok.Data;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFEQ;

@Data
public class If extends Statement {

    private Expression expression;
    private Block ifBlock, elseBlock;
    private Label startElse = new Label();
    private Label endElse = new Label();

    public If(Expression expression, Block ifBlock, Block elseBlock) {
        this.expression = expression;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }


    public void setElseBlock(Block elseBlock) {
        this.elseBlock = elseBlock;
    }


    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        SymbolTableHandler.getInstance().addScope(Scope.IF);
        NotEqual notEqual = new NotEqual(expression, new IntegerConst(0));
        notEqual.codegen(mv, cw);
        mv.visitJumpInsn(IFEQ, startElse);
        ifBlock.codegen(mv, cw);
        mv.visitJumpInsn(GOTO, endElse);
        SymbolTableHandler.getInstance().popScope();
        if (elseBlock != null) {
            SymbolTableHandler.getInstance().addScope(Scope.IF);
            mv.visitLabel(startElse);
            elseBlock.codegen(mv, cw);
            SymbolTableHandler.getInstance().popScope();
        }
        else
            mv.visitLabel(startElse);
        mv.visitLabel(endElse);
    }
}
