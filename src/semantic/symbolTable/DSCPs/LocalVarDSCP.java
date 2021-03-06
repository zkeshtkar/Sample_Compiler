package semantic.symbolTable.DSCPs;

import lombok.Data;
import org.objectweb.asm.Type;

@Data
public class LocalVarDSCP extends LocalDSCP {


    public LocalVarDSCP(Type type, boolean isValid, int index, boolean constant) {
        super(type, isValid, index);
        this.constant = constant;
    }

}
