package semantic.AST.statement;

import semantic.symbolTable.SymbolTableHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static lexical.scanner.yyline;
import static org.objectweb.asm.Opcodes.GOTO;

public class Continue extends Statement {

    public Continue() {}

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        if(SymbolTableHandler.getInstance().getInnerLoop() != null)
            mv.visitJumpInsn(GOTO, SymbolTableHandler.getInstance().getInnerLoop().getStartLoop());
        else
            throw new RuntimeException("This part is not switch nor Loop" + " in line " + yyline);
    }
}
