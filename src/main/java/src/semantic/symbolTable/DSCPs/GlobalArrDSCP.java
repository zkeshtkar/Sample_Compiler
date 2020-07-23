package src.semantic.symbolTable.DSCPs;

import semantic.AST.expression.Expression;
import lombok.Data;
import org.objectweb.asm.Type;
import java.util.List;

@Data
public class GlobalArrDSCP extends GlobalDSCP {

    protected List<Expression> dimList;
    protected int dimNum;

    public GlobalArrDSCP(Type type, boolean isValid, List<Expression> dimList, int dimNum) {
        super(type, isValid);
        this.dimList = dimList;
        this.dimNum = dimNum;
    }

    public GlobalArrDSCP(Type type, boolean isValid, int dimNum) {
        super(type, isValid);
        this.dimNum = dimNum;
    }

    public List<Expression> getDimList() {
        return dimList;
    }

    public int getDimNum() {
        return dimNum;
    }

    public void setDimList(List<Expression> dimList) {
        this.dimList = dimList;
    }

}
