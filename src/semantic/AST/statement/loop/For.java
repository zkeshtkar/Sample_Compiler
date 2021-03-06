package semantic.AST.statement.loop;

import semantic.AST.block.Block;
import semantic.AST.expression.Expression;
import semantic.AST.expression.binary.conditional.NotEqual;
import semantic.AST.expression.constant.IntegerConst;
import semantic.AST.expression.unary.PostMM;
import semantic.AST.expression.unary.PostPP;
import semantic.AST.expression.unary.PreMM;
import semantic.AST.expression.unary.PrePP;
import semantic.symbolTable.Scope;

import semantic.symbolTable.SymbolTableHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class For extends Loop {

    private InitExp init;
    private Expression expression;
    private StepExp step;
    private Label expLabel = new Label();
    private Label stepLabel = new Label();
    private Label blockLabel = new Label();


    public For(Block block, InitExp init, Expression expression, StepExp step) {
        super(block);
        this.init = init;
        this.expression = expression;
        this.step = step;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        SymbolTableHandler.getInstance().addScope(Scope.LOOP);
        SymbolTableHandler.getInstance().setInnerLoop(this);
        // ST init
        if (init != null) {
            init.codegen(mv, cw);
            if (init instanceof PostPP || init instanceof PrePP
                    || init instanceof PostMM || init instanceof PreMM)
                mv.visitInsn(POP);
        }
        // Boolean Expression
        mv.visitLabel(expLabel);

        // jz, BE, end
        // jnz, BE, blockLabel
        NotEqual notEqual = new NotEqual(expression, new IntegerConst(0));
        notEqual.codegen(mv, cw);
        mv.visitJumpInsn(IFEQ, end);
        mv.visitJumpInsn(GOTO, blockLabel);

        // ST step
        mv.visitLabel(stepLabel);
        mv.visitLabel(startLoop);
        if (step != null) {
            step.codegen(mv, cw);
            if (step instanceof PostPP || step instanceof PrePP
                    || step instanceof PostMM || step instanceof PreMM)
            mv.visitInsn(POP);
        }

        mv.visitJumpInsn(GOTO, expLabel);

        // ST body
        mv.visitLabel(blockLabel);
        block.codegen(mv, cw);
        mv.visitJumpInsn(GOTO, stepLabel);

        mv.visitLabel(end);

        SymbolTableHandler.getInstance().popScope();
        SymbolTableHandler.getInstance().setInnerLoop(null);
    }
}
