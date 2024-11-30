
package com.document.render.office.ss.other;

import android.graphics.Rect;


public class FocusCell {
    public final static short UNKNOWN = 0;

    public final static short ROWHEADER = UNKNOWN + 1;

    public final static short COLUMNHEADER = ROWHEADER + 1;

    public final static short CELL = COLUMNHEADER + 1;

    private short headerType = UNKNOWN;

    private int row;
    private int col;
    private Rect area;

    public FocusCell() {

    }


    public FocusCell(short type, Rect area, int row, int col) {
        this.headerType = type;
        this.area = area;
        if (type == ROWHEADER) {
            this.row = row;
        } else if (type == COLUMNHEADER) {
            this.col = col;
        }
    }

    public FocusCell clone() {
        Rect rect = new Rect(area);
        return new FocusCell(headerType, rect, row, col);
    }


    public int getType() {
        return headerType;
    }


    public void setType(short type) {
        headerType = type;
    }


    public int getRow() {
        if (headerType == ROWHEADER || headerType == CELL) {
            return row;
        }
        return -1;
    }


    public void setRow(int row) {
        if (headerType == ROWHEADER || headerType == CELL) {
            this.row = row;
        }

    }


    public int getColumn() {
        if (headerType == COLUMNHEADER || headerType == CELL) {
            return col;
        }
        return -1;
    }


    public void setColumn(int col) {
        if (headerType == COLUMNHEADER || headerType == CELL) {
            this.col = col;
        }

    }

    public Rect getRect() {
        return area;
    }

    public void setRect(Rect area) {
        this.area = area;
    }

    public void dispose() {
    }
}
