package semantic;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import semantic.AST.Operation;

public class NOP implements Operation {

    String name;

    public NOP(String name) {
        this.name = name;
    }
    public NOP() {
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {

    }
}
