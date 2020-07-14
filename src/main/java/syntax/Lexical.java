
package syntax;
import Lexical.MySymbol;



public interface Lexical {
    MySymbol currentToken();
    String nextToken();
}
