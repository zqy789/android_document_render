

package com.document.render.office.fc.ss.usermodel;

import java.util.Iterator;


public interface IRow extends Iterable<ICell> {


    public static final MissingCellPolicy RETURN_NULL_AND_BLANK = new MissingCellPolicy();

    public static final MissingCellPolicy RETURN_BLANK_AS_NULL = new MissingCellPolicy();

    public static final MissingCellPolicy CREATE_NULL_AS_BLANK = new MissingCellPolicy();


    ICell createCell(int column);


    ICell createCell(int column, int type);


    void removeCell(ICell cell);


    int getRowNum();


    void setRowNum(int rowNum);


    ICell getCell(int cellnum);


    ICell getCell(int cellnum, MissingCellPolicy policy);


    short getFirstCellNum();


    short getLastCellNum();


    int getPhysicalNumberOfCells();


    boolean getZeroHeight();


    void setZeroHeight(boolean zHeight);


    short getHeight();


    void setHeight(short height);


    float getHeightInPoints();


    void setHeightInPoints(float height);


    boolean isFormatted();


    ICellStyle getRowStyle();


    void setRowStyle(ICellStyle style);


    Iterator<ICell> cellIterator();


    Sheet getSheet();


    public static final class MissingCellPolicy {
        private static int NEXT_ID = 1;
        public final int id;

        private MissingCellPolicy() {
            this.id = NEXT_ID++;
        }
    }
}
