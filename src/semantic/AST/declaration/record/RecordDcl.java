package semantic.AST.declaration.record;

import com.sun.org.apache.bcel.internal.generic.ARETURN;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import semantic.AST.block.Block;
import semantic.AST.declaration.Declaration;
import lombok.Data;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import semantic.AST.declaration.variable.ArrDcl;
import semantic.AST.declaration.variable.SimpleVarDcl;
import semantic.AST.declaration.variable.VarDCL;
import semantic.AST.expression.variable.SimpleVar;
import semantic.symbolTable.Scope;
import semantic.symbolTable.SymbolTableHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static org.objectweb.asm.Opcodes.*;

@Data
public class RecordDcl implements Declaration {

    private String name;
    private ArrayList<VarDCL> varDCLS;
    private Block block;

    public RecordDcl(Block block, String name) {
        this.block = block;
        this.name = name;
        varDCLS = new ArrayList<>();
        for (int i = 0; i < block.getOperations().size(); i++) {
            if (block.getOperations().get(i) instanceof VarDCL) {
                varDCLS.add((VarDCL) block.getOperations().get(i));
                block.getOperations().remove(i);
                i--;
            }
        }
    }

    public RecordDcl(String name, ArrayList<VarDCL> varDCLS, Block block) {
        this.name = name;
        this.varDCLS = varDCLS;
        this.block = block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public String getName() {
        return name;
    }

    public Block getBlock() {
        return block;
    }

    public ArrayList<VarDCL> getVarDCLS() {
        return varDCLS;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
//        System.out.println(getName()+"{{{");
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, name, null, "java/lang/Object", null);
        for (VarDCL varDCL : varDCLS) {
            if (varDCL instanceof SimpleVarDcl)
                classWriter.visitField(Opcodes.ACC_PUBLIC, varDCL.getName(), varDCL.getType().getDescriptor(), null, null).visitEnd();
            else if (varDCL instanceof ArrDcl){
                String repeatedArray = "";
                for (int i = 0; i < ((ArrDcl) varDCL).getDimNum(); i++) {
                    repeatedArray += "[";
                }
                Type arrayType = Type.getType(repeatedArray + varDCL.getType().getDescriptor());
                classWriter.visitField(ACC_PUBLIC, varDCL.getName(), arrayType.getDescriptor(), null, null).visitEnd();
            }
        }
        SymbolTableHandler.getInstance().addScope(Scope.RECORD);
        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        methodVisitor.visitCode();

        for (VarDCL varDCL : varDCLS) {
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            if (varDCL instanceof SimpleVarDcl) {
                ((SimpleVarDcl) (varDCL)).getExp().codegen(methodVisitor, classWriter);
                methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, name, varDCL.getName(), varDCL.getType().getDescriptor());
            }
            else if (varDCL instanceof ArrDcl) {
                for (int i = ((ArrDcl) varDCL).getDimNum() - 1; i >= 0; i--) {
                    ((ArrDcl) varDCL).getDimensions().get(i).codegen(methodVisitor, classWriter);
                }
                String repeatedArray = "";
                for (int i = 0; i < ((ArrDcl) varDCL).getDimNum(); i++) {
                    repeatedArray += "[";
                }

                Type arrayType = Type.getType(repeatedArray + varDCL.getType().getDescriptor());
                methodVisitor.visitMultiANewArrayInsn(arrayType.getDescriptor(), ((ArrDcl) varDCL).getDimNum());
                methodVisitor.visitFieldInsn(PUTFIELD, name, varDCL.getName(), arrayType.getDescriptor());
            }

            //SymbolTableHandler.getInstance().addVariable(paramPair.name,paramPair.dscp);
        }

        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
        classWriter.visitEnd();

        // Generate class file
        try (FileOutputStream fos = new FileOutputStream(name + ".class")) {
            fos.write(classWriter.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Update symbol table
        // RecordTypeDSCP recordDSCP = new RecordTypeDSCP(getName(), 1, Display.pop());
        SymbolTableHandler.getInstance().popScope();
    }

}
