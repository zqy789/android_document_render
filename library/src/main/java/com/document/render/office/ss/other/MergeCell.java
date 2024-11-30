
package com.document.render.office.ss.other;


public class MergeCell {
    private float width;
    private float height;
    private boolean frozenRow;
    private boolean frozenColumn;



    private float novisibleWidth;

    private float noVisibleHeight;

    public void reset() {
        setWidth(0);
        setHeight(0);
        setFrozenRow(false);
        setFrozenColumn(false);
        setNovisibleWidth(0);
        setNoVisibleHeight(0);
    }


    public float getWidth() {
        return width;
    }


    public void setWidth(float width) {
        this.width = width;
    }


    public float getHeight() {
        return height;
    }


    public void setHeight(float height) {
        this.height = height;
    }


    public void dispose() {

    }


    public boolean isFrozenRow() {
        return frozenRow;
    }


    public void setFrozenRow(boolean frozenRow) {
        this.frozenRow = frozenRow;
    }


    public boolean isFrozenColumn() {
        return frozenColumn;
    }


    public void setFrozenColumn(boolean frozenColumn) {
        this.frozenColumn = frozenColumn;
    }


    public float getNovisibleWidth() {
        return novisibleWidth;
    }


    public void setNovisibleWidth(float novisibleWidth) {
        this.novisibleWidth = novisibleWidth;
    }


    public float getNoVisibleHeight() {
        return noVisibleHeight;
    }


    public void setNoVisibleHeight(float noVisibleHeight) {
        this.noVisibleHeight = noVisibleHeight;
    }

}
