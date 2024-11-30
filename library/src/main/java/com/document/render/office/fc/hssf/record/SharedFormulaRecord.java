

package com.document.render.office.fc.hssf.record;

import com.document.render.office.fc.hssf.formula.Formula;
import com.document.render.office.fc.hssf.formula.SharedFormula;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.util.CellRangeAddress8Bit;
import com.document.render.office.fc.ss.SpreadsheetVersion;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class SharedFormulaRecord extends SharedValueRecordBase {
    public final static short sid = 0x04BC;

    private int field_5_reserved;
    private Formula field_7_parsed_expr;


    public SharedFormulaRecord() {
        this(new CellRangeAddress8Bit(0, 0, 0, 0));
    }

    private SharedFormulaRecord(CellRangeAddress8Bit range) {
        super(range);
        field_7_parsed_expr = Formula.create(Ptg.EMPTY_PTG_ARRAY);
    }


    public SharedFormulaRecord(RecordInputStream in) {
        super(in);
        field_5_reserved = in.readShort();
        int field_6_expression_len = in.readShort();
        int nAvailableBytes = in.available();
        field_7_parsed_expr = Formula.read(field_6_expression_len, in, nAvailableBytes);
    }

    protected void serializeExtraData(LittleEndianOutput out) {
        out.writeShort(field_5_reserved);
        field_7_parsed_expr.serialize(out);
    }

    protected int getExtraDataSize() {
        return 2 + field_7_parsed_expr.getEncodedSize();
    }



    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[SHARED FORMULA (").append(HexDump.intToHex(sid)).append("]\n");
        buffer.append("    .range      = ").append(getRange().toString()).append("\n");
        buffer.append("    .reserved    = ").append(HexDump.shortToHex(field_5_reserved)).append("\n");

        Ptg[] ptgs = field_7_parsed_expr.getTokens();
        for (int k = 0; k < ptgs.length; k++) {
            buffer.append("Formula[").append(k).append("]");
            Ptg ptg = ptgs[k];
            buffer.append(ptg.toString()).append(ptg.getRVAType()).append("\n");
        }

        buffer.append("[/SHARED FORMULA]\n");
        return buffer.toString();
    }

    public short getSid() {
        return sid;
    }


    public Ptg[] getFormulaTokens(FormulaRecord formula) {
        int formulaRow = formula.getRow();
        int formulaColumn = formula.getColumn();

        if (!isInRange(formulaRow, formulaColumn)) {
            throw new RuntimeException("Shared Formula Conversion: Coding Error");
        }

        SharedFormula sf = new SharedFormula(SpreadsheetVersion.EXCEL97);
        return sf.convertSharedFormulas(field_7_parsed_expr.getTokens(), formulaRow, formulaColumn);
    }

    public Object clone() {
        SharedFormulaRecord result = new SharedFormulaRecord(getRange());
        result.field_5_reserved = field_5_reserved;
        result.field_7_parsed_expr = field_7_parsed_expr.copy();
        return result;
    }

    public boolean isFormulaSame(SharedFormulaRecord other) {
        return field_7_parsed_expr.isSame(other.field_7_parsed_expr);
    }
}
