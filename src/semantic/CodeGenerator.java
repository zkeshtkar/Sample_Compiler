package semantic;

import semantic.AST.Node;
import semantic.AST.Operation;
import semantic.AST.block.Block;
import semantic.AST.block.GlobalBlock;
import semantic.AST.declaration.Declaration;
import semantic.AST.declaration.function.FunctionDcl;
import semantic.AST.declaration.record.RecordDcl;
import semantic.AST.declaration.variable.ArrDcl;
import semantic.AST.declaration.variable.RecordVarDCL;
import semantic.AST.declaration.variable.SimpleVarDcl;
import semantic.AST.declaration.variable.VarDCL;
import semantic.AST.expression.*;
import semantic.AST.expression.binary.arithmetic.*;
import semantic.AST.expression.binary.conditional.*;
import semantic.AST.expression.constant.*;
import semantic.AST.expression.unary.*;
import semantic.AST.expression.variable.ArrayVar;
import semantic.AST.expression.variable.RecordVar;
import semantic.AST.expression.variable.SimpleVar;
import semantic.AST.expression.variable.Variable;
import semantic.AST.statement.Break;
import semantic.AST.statement.Condition.Case;
import semantic.AST.statement.Condition.If;
import semantic.AST.statement.Condition.Switch;
import semantic.AST.statement.Continue;
import semantic.AST.statement.FuncReturn;
import semantic.AST.statement.Println;
import semantic.AST.statement.assignment.*;
import semantic.AST.statement.loop.*;
import semantic.symbolTable.DSCPs.*;
import semantic.symbolTable.Scope;
import semantic.symbolTable.SymbolTable;
import semantic.symbolTable.SymbolTableHandler;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import syntax.Lexical;

import javax.print.attribute.standard.NumberUp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static lexical.scanner.yyline;

public class CodeGenerator implements syntax.CodeGenerator {

    private Lexical lexical;
    private SemanticStack semanticStack;
    private boolean assignFlag = false;
    private  ArrayList<Variable>variables ;

    public CodeGenerator(Lexical lexical) {
        this.lexical = lexical;
        semanticStack = new SemanticStack();
        semanticStack.push(GlobalBlock.getInstance());
        variables = new ArrayList<>();
    }

    public Node getResult() {
        return (Node) semanticStack.getFirst();
    }

    public void doSemantic(String sem) {
        switch (sem) {
            case "mkFlagAssignTrue":
            {
                assignFlag = true;
                Variable variable = (Variable) semanticStack.pop();
                if(variable instanceof SimpleVar)
                {
                    variables.add((SimpleVar)variable);
                }
                else if(variable instanceof ArrayVar)
                {
                    variables.add((ArrayVar)variable);
                }
                break;
            }
            /* --------------------- global --------------------- */
            case "push": {
                semanticStack.push(lexical.currentToken().getValue());
                break;
            }
            case "pop": {
                semanticStack.pop();
                break;
            }
            case "createFlag": {
                Byte flag = 0;
                semanticStack.push(flag);
                break;
            }
            case "createBlock": {
                semanticStack.push(new Block(new ArrayList<>()));
                break;
            }

            /* --------------------- declarations --------------------- */
            case "mkFuncDCL": {
                Type type = SymbolTableHandler.getTypeFromName((String) semanticStack.pop());
                FunctionDcl functionDcl = new FunctionDcl(type,
                        (String) lexical.currentToken().getValue(), null, new ArrayList<>());
                semanticStack.push(functionDcl);
                SymbolTableHandler.getInstance().setLastFunction(functionDcl);
                SymbolTableHandler.getInstance().addScope(Scope.FUNCTION);
                break;
            }
            case "addParameter": {
                String name = ((NOP) semanticStack.pop()).name;
                LocalDSCP dscp = (LocalDSCP) SymbolTableHandler.getInstance().getDescriptor(name);
                dscp.setValid(true);
                FunctionDcl function = (FunctionDcl) semanticStack.pop();
                function.addParameter(name, dscp);
                semanticStack.push(function);
                break;
            }
            case "completeFuncDCL": {
                Block block = (Block) semanticStack.pop();
                FunctionDcl function = (FunctionDcl) semanticStack.pop();
                function.setBlock(block);
                semanticStack.push(function);
                SymbolTableHandler.getInstance().setLastFunction(null);
                SymbolTableHandler.getInstance().popScope();
                break;
            }
            case "addFuncDCL": {
                FunctionDcl function = (FunctionDcl) semanticStack.pop();
                function.declare();
                semanticStack.push(function);
                break;
            }
            case "mkSimpleVarDCL": {
                String name = (String) lexical.currentToken().getValue();
                Type type = SymbolTableHandler.getTypeFromName((String) semanticStack.pop());
                if (semanticStack.peek() instanceof GlobalBlock)
                    SymbolTableHandler.getInstance().addVariable(name, new GlobalVarDSCP(type, false, false));
                else
                    SymbolTableHandler.getInstance().addVariable(name, new LocalVarDSCP(type, false,
                            SymbolTableHandler.getInstance().getIndex(), false));
                semanticStack.push(new NOP(name));
                break;
            }
            case "constTrue": {
                String varName = ((NOP) semanticStack.pop()).name;
                DSCP dscp = SymbolTableHandler.getInstance().getDescriptor(varName);
                dscp.setConstant(true);
                semanticStack.push(new NOP(varName));
                break;
            }
            case "pushBlock": {  //begin
                semanticStack.push(new Block(new ArrayList<>()));
                break;
            }
            case "addBlock": { //fill function's block
                Operation operation = (Operation) semanticStack.pop();
                Block block = (Block) semanticStack.pop();
                block.addOperation(operation);
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
            case "setValueToVar": {
                Expression exp = (Expression) semanticStack.pop();
                String name = ((NOP) semanticStack.pop()).name;
                DSCP dscp = SymbolTableHandler.getInstance().getDescriptor(name);
                SimpleVarDcl varDcl = new SimpleVarDcl(name, dscp.getType(), dscp.isConstant(), dscp instanceof GlobalDSCP);
                varDcl.setExp(exp);
                semanticStack.push(varDcl);
                break;
            }
            case "mkSimpleAutoVarDCL": {
                Expression exp = (Expression) semanticStack.pop();
                String varName = (String) semanticStack.pop();
                SimpleVarDcl varDcl;
                if (semanticStack.peek() instanceof GlobalBlock)
                    varDcl = new SimpleVarDcl(varName, "auto", false, true, exp);
                else
                    varDcl = new SimpleVarDcl(varName, "auto", false, false, exp);
                varDcl.declare();
                semanticStack.push(varDcl);
                break;
            }
            case "mkSimpleAutoConstVarDCL":
            {
                Expression exp = (Expression) semanticStack.pop();
                String varName = (String) semanticStack.pop();
                SimpleVarDcl varDcl;
                if (semanticStack.peek() instanceof GlobalBlock)
                    varDcl = new SimpleVarDcl(varName, "auto", true, true, exp);
                else
                    varDcl = new SimpleVarDcl(varName, "auto", true, false, exp);
                varDcl.declare();
                semanticStack.push(varDcl);
                break;
            }
            case "dimpp": {
                Byte flag = (Byte) semanticStack.pop();
                flag++;
                semanticStack.push(flag);
                break;
            }
            case "arrDCL": {
                String name = (String) lexical.currentToken().getValue();
                Byte flag = (Byte) semanticStack.pop();
                Type type = SymbolTableHandler.getTypeFromName((String) semanticStack.pop());
                if (semanticStack.peek() instanceof GlobalBlock)
                    ArrDcl.declare(name, type, new ArrayList<>(), flag, true);
                else
                    ArrDcl.declare(name, type, new ArrayList<>(), flag, false);
                semanticStack.push(new NOP(name));
                break;
            }
            case "mkArrayVarDCL": {
                Byte flag = (Byte) semanticStack.pop();
                List<Expression> expressionList = new ArrayList<>();
                int i = flag;
                while (i > 0) {
                    expressionList.add((Expression) semanticStack.pop());
                    i--;
                }
                Type type = SymbolTableHandler.getTypeFromName((String) semanticStack.pop());
                String name = ((NOP) semanticStack.pop()).name;
                DSCP dscp = SymbolTableHandler.getInstance().getDescriptor(name);
                if (!dscp.getType().equals(type))
                    throw new RuntimeException("Types don't match");
                ArrDcl arrDcl;
                if (semanticStack.peek() instanceof GlobalBlock) {
                    if (((GlobalArrDSCP) dscp).getDimNum() != flag)
                        throw new RuntimeException("Number of dimensions doesn't match");
                    arrDcl = new ArrDcl(name, type, true, flag);
                    ((GlobalArrDSCP) dscp).setDimList(expressionList);
                } else {
                    if (((LocalArrDSCP) dscp).getDimNum() != flag)
                        throw new RuntimeException("Number of dimensions doesn't match");
                    arrDcl = new ArrDcl(name, type, false, flag);
                    ((LocalArrDSCP) dscp).setDimList(expressionList);
                }
                arrDcl.setDimensions(expressionList);
                semanticStack.push(arrDcl);
                break;
            }
            case "mkAutoArrVarDCL": {
                Byte flag = (Byte) semanticStack.pop();
                Byte dim2 = flag;
                List<Expression> expressionList = new ArrayList<>();
                while (flag > 0) {
                    expressionList.add((Expression) semanticStack.pop());
                    flag--;
                }
                Type type = SymbolTableHandler.getTypeFromName((String) semanticStack.pop());
                String name = (String) semanticStack.pop();
                ArrDcl arrDcl;
                if (semanticStack.peek() instanceof GlobalBlock) {
                    arrDcl = new ArrDcl(name, type, true, expressionList.size());
                    arrDcl.declare(name, type, expressionList, expressionList.size(), true);
                } else {
                    arrDcl = new ArrDcl(name, type, false, expressionList.size());
                    arrDcl.declare(name, type, expressionList, expressionList.size(), false);
                }
                arrDcl.setDimensions(expressionList);
                semanticStack.push(arrDcl);
                break;
            }

            case "mkNewArray":{
                Byte flag = (Byte) semanticStack.pop();
                List<Expression> expressionList = new ArrayList<>();
                int i = flag;
                while (i > 0) {
                    expressionList.add((Expression) semanticStack.pop());
                    i--;
                }
                Type type = SymbolTableHandler.getTypeFromName((String) semanticStack.pop());
                String name = ((ArrayVar) semanticStack.pop()).getName();
                DSCP dscp = SymbolTableHandler.getInstance().getDescriptor(name);
                if (!dscp.getType().equals(type))
                    throw new RuntimeException("Types don't match");
                ArrDcl arrDcl;
                if (dscp.isConstant()){
                    throw new RuntimeException("create new array is impossible");
                }

                if ((semanticStack.peek()) instanceof GlobalBlock) {
                    if (((GlobalArrDSCP) dscp).getDimNum() != flag)
                        throw new RuntimeException("Number of dimensions doesn't match");
                    arrDcl = new ArrDcl(name, type, true, flag);
                    ((GlobalArrDSCP) dscp).setDimList(expressionList);
                } else {
                    if (((LocalArrDSCP) dscp).getDimNum() != flag)
                        throw new RuntimeException("Number of dimensions doesn't match");
                    arrDcl = new ArrDcl(name, type, false, flag);
                    ((LocalArrDSCP) dscp).setDimList(expressionList);
                }
                arrDcl.setDimensions(expressionList);
                semanticStack.push(arrDcl);
                break;
            }
            case "DCLWithoutAssign": {
                String name = ((NOP) semanticStack.pop()).name;
                DSCP dscp = SymbolTableHandler.getInstance().getDescriptor(name);
                SimpleVarDcl varDcl = new SimpleVarDcl(name, dscp.getType(), dscp.isConstant(), dscp instanceof GlobalDSCP);
                semanticStack.push(varDcl);
                break;
            }
            case "multiVarDCL": {
                String name = ((VarDCL) semanticStack.peek()).getName();
                DSCP dscp = SymbolTableHandler.getInstance().getDescriptor(name);
                Type type = dscp.getType();
                semanticStack.push(type.getDescriptor());
                break;
            }
            /* --------------------- binary expressions --------------------- */
            /* ---------------------- Arithmetic ------------------------ */
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
            case "rmn": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Remainder(first, second));
                break;
            }
            case "sum": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Sum(first, second));
                break;
            }
            /* ---------------------- conditional ------------------------- */
            case "and": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new And(first, second));
                break;
            }
            case "andBit": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new AndBit(first, second));
                break;
            }
            case "biggerAndEqual": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new BiggerEqual(first, second));
                break;
            }
            case "biggerThan": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new BiggerThan(first, second));
                break;
            }
            case "equal": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Equal(first, second));
                break;
            }
            case "lessAndEqual": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new LessEqual(first, second));
                break;
            }
            case "lessThan": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new LessThan(first, second));
                break;
            }
            case "notEqual": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new NotEqual(first, second));
                break;
            }
            case "or": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new OR(first, second));
                break;
            }
            case "orBit": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new ORBit(first, second));
                break;
            }
            case "xorBit": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new XORBit(first, second));
                break;
            }
            /* -------------------------- Unary   ---------------------------- */
            case "bitwiseNot": {
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new BitwiseNot(exp));
                break;
            }
            case "cast": {
                Expression exp = (Expression) semanticStack.pop();
                Type newType = SymbolTableHandler.getTypeFromName((String) semanticStack.pop());
                semanticStack.push(new Cast(exp, newType));
                break;
            }
            case "negative": {
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new Negative(exp));
                break;
            }
            case "not": {
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new Not(exp));
                break;
            }
            case "postmm": {
                Variable var = (Variable) semanticStack.pop();
                if (var instanceof RecordVar)
                    throw new RuntimeException("Undefined operand for record type" + " in line " + yyline);
                checkAssign(var);
                semanticStack.push(new PostMM(var));
                break;
            }
            case "postpp": {
                Variable var = (Variable) semanticStack.pop();
                if (var instanceof RecordVar)
                    throw new RuntimeException("Undefined operand for record type"  + " in line "+ yyline);
                checkAssign(var);
                semanticStack.push(new PostPP(var));
                break;
            }
            case "premm": {
                Variable var = (Variable) semanticStack.pop();
                if (var instanceof RecordVar)
                    throw new RuntimeException("Undefined operand for record type" + " in line "+ yyline);
                checkAssign(var);
                semanticStack.push(new PreMM(var));
                break;
            }
            case "prepp": {
                Variable var = (Variable) semanticStack.pop();
                if (var instanceof RecordVar)
                    throw new RuntimeException("Undefined operand for record type" + " in line "+ yyline);
                checkAssign(var);
                semanticStack.push(new PrePP(var));
                break;
            }
            /* -------------------------- Const ---------------------------- */
            case "pushReal": {
                Object realNum = lexical.currentToken().getValue();
                if (realNum instanceof Float)
                    semanticStack.push(new FloatConst((Float) realNum));
                else
                    semanticStack.push(new DoubleConst((Double) realNum));
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
            case "pushBool": {
                Object value = lexical.currentToken().getValue();
                semanticStack.push(new BooleanConst((Boolean) value));
                break;
            }
            case "pushChar": {
                semanticStack.push(new CharConst((Character) lexical.currentToken().getValue()));
                break;
            }
            case "pushString": {
                semanticStack.push(new StringConst((String) lexical.currentToken().getValue()));
                break;
            }
            /* -------------------------- variable ---------------------------- */
            case "pushVar": {
                String name = (String) lexical.currentToken().getValue();
                if (SymbolTableHandler.getInstance().getFuncNames().contains(name)) {
                    semanticStack.push(name);
                    break;
                }
                DSCP dscp = SymbolTableHandler.getInstance().getDescriptor(name);
                if (dscp instanceof GlobalVarDSCP || dscp instanceof LocalVarDSCP)
                    semanticStack.push(new SimpleVar(name, dscp.getType()));
                else if (dscp instanceof GlobalArrDSCP || dscp instanceof LocalArrDSCP)
                    semanticStack.push(new ArrayVar(name, new ArrayList<>(), dscp.getType()));
                else if (dscp instanceof RecordDSCP)
                    semanticStack.push(new RecordVar(name, null, null));
                break;
            }
            case "flagpp": {
                Expression exp = (Expression) semanticStack.pop();
                Byte flag = (Byte) semanticStack.pop();
                flag++;
                semanticStack.push(exp);
                semanticStack.push(flag);
                break;
            }
            case "pushArrayVar": {
                Byte flag = (Byte) semanticStack.pop();
                List<Expression> expressionList = new ArrayList<>();
                while (flag > 0) {
                    expressionList.add((Expression) semanticStack.pop());
                    flag--;
                }
                ArrayVar var = (ArrayVar) semanticStack.pop();
                semanticStack.push(new ArrayVar(var.getName(), expressionList, var.getType()));
                break;
            }
            /* -------------------------- Assignment -------------------------- */
            case "assign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                if(assignFlag)
                {
                    for (Variable variable : variables) {
                        checkAssign(variable);
                        semanticStack.push(new Assign(exp, variable));
                        Operation operation = (Operation) semanticStack.pop();
                        Block block = (Block) semanticStack.pop();
                        block.addOperation(operation);
                        semanticStack.push(block);
                    }
                    assignFlag = false ;
                }
                checkAssign(var);
                semanticStack.push(new Assign(exp, var));
                break;
            }
            case "sumAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new SumAssign(exp, var));
                break;
            }
            case "minAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new MinAssign(exp, var));
                break;
            }
            case "divAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new DivAssign(exp, var));
                break;
            }
            case "mulAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new MulAssign(exp, var));
                break;
            }
            case "rmnAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new RmnAssign(exp, var));
                break;
            }
            case "check2types": {
                Type type = SymbolTableHandler.getTypeFromName((String) semanticStack.pop());
                Variable variable = (Variable) semanticStack.pop();
                if (!(variable instanceof ArrayVar))
                    throw new RuntimeException("You can't new a simple variable");
                if (variable.getType() != null && !type.equals(variable.getType()))
                    throw new RuntimeException("types don't match");
                semanticStack.push(variable);
                break;
            }
            case "setCheckDim": {
                Byte flag = (Byte) semanticStack.pop();
                List<Expression> expressionList = new ArrayList<>();
                int i = flag;
                while (i > 0) {
                    expressionList.add((Expression) semanticStack.pop());
                    i--;
                }
                ArrayVar var = (ArrayVar) semanticStack.pop();
                if (var.getDSCP() instanceof GlobalArrDSCP)
                    if (((GlobalArrDSCP) var.getDSCP()).getDimNum() != flag)
                        throw new RuntimeException("Number of dimensions doesn't match");
                if (var.getDSCP() instanceof LocalArrDSCP)
                    if (((LocalArrDSCP) var.getDSCP()).getDimNum() != flag)
                        throw new RuntimeException("Number of dimensions doesn't match");
                var.setDimensions(expressionList);
                semanticStack.push(new NOP());
                break;
            }
            /* ---------------------- functions ---------------------------- */
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
            case "break": {
                semanticStack.push(new Break());
                break;
            }
            case "continue": {
                semanticStack.push(new Continue());
                break;
            }
            case "funcCall": {
                String name = (String) semanticStack.pop();
                semanticStack.push(new FuncCall(name, new ArrayList<>()));
                break;
            }
            case "addParam": {
                Expression exp = (Expression) semanticStack.pop();
                FuncCall funcCall = (FuncCall) semanticStack.pop();
                funcCall.addParam(exp);
                semanticStack.push(funcCall);
                break;
            }
            /* --------------------- loops --------------------- */
            /* --------------------- for --------------------- */
            case "changeTop": {
                Expression exp = (Expression) semanticStack.pop();
                Byte flag = (Byte) semanticStack.pop();
                semanticStack.push(exp);
                semanticStack.push(flag);
                break;
            }
            case "trueInitFlag": {
                InitExp initExp = (InitExp) semanticStack.pop();
                semanticStack.pop();
                Byte flag = 1;
                semanticStack.push(initExp);
                semanticStack.push(flag);
                break;
            }
            case "trueStepFlag": {
                StepExp stepExp = (StepExp) semanticStack.pop();
                Byte flag = (Byte) semanticStack.pop();
                if (flag == 0)
                    flag = 2;
                else
                    flag = 3;
                semanticStack.push(stepExp);
                semanticStack.push(flag);
                break;
            }
            case "for": {
                Block block = (Block) semanticStack.pop();
                Byte flag = (Byte) semanticStack.pop();
                InitExp initExp = null;
                Expression exp = null;
                StepExp stepExp = null;
                if (flag == 0) {
                    exp = (Expression) semanticStack.pop();
                } else if (flag == 1) {
                    exp = (Expression) semanticStack.pop();
                    initExp = (InitExp) semanticStack.pop();
                } else if (flag == 2) {
                    stepExp = (StepExp) semanticStack.pop();
                    exp = (Expression) semanticStack.pop();
                } else {
                    stepExp = (StepExp) semanticStack.pop();
                    exp = (Expression) semanticStack.pop();
                    initExp = (InitExp) semanticStack.pop();
                }
                semanticStack.push(new For(block, initExp, exp, stepExp));
                break;
            }
            /*-----------------------foreach-------------------------*/
            case "foreach":
            {
                Block block = ((Block) semanticStack.pop()) ;
                ArrayVar arr = (ArrayVar) semanticStack.pop();
                NOP no = (NOP) semanticStack.pop();
                semanticStack.push(new Foreach(block,arr,no));

                break;
            }
            /* --------------------- repeat --------------------- */
            case "repeat": {
                Expression exp = (Expression) semanticStack.pop();
                Block block = (Block) semanticStack.pop();
                semanticStack.push(new Repeat(block, exp));
                break;
            }
            /* --------------------- conditions --------------------- */
            /* --------------------- if --------------------- */
            case "if": {
                Block block = (Block) semanticStack.pop();
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new If(exp, block, null));
                break;
            }
            case "else": {
                Block block = (Block) semanticStack.pop();
                If ifSt = (If) semanticStack.pop();
                ifSt.setElseBlock(block);
                semanticStack.push(ifSt);
                break;
            }
            /* --------------------- switch --------------------- */
            case "switch": {
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new Switch(exp, new ArrayList<>(), null));
                break;
            }
            case "addCase": {
                Block block = (Block) semanticStack.pop();
                IntegerConst intConst = (IntegerConst) semanticStack.pop();
                Switch switchSt = (Switch) semanticStack.pop();
                Case caseSt = new Case(intConst, block);
                switchSt.addCase(caseSt);
                semanticStack.push(switchSt);
                break;
            }
            case "addDefault": {
                Block defaultBlock = (Block) semanticStack.pop();
                Switch switchSt = (Switch) semanticStack.pop();
                switchSt.setDefaultBlock(defaultBlock);
                semanticStack.push(switchSt);
                break;
            }
            /* --------------------- special method calls --------------------- */
            case "print": {
                Expression expression = (Expression) semanticStack.pop();
                semanticStack.push(new Println(expression));
                break;
            }
            case "printLine": {
                semanticStack.push(new Println(null));
                break;
            }
            case "input": {
                String type = (String) lexical.currentToken().getValue();
                semanticStack.push(new Input(SymbolTableHandler.getTypeFromName(type)));
                break;
            }
            case "inputLine": {
                semanticStack.push(new Input(null));
                break;
            }
            case "len": {
                Expression expression = (Expression) semanticStack.pop();
                semanticStack.push(new Len(expression));
                break;
            }
            case "sizeof": {
                Variable variable = (Variable) semanticStack.pop();
                String baseType = variable.getType().toString();
                semanticStack.push(new Sizeof(baseType));
                break;
            }
            /*---------------------record---------------------*/
            case "record":{
                Block block = (Block) semanticStack.pop();
                String name = (String) semanticStack.pop();
                RecordDcl recordDcl = new RecordDcl(block,name);
                semanticStack.push(recordDcl);
                SymbolTableHandler.getInstance().addRecord(recordDcl);
                break;
            }
            case "newRecord":{
                String type2 = (String) lexical.currentToken().getValue();
                String name = (String) semanticStack.pop();
                String type1 = (String) semanticStack.pop();
                if (!type1.equals(type2)){
                    throw new RuntimeException("Types are not same");
                }
                if (!SymbolTableHandler.getInstance().isRecordDefined(name)){
                    throw new RuntimeException("this record type is not defined");
                }
                RecordDcl type = SymbolTableHandler.getInstance().getRecordDcls().get(type1);
                RecordDSCP recdscp = new RecordDSCP(type, true, SymbolTableHandler.getInstance().getIndex());
                SymbolTableHandler.getInstance().addVariable(name,recdscp);
                RecordVarDCL recordVarDCL = new RecordVarDCL(type, name);
                semanticStack.push(recordVarDCL);
                break;
            }
            case "pushRecordVar" :{
                String field = (String) lexical.currentToken().getValue();
                RecordVar recordVar = (RecordVar)(semanticStack.pop());
                DSCP dscp = SymbolTableHandler.getInstance().getDescriptor(recordVar.getRecord());
                boolean isfield = false;
               RecordVar recvar = new RecordVar(null, null, null);
                for (VarDCL v:((RecordDSCP)(dscp)).getRecordtype().getVarDCLS()) {
                    if (v.getName().equals(field)) {
                        isfield = true;
                        recvar = new RecordVar(recordVar.getRecord(), v.getName(), v.getType());
                    }
                }
                if (!isfield){
                    throw new RuntimeException("this field is not existed");
                }
                semanticStack.push(recvar);
                break;
            }
            default:
                throw new RuntimeException("Illegal semantic function: " + sem + " in line "+ yyline);

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

    private void checkAssign(Variable variable) {
        if (variable instanceof ArrayVar) {
            ArrayVar var = (ArrayVar) variable;
            int numberOfExp = var.getDimensions().size();
            DSCP dscp = SymbolTableHandler.getInstance().getDescriptor(var.getName());
            if (dscp instanceof GlobalArrDSCP) {
                if (((GlobalArrDSCP) dscp).getDimNum() != numberOfExp)
                    throw new RuntimeException("you can't assign an expression to array");
            }
            if (dscp instanceof LocalArrDSCP) {
                if (((LocalArrDSCP) dscp).getDimNum() != numberOfExp)
                    throw new RuntimeException("you can't assign an expression to array");
            }
        }
    }
}

