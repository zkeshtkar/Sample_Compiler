package semantic.DSCPs;

import lombok.Data;
import org.objectweb.asm.Type;
import semantic.AST.expression.Expression;

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

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
            this.type = type ;
    }
}
