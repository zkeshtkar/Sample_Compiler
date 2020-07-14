package semantic.expression;
import org.objectweb.asm.Type;
import semantic.Node;


abstract public class Expression implements Node {
        protected Type type;


        public Type getType() {
            if(type == null)
                throw new RuntimeException("The type of expression is not set!");
            return type;
        }
    }

