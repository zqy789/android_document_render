

package com.document.render.office.fc.ss.usermodel;



public enum FontScheme {


    NONE(1),
    MAJOR(2),
    MINOR(3);

    private static FontScheme[] _table = new FontScheme[4];

    static {
        for (FontScheme c : values()) {
            _table[c.getValue()] = c;
        }
    }

    private int value;

    private FontScheme(int val) {
        value = val;
    }

    public static FontScheme valueOf(int value) {
        return _table[value];
    }

    public int getValue() {
        return value;
    }
}
