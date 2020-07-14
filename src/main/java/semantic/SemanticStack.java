package semantic;

import java.util.ArrayDeque;

public class SemanticStack extends ArrayDeque<Object> {


    @Override
    public Object pop(){
        return this.removeFirst();
    }

    @Override
    public void push(Object var1) {
        this.addFirst(var1);
    }
}
