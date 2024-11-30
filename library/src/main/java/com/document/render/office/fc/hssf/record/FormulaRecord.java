

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.formula.Formula;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.ss.model.baseModel.Cell;



public final class FormulaRecord extends CellRecord {

    @Keep
    public static final short sid = 0x0006;
    private static final BitField alwaysCalc = BitFieldFactory.getInstance(0x0001);
    private static final BitField calcOnLoad = BitFieldFactory.getInstance(0x0002);
    private static final BitField sharedFormula = BitFieldFactory.getInstance(0x0008);
    private static int FIXED_SIZE = 14;
    private double field_4_value;
    private short field_5_options;

    private int field_6_zero;
    private Formula field_8_parsed_expr;

    private SpecialCachedValue specialCachedValue;



    public FormulaRecord() {
        field_8_parsed_expr = Formula.create(Ptg.EMPTY_PTG_ARRAY);
    }

    public FormulaRecord(RecordInputStream ris) {
        super(ris);
        LittleEndianInput in = ris;
        long valueLongBits = in.readLong();
        field_5_options = in.readShort();
        specialCachedValue = SpecialCachedValue.create(valueLongBits);
        if (specialCachedValue == null) {
            field_4_value = Double.longBitsToDouble(valueLongBits);
        }

        field_6_zero = in.readInt();

        int field_7_expression_len = in.readShort();
        int nBytesAvailable = in.available();
        field_8_parsed_expr = Formula.read(field_7_expression_len, in, nBytesAvailable);
    }

    public void setCachedResultTypeEmptyString() {
        specialCachedValue = SpecialCachedValue.createCachedEmptyValue();
    }

    public void setCachedResultTypeString() {
        specialCachedValue = SpecialCachedValue.createForString();
    }

    public void setCachedResultErrorCode(int errorCode) {
        specialCachedValue = SpecialCachedValue.createCachedErrorCode(errorCode);
    }

    public void setCachedResultBoolean(boolean value) {
        specialCachedValue = SpecialCachedValue.createCachedBoolean(value);
    }


    public boolean hasCachedResultString() {
        if (specialCachedValue == null) {
            return false;
        }
        return specialCachedValue.getTypeCode() == SpecialCachedValue.STRING;
    }

    public int getCachedResultType() {
        if (specialCachedValue == null) {
            return Cell.CELL_TYPE_NUMERIC;
        }
        return specialCachedValue.getValueType();
    }

    public boolean getCachedBooleanValue() {
        return specialCachedValue.getBooleanValue();
    }

    public int getCachedErrorValue() {
        return specialCachedValue.getErrorValue();
    }


    public double getValue() {
        return field_4_value;
    }


    public void setValue(double value) {
        field_4_value = value;
        specialCachedValue = null;
    }


    public short getOptions() {
        return field_5_options;
    }


    public void setOptions(short options) {
        field_5_options = options;
    }

    public boolean isSharedFormula() {
        return sharedFormula.isSet(field_5_options);
    }

    public void setSharedFormula(boolean flag) {
        field_5_options =
                sharedFormula.setShortBoolean(field_5_options, flag);
    }

    public boolean isAlwaysCalc() {
        return alwaysCalc.isSet(field_5_options);
    }

    public void setAlwaysCalc(boolean flag) {
        field_5_options =
                alwaysCalc.setShortBoolean(field_5_options, flag);
    }

    public boolean isCalcOnLoad() {
        return calcOnLoad.isSet(field_5_options);
    }

    public void setCalcOnLoad(boolean flag) {
        field_5_options =
                calcOnLoad.setShortBoolean(field_5_options, flag);
    }


    public Ptg[] getParsedExpression() {
        return field_8_parsed_expr.getTokens();
    }

    public void setParsedExpression(Ptg[] ptgs) {
        field_8_parsed_expr = Formula.create(ptgs);
    }

    public Formula getFormula() {
        return field_8_parsed_expr;
    }

    public short getSid() {
        return sid;
    }

    @Override
    protected int getValueDataSize() {
        return FIXED_SIZE + field_8_parsed_expr.getEncodedSize();
    }

    @Override
    protected void serializeValue(LittleEndianOutput out) {

        if (specialCachedValue == null) {
            out.writeDouble(field_4_value);
        } else {
            specialCachedValue.serialize(out);
        }

        out.writeShort(getOptions());

        out.writeInt(field_6_zero);
        field_8_parsed_expr.serialize(out);
    }

    @Override
    protected String getRecordName() {
        return "FORMULA";
    }

    @Override
    protected void appendValueText(StringBuilder sb) {
        sb.append("  .value	 = ");
        if (specialCachedValue == null) {
            sb.append(field_4_value).append("\n");
        } else {
            sb.append(specialCachedValue.formatDebugString()).append("\n");
        }
        sb.append("  .options   = ").append(HexDump.shortToHex(getOptions())).append("\n");
        sb.append("    .alwaysCalc= ").append(isAlwaysCalc()).append("\n");
        sb.append("    .calcOnLoad= ").append(isCalcOnLoad()).append("\n");
        sb.append("    .shared    = ").append(isSharedFormula()).append("\n");
        sb.append("  .zero      = ").append(HexDump.intToHex(field_6_zero)).append("\n");

        Ptg[] ptgs = field_8_parsed_expr.getTokens();
        for (int k = 0; k < ptgs.length; k++) {
            if (k > 0) {
                sb.append("\n");
            }
            sb.append("    Ptg[").append(k).append("]=");
            Ptg ptg = ptgs[k];
            sb.append(ptg.toString()).append(ptg.getRVAType());
        }
    }

    public Object clone() {
        FormulaRecord rec = new FormulaRecord();
        copyBaseFields(rec);
        rec.field_4_value = field_4_value;
        rec.field_5_options = field_5_options;
        rec.field_6_zero = field_6_zero;
        rec.field_8_parsed_expr = field_8_parsed_expr;
        rec.specialCachedValue = specialCachedValue;
        return rec;
    }


    private static final class SpecialCachedValue {
        public static final int STRING = 0;
        public static final int BOOLEAN = 1;
        public static final int ERROR_CODE = 2;
        public static final int EMPTY = 3;

        private static final long BIT_MARKER = 0xFFFF000000000000L;
        private static final int VARIABLE_DATA_LENGTH = 6;
        private static final int DATA_INDEX = 2;
        private final byte[] _variableData;

        private SpecialCachedValue(byte[] data) {
            _variableData = data;
        }


        @Keep
        public static SpecialCachedValue create(long valueLongBits) {
            if ((BIT_MARKER & valueLongBits) != BIT_MARKER) {
                return null;
            }

            byte[] result = new byte[VARIABLE_DATA_LENGTH];
            long x = valueLongBits;
            for (int i = 0; i < VARIABLE_DATA_LENGTH; i++) {
                result[i] = (byte) x;
                x >>= 8;
            }
            switch (result[0]) {
                case STRING:
                case BOOLEAN:
                case ERROR_CODE:
                case EMPTY:
                    break;
                default:
                    throw new RecordFormatException("Bad special value code (" + result[0] + ")");
            }
            return new SpecialCachedValue(result);
        }

        public static SpecialCachedValue createCachedEmptyValue() {
            return create(EMPTY, 0);
        }

        public static SpecialCachedValue createForString() {
            return create(STRING, 0);
        }

        public static SpecialCachedValue createCachedBoolean(boolean b) {
            return create(BOOLEAN, b ? 1 : 0);
        }

        public static SpecialCachedValue createCachedErrorCode(int errorCode) {
            return create(ERROR_CODE, errorCode);
        }

        @Keep
        private static SpecialCachedValue create(int code, int data) {
            byte[] vd = {
                    (byte) code,
                    0,
                    (byte) data,
                    0,
                    0,
                    0,
            };
            return new SpecialCachedValue(vd);
        }

        public int getTypeCode() {
            return _variableData[0];
        }

        public void serialize(LittleEndianOutput out) {
            out.write(_variableData);
            out.writeShort(0xFFFF);
        }

        public String formatDebugString() {
            return formatValue() + ' ' + HexDump.toHex(_variableData);
        }

        private String formatValue() {
            int typeCode = getTypeCode();
            switch (typeCode) {
                case STRING:
                    return "<string>";
                case BOOLEAN:
                    return getDataValue() == 0 ? "FALSE" : "TRUE";
                case ERROR_CODE:
                    return ErrorEval.getText(getDataValue());
                case EMPTY:
                    return "<empty>";
            }
            return "#error(type=" + typeCode + ")#";
        }

        private int getDataValue() {
            return _variableData[DATA_INDEX];
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName());
            sb.append('[').append(formatValue()).append(']');
            return sb.toString();
        }

        public int getValueType() {
            int typeCode = getTypeCode();
            switch (typeCode) {
                case STRING:
                    return Cell.CELL_TYPE_STRING;
                case BOOLEAN:
                    return Cell.CELL_TYPE_BOOLEAN;
                case ERROR_CODE:
                    return Cell.CELL_TYPE_ERROR;
                case EMPTY:
                    return Cell.CELL_TYPE_STRING;
            }
            throw new IllegalStateException("Unexpected type id (" + typeCode + ")");
        }

        public boolean getBooleanValue() {
            if (getTypeCode() != BOOLEAN) {
                throw new IllegalStateException("Not a boolean cached value - " + formatValue());
            }
            return getDataValue() != 0;
        }

        public int getErrorValue() {
            if (getTypeCode() != ERROR_CODE) {
                throw new IllegalStateException("Not an error cached value - " + formatValue());
            }
            return getDataValue();
        }
    }
}

