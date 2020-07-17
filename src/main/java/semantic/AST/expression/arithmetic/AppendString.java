package semantic.AST.expression.arithmetic;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import semantic.AST.expression.BaseExp;
import semantic.AST.expression.Expression;
import java.util.ArrayList;
///in class kamel nist irad dard.

public class AppendString extends BaseExp {
    ArrayList<String>strings;

    public AppendString(Expression firstop, Expression secondop) {
        super(firstop, secondop);
    }
    //constants

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {
        final String stringPath = "java/lang/String";
        final String stringBPath = "java/lang/StringBuilder";
        //initialize string builder (no + in bytecode, sorry!)
        mv.visitTypeInsn(Opcodes.NEW,"java/lang/StringBuilder");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,"java/lang/StringBuilder","<init>","()V");
        //loop through strings and append
        for(String string : strings){
            mv.visitLdcInsn(string);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,stringBPath,"append", "(L"+stringPath+";)L"+stringBPath+";");
        }
        //turn builder to string and throw on stack to be processed
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,stringBPath,"toString","()L"+stringPath+";");
        ///https://gist.github.com/scarger/0a202854a5ef1fe7542adf2af088fae1

    }
}
