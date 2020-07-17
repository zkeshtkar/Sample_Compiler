package semantic.DSCPs;
import lombok.Data;
import org.objectweb.asm.Type;
import semantic.AST.expression.Expression;

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

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type ;
    }
}
