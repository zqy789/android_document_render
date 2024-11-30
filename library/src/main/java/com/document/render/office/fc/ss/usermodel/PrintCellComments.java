

package com.document.render.office.fc.ss.usermodel;


public enum PrintCellComments {


    NONE(1),

    AS_DISPLAYED(2),

    AT_END(3);


    private static PrintCellComments[] _table = new PrintCellComments[4];

    static {
        for (PrintCellComments c : values()) {
            _table[c.getValue()] = c;
        }
    }

    private int comments;

    private PrintCellComments(int comments) {
        this.comments = comments;
    }

    public static PrintCellComments valueOf(int value) {
        return _table[value];
    }

    public int getValue() {
        return comments;
    }
}
