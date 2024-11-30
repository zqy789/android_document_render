

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;

import com.document.render.office.fc.ss.usermodel.ErrorConstants;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class BoolErrRecord extends CellRecord {

    @Keep
    public final static short sid = 0x0205;
    private int _value;

    private boolean _isError;


    public BoolErrRecord() {

    }


    public BoolErrRecord(RecordInputStream in) {
        super(in);
        switch (in.remaining()) {
            case 2:
                _value = in.readByte();
                break;
            case 3:
                _value = in.readUShort();
                break;
            default:
                throw new RecordFormatException("Unexpected size ("
                        + in.remaining() + ") for BOOLERR record.");
        }
        int flag = in.readUByte();
        switch (flag) {
            case 0:
                _isError = false;
                break;
            case 1:
                _isError = true;
                break;
            default:
                throw new RecordFormatException("Unexpected isError flag ("
                        + flag + ") for BOOLERR record.");
        }
    }


    public void setValue(boolean value) {
        _value = value ? 1 : 0;
        _isError = false;
    }


    public void setValue(byte value) {
        switch (value) {
            case ErrorConstants.ERROR_NULL:
            case ErrorConstants.ERROR_DIV_0:
            case ErrorConstants.ERROR_VALUE:
            case ErrorConstants.ERROR_REF:
            case ErrorConstants.ERROR_NAME:
            case ErrorConstants.ERROR_NUM:
            case ErrorConstants.ERROR_NA:
                _value = value;
                _isError = true;
                return;
        }
        throw new IllegalArgumentException("Error Value can only be 0,7,15,23,29,36 or 42. It cannot be " + value);
    }


    public boolean getBooleanValue() {
        return _value != 0;
    }


    public byte getErrorValue() {
        return (byte) _value;
    }


    public boolean isBoolean() {
        return !_isError;
    }


    public boolean isError() {
        return _isError;
    }

    @Override
    protected String getRecordName() {
        return "BOOLERR";
    }

    @Override
    protected void appendValueText(StringBuilder sb) {
        if (isBoolean()) {
            sb.append("  .boolVal = ");
            sb.append(getBooleanValue());
        } else {
            sb.append("  .errCode = ");
            sb.append(ErrorConstants.getText(getErrorValue()));
            sb.append(" (").append(HexDump.byteToHex(getErrorValue())).append(")");
        }
    }

    @Override
    protected void serializeValue(LittleEndianOutput out) {
        out.writeByte(_value);
        out.writeByte(_isError ? 1 : 0);
    }

    @Override
    protected int getValueDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        BoolErrRecord rec = new BoolErrRecord();
        copyBaseFields(rec);
        rec._value = _value;
        rec._isError = _isError;
        return rec;
    }
}
