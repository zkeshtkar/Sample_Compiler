package syntax;

import lexical.MySymbol;

public interface Lexical {
    MySymbol currentToken();
    String nextToken();
}