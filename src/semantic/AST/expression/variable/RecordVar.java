package semantic.AST.expression.variable;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import semantic.AST.declaration.variable.VarDCL;
import semantic.symbolTable.DSCPs.DSCP;
import semantic.symbolTable.DSCPs.RecordDSCP;
import semantic.symbolTable.SymbolTable;
import semantic.symbolTable.SymbolTableHandler;

public class RecordVar extends Variable {
    private String record;

    public RecordVar(String record, String name, Type type) {
        this.record = record;
        this.type = type;
        this.name = name;
    }

    public String getRecord() {
        return record;
    }

    @Override
    public DSCP getDSCP() {
        return SymbolTableHandler.getInstance().getDescriptor(record);
    }
    @Override
    public Type getType() {
        DSCP dscp = getDSCP();
        for (VarDCL v:((RecordDSCP)(dscp)).getRecordtype().getVarDCLS()) {
            if (v.getName().equals(name)) {
                return v.getType();
            }
        }
        return null;

    }
    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        DSCP dscp = SymbolTableHandler.getInstance().getDescriptor(record);
        mv.visitVarInsn(Opcodes.ALOAD, ((RecordDSCP)(dscp)).getIndex());
        mv.visitFieldInsn(Opcodes.GETFIELD, ((RecordDSCP)getDSCP()).getRecordtype().getName(), name, type.getDescriptor());
    }
}
