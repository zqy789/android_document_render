
package com.document.render.office.fc.hssf.util;


public class ColumnInfo {
    private int _firstCol;
    private int _lastCol;
    private int _colWidth;
    private boolean hidden;
    private int style;

    public ColumnInfo(int _firstCol, int _lastCol, int _colWidth, int style, boolean hidden) {
        this._firstCol = _firstCol;
        this._lastCol = _lastCol;
        this._colWidth = _colWidth;
        this.setStyle(style);
        this.hidden = hidden;
    }


    public int getFirstCol() {
        return _firstCol;
    }


    public void setFirstCol(int _firstCol) {
        this._firstCol = _firstCol;
    }


    public int getLastCol() {
        return _lastCol;
    }


    public void setLastCol(int _lastCol) {
        this._lastCol = _lastCol;
    }


    public int getColWidth() {
        return _colWidth;
    }


    public void setColWidth(int _colWidth) {
        this._colWidth = _colWidth;
    }


    public boolean isHidden() {
        return hidden;
    }


    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }


    public int getStyle() {
        return style;
    }


    public void setStyle(int style) {
        this.style = style;
    }
}
