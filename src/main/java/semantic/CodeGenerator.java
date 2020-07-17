package semantic;


import semantic.AST.Operation;
import semantic.AST.block.Block;
import semantic.AST.block.GlobalBlock;
import semantic.AST.declaration.Declaration;
import semantic.AST.declaration.function.FunctionDcl;
import semantic.AST.expression.Expression;
import semantic.AST.expression.arithmetic.*;
import semantic.AST.expression.variable.Variable;
import semantic.AST.statement.FuncReturn;
import semantic.AST.statement.assignment.Assign;
import semantic.symbolTable.SymbolTableHandler;
import syntax.Lexical;

import java.util.ArrayList;

public class CodeGenerator {
    private Lexical lexical;
    private SemanticStack semanticStack;

    public CodeGenerator(Lexical lexical) {
        this.lexical = lexical;
        semanticStack = new SemanticStack();
        semanticStack.push(GlobalBlock.getInstance());
    }

    public void run(String str)
    {
        switch (str)
        {
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
            case "pushInt": {
                semanticStack.push(lexical.currentToken().getValue());
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
}
