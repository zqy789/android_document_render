

package com.document.render.office.fc.ss.usermodel;


public interface ICellStyle {



    public final static short ALIGN_GENERAL = 0x0;



    public final static short ALIGN_LEFT = 0x1;



    public final static short ALIGN_CENTER = 0x2;



    public final static short ALIGN_RIGHT = 0x3;



    public final static short ALIGN_FILL = 0x4;



    public final static short ALIGN_JUSTIFY = 0x5;



    public final static short ALIGN_CENTER_SELECTION = 0x6;



    public final static short VERTICAL_TOP = 0x0;



    public final static short VERTICAL_CENTER = 0x1;



    public final static short VERTICAL_BOTTOM = 0x2;



    public final static short VERTICAL_JUSTIFY = 0x3;



    public final static short BORDER_NONE = 0x0;



    public final static short BORDER_THIN = 0x1;



    public final static short BORDER_MEDIUM = 0x2;



    public final static short BORDER_DASHED = 0x3;



    public final static short BORDER_HAIR = 0x4;



    public final static short BORDER_THICK = 0x5;



    public final static short BORDER_DOUBLE = 0x6;



    public final static short BORDER_DOTTED = 0x7;



    public final static short BORDER_MEDIUM_DASHED = 0x8;



    public final static short BORDER_DASH_DOT = 0x9;



    public final static short BORDER_MEDIUM_DASH_DOT = 0xA;



    public final static short BORDER_DASH_DOT_DOT = 0xB;



    public final static short BORDER_MEDIUM_DASH_DOT_DOT = 0xC;



    public final static short BORDER_SLANTED_DASH_DOT = 0xD;


    public final static short NO_FILL = 0;


    public final static short SOLID_FOREGROUND = 1;


    public final static short FINE_DOTS = 2;


    public final static short ALT_BARS = 3;


    public final static short SPARSE_DOTS = 4;


    public final static short THICK_HORZ_BANDS = 5;


    public final static short THICK_VERT_BANDS = 6;


    public final static short THICK_BACKWARD_DIAG = 7;


    public final static short THICK_FORWARD_DIAG = 8;


    public final static short BIG_SPOTS = 9;


    public final static short BRICKS = 10;


    public final static short THIN_HORZ_BANDS = 11;


    public final static short THIN_VERT_BANDS = 12;


    public final static short THIN_BACKWARD_DIAG = 13;


    public final static short THIN_FORWARD_DIAG = 14;


    public final static short SQUARES = 15;


    public final static short DIAMONDS = 16;


    public final static short LESS_DOTS = 17;


    public final static short LEAST_DOTS = 18;



    short getIndex();


    short getDataFormat();



    void setDataFormat(short fmt);


    public String getDataFormatString();



    void setFont(IFont font);


    short getFontIndex();



    boolean getHidden();



    void setHidden(boolean hidden);



    boolean getLocked();



    void setLocked(boolean locked);



    short getAlignment();



    void setAlignment(short align);



    boolean getWrapText();



    void setWrapText(boolean wrapped);



    short getVerticalAlignment();



    void setVerticalAlignment(short align);



    short getRotation();



    void setRotation(short rotation);



    short getIndention();



    void setIndention(short indent);



    short getBorderLeft();



    void setBorderLeft(short border);



    short getBorderRight();



    void setBorderRight(short border);



    short getBorderTop();



    void setBorderTop(short border);


    short getBorderBottom();



    void setBorderBottom(short border);


    short getLeftBorderColor();


    void setLeftBorderColor(short color);


    short getRightBorderColor();


    void setRightBorderColor(short color);


    short getTopBorderColor();


    void setTopBorderColor(short color);


    short getBottomBorderColor();


    void setBottomBorderColor(short color);



    short getFillPattern();


    void setFillPattern(short fp);


    short getFillBackgroundColor();



    void setFillBackgroundColor(short bg);


    Color getFillBackgroundColorColor();


    short getFillForegroundColor();


    void setFillForegroundColor(short bg);


    Color getFillForegroundColorColor();


    public void cloneStyleFrom(ICellStyle source);
}
