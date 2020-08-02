package semantic.symbolTable.DSCPs;

import semantic.AST.declaration.record.RecordDcl;

public class RecordDSCP extends LocalDSCP {
    protected RecordDcl recordtype;


    public RecordDSCP(RecordDcl recordtype, boolean isValid, int index) {
        super(isValid, index);
        this.recordtype = recordtype;

    }

    public RecordDcl getRecordtype() {
        return recordtype;
    }


    public int getIndex() {
        return index;
    }

    public void setRecordtype(RecordDcl recordtype) {
        this.recordtype = recordtype;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
