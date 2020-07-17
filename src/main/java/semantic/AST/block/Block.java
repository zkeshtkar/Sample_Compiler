package semantic.AST.block;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import semantic.AST.Node;
import semantic.AST.Operation;

import java.util.ArrayList;

public class Block implements Node {
    private ArrayList<Operation> operations;

    public Block(ArrayList<Operation> operations){
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
