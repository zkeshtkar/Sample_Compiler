package semantic.AST.expression.constant;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class FloatConst extends ConstantExp {
    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {

    }
}
