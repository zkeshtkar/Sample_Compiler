package src;

import lexical.scanner;
import semantic.AST.Node;
import semantic.CodeGenerator;
import syntax.Parser;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        scanner scanner = new scanner(new FileReader("test.txt"));
        CodeGenerator codeGenerator = new CodeGenerator(scanner);
        parseInput(scanner, codeGenerator);
    }

    private static void parseInput(scanner lexicalAnalyzer, CodeGenerator codeGenerator) {
        Parser parser = new Parser(lexicalAnalyzer, codeGenerator, "src/syntax/table.npt");
        Node result;
        try {
            // Parse given file
            parser.parse();
            // Get Root of AST
            result = codeGenerator.getResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, "Main",
                null, "java/lang/Object", null);
        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC,
                "main", "([Ljava/lang/String;)V", null, null);
        methodVisitor.visitCode();
        result.codegen(methodVisitor, classWriter);
        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();

        // Generate class file
        try (FileOutputStream fos = new FileOutputStream("Main.class")) {
            fos.write(classWriter.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Code compiled successfully");
    }

}