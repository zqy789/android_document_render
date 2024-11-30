

package com.document.render.office.fc.hslf.model;

import com.document.render.office.fc.hslf.record.FontEntityAtom;


public final class PPFont {

    public final static byte ANSI_CHARSET = 0;


    public final static byte DEFAULT_CHARSET = 1;


    public final static byte SYMBOL_CHARSET = 2;


    public final static byte DEFAULT_PITCH = 0;
    public final static byte FIXED_PITCH = 1;
    public final static byte VARIABLE_PITCH = 2;


    public final static byte FF_DONTCARE = 0;

    public final static byte FF_ROMAN = 16;

    public final static byte FF_SWISS = 32;

    public final static byte FF_SCRIPT = 64;

    public final static byte FF_MODERN = 48;

    public final static byte FF_DECORATIVE = 80;
    public static final PPFont ARIAL;
    public static final PPFont TIMES_NEW_ROMAN;
    public static final PPFont COURIER_NEW;
    public static final PPFont WINGDINGS;

    static {
        ARIAL = new PPFont();
        ARIAL.setFontName("Arial");
        ARIAL.setCharSet(ANSI_CHARSET);
        ARIAL.setFontType(4);
        ARIAL.setFontFlags(0);
        ARIAL.setPitchAndFamily(VARIABLE_PITCH | FF_SWISS);

        TIMES_NEW_ROMAN = new PPFont();
        TIMES_NEW_ROMAN.setFontName("Times New Roman");
        TIMES_NEW_ROMAN.setCharSet(ANSI_CHARSET);
        TIMES_NEW_ROMAN.setFontType(4);
        TIMES_NEW_ROMAN.setFontFlags(0);
        TIMES_NEW_ROMAN.setPitchAndFamily(VARIABLE_PITCH | FF_ROMAN);

        COURIER_NEW = new PPFont();
        COURIER_NEW.setFontName("Courier New");
        COURIER_NEW.setCharSet(ANSI_CHARSET);
        COURIER_NEW.setFontType(4);
        COURIER_NEW.setFontFlags(0);
        COURIER_NEW.setPitchAndFamily(FIXED_PITCH | FF_MODERN);

        WINGDINGS = new PPFont();
        WINGDINGS.setFontName("Wingdings");
        WINGDINGS.setCharSet(SYMBOL_CHARSET);
        WINGDINGS.setFontType(4);
        WINGDINGS.setFontFlags(0);
        WINGDINGS.setPitchAndFamily(VARIABLE_PITCH | FF_DONTCARE);
    }

    protected int charset;
    protected int type;
    protected int flags;
    protected int pitch;
    protected String name;


    public PPFont() {

    }


    public PPFont(FontEntityAtom fontAtom) {
        name = fontAtom.getFontName();
        charset = fontAtom.getCharSet();
        type = fontAtom.getFontType();
        flags = fontAtom.getFontFlags();
        pitch = fontAtom.getPitchAndFamily();
    }


    public String getFontName() {
        return name;
    }


    public void setFontName(String val) {
        name = val;
    }


    public int getCharSet() {
        return charset;
    }


    public void setCharSet(int val) {
        charset = val;
    }


    public int getFontFlags() {
        return flags;
    }


    public void setFontFlags(int val) {
        flags = val;
    }


    public int getFontType() {
        return type;
    }


    public void setFontType(int val) {
        type = val;
    }


    public int getPitchAndFamily() {
        return pitch;
    }


    public void setPitchAndFamily(int val) {
        pitch = val;
    }
}
