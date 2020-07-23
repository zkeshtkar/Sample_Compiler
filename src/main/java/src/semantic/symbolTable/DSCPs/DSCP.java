package src.semantic.symbolTable.DSCPs;

import lombok.Data;
import org.objectweb.asm.Type;

@Data
public abstract class DSCP {
    protected Type type;
    protected boolean isValid;
    protected boolean constant;

    public DSCP(Type type, boolean isValid) {
        this.type = type;
        this.isValid = isValid;
    }

    public Type getType() {
        return type;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    public boolean isConstant() {
        return constant;
    }
}
