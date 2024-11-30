

package com.document.render.office.ss.model.style;

import com.document.render.office.common.bg.BackgroundAndFill;



public class CellStyle {

    public final static short ALIGN_GENERAL = 0x00;

    public final static short ALIGN_LEFT = 0x01;

    public final static short ALIGN_CENTER = 0x02;

    public final static short ALIGN_RIGHT = 0x03;

    public final static short ALIGN_FILL = 0x04;

    public final static short ALIGN_JUSTIFY = 0x05;

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

    private short index;
    private NumberFormat numFmt;

    private short fontIndex;

    private boolean isHidden;

    private boolean isLocked;
    private Alignment alignment;
    private CellBorder cellBorder;
    private BackgroundAndFill fill;


    public CellStyle() {

    }


    public short getIndex() {
        return index;
    }


    public void setIndex(short index) {
        this.index = index;
    }

    private void checkDataFormat() {
        if (numFmt == null) {
            numFmt = new NumberFormat();
        }
    }


    public void setNumberFormat(NumberFormat numFmt) {
        this.numFmt = numFmt;
    }


    public short getNumberFormatID() {
        checkDataFormat();
        return numFmt.getNumberFormatID();
    }


    public void setNumberFormatID(short id) {
        checkDataFormat();
        numFmt.setNumberFormatID(id);
    }


    public String getFormatCode() {
        checkDataFormat();
        return numFmt.getFormatCode();
    }


    public void setFormatCode(String formatCode) {
        checkDataFormat();
        numFmt.setFormatCode(formatCode);
    }


    public short getFontIndex() {
        return fontIndex;
    }


    public void setFontIndex(short fontIndex) {
        this.fontIndex = fontIndex;
    }


    public boolean isHidden() {
        return isHidden;
    }


    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }


    public boolean isLocked() {
        return isLocked;
    }


    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    private void checkAlignmeng() {
        if (alignment == null) {
            alignment = new Alignment();
        }
    }


    public boolean isWrapText() {
        checkAlignmeng();
        return alignment.isWrapText()
                || alignment.getHorizontalAlign() == ALIGN_JUSTIFY
                || alignment.getVerticalAlign() == VERTICAL_JUSTIFY;
    }


    public void setWrapText(boolean isWrapText) {
        checkAlignmeng();
        alignment.setWrapText(isWrapText);
    }


    public short getHorizontalAlign() {
        checkAlignmeng();
        return alignment.getHorizontalAlign();
    }


    public void setHorizontalAlign(String horAlign) {
        checkAlignmeng();
        if (horAlign == null || horAlign.equalsIgnoreCase("general")) {
            alignment.setHorizontalAlign(ALIGN_GENERAL);
        } else if (horAlign.equalsIgnoreCase("left")) {
            alignment.setHorizontalAlign(ALIGN_LEFT);
        } else if (horAlign.equalsIgnoreCase("center")) {
            alignment.setHorizontalAlign(ALIGN_CENTER);
        } else if (horAlign.equalsIgnoreCase("right")) {
            alignment.setHorizontalAlign(ALIGN_RIGHT);
        } else if (horAlign.equalsIgnoreCase("fill")) {
            alignment.setHorizontalAlign(ALIGN_FILL);
        } else if (horAlign.equalsIgnoreCase("justify")) {
            alignment.setHorizontalAlign(ALIGN_JUSTIFY);
        } else if (horAlign.equalsIgnoreCase("distributed")) {
            alignment.setHorizontalAlign(ALIGN_JUSTIFY);
        }

    }


    public void setHorizontalAlign(short horAlign) {
        checkAlignmeng();
        alignment.setHorizontalAlign(horAlign);
    }


    public short getVerticalAlign() {
        checkAlignmeng();
        return alignment.getVerticalAlign();
    }


    public void setVerticalAlign(String verAlign) {
        checkAlignmeng();
        if (verAlign.equalsIgnoreCase("top")) {
            alignment.setVerticalAlign(VERTICAL_TOP);
        } else if (verAlign == null || verAlign.equalsIgnoreCase("center")) {
            alignment.setVerticalAlign(VERTICAL_CENTER);
        } else if (verAlign.equalsIgnoreCase("bottom")) {
            alignment.setVerticalAlign(VERTICAL_BOTTOM);
        } else if (verAlign.equalsIgnoreCase("justify")) {
            alignment.setVerticalAlign(VERTICAL_JUSTIFY);
        } else if (verAlign.equalsIgnoreCase("distributed")) {
            alignment.setVerticalAlign(VERTICAL_JUSTIFY);
        }
    }


    public void setVerticalAlign(short verAlign) {
        checkAlignmeng();
        alignment.setVerticalAlign(verAlign);
    }


    public short getRotation() {
        checkAlignmeng();
        return alignment.getRotaion();
    }


    public void setRotation(short rotation) {
        checkAlignmeng();
        alignment.setRotation(rotation);
    }


    public short getIndent() {
        checkAlignmeng();
        return alignment.getIndent();
    }


    public void setIndent(short indent) {
        checkAlignmeng();
        alignment.setIndent(indent);
    }

    private void checkBorder() {
        if (cellBorder == null) {
            cellBorder = new CellBorder();
        }
    }


    public void setBorder(CellBorder cellBorder) {
        this.cellBorder = cellBorder;
    }


    public short getBorderLeft() {
        checkBorder();
        return cellBorder.getLeftBorder().getStyle();
    }


    public void setBorderLeft(short borderLeft) {
        checkBorder();
        cellBorder.getLeftBorder().setStyle(borderLeft);
    }


    public short getBorderLeftColorIdx() {
        checkBorder();
        return cellBorder.getLeftBorder().getColor();
    }


    public void setBorderLeftColorIdx(short borderLeftColorIdx) {
        checkBorder();
        cellBorder.getLeftBorder().setColor(borderLeftColorIdx);
    }


    public short getBorderRight() {
        checkBorder();
        return cellBorder.getRightBorder().getStyle();
    }


    public void setBorderRight(short borderRight) {
        checkBorder();
        cellBorder.getRightBorder().setStyle(borderRight);
    }


    public short getBorderRightColorIdx() {
        checkBorder();
        return cellBorder.getRightBorder().getColor();
    }


    public void setBorderRightColorIdx(short borderRightColorIdx) {
        checkBorder();
        cellBorder.getRightBorder().setColor(borderRightColorIdx);
    }


    public short getBorderTop() {
        checkBorder();
        return cellBorder.getTopBorder().getStyle();
    }


    public void setBorderTop(short borderTop) {
        checkBorder();
        cellBorder.getTopBorder().setStyle(borderTop);
    }


    public short getBorderTopColorIdx() {
        checkBorder();
        return cellBorder.getTopBorder().getColor();
    }


    public void setBorderTopColorIdx(short borderTopColorIdx) {
        checkBorder();
        cellBorder.getTopBorder().setColor(borderTopColorIdx);
    }


    public short getBorderBottom() {
        checkBorder();
        return cellBorder.getBottomBorder().getStyle();
    }


    public void setBorderBottom(short borderBottom) {
        checkBorder();
        cellBorder.getBottomBorder().setStyle(borderBottom);
    }


    public short getBorderBottomColorIdx() {
        checkBorder();
        return cellBorder.getBottomBorder().getColor();
    }


    public void setBorderBottomColorIdx(short borderBottomColorIdx) {
        checkBorder();
        cellBorder.getBottomBorder().setColor(borderBottomColorIdx);
    }

    private void checkFillPattern() {
        if (fill == null) {
            fill = new BackgroundAndFill();
            fill.setFillType(BackgroundAndFill.FILL_NO);
        }
    }

    public BackgroundAndFill getFillPattern() {
        return fill;
    }


    public void setFillPattern(BackgroundAndFill fill) {
        this.fill = fill;
    }


    public byte getFillPatternType() {
        checkFillPattern();
        return fill.getFillType();
    }


    public void setFillPatternType(byte type) {
        checkFillPattern();
        fill.setFillType(type);
    }


    public int getBgColor() {
        checkFillPattern();
        return fill.getBackgoundColor();
    }


    public void setBgColor(int color) {
        checkFillPattern();
        fill.setBackgoundColor(color);
    }


    public int getFgColor() {
        checkFillPattern();
        return fill.getForegroundColor();
    }

    public void setFgColor(int color) {
        checkFillPattern();
        fill.setForegroundColor(color);
    }


    public void dispose() {
        numFmt = null;
        fill = null;

        if (cellBorder != null) {
            cellBorder.dispose();
            cellBorder = null;
        }
        if (alignment != null) {
            alignment.dispose();
            alignment = null;
        }
    }
}
