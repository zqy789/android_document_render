

package com.document.render.office.fc.ss.usermodel;



public enum FontCharset {

    ANSI(0),
    DEFAULT(1),
    SYMBOL(2),
    MAC(77),
    SHIFTJIS(128),
    HANGEUL(129),
    JOHAB(130),
    GB2312(134),
    CHINESEBIG5(136),
    GREEK(161),
    TURKISH(162),
    VIETNAMESE(163),
    HEBREW(177),
    ARABIC(178),
    BALTIC(186),
    RUSSIAN(204),
    THAI(222),
    EASTEUROPE(238),
    OEM(255);


    private static FontCharset[] _table = new FontCharset[256];

    static {
        for (FontCharset c : values()) {
            _table[c.getValue()] = c;
        }
    }

    private int charset;

    private FontCharset(int value) {
        charset = value;
    }

    public static FontCharset valueOf(int value) {
        if (value >= _table.length)
            return null;
        return _table[value];
    }


    public int getValue() {
        return charset;
    }
}
