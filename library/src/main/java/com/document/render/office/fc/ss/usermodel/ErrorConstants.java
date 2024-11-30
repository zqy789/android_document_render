

package com.document.render.office.fc.ss.usermodel;


public class ErrorConstants {

    public static final int ERROR_NULL = 0x00;

    public static final int ERROR_DIV_0 = 0x07;

    public static final int ERROR_VALUE = 0x0F;

    public static final int ERROR_REF = 0x17;

    public static final int ERROR_NAME = 0x1D;

    public static final int ERROR_NUM = 0x24;

    public static final int ERROR_NA = 0x2A;
    protected ErrorConstants() {

    }


    public static final String getText(int errorCode) {
        switch (errorCode) {
            case ERROR_NULL:
                return "#NULL!";
            case ERROR_DIV_0:
                return "#DIV/0!";
            case ERROR_VALUE:
                return "#VALUE!";
            case ERROR_REF:
                return "#REF!";
            case ERROR_NAME:
                return "#NAME?";
            case ERROR_NUM:
                return "#NUM!";
            case ERROR_NA:
                return "#N/A";
        }
        throw new IllegalArgumentException("Bad error code (" + errorCode + ")");
    }


    public static final boolean isValidCode(int errorCode) {


        switch (errorCode) {
            case ERROR_NULL:
            case ERROR_DIV_0:
            case ERROR_VALUE:
            case ERROR_REF:
            case ERROR_NAME:
            case ERROR_NUM:
            case ERROR_NA:
                return true;
        }
        return false;
    }
}
