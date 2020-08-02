package semantic.AST.statement.Condition;

import semantic.AST.block.Block;
import semantic.AST.expression.constant.IntegerConst;
import semantic.AST.statement.Statement;
import semantic.symbolTable.Scope;
import semantic.symbolTable.SymbolTableHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.GOTO;

public class Case extends Statement {
    IntegerConst exp;
    private Block block;
    Label StartCase = new Label();
    Label jump;
    public Case(IntegerConst exp, Block block){
        this.exp = exp;
        this.block = block;
    }
    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        mv.visitLabel(StartCase);
        SymbolTableHandler.getInstance().addScope(Scope.SWITCH);
        block.codegen(mv, cw);
        SymbolTableHandler.getInstance().popScope();
        mv.visitJumpInsn(GOTO,jump);
    }
}
