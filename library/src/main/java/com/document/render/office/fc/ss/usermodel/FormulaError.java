
package com.document.render.office.fc.ss.usermodel;

import java.util.HashMap;
import java.util.Map;


public enum FormulaError {

    NULL(0x00, "#NULL!"),


    DIV0(0x07, "#DIV/0!"),


    VALUE(0x0F, "#VALUE!"),


    REF(0x17, "#REF!"),


    NAME(0x1D, "#NAME?"),


    NUM(0x24, "#NUM!"),


    NA(0x2A, "#N/A");

    private static Map<String, FormulaError> smap = new HashMap<String, FormulaError>();
    private static Map<Byte, FormulaError> imap = new HashMap<Byte, FormulaError>();

    static {
        for (FormulaError error : values()) {
            imap.put(error.getCode(), error);
            smap.put(error.getString(), error);
        }
    }

    private byte type;
    private String repr;

    private FormulaError(int type, String repr) {
        this.type = (byte) type;
        this.repr = repr;
    }

    public static FormulaError forInt(byte type) {
        FormulaError err = imap.get(type);
        if (err == null) throw new IllegalArgumentException("Unknown error type: " + type);
        return err;
    }

    public static FormulaError forString(String code) {
        FormulaError err = smap.get(code);
        if (err == null) throw new IllegalArgumentException("Unknown error code: " + code);
        return err;
    }


    public byte getCode() {
        return type;
    }


    public String getString() {
        return repr;
    }
}
