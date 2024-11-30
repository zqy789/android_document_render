

package com.document.render.office.fc.ss.usermodel;



public enum FontUnderline {


    SINGLE(1),


    DOUBLE(2),


    SINGLE_ACCOUNTING(3),


    DOUBLE_ACCOUNTING(4),


    NONE(5);

    private static FontUnderline[] _table = new FontUnderline[6];

    static {
        for (FontUnderline c : values()) {
            _table[c.getValue()] = c;
        }
    }

    private int value;

    private FontUnderline(int val) {
        value = val;
    }

    public static FontUnderline valueOf(int value) {
        return _table[value];
    }

    public static FontUnderline valueOf(byte value) {
        FontUnderline val;
        switch (value) {
            case IFont.U_DOUBLE:
                val = FontUnderline.DOUBLE;
                break;
            case IFont.U_DOUBLE_ACCOUNTING:
                val = FontUnderline.DOUBLE_ACCOUNTING;
                break;
            case IFont.U_SINGLE_ACCOUNTING:
                val = FontUnderline.SINGLE_ACCOUNTING;
                break;
            case IFont.U_SINGLE:
                val = FontUnderline.SINGLE;
                break;
            default:
                val = FontUnderline.NONE;
                break;
        }
        return val;
    }

    public int getValue() {
        return value;
    }

    public byte getByteValue() {
        switch (this) {
            case DOUBLE:
                return IFont.U_DOUBLE;
            case DOUBLE_ACCOUNTING:
                return IFont.U_DOUBLE_ACCOUNTING;
            case SINGLE_ACCOUNTING:
                return IFont.U_SINGLE_ACCOUNTING;
            case NONE:
                return IFont.U_NONE;
            case SINGLE:
                return IFont.U_SINGLE;
            default:
                return IFont.U_SINGLE;
        }
    }

}
