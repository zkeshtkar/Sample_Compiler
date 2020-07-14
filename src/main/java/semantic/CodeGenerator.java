package semantic;


import semantic.expression.Expression;
import semantic.expression.arithmetic.Multiply;
import syntax.Lexical;

public class CodeGenerator {
    private Lexical lexical;
    private SemanticStack semanticStack;

    public CodeGenerator(Lexical lexical) {
        this.lexical = lexical;
        semanticStack = new SemanticStack();
        //semanticStack.push(GlobalBlock.getInstance());
    }

    public void run(String str)
    {
        switch (str)
        {
            case "mult":
            {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Multiply(first, second));
              break;
            }
            case "add":
            {
                break;
            }

        }
    }
}
