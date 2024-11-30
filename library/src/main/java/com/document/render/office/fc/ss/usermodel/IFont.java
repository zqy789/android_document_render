

package com.document.render.office.fc.ss.usermodel;

public interface IFont {


    public final static short BOLDWEIGHT_NORMAL = 0x190;



    public final static short BOLDWEIGHT_BOLD = 0x2bc;



    public final static short COLOR_NORMAL = 0x7fff;



    public final static short COLOR_RED = 0xa;



    public final static short SS_NONE = 0;



    public final static short SS_SUPER = 1;



    public final static short SS_SUB = 2;



    public final static byte U_NONE = 0;



    public final static byte U_SINGLE = 1;



    public final static byte U_DOUBLE = 2;



    public final static byte U_SINGLE_ACCOUNTING = 0x21;



    public final static byte U_DOUBLE_ACCOUNTING = 0x22;


    public final static byte ANSI_CHARSET = 0;


    public final static byte DEFAULT_CHARSET = 1;


    public final static byte SYMBOL_CHARSET = 2;



    String getFontName();



    void setFontName(String name);



    short getFontHeight();



    void setFontHeight(short height);



    short getFontHeightInPoints();



    void setFontHeightInPoints(short height);



    boolean getItalic();



    void setItalic(boolean italic);



    boolean getStrikeout();



    void setStrikeout(boolean strikeout);


    short getColor();



    void setColor(short color);



    short getTypeOffset();



    void setTypeOffset(short offset);



    byte getUnderline();



    void setUnderline(byte underline);


    int getCharSet();


    void setCharSet(byte charset);


    void setCharSet(int charset);


    public short getIndex();

    public short getBoldweight();

    public void setBoldweight(short boldweight);

}
