
package com.document.render.office.simpletext.view;



public class PageAttr {

    public static final byte GRIDTYPE_NONE = 0;
    public static final byte GRIDTYPE_LINE_AND_CHAR = 1;
    public static final byte GRIDTYPE_LINE = 2;
    public static final byte GRIDTYPE_CHAR = 3;


    public int pageWidth;

    public int pageHeight;

    public int topMargin;

    public int bottomMargin;

    public int leftMargin;

    public int rightMargin;

    public byte verticalAlign;

    public byte horizontalAlign;

    public int headerMargin;

    public int footerMargin;

    public int pageBRColor;

    public int pageBorder;

    public float pageLinePitch;


    public void reset() {
        verticalAlign = 0;
        horizontalAlign = 0;
        pageWidth = 0;
        pageHeight = 0;
        topMargin = 0;
        bottomMargin = 0;
        leftMargin = 0;
        rightMargin = 0;
        headerMargin = 0;
        footerMargin = 0;
        pageBorder = 0;
        pageBRColor = 0xFFFFFFFF;
        pageLinePitch = 0;
    }


    public void dispose() {

    }
}
