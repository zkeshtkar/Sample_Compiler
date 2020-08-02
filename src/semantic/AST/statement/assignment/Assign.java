package semantic.AST.statement.assignment;

import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import semantic.AST.expression.Expression;
import semantic.AST.expression.variable.ArrayVar;
import semantic.AST.expression.variable.RecordVar;
import semantic.AST.expression.variable.Variable;
import semantic.symbolTable.DSCPs.*;
import lombok.Data;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static lexical.scanner.yyline;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.AALOAD;

public class Assign extends Assignment {

    public Assign(Expression expression, Variable variable) {
        super(expression, variable);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        checkConst();
        DSCP dscp = variable.getDSCP();

        if (dscp instanceof LocalDSCP) {

            if (dscp instanceof LocalVarDSCP) {
                expression.codegen(mv, cw);
                if (variable.getType() != expression.getType())
                    throw new RuntimeException("you should cast expression!");
                int index = ((LocalDSCP) dscp).getIndex();
                mv.visitVarInsn(variable.getType().getOpcode(ISTORE), index);
            } else if (dscp instanceof LocalArrDSCP) {
                int index = ((LocalArrDSCP) dscp).getIndex();
                mv.visitVarInsn(ALOAD, index);
                List<Expression> dimensions = ((ArrayVar) variable).getDimensions();
                for (int i = dimensions.size() - 1; i >= 0; i--) {
                    dimensions.get(i).codegen(mv, cw);
                    if (!dimensions.get(i).getType().equals(Type.INT_TYPE))
                        throw new RuntimeException("index is not int");
                    if (i != 0)
                        mv.visitInsn(AALOAD);
                }
                expression.codegen(mv, cw);
                if (variable.getType() != expression.getType())
                    throw new RuntimeException("you should cast expression!");
                mv.visitInsn(variable.getType().getOpcode(AASTORE));
            }
            else if (dscp instanceof RecordDSCP){
                mv.visitVarInsn(ALOAD, ((RecordDSCP) dscp).getIndex());
                expression.codegen(mv, cw);
                mv.visitFieldInsn(PUTFIELD, ((RecordDSCP) dscp).getRecordtype().getName(), variable.getName(), variable.getType().getDescriptor());
            }

        } else if (dscp instanceof GlobalDSCP){
            if (dscp instanceof GlobalVarDSCP) {
                expression.codegen(mv, cw);
                if (variable.getType() != expression.getType())
                    throw new RuntimeException("you should cast expression!");
                mv.visitFieldInsn(PUTSTATIC, "Main", variable.getName(), dscp.getType().toString());
            }
            else if (dscp instanceof GlobalArrDSCP){
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
                expression.codegen(mv, cw);
                if (variable.getType() != expression.getType())
                    throw new RuntimeException("you should cast expression!");
                mv.visitInsn(variable.getType().getOpcode(IASTORE));
            }
        }

        dscp.setValid(true);
    }
}
