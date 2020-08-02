package semantic.AST.expression.variable;

import semantic.AST.expression.Expression;
import semantic.AST.expression.binary.conditional.BiggerEqual;
import semantic.symbolTable.DSCPs.DSCP;
import semantic.symbolTable.DSCPs.GlobalArrDSCP;
import semantic.symbolTable.DSCPs.LocalArrDSCP;
import semantic.symbolTable.SymbolTableHandler;
import lombok.Data;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.List;

import static lexical.scanner.yyline;
import static org.objectweb.asm.Opcodes.*;


@Data
public class ArrayVar extends Variable {

    private List<Expression> dimensions;

    public ArrayVar(String name, List<Expression> dimensions, Type type) {
        this.name = name;
        this.dimensions = dimensions;
        this.type = type;
    }

    public void setDimensions(List<Expression> dimensions) {
        this.dimensions = dimensions;
    }

    public List<Expression> getDimensions() {
        return dimensions;
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {

        Label exceptionLabel = new Label();
        Label endLabel = new Label();

        DSCP dscp = SymbolTableHandler.getInstance().getDescriptor(name);

        if (dscp instanceof LocalArrDSCP) {
            mv.visitVarInsn(ALOAD, ((LocalArrDSCP) SymbolTableHandler.getInstance().getDescriptor(name)).getIndex());
            for (int i = dimensions.size() - 1; i >= 0; i--) {
                dimensions.get(i).codegen(mv, cw);

                BiggerEqual biggerEqual = new BiggerEqual(dimensions.get(i), ((LocalArrDSCP) dscp).getDimList().get(i));
                biggerEqual.codegen(mv, cw);
                mv.visitJumpInsn(IFGE, exceptionLabel);

                if (dimensions.get(i).getType().getElementType().equals(Type.INT_TYPE))
                    throw new RuntimeException("Index should be an integer number");

                if (i != 0)
                    mv.visitInsn(AALOAD);
            }
            mv.visitInsn(type.getOpcode(IALOAD));

        } else if (dscp instanceof GlobalArrDSCP) {
            String repeatedArray = "";
            for (int i = 0; i < ((GlobalArrDSCP) dscp).getDimNum(); i++) {
                repeatedArray += "[";
            }
            mv.visitFieldInsn(GETSTATIC, "Main", name, repeatedArray + type.getDescriptor());
            for (int i = dimensions.size() - 1; i >= 0; i--) {
                dimensions.get(i).codegen(mv, cw);

                BiggerEqual biggerEqual = new BiggerEqual(dimensions.get(i), ((GlobalArrDSCP) dscp).getDimList().get(i));
                biggerEqual.codegen(mv, cw);
                mv.visitJumpInsn(IFGE, exceptionLabel);

                if (dimensions.get(i).getType().getElementType().equals(Type.INT_TYPE))
                    throw new RuntimeException("Index should be an integer number");

                if (i != 0)
                    mv.visitInsn(AALOAD);
            }
            mv.visitInsn(type.getOpcode(IALOAD));
        }

        mv.visitJumpInsn(GOTO, endLabel);
        mv.visitLabel(exceptionLabel);
        mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "()V", false);
        mv.visitInsn(ATHROW);
        mv.visitLabel(endLabel);
    }
}
