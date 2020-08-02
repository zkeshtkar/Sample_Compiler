package semantic.symbolTable.DSCPs;

import lombok.Data;
import org.objectweb.asm.Type;

@Data
public abstract class GlobalDSCP extends DSCP{

    public GlobalDSCP(Type type, boolean isValid) {
        super(type, isValid);
    }

    public GlobalDSCP(Type type, boolean isValid, boolean constant) {
        super(type, isValid, constant);
    }
}
