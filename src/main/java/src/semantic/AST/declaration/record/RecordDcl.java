package src.semantic.AST.declaration.record;

import semantic.AST.declaration.Declaration;
import lombok.Data;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

@Data
public class RecordDcl implements Declaration {

    private String name;

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
    }

    public String getName() {
        return name;
    }
}
