
package com.document.render.office.ss.model.table;


public class SSTableCellStyle {
    private int fontColor = 0xFF000000;

    private Integer borderColor;

    private Integer fillColor;

    public SSTableCellStyle(int fillColor) {
        this.fillColor = fillColor;
    }

    public int getFontColor() {
        return fontColor;
    }

    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }

    public Integer getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public Integer getFillColor() {
        return fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }
}
