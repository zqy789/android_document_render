
package com.document.render.office.ss.other;


public class DrawingCell {

    private int rowIndex;
    private int columnIndex;

    private float left;
    private float top;
    private float width;
    private float height;

    private float visibleWidth;
    private float visibleHeight;

    public void reset() {
        setRowIndex(0);
        setColumnIndex(0);
        setLeft(0);
        setTop(0);
        setWidth(0);
        setHeight(0);
        setVisibleWidth(0);
        setVisibleHeight(0);
    }


    public int getRowIndex() {
        return rowIndex;
    }


    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }


    public void increaseRow() {
        rowIndex = rowIndex + 1;
    }


    public void increaseColumn() {
        columnIndex = columnIndex + 1;
    }


    public int getColumnIndex() {
        return columnIndex;
    }


    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }


    public float getLeft() {
        return left;
    }


    public void setLeft(float left) {
        this.left = left;
    }

    public void increaseLeftWithVisibleWidth() {
        left = left + visibleWidth;
    }


    public float getTop() {
        return top;
    }


    public void setTop(float top) {
        this.top = top;
    }

    public void increaseTopWithVisibleHeight() {
        top = top + visibleHeight;
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


    public float getVisibleWidth() {
        return visibleWidth;
    }


    public void setVisibleWidth(float visibleWidth) {
        this.visibleWidth = visibleWidth;
    }


    public float getVisibleHeight() {
        return visibleHeight;
    }


    public void setVisibleHeight(float visibleHeight) {
        this.visibleHeight = visibleHeight;
    }

    public void dispose() {

    }
}
