package src.semantic.AST.expression;

import semantic.AST.Operation;
import semantic.AST.expression.constant.IntegerConst;
import semantic.symbolTable.SymbolTableHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class Sizeof extends Expression implements Operation {

    private Integer value;

    public Sizeof(String baseType){
       value = SymbolTableHandler.getSize(baseType);
        type = Type.INT_TYPE;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        IntegerConst.storeIntValue(mv,value);
    }
}
