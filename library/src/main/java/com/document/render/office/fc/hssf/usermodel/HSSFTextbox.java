

package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.ShapeKit;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.fc.ss.usermodel.RichTextString;


public class HSSFTextbox extends HSSFSimpleShape {
    public final static short OBJECT_TYPE_TEXT = 6;


    public final static short HORIZONTAL_ALIGNMENT_LEFT = 1;
    public final static short HORIZONTAL_ALIGNMENT_CENTERED = 2;
    public final static short HORIZONTAL_ALIGNMENT_RIGHT = 3;
    public final static short HORIZONTAL_ALIGNMENT_JUSTIFIED = 4;
    public final static short HORIZONTAL_ALIGNMENT_DISTRIBUTED = 7;


    public final static short VERTICAL_ALIGNMENT_TOP = 1;
    public final static short VERTICAL_ALIGNMENT_CENTER = 2;
    public final static short VERTICAL_ALIGNMENT_BOTTOM = 3;
    public final static short VERTICAL_ALIGNMENT_JUSTIFY = 4;
    public final static short VERTICAL_ALIGNMENT_DISTRIBUTED = 7;


    HSSFRichTextString string = new HSSFRichTextString("");
    private int marginLeft;
    private int marginRight;
    private int marginTop;
    private int marginBottom;

    private short halign;
    private short valign;
    private boolean isWrapLine;
    private boolean isWordArt;
    private int fontColor;


    public HSSFTextbox(EscherContainerRecord escherContainer, HSSFShape parent, HSSFAnchor anchor) {
        super(escherContainer, parent, anchor);
        setShapeType(OBJECT_TYPE_TEXT);

        halign = HORIZONTAL_ALIGNMENT_LEFT;
        valign = VERTICAL_ALIGNMENT_TOP;

        marginLeft = Math.round(ShapeKit.getTextboxMarginLeft(escherContainer));
        marginTop = Math.round(ShapeKit.getTextboxMarginTop(escherContainer));
        marginRight = Math.round(ShapeKit.getTextboxMarginRight(escherContainer));
        marginBottom = Math.round(ShapeKit.getTextboxMarginBottom(escherContainer));
        isWrapLine = ShapeKit.isTextboxWrapLine(escherContainer);
    }


    public HSSFRichTextString getString() {
        return string;
    }


    public void setString(RichTextString string) {
        HSSFRichTextString rtr = (HSSFRichTextString) string;


        if (rtr.numFormattingRuns() == 0) rtr.applyFont((short) 0);

        this.string = rtr;
    }


    public boolean isTextboxWrapLine() {
        return isWrapLine;
    }


    public int getMarginLeft() {
        return marginLeft;
    }


    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }


    public int getMarginRight() {
        return marginRight;
    }


    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }


    public int getMarginTop() {
        return marginTop;
    }


    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }


    public int getMarginBottom() {
        return marginBottom;
    }


    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }


    public short getHorizontalAlignment() {
        return halign;
    }


    public void setHorizontalAlignment(short align) {
        this.halign = align;
    }


    public short getVerticalAlignment() {
        return valign;
    }


    public void setVerticalAlignment(short align) {
        this.valign = align;
    }

    public boolean isWordArt() {
        return isWordArt;
    }

    public void setWordArt(boolean isWordArt) {
        this.isWordArt = isWordArt;
    }

    public int getFontColor() {
        return fontColor;
    }

    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }
}
