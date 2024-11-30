

package com.document.render.office.fc.ss.usermodel;


public interface PatternFormatting {

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

    short getFillBackgroundColor();

    void setFillBackgroundColor(short bg);

    short getFillForegroundColor();

    void setFillForegroundColor(short fg);

    short getFillPattern();

    void setFillPattern(short fp);
}
