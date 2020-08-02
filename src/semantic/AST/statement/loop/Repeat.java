package semantic.AST.statement.loop;

import semantic.AST.block.Block;
import semantic.AST.expression.Expression;
import semantic.AST.expression.binary.conditional.NotEqual;
import semantic.AST.expression.constant.IntegerConst;
import semantic.symbolTable.Scope;
import semantic.symbolTable.SymbolTable;
import semantic.symbolTable.SymbolTableHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.IFNE;

public class Repeat extends Loop{
    private Expression expression;

    public Repeat(Block block, Expression expression) {
        super(block);
        this.expression = expression;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        SymbolTableHandler.getInstance().addScope(Scope.LOOP);
        SymbolTableHandler.getInstance().setInnerLoop(this);
        mv.visitLabel(startLoop);
        block.codegen(mv, cw);
        NotEqual notEqual = new NotEqual(expression, new IntegerConst(0));
        notEqual.codegen(mv, cw);
        mv.visitJumpInsn(IFNE, startLoop);
        mv.visitLabel(end);
        SymbolTableHandler.getInstance().popScope();
        SymbolTableHandler.getInstance().setInnerLoop(null);
    }
}
