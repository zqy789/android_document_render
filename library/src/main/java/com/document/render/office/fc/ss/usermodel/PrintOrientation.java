

package com.document.render.office.fc.ss.usermodel;


public enum PrintOrientation {


    DEFAULT(1),

    PORTRAIT(2),

    LANDSCAPE(3);


    private static PrintOrientation[] _table = new PrintOrientation[4];

    static {
        for (PrintOrientation c : values()) {
            _table[c.getValue()] = c;
        }
    }

    private int orientation;


    private PrintOrientation(int orientation) {
        this.orientation = orientation;
    }

    public static PrintOrientation valueOf(int value) {
        return _table[value];
    }

    public int getValue() {
        return orientation;
    }
}
