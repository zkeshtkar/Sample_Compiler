package semantic.DSCPs;

import lombok.Data;
import org.objectweb.asm.Type;

@Data
public class GlobalVarDSCP extends GlobalDSCP {


    public GlobalVarDSCP(Type type, boolean isValid, boolean constant) {
        super(type, isValid);
        this.constant = constant;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }
}
