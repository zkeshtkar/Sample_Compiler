package semantic.AST.declaration.variable;

import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import semantic.AST.Node;
import semantic.AST.declaration.record.RecordDcl;
import semantic.symbolTable.DSCPs.DSCP;
import semantic.symbolTable.DSCPs.LocalDSCP;
import semantic.symbolTable.DSCPs.RecordDSCP;
import semantic.symbolTable.SymbolTableHandler;

public class RecordVarDCL extends VarDCL {
    private RecordDcl recordtype;

    public RecordVarDCL(RecordDcl recordtype, String name) {
        this.name = name;
        this.recordtype = recordtype;
    }

    public RecordDcl getRecordtype() {
        return recordtype;
    }

    public String getName() {
        return name;
    }

    public void setRecordtype(RecordDcl recordtype) {
        this.recordtype = recordtype;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        try {
             SymbolTableHandler.getInstance().getDescriptor(name);
        } catch (Exception e) {
            declare(name, recordtype);
        }
        DSCP dscp = SymbolTableHandler.getInstance().getDescriptor(name);

        mv.visitTypeInsn(Opcodes.NEW, recordtype.getName());
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, recordtype.getName(), "<init>", "()V", false);
        mv.visitVarInsn(Opcodes.ASTORE, ((LocalDSCP) (dscp)).getIndex());

    }

    public static void declare(String name, RecordDcl recordtype) {
        DSCP dscp;
        dscp = new RecordDSCP(recordtype, true, SymbolTableHandler.getInstance().getIndex());
        SymbolTableHandler.getInstance().addVariable(name, dscp);

    }
}
