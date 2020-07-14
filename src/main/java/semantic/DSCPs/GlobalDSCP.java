package semantic.DSCPs;

import lombok.Data;
import org.objectweb.asm.Type;

@Data
public abstract class GlobalDSCP extends DSCP{

    public GlobalDSCP(Type type, boolean isValid) {
        super(type, isValid);
    }
}
