
package com.document.render.office.ss.model.baseModel;

import com.document.render.office.common.bg.BackgroundAndFill;
import com.document.render.office.common.shape.IShape;
import com.document.render.office.constant.SSConstant;
import com.document.render.office.simpletext.view.STRoot;
import com.document.render.office.ss.model.CellRangeAddress;
import com.document.render.office.ss.model.interfacePart.IReaderListener;
import com.document.render.office.ss.model.sheetProperty.ColumnInfo;
import com.document.render.office.ss.model.sheetProperty.PaneInformation;
import com.document.render.office.ss.model.style.CellStyle;
import com.document.render.office.ss.model.table.SSTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class Sheet {

    public final static short TYPE_WORKSHEET = 0;



    public final static short TYPE_CHARTSHEET = 1;



    public final static int INITIAL_CAPACITY = 20;


    public final static short ACTIVECELL_SINGLE = 0;


    public final static short ACTIVECELL_ROW = 1;


    public final static short ACTIVECELL_COLUMN = 2;


    public final static short State_NotAccomplished = 0;


    public final static short State_Reading = 1;


    public final static short State_Accomplished = 2;
    protected Workbook book;

    protected Map<Integer, Row> rows;
    protected List<IShape> shapesList;

    private boolean isGridsPrinted;

    private int firstRow;

    private int lastRow;

    private int activeCellRow;

    private int activeCellColumn;

    private float maxScrollX;
    private float maxScrollY;

    private int scrollX;
    private int scrollY;

    private short type;
    private short activeCellType;

    private float zoom = 1;

    private String sheetName;

    private Cell activeCell;

    private List<CellRangeAddress> merges;

    private PaneInformation paneInformation;

    private List<ColumnInfo> columnInfoList;
    private int defaultRowHeight = SSConstant.DEFAULT_ROW_HEIGHT;
    private int defaultColWidth = SSConstant.DEFAULT_COLUMN_WIDTH;
    private short state;

    private IReaderListener iReaderListener;

    private List<STRoot> rootViewMap;

    private List<SSTable> tableList;


    public Sheet() {
        activeCellType = ACTIVECELL_SINGLE;

        rows = new HashMap<Integer, Row>();
        merges = new ArrayList<CellRangeAddress>();

        maxScrollX = Integer.MAX_VALUE;
        maxScrollY = Integer.MAX_VALUE;

        shapesList = new ArrayList<IShape>();
    }


    public Workbook getWorkbook() {
        return book;
    }


    public void setWorkbook(Workbook book) {
        this.book = book;
    }


    public void addRow(Row row) {
        if (row == null) {
            return;
        }

        rows.put(Integer.valueOf(row.getRowNumber()), row);
        if (rows.size() == 1) {
            firstRow = row.getRowNumber();
            lastRow = row.getRowNumber();
        } else {
            firstRow = Math.min(firstRow, row.getRowNumber());
            lastRow = Math.max(lastRow, row.getRowNumber());
        }
    }


    public int addMergeRange(CellRangeAddress range) {
        merges.add(range);
        return merges.size();
    }


    public int getMergeRangeCount() {
        return merges.size();
    }


    public CellRangeAddress getMergeRange(int index) {
        if (index < 0 || index >= merges.size()) {
            return null;
        }
        return merges.get(index);
    }


    public Row getRow(int rowIndex) {
        return rows.get(Integer.valueOf(rowIndex));
    }


    public Row getRowByColumnsStyle(int rowIndex) {
        Row row = rows.get(Integer.valueOf(rowIndex));
        if (row != null) {
            return row;
        }

        if (columnInfoList == null || columnInfoList.size() == 0) {
            return null;
        }

        ColumnInfo columnInfo;
        int index = 0;
        while (index < columnInfoList.size()) {
            columnInfo = columnInfoList.get(index++);
            CellStyle cellStyle = book.getCellStyle(columnInfo.getStyle());
            if (cellStyle != null
                    && ((cellStyle.getFillPatternType() == BackgroundAndFill.FILL_SOLID && (cellStyle.getFgColor() & 0xFFFFFF) != 0xFFFFFF)
                    || cellStyle.getBorderLeft() > 0
                    || cellStyle.getBorderTop() > 0
                    || cellStyle.getBorderRight() > 0
                    || cellStyle.getBorderBottom() > 0)) {
                row = new Row(1);
                row.setRowNumber(rowIndex);
                row.setRowPixelHeight(defaultRowHeight);
                row.setSheet(this);
                row.completed();

                rows.put(rowIndex, row);

                return row;
            }
        }

        return null;
    }


    public int getPhysicalNumberOfRows() {
        return rows.size();
    }


    public String getSheetName() {
        return sheetName;
    }


    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }


    public float getZoom() {
        return zoom;
    }


    public void setZoom(float zoom) {
        this.zoom = zoom;
    }


    public float getMaxScrollX() {
        return maxScrollX;
    }


    public float getMaxScrollY() {
        return maxScrollY;
    }


    public int getScrollX() {
        return scrollX;
    }


    public void setScrollX(int scrollX) {
        this.scrollX = scrollX;
    }


    public int getScrollY() {
        return scrollY;
    }


    public void setScrollY(int scrollY) {
        this.scrollY = scrollY;
    }


    public void setScroll(int scrollX, int scrollY) {
        this.scrollX = scrollX;
        this.scrollY = scrollY;
    }


    public int getFirstRowNum() {
        return firstRow;
    }


    public void setFirstRowNum(int firstRow) {
        this.firstRow = firstRow;
    }


    public int getLastRowNum() {
        return lastRow;
    }


    public void setLastRowNum(int lastRow) {
        this.lastRow = lastRow;
    }


    public void addColumnInfo(ColumnInfo columnInfo) {
        if (columnInfoList == null) {
            columnInfoList = new ArrayList<ColumnInfo>();
        }
        columnInfoList.add(columnInfo);
    }


    public int getColumnStyle(int column) {
        if (columnInfoList != null) {
            ColumnInfo columnInfo;
            int index = 0;
            while (index < columnInfoList.size()) {
                columnInfo = columnInfoList.get(index++);
                if (columnInfo.getFirstCol() <= column && columnInfo.getLastCol() >= column) {
                    return columnInfo.getStyle();
                }
            }

        }
        return 0;
    }

    public void setColumnPixelWidth(int column, int width) {
        if (columnInfoList != null) {
            ColumnInfo columnInfo;
            int index = 0;
            while (index < columnInfoList.size()) {
                columnInfo = columnInfoList.get(index++);
                if (columnInfo.getFirstCol() == column && columnInfo.getLastCol() == column) {
                    columnInfo.setColWidth(width);
                    return;
                } else if (columnInfo.getFirstCol() == column) {
                    ColumnInfo columnInfo3 =
                            new ColumnInfo(column + 1, columnInfo.getLastCol(), columnInfo.getColWidth(), columnInfo.getStyle(), columnInfo.isHidden());

                    columnInfo.setColWidth(width);
                    columnInfo.setLastCol(column);

                    columnInfoList.add(columnInfo3);
                    return;
                } else if (columnInfo.getLastCol() == column) {
                    ColumnInfo columnInfo1 =
                            new ColumnInfo(columnInfo.getFirstCol(), column - 1, columnInfo.getColWidth(), columnInfo.getStyle(), columnInfo.isHidden());

                    columnInfo.setColWidth(width);
                    columnInfo.setFirstCol(column);

                    columnInfoList.add(columnInfo1);
                    return;
                } else if (columnInfo.getFirstCol() < column && columnInfo.getLastCol() > column) {
                    ColumnInfo columnInfo1 =
                            new ColumnInfo(columnInfo.getFirstCol(), column - 1, columnInfo.getColWidth(), columnInfo.getStyle(), columnInfo.isHidden());


                    ColumnInfo columnInfo2 =
                            new ColumnInfo(column + 1, columnInfo.getLastCol(), columnInfo.getColWidth(), columnInfo.getStyle(), columnInfo.isHidden());

                    columnInfo.setFirstCol(column);
                    columnInfo.setLastCol(column);
                    columnInfo.setColWidth(width);

                    columnInfoList.add(columnInfo1);
                    columnInfoList.add(columnInfo2);
                    return;
                }
            }

            columnInfoList.add(new ColumnInfo(column, column, width, 0, false));


        } else {
            columnInfoList = new ArrayList<ColumnInfo>();

            columnInfoList.add(new ColumnInfo(column, column, width, 0, false));
        }
    }


    public float getColumnPixelWidth(int column) {
        if (columnInfoList != null) {
            ColumnInfo columnInfo;
            int index = 0;
            while (index < columnInfoList.size()) {
                columnInfo = columnInfoList.get(index++);
                if (columnInfo.getFirstCol() <= column && columnInfo.getLastCol() >= column) {
                    return columnInfo.getColWidth();
                }
            }
        }

        return defaultColWidth;
    }

    public ColumnInfo getColumnInfo(int column) {
        if (columnInfoList != null) {
            ColumnInfo columnInfo;
            int index = 0;
            while (index < columnInfoList.size()) {
                columnInfo = columnInfoList.get(index++);
                if (columnInfo.getFirstCol() <= column && columnInfo.getLastCol() >= column) {
                    return columnInfo;
                }
            }
        }

        return null;
    }


    public boolean isGridsPrinted() {
        return isGridsPrinted;
    }


    public void setGridsPrinted(boolean isGridsPrinted) {
        this.isGridsPrinted = isGridsPrinted;
    }


    public PaneInformation getPaneInformation() {
        return null;
    }


    public void setPaneInformation(PaneInformation paneInformation) {
        this.paneInformation = paneInformation;
    }


    public boolean isColumnHidden(int column) {
        if (columnInfoList != null) {
            ColumnInfo columnInfo;
            int index = 0;
            while (index < columnInfoList.size()) {
                columnInfo = columnInfoList.get(index++);
                if (columnInfo.getFirstCol() <= column && columnInfo.getLastCol() >= column) {
                    return columnInfo.isHidden();
                }
            }
        }
        return false;
    }


    public void setColumnHidden(int columnNumber, boolean isColumnHidden) {

    }

    public short getActiveCellType() {
        return activeCellType;
    }


    public void setActiveCellType(short type) {
        this.activeCellType = type;
    }


    private void checkActiveRowAndColumnBounds() {
        if (book.isBefore07Version()) {

            activeCellRow = Math.min(activeCellRow, Workbook.MAXROW_03 - 1);
            activeCellColumn = Math.min(activeCellColumn, Workbook.MAXCOLUMN_03 - 1);
        } else {

            activeCellRow = Math.min(activeCellRow, Workbook.MAXROW_07 - 1);
            activeCellColumn = Math.min(activeCellColumn, Workbook.MAXCOLUMN_07 - 1);
        }
    }


    public int getActiveCellRow() {
        return activeCellRow;
    }

    public void setActiveCellRow(int activeCellRow) {
        this.activeCellRow = activeCellRow;
        checkActiveRowAndColumnBounds();
    }


    public int getActiveCellColumn() {
        return activeCellColumn;
    }

    public void setActiveCellColumn(int activeCellColumn) {
        this.activeCellColumn = activeCellColumn;
        checkActiveRowAndColumnBounds();
    }


    public void setActiveCellRowCol(int row, int col) {
        activeCellType = ACTIVECELL_SINGLE;
        activeCellRow = row;
        activeCellColumn = col;
        checkActiveRowAndColumnBounds();

        CellRangeAddress cellRangeAddress;
        int index = 0;
        while (index < merges.size()) {
            cellRangeAddress = merges.get(index++);
            if (cellRangeAddress.isInRange(row, col)) {
                activeCellRow = cellRangeAddress.getFirstRow();
                activeCellColumn = cellRangeAddress.getFirstColumn();
            }
        }


        if (getRow(row) != null) {
            activeCell = getRow(row).getCell(col);
        } else {
            activeCell = null;
        }

    }


    public Cell getActiveCell() {
        return activeCell;
    }


    public void setActiveCell(Cell activeCell) {
        this.activeCell = activeCell;
        if (activeCell != null) {
            activeCellRow = activeCell.getRowNumber();
            activeCellColumn = activeCell.getColNumber();
        } else {
            activeCellRow = -1;
            activeCellColumn = -1;
        }
    }


    public void appendShapes(IShape shape) {
        this.shapesList.add(shape);
    }


    public IShape[] getShapes() {
        return shapesList.toArray(new IShape[shapesList.size()]);
    }


    public int getShapeCount() {
        return shapesList.size();
    }


    public IShape getShape(int index) {
        if (index < 0 || index >= shapesList.size()) {
            return null;
        }
        return shapesList.get(index);
    }


    public int getDefaultRowHeight() {
        return defaultRowHeight;
    }


    public void setDefaultRowHeight(int defaultRowHeight) {
        this.defaultRowHeight = defaultRowHeight;
    }


    public int getDefaultColWidth() {
        return defaultColWidth;
    }


    public void setDefaultColWidth(int defaultColWidth) {
        this.defaultColWidth = defaultColWidth;
    }


    public short getSheetType() {
        return type;
    }


    public void setSheetType(short type) {
        this.type = type;
    }


    public synchronized short getState() {
        return state;
    }


    public void setState(short state) {
        this.state = state;
        if (state == State_Accomplished && iReaderListener != null) {
            iReaderListener.OnReadingFinished();
        }

        maxScrollX = 0;
        maxScrollY = 0;

        int columnsCnt = 0;
        if (columnInfoList != null) {
            Iterator<ColumnInfo> iter = columnInfoList.iterator();
            ColumnInfo info;
            while (iter.hasNext()) {
                info = iter.next();
                columnsCnt += info.getLastCol() - info.getFirstCol() + 1;
                if (info.isHidden()) {
                    continue;
                }
                maxScrollX += info.getColWidth() * (info.getLastCol() - info.getFirstCol() + 1);
            }
        }

        int rowCnt = rows.size();
        Iterator<Row> iter = rows.values().iterator();
        while (iter.hasNext()) {
            maxScrollY += iter.next().getRowPixelHeight();
        }

        if (!book.isBefore07Version()) {

            maxScrollX += (Workbook.MAXCOLUMN_07 - columnsCnt) * defaultColWidth;
            maxScrollY += (Workbook.MAXROW_07 - rowCnt) * defaultRowHeight;
        } else {

            maxScrollX += (Workbook.MAXCOLUMN_03 - columnsCnt) * defaultColWidth;
            maxScrollY += (Workbook.MAXROW_03 - rowCnt) * defaultRowHeight;
        }

    }


    public boolean isAccomplished() {
        return state == State_Accomplished;
    }


    public void setReaderListener(IReaderListener iReaderListener) {
        this.iReaderListener = iReaderListener;
    }


    public int addSTRoot(STRoot root) {
        if (rootViewMap == null) {
            rootViewMap = new ArrayList<STRoot>();
        }
        int id = rootViewMap.size();
        rootViewMap.add(id, root);

        return id;
    }


    public STRoot getSTRoot(int id) {
        if (id < 0 || id >= rootViewMap.size()) {
            return null;
        }
        return rootViewMap.get(id);
    }


    public void removeSTRoot() {
        if (rootViewMap != null) {
            int cnt = rootViewMap.size();
            int index = 0;
            while (index < cnt) {
                STRoot root = rootViewMap.get(index++);
                if (root != null) {
                    root.dispose();
                }
            }
            rootViewMap.clear();
        }

        int rowIndex = firstRow;
        while (rowIndex <= lastRow) {
            Row row = getRow(rowIndex++);
            if (row == null || (row != null && row.isZeroHeight())) {
                continue;
            }
            row.setInitExpandedRangeAddress(false);

            Iterator<Cell> iter = row.cellCollection().iterator();
            while (iter.hasNext()) {
                iter.next().removeSTRoot();
            }
        }
    }


    public void addTable(SSTable table) {
        if (tableList == null) {
            tableList = new ArrayList<SSTable>();
        }

        tableList.add(table);
    }


    public SSTable[] getTables() {
        if (tableList != null) {
            return tableList.toArray(new SSTable[tableList.size()]);
        }

        return null;
    }


    public void dispose() {
        book = null;
        sheetName = null;
        paneInformation = null;

        iReaderListener = null;

        if (activeCell != null) {
            activeCell.dispose();
            activeCell = null;
        }

        if (rows != null) {
            Collection<Row> rowCollection = rows.values();
            for (Row row : rowCollection) {
                row.dispose();
            }
            rows.clear();
            rows = null;
        }

        if (merges != null) {
            Iterator<CellRangeAddress> iter = merges.iterator();
            while (iter.hasNext()) {
                (iter.next()).dispose();
            }

            merges.clear();
            merges = null;
        }

        if (columnInfoList != null) {
            columnInfoList.clear();
            columnInfoList = null;
        }

        if (shapesList != null) {
            Iterator<IShape> iter = shapesList.iterator();
            while (iter.hasNext()) {
                (iter.next()).dispose();
            }

            shapesList.clear();
            shapesList = null;
        }

        if (rootViewMap != null) {
            removeSTRoot();
            rootViewMap = null;
        }

        if (tableList != null) {
            tableList.clear();
            tableList = null;
        }
    }
}
