

package com.document.render.office.fc.hssf.formula.ptg;


import com.document.render.office.fc.ss.usermodel.ErrorConstants;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class ErrPtg extends ScalarConstantPtg {

    public static final short sid = 0x1c;

    private static final ErrorConstants EC = null;

    public static final ErrPtg NULL_INTERSECTION = new ErrPtg(EC.ERROR_NULL);

    public static final ErrPtg DIV_ZERO = new ErrPtg(EC.ERROR_DIV_0);

    public static final ErrPtg VALUE_INVALID = new ErrPtg(EC.ERROR_VALUE);

    public static final ErrPtg REF_INVALID = new ErrPtg(EC.ERROR_REF);

    public static final ErrPtg NAME_INVALID = new ErrPtg(EC.ERROR_NAME);

    public static final ErrPtg NUM_ERROR = new ErrPtg(EC.ERROR_NUM);

    public static final ErrPtg N_A = new ErrPtg(EC.ERROR_NA);
    private static final int SIZE = 2;
    private final int field_1_error_code;



    private ErrPtg(int errorCode) {
        if (!ErrorConstants.isValidCode(errorCode)) {
            throw new IllegalArgumentException("Invalid error code (" + errorCode + ")");
        }
        field_1_error_code = errorCode;
    }

    public static ErrPtg read(LittleEndianInput in) {
        return valueOf(in.readByte());
    }

    public static ErrPtg valueOf(int code) {
        switch (code) {
            case ErrorConstants.ERROR_DIV_0:
                return DIV_ZERO;
            case ErrorConstants.ERROR_NA:
                return N_A;
            case ErrorConstants.ERROR_NAME:
                return NAME_INVALID;
            case ErrorConstants.ERROR_NULL:
                return NULL_INTERSECTION;
            case ErrorConstants.ERROR_NUM:
                return NUM_ERROR;
            case ErrorConstants.ERROR_REF:
                return REF_INVALID;
            case ErrorConstants.ERROR_VALUE:
                return VALUE_INVALID;
        }
        throw new RuntimeException("Unexpected error code (" + code + ")");
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(sid + getPtgClass());
        out.writeByte(field_1_error_code);
    }

    public String toFormulaString() {
        return ErrorConstants.getText(field_1_error_code);
    }

    public int getSize() {
        return SIZE;
    }

    public int getErrorCode() {
        return field_1_error_code;
    }
}
