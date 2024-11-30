
package com.document.render.office.ss.model.style;


public class BorderStyle {



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
    private short style = BORDER_NONE;
    private short colorIdx = 0;

    public BorderStyle() {

    }

    public BorderStyle(short style, short colorIdx) {
        this.style = style;
        this.colorIdx = colorIdx;
    }

    public BorderStyle(String style, short colorIdx) {
        this.style = getStyle(style);
        this.colorIdx = colorIdx;
    }


    private short getStyle(String style) {
        if (style == null || style.equals("none")) {
            return BORDER_NONE;
        } else if (style.equals("thin")) {
            return BORDER_THIN;
        } else if (style.equals("medium")) {
            return BORDER_MEDIUM;
        } else if (style.equals("dashed")) {
            return BORDER_DASHED;
        } else if (style.equals("dotted")) {
            return BORDER_DOTTED;
        } else if (style.equals("thick")) {
            return BORDER_THICK;
        } else if (style.equals("double")) {
            return BORDER_DOUBLE;
        } else if (style.equals("hair")) {
            return BORDER_HAIR;
        } else if (style.equals("mediumDashed")) {
            return BORDER_MEDIUM_DASHED;
        } else if (style.equals("dashDot")) {
            return BORDER_DASH_DOT;
        } else if (style.equals("mediumDashDot")) {
            return BORDER_MEDIUM_DASH_DOT;
        } else if (style.equals("dashDotDot")) {
            return BORDER_DASH_DOT_DOT;
        } else if (style.equals("mediumDashDotDot")) {
            return BORDER_MEDIUM_DASH_DOT_DOT;
        } else if (style.equals("slantDashDot")) {
            return BORDER_SLANTED_DASH_DOT;
        }

        return BORDER_NONE;

    }


    public short getStyle() {
        return style;
    }


    public void setStyle(short style) {
        this.style = style;
    }


    public short getColor() {
        return colorIdx;
    }


    public void setColor(short colorIdx) {
        this.colorIdx = colorIdx;
    }


    public void dispose() {

    }
}
