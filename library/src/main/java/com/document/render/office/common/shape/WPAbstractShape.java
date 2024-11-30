
package com.document.render.office.common.shape;


public class WPAbstractShape extends ArbitraryPolygonShape {


    public static final short WRAP_TIGHT = 0;

    public static final short WRAP_SQUARE = 1;

    public static final short WRAP_OLE = 2;

    public static final short WRAP_TOP = 3;

    public static final short WRAP_THROUGH = 4;

    public static final short WRAP_TOPANDBOTTOM = 5;

    public static final short WRAP_BOTTOM = 6;


    public static final byte RELATIVE_COLUMN = 0;

    public static final byte RELATIVE_MARGIN = RELATIVE_COLUMN + 1;

    public static final byte RELATIVE_PAGE = RELATIVE_MARGIN + 1;

    public static final byte RELATIVE_CHARACTER = RELATIVE_PAGE + 1;

    public static final byte RELATIVE_LEFT = RELATIVE_CHARACTER + 1;

    public static final byte RELATIVE_RIGHT = RELATIVE_LEFT + 1;

    public static final byte RELATIVE_TOP = RELATIVE_RIGHT + 1;

    public static final byte RELATIVE_BOTTOM = RELATIVE_TOP + 1;

    public static final byte RELATIVE_INNER = RELATIVE_BOTTOM + 1;

    public static final byte RELATIVE_OUTER = RELATIVE_INNER + 1;

    public static final byte RELATIVE_PARAGRAPH = RELATIVE_OUTER + 1;

    public static final byte RELATIVE_LINE = RELATIVE_PARAGRAPH + 1;


    public static final byte ALIGNMENT_ABSOLUTE = 0;

    public static final byte ALIGNMENT_LEFT = 1;

    public static final byte ALIGNMENT_CENTER = 2;

    public static final byte ALIGNMENT_RIGHT = 3;

    public static final byte ALIGNMENT_TOP = 4;

    public static final byte ALIGNMENT_BOTTOM = 5;

    public static final byte ALIGNMENT_INSIDE = 6;

    public static final byte ALIGNMENT_OUTSIDE = 7;



    public static final byte POSITIONTYPE_ABSOLUTE = 0;

    public static final byte POSITIONTYPE_RELATIVE = 1;

    private byte horPositionType;

    private byte horRelativeTo;

    private int horRelativeValue;

    private byte horAlignment = ALIGNMENT_ABSOLUTE;

    private byte verPositionType;

    private byte verRelativeTo = RELATIVE_PARAGRAPH;

    private int verRelativeValue;

    private byte verAlignment = ALIGNMENT_ABSOLUTE;

    private short wrapType = 3;

    private int elementIndex = -1;

    private boolean isTextWrapLine = true;


    public byte getHorPositionType() {
        return horPositionType;
    }


    public void setHorPositionType(byte horPositionType) {
        this.horPositionType = horPositionType;
    }


    public byte getVerPositionType() {
        return verPositionType;
    }


    public void setVerPositionType(byte verPositionType) {
        this.verPositionType = verPositionType;
    }


    public int getWrap() {
        return wrapType;
    }


    public void setWrap(short wrapType) {
        this.wrapType = wrapType;
    }


    public byte getHorizontalRelativeTo() {
        return horRelativeTo;
    }


    public void setHorizontalRelativeTo(byte horRelativeTo) {
        this.horRelativeTo = horRelativeTo;
    }


    public byte getHorizontalAlignment() {
        return horAlignment;
    }


    public void setHorizontalAlignment(byte horAlignment) {
        this.horAlignment = horAlignment;
    }


    public byte getVerticalRelativeTo() {
        return verRelativeTo;
    }


    public void setVerticalRelativeTo(byte verRelativeTo) {
        this.verRelativeTo = verRelativeTo;
    }


    public byte getVerticalAlignment() {
        return verAlignment;
    }


    public void setVerticalAlignment(byte verAlignment) {
        this.verAlignment = verAlignment;
    }


    public int getHorRelativeValue() {
        return horRelativeValue;
    }

    public void setHorRelativeValue(int horRelativeValue) {
        this.horRelativeValue = horRelativeValue;
    }


    public int getVerRelativeValue() {
        return verRelativeValue;
    }

    public void setVerRelativeValue(int verRelativeValue) {
        this.verRelativeValue = verRelativeValue;
    }


    public int getElementIndex() {
        return elementIndex;
    }


    public void setElementIndex(int elementIndex) {
        this.elementIndex = elementIndex;
    }


    public boolean isTextWrapLine() {
        return isTextWrapLine;
    }


    public void setTextWrapLine(boolean isTextWrapLine) {
        this.isTextWrapLine = isTextWrapLine;
    }

}
