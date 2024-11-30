

package com.document.render.office.fc.ss.usermodel;



public enum FontFamily {

    NOT_APPLICABLE(0),
    ROMAN(1),
    SWISS(2),
    MODERN(3),
    SCRIPT(4),
    DECORATIVE(5);

    private static FontFamily[] _table = new FontFamily[6];

    static {
        for (FontFamily c : values()) {
            _table[c.getValue()] = c;
        }
    }

    private int family;

    private FontFamily(int value) {
        family = value;
    }

    public static FontFamily valueOf(int family) {
        return _table[family];
    }


    public int getValue() {
        return family;
    }
}
