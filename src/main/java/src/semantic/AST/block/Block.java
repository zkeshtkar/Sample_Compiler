package src.semantic.AST.block;

import semantic.AST.Node;
import semantic.AST.Operation;
import semantic.AST.declaration.variable.ArrDcl;
import semantic.AST.expression.FuncCall;
import semantic.AST.statement.FuncReturn;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;

public class Block implements Node {

    private ArrayList<Operation> operations;

    public Block(ArrayList<Operation> operations){
        this.operations = operations;
    }

    public void setOperations(ArrayList<Operation> operations) {
        this.operations = operations;
    }

    public void addOperation(Operation operation){
        operations.add(operation);
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        if(operations == null)
            throw new RuntimeException("No expression found!");
        for (Operation op : operations) {
            op.codegen(mv, cw);
        }
    }
}
