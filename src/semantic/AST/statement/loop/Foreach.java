package semantic.AST.statement.loop;

import org.objectweb.asm.Label;
import semantic.AST.block.Block;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import semantic.AST.expression.Expression;
import semantic.AST.expression.binary.arithmetic.Sum;
import semantic.AST.expression.binary.conditional.LessThan;
import semantic.AST.expression.constant.IntegerConst;
import semantic.AST.expression.unary.PostMM;
import semantic.AST.expression.unary.PostPP;
import semantic.AST.expression.unary.PreMM;
import semantic.AST.expression.unary.PrePP;
import semantic.AST.expression.variable.ArrayVar;
import semantic.NOP;
import semantic.symbolTable.Scope;
import semantic.symbolTable.SymbolTableHandler;

import static org.objectweb.asm.Opcodes.*;

public class Foreach extends Loop {

    private InitExp init;
    private Expression expression;
    private StepExp step;
    private Label expLabel = new Label();
    private Label stepLabel = new Label();
    private Label blockLabel = new Label();
    private ArrayVar arrayVar ;
    private NOP nop;

    public Foreach(Block block, ArrayVar arrayVar, NOP exp) {
        super(block);
        this.arrayVar = arrayVar;
        this.nop= exp;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        SymbolTableHandler.getInstance().addScope(Scope.LOOP);
        SymbolTableHandler.getInstance().setInnerLoop(this);
        // ST init
        // Boolean Expression
        mv.visitLabel(expLabel);

        // jz, BE, end
        // jnz, BE, blockLabel
        Expression ex = new Sum(arrayVar.getDimensions().get(0),new IntegerConst(444));
        LessThan lessThan = new LessThan(new IntegerConst(444),ex);
        lessThan.codegen(mv, cw);
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
