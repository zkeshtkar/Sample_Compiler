package syntax.Parser;

import semantic.CodeGenerator;
import syntax.Lexical;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

enum Action {
    ERROR, SHIFT, GOTO, PUSH_GOTO, REDUCE, ACCEPT
}

class LLCell {
    private Action action;
    private int target;
    private List<String> functions;

    public LLCell(Action action, int target, List<String> functions) {
        this.action = action;
        this.target = target;
        this.functions = functions;
    }

    public Action getAction() {
        return action;
    }

    public int getTarget() {
        return target;
    }

    public List<String> getFunction() {
        return functions;
    }
}

public class Parser {
    public static String TABLE_DELIMITER = ",";

    private Lexical lexical;
    private CodeGenerator codeGenerator;
    private String[] symbols;
    private LLCell[][] parseTable;
    private int startNode;
    private Deque<Integer> parseStack = new ArrayDeque<>();

    private List<String> recoveryState;
    public Parser(Lexical lexical, CodeGenerator codeGenerator, String nptPath) {
        this.lexical = lexical;
        this.codeGenerator = codeGenerator;
        this.recoveryState = new ArrayList<>();
        try {
            Scanner in = new Scanner(new FileInputStream(nptPath));
            String[] tmpArr = in.nextLine().trim().split(" ");
            int rowSize = Integer.parseInt(tmpArr[0]);
            int colSize = Integer.parseInt(tmpArr[1]);
            startNode = Integer.parseInt(in.nextLine());
            symbols = in.nextLine().trim().split(TABLE_DELIMITER);
            for (String symbol : symbols) {
                System.out.println(symbol + " ");
            }

            parseTable = new LLCell[rowSize][colSize];
            for (int i = 0; i < rowSize; i++) {
                tmpArr = in.nextLine().trim().split(TABLE_DELIMITER);
                for (int j = 0; j < colSize; j++) {
                    String[] cellParts = tmpArr[j].split(" ");
                    Action action = Action.values()[Integer.parseInt(cellParts[0])];
                    int target = Integer.parseInt(cellParts[1]);
                    List<String> allFunctions;
                    if (cellParts[2].equals("NoSem")) {
                        allFunctions = new ArrayList<>();
                    } else {
                        allFunctions = Arrays.stream(cellParts[2].substring(1).split("[;]"))
                                .filter(s -> !s.isEmpty()).collect(Collectors.toList());
                    }
                    parseTable[i][j] = new LLCell(action, target, allFunctions);
                }
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Invalid .npt file.");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to load .npt file.", e);
        }
    }
    public void parse() {
        int tokenID = nextTokenID();
        int currentNode = startNode;
        boolean accepted = false;
        while (!accepted) {
            String tokenText = symbols[tokenID];
            LLCell cell = parseTable[currentNode][tokenID];
            switch (cell.getAction()) {
                case ERROR:
                    updateRecoveryState(currentNode, tokenText);
                    generateError("Unable to parse input");
                case SHIFT:
                    doSemantics(cell.getFunction());
                    tokenID = nextTokenID();
                    currentNode = cell.getTarget();
                    recoveryState.clear();
                    break;
                case GOTO:
                    updateRecoveryState(currentNode, tokenText);
                    doSemantics(cell.getFunction());
                    currentNode = cell.getTarget();
                    break;
                case PUSH_GOTO:
                    updateRecoveryState(currentNode, tokenText);
                    parseStack.push(currentNode);
                    currentNode = cell.getTarget();
                    break;
                case REDUCE:
                    if (parseStack.size() == 0) {
                        generateError("Unable to Reduce: token=" + tokenText + " node=" + currentNode);
                    }
                    updateRecoveryState(currentNode, tokenText);
                    int graphToken = cell.getTarget();
                    int preNode = parseStack.pop();
                    doSemantics(parseTable[preNode][graphToken].getFunction());
                    currentNode = parseTable[preNode][graphToken].getTarget();
                    break;
                case ACCEPT:
                    accepted = true;
                    break;
            }
        }
    }
    private void updateRecoveryState(int currentNode, String token) {
        List<String> availableTokens = new ArrayList<>();
        LLCell[] cellTokens = parseTable[currentNode];
        for (int i = 0; i < cellTokens.length; i++) {
            if (cellTokens[i].getAction() != Action.ERROR) {
                availableTokens.add(symbols[i]);
            }
        }
        recoveryState.add("At node " + currentNode + ": current token is " + token + " but except: " + availableTokens);
    }
}
