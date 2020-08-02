package semantic.AST.statement.assignment;

import org.objectweb.asm.Type;
import semantic.AST.expression.Expression;
import semantic.AST.expression.variable.ArrayVar;
import semantic.AST.expression.variable.Variable;
import semantic.symbolTable.DSCPs.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

import static lexical.scanner.yyline;
import static org.objectweb.asm.Opcodes.*;

public class SumAssign extends Assignment {

    public SumAssign(Expression expression, Variable variable) {
        super(expression, variable);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        checkConst();
        DSCP dscp = variable.getDSCP();

        if (dscp instanceof LocalDSCP) {
            int index = ((LocalDSCP) dscp).getIndex();
            if (dscp instanceof LocalVarDSCP) {
                variable.codegen(mv, cw);
                expression.codegen(mv, cw);
                if (variable.getType() != expression.getType())
                    throw new RuntimeException("you should cast expression!" + " in line" + yyline);
                mv.visitInsn(variable.getType().getOpcode(IADD));
                mv.visitVarInsn(variable.getType().getOpcode(ISTORE), index);
            }
            if (dscp instanceof LocalArrDSCP) {
                mv.visitVarInsn(ALOAD, index);
                List<Expression> dimensions = ((ArrayVar) variable).getDimensions();
                for (int i = dimensions.size() - 1; i >= 0; i--) {
                    dimensions.get(i).codegen(mv, cw);
                    if (!dimensions.get(i).getType().equals(Type.INT_TYPE))
                        throw new RuntimeException("index is not int");
                    if (i != 0)
                        mv.visitInsn(AALOAD);
                }
                mv.visitInsn(DUP2);
                mv.visitInsn(variable.getType().getOpcode(IALOAD));
                expression.codegen(mv, cw);
                if (variable.getType() != expression.getType())
                    throw new RuntimeException("you should cast expression!" + " in line" + yyline);
                mv.visitInsn(variable.getType().getOpcode(IADD));
                mv.visitInsn(variable.getType().getOpcode(IASTORE));
            }
            else if (dscp instanceof RecordDSCP) {
                mv.visitVarInsn(ALOAD, ((RecordDSCP) dscp).getIndex());
                variable.codegen(mv, cw);
                expression.codegen(mv, cw);
                mv.visitInsn(variable.getType().getOpcode(IADD));
                mv.visitFieldInsn(PUTFIELD, ((RecordDSCP) dscp).getRecordtype().getName(), variable.getName(), variable.getType().getDescriptor());
            }
        } else if (dscp instanceof GlobalVarDSCP) {
            expression.codegen(mv, cw);
            if (variable.getType() != expression.getType())
                throw new RuntimeException("you should cast expression!");
            mv.visitInsn(variable.getType().getOpcode(IADD));
            mv.visitFieldInsn(PUTSTATIC, "Main", variable.getName(), dscp.getType().toString());
        } else if (dscp instanceof GlobalArrDSCP) {
            String repeatedArray = "";
            for (int i = 0; i < ((GlobalArrDSCP) dscp).getDimNum(); i++) {
                repeatedArray += "[";
            }
            mv.visitFieldInsn(GETSTATIC, "Main", variable.getName(), repeatedArray + dscp.getType().getDescriptor());

            List<Expression> dimensions = ((ArrayVar) variable).getDimensions();
            for (int i = dimensions.size() - 1; i >= 0; i--) {
                dimensions.get(i).codegen(mv, cw);
                if (!dimensions.get(i).getType().equals(Type.INT_TYPE))
                    throw new RuntimeException("index is not int");
                if (i != 0)
                    mv.visitInsn(AALOAD);
            }
            mv.visitInsn(DUP2);
            mv.visitInsn(variable.getType().getOpcode(IALOAD));
            expression.codegen(mv, cw);
            if (variable.getType() != expression.getType())
                throw new RuntimeException("you should cast expression!");
            mv.visitInsn(variable.getType().getOpcode(IADD));
            mv.visitInsn(variable.getType().getOpcode(IASTORE));
        }
    }
}
