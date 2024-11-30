

package com.document.render.office.ss.model.sheetProperty;


public class PaneInformation {

    public static final byte PANE_LOWER_RIGHT = (byte) 0;

    public static final byte PANE_UPPER_RIGHT = (byte) 1;

    public static final byte PANE_LOWER_LEFT = (byte) 2;

    public static final byte PANE_UPPER_LEFT = (byte) 3;

    private short topRow;
    private short leftColumn;
    private boolean frozen = true;

    public PaneInformation() {

    }


    public PaneInformation(short top, short left, boolean frozen) {
        this.topRow = top;
        this.leftColumn = left;
        this.frozen = frozen;
    }


    public short getHorizontalSplitTopRow() {
        return topRow;
    }

    public void setHorizontalSplitTopRow(short topRow) {
        this.topRow = topRow;
    }


    public short getVerticalSplitLeftColumn() {
        return leftColumn;
    }

    public void setVerticalSplitLeftColumn(short leftColumn) {
        this.leftColumn = leftColumn;
    }

    public void setFreePane(boolean frozen) {
        this.frozen = frozen;
    }


    public boolean isFreezePane() {
        return frozen;
    }
}
