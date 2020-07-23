package src.semantic.symbolTable;


import semantic.symbolTable.DSCPs.DSCP;
import lombok.Data;

import java.util.HashMap;

@Data
public class SymbolTable extends HashMap<String, DSCP> {

    private int index = 0;
    private Scope typeOfScope;

    public void addIndex(int add){
        index++;
    }

    public int getIndex (){
        return index;
    }

    public Scope getTypeOfScope() {
        return typeOfScope;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setTypeOfScope(Scope typeOfScope) {
        this.typeOfScope = typeOfScope;
    }
}
