

package com.document.render.office.fc.ss.usermodel;


public interface FontFormatting {

    public final static short SS_NONE = 0;

    public final static short SS_SUPER = 1;

    public final static short SS_SUB = 2;


    public final static byte U_NONE = 0;

    public final static byte U_SINGLE = 1;

    public final static byte U_DOUBLE = 2;

    public final static byte U_SINGLE_ACCOUNTING = 0x21;

    public final static byte U_DOUBLE_ACCOUNTING = 0x22;


    short getEscapementType();


    void setEscapementType(short escapementType);


    short getFontColorIndex();



    void setFontColorIndex(short color);


    int getFontHeight();


    void setFontHeight(int height);


    short getUnderlineType();


    void setUnderlineType(short underlineType);


    boolean isBold();


    boolean isItalic();


    void setFontStyle(boolean italic, boolean bold);


    void resetFontStyle();
}
