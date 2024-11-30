
package com.document.render.office.simpletext.font;


public class Font {

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

    public static final int PLAIN = 0;

    public static final int BOLD = 1;


    public static final int ITALIC = 2;

    protected int style;

    private int index;

    private String name;

    private double fontSize;

    private boolean isItalic;

    private boolean isBold;

    private int colorIndex;

    private byte superSubScript;

    private int underline;

    private boolean strikeline;

    public Font() {
    }

    public Font(String name, int style, int size) {
        this.name = (name != null) ? name : "Default";
        this.style = (style & ~0x03) == 0 ? style : 0;
        this.fontSize = size;
    }


    public int getStyle() {
        return style;
    }


    public int getIndex() {
        return index;
    }


    public void setIndex(int index) {
        this.index = index;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public double getFontSize() {
        return fontSize;
    }


    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }


    public boolean isItalic() {
        return isItalic;
    }


    public void setItalic(boolean isItalic) {
        this.isItalic = isItalic;
    }


    public boolean isBold() {
        return isBold;
    }


    public void setBold(boolean isBold) {
        this.isBold = isBold;
    }


    public int getColorIndex() {
        return colorIndex;
    }


    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }


    public byte getSuperSubScript() {
        return superSubScript;
    }


    public void setSuperSubScript(byte superSubScript) {
        this.superSubScript = superSubScript;
    }


    public int getUnderline() {
        return underline;
    }

    public void setUnderline(String underline) {
        if (underline.equalsIgnoreCase("none")) {
            setUnderline(U_NONE);
        } else if (underline.equalsIgnoreCase("single")) {
            setUnderline(U_SINGLE);
        } else if (underline.equalsIgnoreCase("double")) {
            setUnderline(U_DOUBLE);
        } else if (underline.equalsIgnoreCase("singleAccounting")) {
            setUnderline(U_SINGLE_ACCOUNTING);
        } else if (underline.equalsIgnoreCase("doubleAccounting")) {
            setUnderline(U_DOUBLE_ACCOUNTING);
        }
    }


    public void setUnderline(int underline) {
        this.underline = underline;
    }


    public boolean isStrikeline() {
        return strikeline;
    }


    public void setStrikeline(boolean strikeline) {
        this.strikeline = strikeline;
    }

    public void dispose() {
        name = null;
    }
}
