
package com.document.render.office.simpletext.view;


public class CharAttr {

    public int fontSize;

    public int fontIndex;

    public int fontScale;

    public int fontColor;

    public boolean isBold;

    public boolean isItalic;

    public int underlineType;

    public int underlineColor;

    public boolean isStrikeThrough;

    public boolean isDoubleStrikeThrough;

    public short subSuperScriptType;

    public int highlightedColor;

    public byte encloseType;

    public byte pageNumberType;


    public void reset() {
        underlineType = 0;
        fontColor = -1;
        underlineColor = -1;
        isStrikeThrough = false;
        isDoubleStrikeThrough = false;
        subSuperScriptType = 0;
        fontIndex = -1;
        encloseType = -1;
        pageNumberType = -1;
    }
}
