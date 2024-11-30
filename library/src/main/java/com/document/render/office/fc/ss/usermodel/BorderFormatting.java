

package com.document.render.office.fc.ss.usermodel;


public interface BorderFormatting {
    
    final static short BORDER_NONE = 0x0;
    
    final static short BORDER_THIN = 0x1;
    
    final static short BORDER_MEDIUM = 0x2;
    
    final static short BORDER_DASHED = 0x3;
    
    final static short BORDER_HAIR = 0x4;
    
    final static short BORDER_THICK = 0x5;
    
    final static short BORDER_DOUBLE = 0x6;
    
    final static short BORDER_DOTTED = 0x7;
    
    final static short BORDER_MEDIUM_DASHED = 0x8;
    
    final static short BORDER_DASH_DOT = 0x9;
    
    final static short BORDER_MEDIUM_DASH_DOT = 0xA;
    
    final static short BORDER_DASH_DOT_DOT = 0xB;
    
    final static short BORDER_MEDIUM_DASH_DOT_DOT = 0xC;
    
    final static short BORDER_SLANTED_DASH_DOT = 0xD;

    short getBorderBottom();

    void setBorderBottom(short border);

    short getBorderDiagonal();

    
    void setBorderDiagonal(short border);

    short getBorderLeft();

    
    void setBorderLeft(short border);

    short getBorderRight();

    
    void setBorderRight(short border);

    short getBorderTop();

    
    void setBorderTop(short border);

    short getBottomBorderColor();

    void setBottomBorderColor(short color);

    short getDiagonalBorderColor();

    void setDiagonalBorderColor(short color);

    short getLeftBorderColor();

    void setLeftBorderColor(short color);

    short getRightBorderColor();

    void setRightBorderColor(short color);

    short getTopBorderColor();

    void setTopBorderColor(short color);
}
