package src.semantic.AST.statement.loop;

import semantic.AST.block.Block;
import semantic.AST.statement.Statement;
import lombok.Data;
import org.objectweb.asm.Label;

@Data
public abstract class Loop extends Statement{
    protected Block block;
    Label startLoop = new Label();
    Label end = new Label();
    Loop(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    public Label getStartLoop() {
        return startLoop;
    }

    public Label getEnd() {
        return end;
    }
}
