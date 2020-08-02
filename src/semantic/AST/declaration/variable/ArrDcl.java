package semantic.AST.declaration.variable;

import semantic.AST.expression.Expression;
import semantic.AST.expression.constant.IntegerConst;
import semantic.symbolTable.DSCPs.*;
import semantic.symbolTable.SymbolTableHandler;
import lombok.Data;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

import static lexical.scanner.yyline;
import static org.objectweb.asm.Opcodes.*;

@Data
public class ArrDcl extends VarDCL {
    private boolean costant;
    private List<Expression> dimensions;
    private int dimNum;

    public ArrDcl(String name, Type type, boolean global, int dimNum) {
        this.name = name;
        this.type = type;
        this.global = global;
        dimensions = new ArrayList<>(dimNum);
        this.dimNum = dimNum;
    }

    public ArrDcl(String name, Type type,boolean costant, boolean global, int dimNum) {
        this.name = name;
        this.type = type;
        this.costant = costant;
        this.global = global;
        dimensions = new ArrayList<>(dimNum);
        this.dimNum = dimNum;
    }

    public ArrDcl(String name, String stringType, boolean global, Integer dimNum, Type type, List<Expression> dimensions) {
        this.name = name;
        if (!stringType.equals("auto")) {
            if (!SymbolTableHandler.getTypeFromName(stringType).equals(type))
                throw new RuntimeException("the types of array doesn't match" + " in line " + yyline);
        } else if (dimensions == null)
            throw new RuntimeException("auto variables must have been initialized" + " in line " + yyline);
        if (dimNum != null) {
            if (dimNum != dimensions.size())
                throw new RuntimeException("dimensions are't correct" + " in line " + yyline);
            this.dimNum = dimNum;
        }
        this.type = type;
        this.global = global;
        this.dimensions = dimensions;
    }

    public void setDimensions(List<Expression> dimensions) {
        this.dimensions = dimensions;
    }

    public List<Expression> getDimensions() {
        return dimensions;
    }

    public int getDimNum() {
        return dimNum;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        try {
            SymbolTableHandler.getInstance().getDescriptor(name);
        } catch (Exception e) {
            declare(name, type, dimensions, dimNum, this.global);
        }

        if (global) {
            executeGlobalExp(cw, mv);
            String repeatedArray = "";
            for (int i = 0; i < dimNum; i++) {
                repeatedArray += "[";
            }
//            String repeatedArray = new String(new char[dimensions.size()]).replace("\0", "[");
            Type arrayType = Type.getType(repeatedArray + type.getDescriptor());
            mv.visitMultiANewArrayInsn(arrayType.getDescriptor(), dimensions.size());
            mv.visitFieldInsn(PUTSTATIC, "Main", name, arrayType.getDescriptor());
            cw.visitField(ACC_STATIC, name, arrayType.getDescriptor(), null, null).visitEnd();
        } else {
            for (int i = dimensions.size() - 1; i >= 0; i--) {
                dimensions.get(i).codegen(mv, cw);
            }
            if (dimensions.size() == 0) {
                new IntegerConst(1000).codegen(mv, cw);
            }
            if (dimNum == 1) {
                if (type.getDescriptor().endsWith(";"))
                    mv.visitTypeInsn(ANEWARRAY, getType().getElementType().getInternalName());
                else
                    mv.visitIntInsn(NEWARRAY, SymbolTableHandler.getTType(getType()));

            } else {
                String t = "";
                for (int i = 0; i < dimNum; i++) {
                    t += "[";
                }
                t += type.getDescriptor();
                mv.visitMultiANewArrayInsn(t, dimensions.size());
            }
            mv.visitVarInsn(ASTORE, ((LocalArrDSCP) SymbolTableHandler.getInstance().getDescriptor(name)).getIndex());
        }
    }

    private void executeGlobalExp(ClassWriter cw, MethodVisitor mv) {
        for (int i = dimensions.size() - 1; i >= 0; i--) {
            dimensions.get(i).codegen(mv, cw);
        }
    }

    public static void declare(String name, Type type, List<Expression> dimensions, int dimNum, boolean global) {
        DSCP dscp;
        if (!global)
            dscp = new LocalArrDSCP(type, true, SymbolTableHandler.getInstance().getIndex(), dimensions, dimNum);
        else
            dscp = new GlobalArrDSCP(type, true, dimensions, dimNum);
        SymbolTableHandler.getInstance().addVariable(name, dscp);

    }
}
