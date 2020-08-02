package semantic.symbolTable.DSCPs;

import semantic.AST.expression.Expression;
import lombok.Data;
import org.objectweb.asm.Type;
import java.util.List;

@Data
public class LocalArrDSCP extends LocalDSCP {
    protected List<Expression> dimList;
    protected int dimNum;

    public LocalArrDSCP(Type type, boolean isValid, int index, List<Expression> dimList, int dimNum) {
        super(type, isValid, index);
        this.dimList = dimList;
        this.dimNum = dimNum;
    }

    public LocalArrDSCP(Type type, boolean isValid, int index, int dimNum) {
        super(type, isValid, index);
        this.dimNum = dimNum;
    }

    public LocalArrDSCP(Type type, boolean isValid, boolean constant, int index, List<Expression> dimList, int dimNum) {
        super(type, isValid, constant, index);
        this.dimList = dimList;
        this.dimNum = dimNum;
    }

    public int getDimNum() {
        return dimNum;
    }

    public List<Expression> getDimList() {
        return dimList;
    }

    public void setDimList(List<Expression> dimList) {
        this.dimList = dimList;
    }
}
