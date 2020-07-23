package src.semantic;


import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import semantic.AST.Node;
import semantic.AST.Operation;
import semantic.AST.block.Block;
import semantic.AST.block.GlobalBlock;
import semantic.AST.declaration.Declaration;
import semantic.AST.declaration.function.FunctionDcl;
import semantic.AST.declaration.variable.SimpleVarDcl;
import semantic.AST.expression.Expression;
import semantic.AST.expression.binary.arithmetic.*;
import semantic.AST.expression.constant.IntegerConst;
import semantic.AST.expression.constant.LongConst;
import semantic.AST.expression.variable.Variable;
import semantic.AST.statement.FuncReturn;
import semantic.AST.statement.assignment.Assign;
import semantic.symbolTable.DSCPs.GlobalVarDSCP;
import semantic.symbolTable.DSCPs.LocalVarDSCP;
import semantic.symbolTable.SymbolTableHandler;
import syntax.Lexical;

import java.util.ArrayList;

public class CodeGenerator implements syntax.CodeGenerator {
    private Lexical lexical;
    private SemanticStack semanticStack;

    public CodeGenerator(Lexical lexical) {
        this.lexical = lexical;
        semanticStack = new SemanticStack();
        semanticStack.push(GlobalBlock.getInstance());
    }

    public void doSemantic(String str) {
        switch (str) {
            case "mkSimpleVarDCL": {
                String name = (String) lexical.currentToken().getValue();
                Type type = SymbolTableHandler.getTypeFromName((String) semanticStack.pop());
                if (semanticStack.peek() instanceof GlobalBlock)
                    SymbolTableHandler.getInstance().addVariable(name,new GlobalVarDSCP(type,false,false));
                else
                    SymbolTableHandler.getInstance().addVariable(name,new LocalVarDSCP(type,false,
                            SymbolTableHandler.getInstance().getIndex(),false));
                semanticStack.push(new NOP(name));
                break;
            }
            case "div": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Divide(first, second));
                break;
            }
            case "minus": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Minus(first, second));
                break;
            }
            case "mult": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Multiply(first, second));
                break;
            }
            case "sum": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Sum(first, second));
                break;
            }
            case "assign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                semanticStack.push(new Assign(exp, var));
                break;
            }
            case "ssign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                semanticStack.push(new Assign(exp, var));
                break;
            }
            case "pushInt": {
                Object integerNum = lexical.currentToken().getValue();
                if (integerNum instanceof Integer)
                    semanticStack.push(new IntegerConst((Integer) integerNum));
                else
                    semanticStack.push(new LongConst((Long) integerNum));
                break;
            }
            case "pushDouble": {
                semanticStack.push(lexical.currentToken().getValue());
                break;
            }
            case "pushFloat": {
                semanticStack.push(lexical.currentToken().getValue());
                break;
            }
            case "push": {
                semanticStack.push(lexical.currentToken().getValue());
                break;
            }
            case "ush": {
                semanticStack.push(lexical.currentToken().getValue());
                break;
            }
            case "pop": {
                semanticStack.pop();
                break;
            }
            case "voidReturn": {
                Block block = (Block) semanticStack.pop();
                FunctionDcl functionDcl = SymbolTableHandler.getInstance().getLastFunction();
                FuncReturn funcReturn = new FuncReturn(null, functionDcl);
                functionDcl.addReturn(funcReturn);
                block.addOperation(funcReturn);
                semanticStack.push(block);
                break;
            }
            case "return": {
                Expression exp = (Expression) semanticStack.pop();
                Block block = (Block) semanticStack.pop();
                FunctionDcl functionDcl = SymbolTableHandler.getInstance().getLastFunction();
                FuncReturn funcReturn = new FuncReturn(exp, functionDcl);
                functionDcl.addReturn(funcReturn);
                block.addOperation(funcReturn);
                semanticStack.push(block);
                break;
            }
            case "addGlobalBlock": {
                Declaration declaration = (Declaration) semanticStack.pop();
                if (declaration instanceof FunctionDcl)
                    addFuncToGlobalBlock((FunctionDcl) declaration);
                else
                    GlobalBlock.getInstance().addDeclaration(declaration);
                break;
            }
            case "pushBlock": {  //begin
                semanticStack.push(new Block(new ArrayList<>()));
                break;
            }

        }
    }

    private void addFuncToGlobalBlock(FunctionDcl function) {
        if (GlobalBlock.getInstance().getDeclarationList().contains(function)) {
            int index = GlobalBlock.getInstance().getDeclarationList().indexOf(function);
            FunctionDcl lastFunc = (FunctionDcl) GlobalBlock.getInstance().getDeclarationList().get(index);
            if (lastFunc.getBlock() == null && function.getBlock() != null) {
                GlobalBlock.getInstance().getDeclarationList().remove(lastFunc);
                GlobalBlock.getInstance().addDeclaration(function);
            } else if (lastFunc.getBlock() != null && lastFunc.getBlock() == null) {
            } else
                throw new RuntimeException("the function is duplicate!!!");
        } else {
            GlobalBlock.getInstance().addDeclaration(function);
        }

    }

    public Node getResult() {
        return (Node) semanticStack.getFirst();
    }

}
class NOP implements Operation {

    String name;

    public NOP(String name) {
        this.name = name;
    }
    public NOP() {
    }

    @Override
    public void codegen(MethodVisitor mv, ClassWriter cw) {

    }
}
