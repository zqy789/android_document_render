

package com.document.render.office.ss.model;

import com.document.render.office.constant.MainConstant;
import com.document.render.office.ss.model.baseModel.Row;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.model.interfacePart.IClientAnchor;


public final class ClientAnchor extends Anchor implements IClientAnchor {

    private short col1;

    private int row1;

    private short col2;

    private int row2;

    private int anchorType;


    public ClientAnchor() {
    }


    public ClientAnchor(int dx1, int dy1, int dx2, int dy2, short col1, int row1, short col2,
                        int row2) {
        super(dx1, dy1, dx2, dy2);

        checkRange(dx1, 0, 1023, "dx1");
        checkRange(dx2, 0, 1023, "dx2");
        checkRange(dy1, 0, 255, "dy1");
        checkRange(dy2, 0, 255, "dy2");
        checkRange(col1, 0, 255, "col1");
        checkRange(col2, 0, 255, "col2");
        checkRange(row1, 0, 255 * 256, "row1");
        checkRange(row2, 0, 255 * 256, "row2");

        this.col1 = col1;
        this.row1 = row1;
        this.col2 = col2;
        this.row2 = row2;
    }


    public float getAnchorHeightInPoints(Sheet sheet) {
        int y1 = getDy1();
        int y2 = getDy2();
        int row1 = Math.min(getRow1(), getRow2());
        int row2 = Math.max(getRow1(), getRow2());

        float points = 0;
        if (row1 == row2) {
            points = ((y2 - y1) / 256.0f) * getRowHeightInPoints(sheet, row2);
        } else {
            points += ((256.0f - y1) / 256.0f) * getRowHeightInPoints(sheet, row1);
            for (int i = row1 + 1; i < row2; i++) {
                points += getRowHeightInPoints(sheet, i);
            }
            points += (y2 / 256.0f) * getRowHeightInPoints(sheet, row2);
        }
        return points;
    }


    private float getRowHeightInPoints(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            return 18 * MainConstant.PIXEL_TO_POINT;
        }
        return row.getRowPixelHeight() * MainConstant.PIXEL_TO_POINT;
    }


    public short getCol1() {
        return col1;
    }


    public void setCol1(short col1) {
        checkRange(col1, 0, 255, "col1");
        this.col1 = col1;
    }


    public void setCol1(int col1) {
        setCol1((short) col1);
    }


    public short getCol2() {
        return col2;
    }


    public void setCol2(short col2) {
        checkRange(col2, 0, 255, "col2");
        this.col2 = col2;
    }


    public void setCol2(int col2) {
        setCol2((short) col2);
    }


    public int getRow1() {
        return row1;
    }


    public void setRow1(int row1) {
        checkRange(row1, 0, 256 * 256, "row1");
        this.row1 = row1;
    }


    public int getRow2() {
        return row2;
    }


    public void setRow2(int row2) {
        checkRange(row2, 0, 256 * 256, "row2");
        this.row2 = row2;
    }


    public void setAnchor(short col1, int row1, int x1, int y1, short col2, int row2, int x2, int y2) {
        checkRange(dx1, 0, 1023, "dx1");
        checkRange(dx2, 0, 1023, "dx2");
        checkRange(dy1, 0, 255, "dy1");
        checkRange(dy2, 0, 255, "dy2");
        checkRange(col1, 0, 255, "col1");
        checkRange(col2, 0, 255, "col2");
        checkRange(row1, 0, 255 * 256, "row1");
        checkRange(row2, 0, 255 * 256, "row2");

        this.col1 = col1;
        this.row1 = row1;
        this.dx1 = x1;
        this.dy1 = y1;
        this.col2 = col2;
        this.row2 = row2;
        this.dx2 = x2;
        this.dy2 = y2;
    }


    public boolean isHorizontallyFlipped() {
        if (col1 == col2) {
            return dx1 > dx2;
        }
        return col1 > col2;
    }


    public boolean isVerticallyFlipped() {
        if (row1 == row2) {
            return dy1 > dy2;
        }
        return row1 > row2;
    }


    public int getAnchorType() {
        return anchorType;
    }


    public void setAnchorType(int anchorType) {
        this.anchorType = anchorType;
    }

    private void checkRange(int value, int minRange, int maxRange, String varName) {
        if (value < minRange || value > maxRange)
            throw new IllegalArgumentException(varName + " must be between " + minRange + " and "
                    + maxRange);
    }

}
