
package com.document.render.office.ss.model.baseModel;

import com.document.render.office.common.hyperlink.Hyperlink;
import com.document.render.office.simpletext.view.STRoot;
import com.document.render.office.ss.model.style.CellStyle;
import com.document.render.office.ss.model.table.SSTable;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;



public class Cell {

    public final static short CELL_TYPE_NUMERIC = 0;

    public final static short CELL_TYPE_STRING = CELL_TYPE_NUMERIC + 1;

    public final static short CELL_TYPE_FORMULA = CELL_TYPE_STRING + 1;

    public final static short CELL_TYPE_BLANK = CELL_TYPE_FORMULA + 1;

    public final static short CELL_TYPE_BOOLEAN = CELL_TYPE_BLANK + 1;

    public final static short CELL_TYPE_ERROR = CELL_TYPE_BOOLEAN + 1;

    public final static short CELL_TYPE_NUMERIC_GENERAL = CELL_TYPE_ERROR + 1;

    public final static short CELL_TYPE_NUMERIC_DECIMAL = CELL_TYPE_NUMERIC_GENERAL + 1;

    public final static short CELL_TYPE_NUMERIC_ACCOUNTING = CELL_TYPE_NUMERIC_DECIMAL + 1;

    public final static short CELL_TYPE_NUMERIC_FRACTIONAL = CELL_TYPE_NUMERIC_ACCOUNTING + 1;

    public final static short CELL_TYPE_NUMERIC_SIMPLEDATE = CELL_TYPE_NUMERIC_FRACTIONAL + 1;

    public final static short CELL_TYPE_NUMERIC_STRING = CELL_TYPE_NUMERIC_SIMPLEDATE + 1;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int HOURS_PER_DAY = 24;
    private static final int SECONDS_PER_DAY = (HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE);
    private static final long DAY_MILLISECONDS = SECONDS_PER_DAY * 1000L;

    private static Calendar CALENDAR = new GregorianCalendar();
    protected Sheet sheet;

    protected short cellType;

    protected int rowNumber;

    protected int colNumber;

    protected int styleIndex;

    protected Object value;
    private CellProperty prop;


    public Cell(short cellType) {
        this.cellType = cellType;

        prop = new CellProperty();
    }


    public Sheet getSheet() {
        return sheet;
    }


    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }


    public short getCellType() {
        return this.cellType;
    }


    public void setCellType(short cellType) {
        this.cellType = cellType;
    }

    public short getCellNumericType() {
        return prop.getCellNumericType();
    }

    public void setCellNumericType(short numericType) {
        if (cellType == CELL_TYPE_NUMERIC) {
            prop.setCellProp(CellProperty.CELLPROPID_NUMERICTYPE, numericType);
        }
    }


    public void setCellValue(Object value) {
        this.value = value;
    }


    public int getStringCellValueIndex() {
        if (cellType == CELL_TYPE_STRING && value != null) {
            return (Integer) value;
        }
        return -1;
    }


    public double getNumberValue() {
        if (cellType == CELL_TYPE_NUMERIC && value != null) {
            return ((Double) value).doubleValue();
        }
        return Double.NaN;
    }

    public int getErrorValue() {
        if (cellType == CELL_TYPE_ERROR && value != null) {
            return (Byte) value;
        }
        return -1;
    }


    public byte getErrorCodeValue() {
        if (cellType == CELL_TYPE_ERROR && value != null) {
            return ((Byte) value).byteValue();
        }
        return Byte.MIN_VALUE;
    }


    public String getCellFormulaValue() {
        if (cellType == CELL_TYPE_FORMULA && value != null) {
            return ((String) value);
        }
        return null;
    }


    public boolean getBooleanValue() {
        if (cellType == CELL_TYPE_BOOLEAN && value != null) {
            return ((Boolean) value).booleanValue();
        }
        return false;
    }


    public Date getDateCellValue(boolean use1904windowing) {
        if (cellType == CELL_TYPE_NUMERIC && value != null) {
            double date = ((Double) value).doubleValue();
            int wholeDays = (int) Math.floor(date);
            int millisecondsInDay = (int) ((date - wholeDays) * DAY_MILLISECONDS + 0.5);

            int startYear = use1904windowing ? 1904 : 1900;



            int dayAdjust = use1904windowing ? 1 : (wholeDays < 61 ? 0 : -1);

            CALENDAR.clear();
            CALENDAR.set(startYear, 0, wholeDays + dayAdjust, 0, 0, 0);
            CALENDAR.set(GregorianCalendar.MILLISECOND, millisecondsInDay);
            return CALENDAR.getTime();
        }
        return null;
    }


    public int getRangeAddressIndex() {
        return prop.getCellMergeRangeAddressIndex();
    }


    public void setRangeAddressIndex(int rangeAddressIndex) {
        prop.setCellProp(CellProperty.CELLPROPID_MERGEDRANGADDRESS, rangeAddressIndex);
    }


    public int getRowNumber() {
        return rowNumber;
    }


    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }


    public int getColNumber() {
        return colNumber;
    }


    public void setColNumber(int colNumber) {
        this.colNumber = colNumber;
    }


    public Hyperlink getHyperLink() {
        return prop.getCellHyperlink();
    }


    public void setHyperLink(Hyperlink link) {
        prop.setCellProp(CellProperty.CELLPROPID_HYPERLINK, link);
    }


    public CellStyle getCellStyle() {
        return sheet.getWorkbook().getCellStyle(styleIndex);
    }


    public void setCellStyle(int styleIndex) {
        this.styleIndex = styleIndex;
    }

    public boolean hasValidValue() {
        return value != null;
    }

    public STRoot getSTRoot() {
        return sheet.getSTRoot(prop.getCellSTRoot());
    }

    public void setSTRoot(STRoot root) {
        if (sheet.getState() == Sheet.State_Accomplished) {
            prop.setCellProp(CellProperty.CELLPROPID_STROOT, sheet.addSTRoot(root));
        }
    }


    public void removeSTRoot() {
        prop.removeCellSTRoot();
    }


    public int getExpandedRangeAddressIndex() {
        return prop.getExpandCellRangeAddressIndex();
    }


    public void setExpandedRangeAddressIndex(int index) {
        prop.setCellProp(CellProperty.CELLPROPID_EXPANDRANGADDRESS, index);
    }


    public SSTable getTableInfo() {
        return prop.getTableInfo();
    }


    public void setTableInfo(SSTable table) {
        prop.setCellProp(CellProperty.CELLPROPID_TABLEINFO, table);
    }


    public void dispose() {
        sheet = null;
        value = null;

        if (prop != null) {
            prop.dispose();
            prop = null;
        }
    }
}
