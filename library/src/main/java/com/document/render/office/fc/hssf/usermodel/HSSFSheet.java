

package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.ddf.EscherRecord;
import com.document.render.office.fc.hssf.formula.FormulaShifter;
import com.document.render.office.fc.hssf.formula.ptg.Area3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.model.InternalSheet;
import com.document.render.office.fc.hssf.model.InternalWorkbook;
import com.document.render.office.fc.hssf.record.AutoFilterInfoRecord;
import com.document.render.office.fc.hssf.record.CellValueRecordInterface;
import com.document.render.office.fc.hssf.record.DVRecord;
import com.document.render.office.fc.hssf.record.DimensionsRecord;
import com.document.render.office.fc.hssf.record.EscherAggregate;
import com.document.render.office.fc.hssf.record.ExtendedFormatRecord;
import com.document.render.office.fc.hssf.record.NameRecord;
import com.document.render.office.fc.hssf.record.NoteRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.RowRecord;
import com.document.render.office.fc.hssf.record.SCLRecord;
import com.document.render.office.fc.hssf.record.WSBoolRecord;
import com.document.render.office.fc.hssf.record.WindowTwoRecord;
import com.document.render.office.fc.hssf.record.aggregates.DataValidityTable;
import com.document.render.office.fc.hssf.record.aggregates.FormulaRecordAggregate;
import com.document.render.office.fc.hssf.record.aggregates.WorksheetProtectionBlock;
import com.document.render.office.fc.hssf.util.ColumnInfo;
import com.document.render.office.fc.hssf.util.HSSFPaneInformation;
import com.document.render.office.fc.hssf.util.Region;
import com.document.render.office.fc.ss.SpreadsheetVersion;
import com.document.render.office.fc.ss.usermodel.CellRange;
import com.document.render.office.fc.ss.usermodel.DataValidation;
import com.document.render.office.fc.ss.usermodel.DataValidationHelper;
import com.document.render.office.fc.ss.usermodel.ICell;
import com.document.render.office.fc.ss.usermodel.ICellStyle;
import com.document.render.office.fc.ss.usermodel.IRow;
import com.document.render.office.fc.ss.util.CellReference;
import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;
import com.document.render.office.fc.ss.util.SSCellRange;
import com.document.render.office.fc.ss.util.SheetUtil;
import com.document.render.office.fc.util.POILogFactory;
import com.document.render.office.fc.util.POILogger;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;



public final class HSSFSheet implements com.document.render.office.fc.ss.usermodel.Sheet {

    public final static int INITIAL_CAPACITY = 20;
    private static final POILogger log = POILogFactory.getLogger(HSSFSheet.class);
    private static final int DEBUG = POILogger.DEBUG;
    protected final InternalWorkbook _book;
    protected final HSSFWorkbook _workbook;

    private final InternalSheet _sheet;

    private final TreeMap<Integer, HSSFRow> _rows;
    HSSFPaneInformation paneInformation;
    private HSSFPatriarch _patriarch;
    private int _firstrow;
    private int _lastrow;

    private boolean isInitForDraw;

    private float zoom = 1;

    private int scrollX;
    private int scrollY;

    private int row_activecell = -1;
    private int column_activecell = -1;


    protected HSSFSheet(HSSFWorkbook workbook) {
        _sheet = InternalSheet.createSheet();
        _rows = new TreeMap<Integer, HSSFRow>();
        this._workbook = workbook;
        this._book = workbook.getWorkbook();
    }


    protected HSSFSheet(HSSFWorkbook workbook, InternalSheet sheet) {
        this._sheet = sheet;
        _rows = new TreeMap<Integer, HSSFRow>();
        this._workbook = workbook;
        this._book = workbook.getWorkbook();
        setPropertiesFromSheet(sheet);
    }

    HSSFSheet cloneSheet(HSSFWorkbook workbook) {
        return new HSSFSheet(workbook, _sheet.cloneSheet());
    }


    public HSSFWorkbook getWorkbook() {
        return _workbook;
    }


    private void setPropertiesFromSheet(InternalSheet sheet) {

        RowRecord row = sheet.getNextRow();
        boolean rowRecordsAlreadyPresent = row != null;

        while (row != null) {
            createRowFromRecord(row);

            row = sheet.getNextRow();
        }

        Iterator<CellValueRecordInterface> iter = sheet.getCellValueIterator();
        long timestart = System.currentTimeMillis();

        if (log.check(POILogger.DEBUG))
            log.log(DEBUG, "Time at start of cell creating in HSSF sheet = ",
                    Long.valueOf(timestart));
        HSSFRow lastrow = null;


        while (iter.hasNext()) {
            CellValueRecordInterface cval = iter.next();

            long cellstart = System.currentTimeMillis();
            HSSFRow hrow = lastrow;

            if (hrow == null || hrow.getRowNum() != cval.getRow()) {
                hrow = getRow(cval.getRow());
                lastrow = hrow;
                if (hrow == null) {


                    if (rowRecordsAlreadyPresent) {

                        throw new RuntimeException(
                                "Unexpected missing row when some rows already present");
                    }

                    RowRecord rowRec = new RowRecord(cval.getRow());
                    sheet.addRow(rowRec);
                    hrow = createRowFromRecord(rowRec);
                }
            }
            if (log.check(POILogger.DEBUG))
                log.log(DEBUG, "record id = " + Integer.toHexString(((Record) cval).getSid()));
            hrow.createCellFromRecord(cval);
            if (log.check(POILogger.DEBUG))
                log.log(DEBUG, "record took ", Long.valueOf(System.currentTimeMillis() - cellstart));

        }
        if (log.check(POILogger.DEBUG))
            log.log(DEBUG, "total sheet cell creation took ",
                    Long.valueOf(System.currentTimeMillis() - timestart));
    }


    public HSSFRow createRow(int rownum) {
        HSSFRow row = new HSSFRow(_workbook, this, rownum);

        addRow(row, true);
        return row;
    }



    private HSSFRow createRowFromRecord(RowRecord row) {
        HSSFRow hrow = new HSSFRow(_workbook, this, row);

        addRow(hrow, false);
        return hrow;
    }


    public void removeRow(IRow row) {
        HSSFRow hrow = (HSSFRow) row;
        if (row.getSheet() != this) {
            throw new IllegalArgumentException("Specified row does not belong to this sheet");
        }
        for (ICell cell : row) {
            HSSFCell xcell = (HSSFCell) cell;
            if (xcell.isPartOfArrayFormulaGroup()) {
                String msg = "Row[rownum="
                        + row.getRowNum()
                        + "] contains cell(s) included in a multi-cell array formula. You cannot change part of an array.";
                xcell.notifyArrayFormulaChanging(msg);
            }
        }

        if (_rows.size() > 0) {
            Integer key = Integer.valueOf(row.getRowNum());
            HSSFRow removedRow = _rows.remove(key);
            if (removedRow != row) {

                throw new IllegalArgumentException("Specified row does not belong to this sheet");
            }
            if (hrow.getRowNum() == getLastRowNum()) {
                _lastrow = findLastRow(_lastrow);
            }
            if (hrow.getRowNum() == getFirstRowNum()) {
                _firstrow = findFirstRow(_firstrow);
            }
            _sheet.removeRow(hrow.getRowRecord());
        }
    }


    private int findLastRow(int lastrow) {
        if (lastrow < 1) {
            return 0;
        }
        int rownum = lastrow - 1;
        HSSFRow r = getRow(rownum);

        while (r == null && rownum > 0) {
            r = getRow(--rownum);
        }
        if (r == null) {
            return 0;
        }
        return rownum;
    }



    private int findFirstRow(int firstrow) {
        int rownum = firstrow + 1;
        HSSFRow r = getRow(rownum);

        while (r == null && rownum <= getLastRowNum()) {
            r = getRow(++rownum);
        }

        if (rownum > getLastRowNum())
            return 0;

        return rownum;
    }



    private void addRow(HSSFRow row, boolean addLow) {
        _rows.put(Integer.valueOf(row.getRowNum()), row);
        if (addLow) {
            _sheet.addRow(row.getRowRecord());
        }
        boolean firstRow = _rows.size() == 1;
        if (row.getRowNum() > getLastRowNum() || firstRow) {
            _lastrow = row.getRowNum();
        }
        if (row.getRowNum() < getFirstRowNum() || firstRow) {
            _firstrow = row.getRowNum();
        }
    }


    public HSSFRow getRow(int rowIndex) {
        return _rows.get(Integer.valueOf(rowIndex));
    }


    public int getPhysicalNumberOfRows() {
        return _rows.size();
    }


    public int getFirstRowNum() {
        return _firstrow;
    }


    public int getLastRowNum() {
        return _lastrow;
    }


    public void addValidationData(DataValidation dataValidation) {
        if (dataValidation == null) {
            throw new IllegalArgumentException("objValidation must not be null");
        }
        HSSFDataValidation hssfDataValidation = (HSSFDataValidation) dataValidation;
        DataValidityTable dvt = _sheet.getOrCreateDataValidityTable();

        DVRecord dvRecord = hssfDataValidation.createDVRecord(this);
        dvt.addDataValidation(dvRecord);
    }


    public void setColumnHidden(short columnIndex, boolean hidden) {
        setColumnHidden(columnIndex & 0xFFFF, hidden);
    }


    public boolean isColumnHidden(short columnIndex) {
        return isColumnHidden(columnIndex & 0xFFFF);
    }


    public void setColumnWidth(short columnIndex, short width) {
        setColumnWidth(columnIndex & 0xFFFF, width & 0xFFFF);
    }


    public short getColumnWidth(short columnIndex) {
        return (short) getColumnWidth(columnIndex & 0xFFFF);
    }


    public void setColumnHidden(int columnIndex, boolean hidden) {
        _sheet.setColumnHidden(columnIndex, hidden);
    }


    public boolean isColumnHidden(int columnIndex) {
        return _sheet.isColumnHidden(columnIndex);
    }


    public List<ColumnInfo> getColumnInfo() {
        return _sheet.getColumnInfo();
    }


    public void setColumnWidth(int columnIndex, int width) {
        _sheet.setColumnWidth(columnIndex, width);
    }


    public int getColumnWidth(int columnIndex) {
        return _sheet.getColumnWidth(columnIndex);
    }


    public int getColumnPixelWidth(int columnIndex) {
        return _sheet.getColumnPixelWidth(columnIndex);
    }


    public void setColumnPixelWidth(int columnIndex, int width) {
        _sheet.setColumnPixelWidth(columnIndex, width);
    }


    public int getDefaultColumnWidth() {
        return _sheet.getDefaultColumnWidth();
    }


    public void setDefaultColumnWidth(short width) {
        setDefaultColumnWidth(width & 0xFFFF);
    }


    public void setDefaultColumnWidth(int width) {
        _sheet.setDefaultColumnWidth(width);
    }


    public short getDefaultRowHeight() {
        return _sheet.getDefaultRowHeight();
    }



    public void setDefaultRowHeight(short height) {
        _sheet.setDefaultRowHeight(height);
    }



    public float getDefaultRowHeightInPoints() {
        return ((float) _sheet.getDefaultRowHeight() / 20);
    }



    public void setDefaultRowHeightInPoints(float height) {
        _sheet.setDefaultRowHeight((short) (height * 20));
    }


    public HSSFCellStyle getColumnStyle(int column) {
        short styleIndex = _sheet.getXFIndexForColAt((short) column);

        if (styleIndex == 0xf) {

            return null;
        }

        ExtendedFormatRecord xf = _book.getExFormatAt(styleIndex);
        return new HSSFCellStyle(styleIndex, xf, _book);
    }



    public boolean isGridsPrinted() {
        return _sheet.isGridsPrinted();
    }



    public void setGridsPrinted(boolean value) {
        _sheet.setGridsPrinted(value);
    }


    public int addMergedRegion(com.document.render.office.fc.ss.util.Region region) {
        return _sheet.addMergedRegion(region.getRowFrom(), region.getColumnFrom(),

                region.getRowTo(), region.getColumnTo());
    }


    public int addMergedRegion(HSSFCellRangeAddress region) {
        region.validate(SpreadsheetVersion.EXCEL97);



        validateArrayFormulas(region);

        return _sheet.addMergedRegion(region.getFirstRow(), region.getFirstColumn(),
                region.getLastRow(), region.getLastColumn());
    }

    private void validateArrayFormulas(HSSFCellRangeAddress region) {
        int firstRow = region.getFirstRow();
        int firstColumn = region.getFirstColumn();
        int lastRow = region.getLastRow();
        int lastColumn = region.getLastColumn();
        for (int rowIn = firstRow; rowIn <= lastRow; rowIn++) {
            for (int colIn = firstColumn; colIn <= lastColumn; colIn++) {
                HSSFRow row = getRow(rowIn);
                if (row == null)
                    continue;

                HSSFCell cell = row.getCell(colIn);
                if (cell == null)
                    continue;

                if (cell.isPartOfArrayFormulaGroup()) {
                    HSSFCellRangeAddress arrayRange = cell.getArrayFormulaRange();
                    if (arrayRange.getNumberOfCells() > 1
                            && (arrayRange.isInRange(region.getFirstRow(), region.getFirstColumn()) || arrayRange
                            .isInRange(region.getFirstRow(), region.getFirstColumn()))) {
                        String msg = "The range " + region.formatAsString()
                                + " intersects with a multi-cell array formula. "
                                + "You cannot merge cells of an array.";
                        throw new IllegalStateException(msg);
                    }
                }
            }
        }

    }


    public boolean getForceFormulaRecalculation() {
        return _sheet.getUncalced();
    }


    public void setForceFormulaRecalculation(boolean value) {
        _sheet.setUncalced(value);
    }


    public boolean getVerticallyCenter(boolean value) {
        return getVerticallyCenter();
    }


    public boolean getVerticallyCenter() {
        return _sheet.getPageSettings().getVCenter().getVCenter();
    }



    public void setVerticallyCenter(boolean value) {
        _sheet.getPageSettings().getVCenter().setVCenter(value);
    }



    public boolean getHorizontallyCenter() {

        return _sheet.getPageSettings().getHCenter().getHCenter();
    }



    public void setHorizontallyCenter(boolean value) {
        _sheet.getPageSettings().getHCenter().setHCenter(value);
    }


    public boolean isRightToLeft() {
        return _sheet.getWindowTwo().getArabic();
    }


    public void setRightToLeft(boolean value) {
        _sheet.getWindowTwo().setArabic(value);
    }



    public void removeMergedRegion(int index) {
        _sheet.removeMergedRegion(index);
    }



    public int getNumMergedRegions() {
        return _sheet.getNumMergedRegions();
    }


    public Region getMergedRegionAt(int index) {
        HSSFCellRangeAddress cra = getMergedRegion(index);

        return new Region(cra.getFirstRow(), (short) cra.getFirstColumn(), cra.getLastRow(),
                (short) cra.getLastColumn());
    }


    public HSSFCellRangeAddress getMergedRegion(int index) {
        return _sheet.getMergedRegionAt(index);
    }


    public Iterator<IRow> rowIterator() {
        @SuppressWarnings("unchecked")

        Iterator<IRow> result = (Iterator<IRow>) (Iterator<? extends IRow>) _rows.values().iterator();
        return result;
    }


    public Iterator<IRow> iterator() {
        return rowIterator();
    }


    InternalSheet getSheet() {
        return _sheet;
    }


    public void setAlternativeExpression(boolean b) {
        WSBoolRecord record = (WSBoolRecord) _sheet.findFirstRecordBySid(WSBoolRecord.sid);

        record.setAlternateExpression(b);
    }


    public void setAlternativeFormula(boolean b) {
        WSBoolRecord record = (WSBoolRecord) _sheet.findFirstRecordBySid(WSBoolRecord.sid);

        record.setAlternateFormula(b);
    }


    public boolean getAlternateExpression() {
        return ((WSBoolRecord) _sheet.findFirstRecordBySid(WSBoolRecord.sid))
                .getAlternateExpression();
    }


    public boolean getAlternateFormula() {
        return ((WSBoolRecord) _sheet.findFirstRecordBySid(WSBoolRecord.sid)).getAlternateFormula();
    }


    public boolean getAutobreaks() {
        return ((WSBoolRecord) _sheet.findFirstRecordBySid(WSBoolRecord.sid)).getAutobreaks();
    }


    public void setAutobreaks(boolean b) {
        WSBoolRecord record = (WSBoolRecord) _sheet.findFirstRecordBySid(WSBoolRecord.sid);

        record.setAutobreaks(b);
    }


    public boolean getDialog() {
        return ((WSBoolRecord) _sheet.findFirstRecordBySid(WSBoolRecord.sid)).getDialog();
    }


    public void setDialog(boolean b) {
        WSBoolRecord record = (WSBoolRecord) _sheet.findFirstRecordBySid(WSBoolRecord.sid);

        record.setDialog(b);
    }


    public boolean getDisplayGuts() {
        return ((WSBoolRecord) _sheet.findFirstRecordBySid(WSBoolRecord.sid)).getDisplayGuts();
    }


    public void setDisplayGuts(boolean b) {
        WSBoolRecord record = (WSBoolRecord) _sheet.findFirstRecordBySid(WSBoolRecord.sid);

        record.setDisplayGuts(b);
    }


    public boolean isDisplayZeros() {
        return _sheet.getWindowTwo().getDisplayZeros();
    }


    public void setDisplayZeros(boolean value) {
        _sheet.getWindowTwo().setDisplayZeros(value);
    }


    public boolean getFitToPage() {
        return ((WSBoolRecord) _sheet.findFirstRecordBySid(WSBoolRecord.sid)).getFitToPage();
    }


    public void setFitToPage(boolean b) {
        WSBoolRecord record = (WSBoolRecord) _sheet.findFirstRecordBySid(WSBoolRecord.sid);

        record.setFitToPage(b);
    }


    public boolean getRowSumsBelow() {
        return ((WSBoolRecord) _sheet.findFirstRecordBySid(WSBoolRecord.sid)).getRowSumsBelow();
    }


    public void setRowSumsBelow(boolean b) {
        WSBoolRecord record = (WSBoolRecord) _sheet.findFirstRecordBySid(WSBoolRecord.sid);

        record.setRowSumsBelow(b);

        record.setAlternateExpression(b);
    }


    public boolean getRowSumsRight() {
        return ((WSBoolRecord) _sheet.findFirstRecordBySid(WSBoolRecord.sid)).getRowSumsRight();
    }


    public void setRowSumsRight(boolean b) {
        WSBoolRecord record = (WSBoolRecord) _sheet.findFirstRecordBySid(WSBoolRecord.sid);

        record.setRowSumsRight(b);
    }


    public boolean isPrintGridlines() {
        return getSheet().getPrintGridlines().getPrintGridlines();
    }


    public void setPrintGridlines(boolean newPrintGridlines) {
        getSheet().getPrintGridlines().setPrintGridlines(newPrintGridlines);
    }


    public HSSFPrintSetup getPrintSetup() {
        return new HSSFPrintSetup(_sheet.getPageSettings().getPrintSetup());
    }

    public HSSFHeader getHeader() {
        return new HSSFHeader(_sheet.getPageSettings());
    }

    public HSSFFooter getFooter() {
        return new HSSFFooter(_sheet.getPageSettings());
    }


    public boolean isSelected() {
        return getSheet().getWindowTwo().getSelected();
    }


    public void setSelected(boolean sel) {
        getSheet().getWindowTwo().setSelected(sel);
    }


    public boolean isActive() {
        return getSheet().getWindowTwo().isActive();
    }


    public void setActive(boolean sel) {
        getSheet().getWindowTwo().setActive(sel);
    }


    public double getMargin(short margin) {
        return _sheet.getPageSettings().getMargin(margin);
    }


    public void setMargin(short margin, double size) {
        _sheet.getPageSettings().setMargin(margin, size);
    }

    private WorksheetProtectionBlock getProtectionBlock() {
        return _sheet.getProtectionBlock();
    }


    public boolean getProtect() {
        return getProtectionBlock().isSheetProtected();
    }


    public short getPassword() {
        return (short) getProtectionBlock().getPasswordHash();
    }


    public boolean getObjectProtect() {
        return getProtectionBlock().isObjectProtected();
    }


    public boolean getScenarioProtect() {
        return getProtectionBlock().isScenarioProtected();
    }


    public void protectSheet(String password) {
        getProtectionBlock().protectSheet(password, true, true);
    }


    public void setZoom(int numerator, int denominator) {
        if (numerator < 1 || numerator > 65535)
            throw new IllegalArgumentException(
                    "Numerator must be greater than 1 and less than 65536");
        if (denominator < 1 || denominator > 65535)
            throw new IllegalArgumentException(
                    "Denominator must be greater than 1 and less than 65536");

        SCLRecord sclRecord = new SCLRecord();
        sclRecord.setNumerator((short) numerator);
        sclRecord.setDenominator((short) denominator);
        getSheet().setSCLRecord(sclRecord);
    }


    public short getTopRow() {
        return _sheet.getTopRow();
    }


    public short getLeftCol() {
        return _sheet.getLeftCol();
    }


    public void showInPane(short toprow, short leftcol) {
        _sheet.setTopRow(toprow);
        _sheet.setLeftCol(leftcol);
    }


    protected void shiftMerged(int startRow, int endRow, int n, boolean isRow) {
        List<HSSFCellRangeAddress> shiftedRegions = new ArrayList<HSSFCellRangeAddress>();

        for (int i = 0; i < getNumMergedRegions(); i++) {
            HSSFCellRangeAddress merged = getMergedRegion(i);

            boolean inStart = (merged.getFirstRow() >= startRow || merged.getLastRow() >= startRow);
            boolean inEnd = (merged.getFirstRow() <= endRow || merged.getLastRow() <= endRow);


            if (!inStart || !inEnd) {
                continue;
            }


            if (!SheetUtil.containsCell(merged, startRow - 1, 0)
                    && !SheetUtil.containsCell(merged, endRow + 1, 0)) {
                merged.setFirstRow(merged.getFirstRow() + n);
                merged.setLastRow(merged.getLastRow() + n);

                shiftedRegions.add(merged);
                removeMergedRegion(i);
                i = i - 1;
            }
        }


        Iterator<HSSFCellRangeAddress> iterator = shiftedRegions.iterator();
        while (iterator.hasNext()) {
            HSSFCellRangeAddress region = iterator.next();

            this.addMergedRegion(region);
        }
    }


    public void shiftRows(int startRow, int endRow, int n) {
        shiftRows(startRow, endRow, n, false, false);
    }


    public void shiftRows(int startRow, int endRow, int n, boolean copyRowHeight,
                          boolean resetOriginalRowHeight) {
        shiftRows(startRow, endRow, n, copyRowHeight, resetOriginalRowHeight, true);
    }


    public void shiftRows(int startRow, int endRow, int n, boolean copyRowHeight,
                          boolean resetOriginalRowHeight, boolean moveComments) {
        int s, inc;
        if (n < 0) {
            s = startRow;
            inc = 1;
        } else if (n > 0) {
            s = endRow;
            inc = -1;
        } else {

            return;
        }

        NoteRecord[] noteRecs;
        if (moveComments) {
            noteRecs = _sheet.getNoteRecords();
        } else {
            noteRecs = NoteRecord.EMPTY_ARRAY;
        }

        shiftMerged(startRow, endRow, n, true);
        _sheet.getPageSettings().shiftRowBreaks(startRow, endRow, n);

        for (int rowNum = s; rowNum >= startRow && rowNum <= endRow && rowNum >= 0
                && rowNum < 65536; rowNum += inc) {
            HSSFRow row = getRow(rowNum);



            if (row != null)
                notifyRowShifting(row);

            HSSFRow row2Replace = getRow(rowNum + n);
            if (row2Replace == null)
                row2Replace = createRow(rowNum + n);






            row2Replace.removeAllCells();



            if (row == null)
                continue;


            if (copyRowHeight) {
                row2Replace.setHeight(row.getHeight());
            }
            if (resetOriginalRowHeight) {
                row.setHeight((short) 0xff);
            }



            for (Iterator<ICell> cells = row.cellIterator(); cells.hasNext(); ) {
                HSSFCell cell = (HSSFCell) cells.next();
                row.removeCell(cell);
                CellValueRecordInterface cellRecord = cell.getCellValueRecord();
                cellRecord.setRow(rowNum + n);
                row2Replace.createCellFromRecord(cellRecord);
                _sheet.addValueRecord(rowNum + n, cellRecord);

                HSSFHyperlink link = cell.getHyperlink();
                if (link != null) {
                    link.setFirstRow(link.getFirstRow() + n);
                    link.setLastRow(link.getLastRow() + n);
                }
            }

            row.removeAllCells();




            if (moveComments) {

                for (int i = noteRecs.length - 1; i >= 0; i--) {
                    NoteRecord nr = noteRecs[i];
                    if (nr.getRow() != rowNum) {
                        continue;
                    }
                    HSSFComment comment = getCellComment(rowNum, nr.getColumn());
                    if (comment != null) {
                        comment.setRow(rowNum + n);
                    }
                }
            }
        }


        if (n > 0) {

            if (startRow == _firstrow) {

                _firstrow = Math.max(startRow + n, 0);
                for (int i = startRow + 1; i < startRow + n; i++) {
                    if (getRow(i) != null) {
                        _firstrow = i;
                        break;
                    }
                }
            }
            if (endRow + n > _lastrow) {
                _lastrow = Math.min(endRow + n, SpreadsheetVersion.EXCEL97.getLastRowIndex());
            }
        } else {

            if (startRow + n < _firstrow) {
                _firstrow = Math.max(startRow + n, 0);
            }
            if (endRow == _lastrow) {

                _lastrow = Math.min(endRow + n, SpreadsheetVersion.EXCEL97.getLastRowIndex());
                for (int i = endRow - 1; i > endRow + n; i++) {
                    if (getRow(i) != null) {
                        _lastrow = i;
                        break;
                    }
                }
            }
        }



        int sheetIndex = _workbook.getSheetIndex(this);
        short externSheetIndex = _book.checkExternSheet(sheetIndex);
        FormulaShifter shifter = FormulaShifter.createForRowShift(externSheetIndex, startRow,
                endRow, n);
        _sheet.updateFormulasAfterCellShift(shifter, externSheetIndex);

        int nSheets = _workbook.getNumberOfSheets();
        for (int i = 0; i < nSheets; i++) {
            InternalSheet otherSheet = _workbook.getSheetAt(i).getSheet();
            if (otherSheet == this._sheet) {
                continue;
            }
            short otherExtSheetIx = _book.checkExternSheet(i);
            otherSheet.updateFormulasAfterCellShift(shifter, otherExtSheetIx);
        }
        _workbook.getWorkbook().updateNamesAfterCellShift(shifter);
    }

    protected void insertChartRecords(List<Record> records) {
        int window2Loc = _sheet.findFirstRecordLocBySid(WindowTwoRecord.sid);
        _sheet.getRecords().addAll(window2Loc, records);
    }

    private void notifyRowShifting(HSSFRow row) {
        String msg = "Row[rownum=" + row.getRowNum()
                + "] contains cell(s) included in a multi-cell array formula. "
                + "You cannot change part of an array.";
        for (ICell cell : row) {
            HSSFCell hcell = (HSSFCell) cell;
            if (hcell.isPartOfArrayFormulaGroup()) {
                hcell.notifyArrayFormulaChanging(msg);
            }
        }
    }


    public void createFreezePane(int colSplit, int rowSplit, int leftmostColumn, int topRow) {
        validateColumn(colSplit);
        validateRow(rowSplit);
        if (leftmostColumn < colSplit)
            throw new IllegalArgumentException(
                    "leftmostColumn parameter must not be less than colSplit parameter");
        if (topRow < rowSplit)
            throw new IllegalArgumentException(
                    "topRow parameter must not be less than leftmostColumn parameter");
        getSheet().createFreezePane(colSplit, rowSplit, topRow, leftmostColumn);
    }


    public void createFreezePane(int colSplit, int rowSplit) {
        createFreezePane(colSplit, rowSplit, colSplit, rowSplit);
    }


    public void createSplitPane(int xSplitPos, int ySplitPos, int leftmostColumn, int topRow,
                                int activePane) {
        getSheet().createSplitPane(xSplitPos, ySplitPos, topRow, leftmostColumn, activePane);
    }


    public HSSFPaneInformation getPaneInformation() {
        if (paneInformation == null) {
            paneInformation = getSheet().getPaneInformation();
        }
        return paneInformation;
    }


    public boolean isDisplayGridlines() {
        return _sheet.isDisplayGridlines();
    }


    public void setDisplayGridlines(boolean show) {
        _sheet.setDisplayGridlines(show);
    }


    public boolean isDisplayFormulas() {
        return _sheet.isDisplayFormulas();
    }


    public void setDisplayFormulas(boolean show) {
        _sheet.setDisplayFormulas(show);
    }


    public boolean isDisplayRowColHeadings() {
        return _sheet.isDisplayRowColHeadings();
    }


    public void setDisplayRowColHeadings(boolean show) {
        _sheet.setDisplayRowColHeadings(show);
    }


    public void setRowBreak(int row) {
        validateRow(row);
        _sheet.getPageSettings().setRowBreak(row, (short) 0, (short) 255);
    }


    public boolean isRowBroken(int row) {
        return _sheet.getPageSettings().isRowBroken(row);
    }


    public void removeRowBreak(int row) {
        _sheet.getPageSettings().removeRowBreak(row);
    }


    public int[] getRowBreaks() {

        return _sheet.getPageSettings().getRowBreaks();
    }


    public int[] getColumnBreaks() {

        return _sheet.getPageSettings().getColumnBreaks();
    }


    public void setColumnBreak(int column) {
        validateColumn((short) column);
        _sheet.getPageSettings().setColumnBreak((short) column, (short) 0,
                (short) SpreadsheetVersion.EXCEL97.getLastRowIndex());
    }


    public boolean isColumnBroken(int column) {
        return _sheet.getPageSettings().isColumnBroken(column);
    }


    public void removeColumnBreak(int column) {
        _sheet.getPageSettings().removeColumnBreak(column);
    }


    protected void validateRow(int row) {
        int maxrow = SpreadsheetVersion.EXCEL97.getLastRowIndex();
        if (row > maxrow)
            throw new IllegalArgumentException("Maximum row number is " + maxrow);
        if (row < 0)
            throw new IllegalArgumentException("Minumum row number is 0");
    }


    protected void validateColumn(int column) {
        int maxcol = SpreadsheetVersion.EXCEL97.getLastColumnIndex();
        if (column > maxcol)
            throw new IllegalArgumentException("Maximum column number is " + maxcol);
        if (column < 0)
            throw new IllegalArgumentException("Minimum column number is 0");
    }


    public void dumpDrawingRecords(boolean fat) {
        _sheet.aggregateDrawingRecords(_book.getDrawingManager(), false);

        EscherAggregate r = (EscherAggregate) getSheet().findFirstRecordBySid(EscherAggregate.sid);
        List<EscherRecord> escherRecords = r.getEscherRecords();
        PrintWriter w = new PrintWriter(System.out);
        for (Iterator<EscherRecord> iterator = escherRecords.iterator(); iterator.hasNext(); ) {
            EscherRecord escherRecord = iterator.next();
            if (fat) {
                System.out.println(escherRecord.toString());
            } else {
                escherRecord.display(w, 0);
            }
        }
        w.flush();
    }


    public HSSFPatriarch createDrawingPatriarch() {
        if (_patriarch == null) {

            _workbook.initDrawings();

            if (_patriarch == null) {
                _sheet.aggregateDrawingRecords(_book.getDrawingManager(), true);
                EscherAggregate agg = (EscherAggregate) _sheet
                        .findFirstRecordBySid(EscherAggregate.sid);

                agg.setPatriarch(_patriarch);
            }
        }
        return _patriarch;
    }


    public EscherAggregate getDrawingEscherAggregate() {
        _book.findDrawingGroup();



        if (_book.getDrawingManager() == null) {
            return null;
        }

        int found = _sheet.aggregateDrawingRecords(_book.getDrawingManager(), false);
        if (found == -1) {

            return null;
        }


        EscherAggregate agg = (EscherAggregate) _sheet.findFirstRecordBySid(EscherAggregate.sid);
        return agg;
    }


    public HSSFPatriarch getDrawingPatriarch() {
        if (_patriarch != null)
            return _patriarch;

        EscherAggregate agg = getDrawingEscherAggregate();
        if (agg == null)
            return null;


        agg.setPatriarch(_patriarch);




        agg.convertRecordsToUserModel(null);


        return _patriarch;
    }


    public void setColumnGroupCollapsed(short columnNumber, boolean collapsed) {
        setColumnGroupCollapsed(columnNumber & 0xFFFF, collapsed);
    }


    public void groupColumn(short fromColumn, short toColumn) {
        groupColumn(fromColumn & 0xFFFF, toColumn & 0xFFFF);
    }


    public void ungroupColumn(short fromColumn, short toColumn) {
        ungroupColumn(fromColumn & 0xFFFF, toColumn & 0xFFFF);
    }


    public void setColumnGroupCollapsed(int columnNumber, boolean collapsed) {
        _sheet.setColumnGroupCollapsed(columnNumber, collapsed);
    }


    public void groupColumn(int fromColumn, int toColumn) {
        _sheet.groupColumnRange(fromColumn, toColumn, true);
    }

    public void ungroupColumn(int fromColumn, int toColumn) {
        _sheet.groupColumnRange(fromColumn, toColumn, false);
    }


    public void groupRow(int fromRow, int toRow) {
        _sheet.groupRowRange(fromRow, toRow, true);
    }

    public void ungroupRow(int fromRow, int toRow) {
        _sheet.groupRowRange(fromRow, toRow, false);
    }

    public void setRowGroupCollapsed(int rowIndex, boolean collapse) {
        if (collapse) {
            _sheet.getRowsAggregate().collapseRow(rowIndex);
        } else {
            _sheet.getRowsAggregate().expandRow(rowIndex);
        }
    }


    public void setDefaultColumnStyle(int column, ICellStyle style) {
        _sheet.setDefaultColumnStyle(column, ((HSSFCellStyle) style).getIndex());
    }


    public void autoSizeColumn(int column) {
        autoSizeColumn(column, false);
    }


    public void autoSizeColumn(int column, boolean useMergedCells) {
        double width = SheetUtil.getColumnWidth(this, column, useMergedCells);

        if (width != -1) {
            width *= 256;
            int maxColumnWidth = 255 * 256;
            if (width > maxColumnWidth) {
                width = maxColumnWidth;
            }
            setColumnWidth(column, (int) (width));
        }

    }


    public HSSFComment getCellComment(int row, int column) {



        HSSFRow r = getRow(row);
        if (r != null) {
            HSSFCell c = r.getCell(column);
            if (c != null) {
                return c.getCellComment();
            }


            return HSSFCell.findCellComment(_sheet, row, column);
        }
        return null;
    }

    public HSSFSheetConditionalFormatting getSheetConditionalFormatting() {
        return new HSSFSheetConditionalFormatting(this);
    }


    public String getSheetName() {
        HSSFWorkbook wb = getWorkbook();
        int idx = wb.getSheetIndex(this);
        return wb.getSheetName(idx);
    }


    private CellRange<HSSFCell> getCellRange(HSSFCellRangeAddress range) {
        int firstRow = range.getFirstRow();
        int firstColumn = range.getFirstColumn();
        int lastRow = range.getLastRow();
        int lastColumn = range.getLastColumn();
        int height = lastRow - firstRow + 1;
        int width = lastColumn - firstColumn + 1;
        List<HSSFCell> temp = new ArrayList<HSSFCell>(height * width);
        for (int rowIn = firstRow; rowIn <= lastRow; rowIn++) {
            for (int colIn = firstColumn; colIn <= lastColumn; colIn++) {
                HSSFRow row = getRow(rowIn);
                if (row == null) {
                    row = createRow(rowIn);
                }
                HSSFCell cell = row.getCell(colIn);
                if (cell == null) {
                    cell = row.createCell(colIn);
                }
                temp.add(cell);
            }
        }
        return SSCellRange.create(firstRow, firstColumn, height, width, temp, HSSFCell.class);
    }

    public CellRange<HSSFCell> setArrayFormula(String formula, HSSFCellRangeAddress range) {














        return null;
    }

    public CellRange<HSSFCell> removeArrayFormula(ICell cell) {
        if (cell.getSheet() != this) {
            throw new IllegalArgumentException("Specified cell does not belong to this sheet.");
        }
        CellValueRecordInterface rec = ((HSSFCell) cell).getCellValueRecord();
        if (!(rec instanceof FormulaRecordAggregate)) {
            String ref = new CellReference(cell).formatAsString();
            throw new IllegalArgumentException("Cell " + ref + " is not part of an array formula.");
        }
        FormulaRecordAggregate fra = (FormulaRecordAggregate) rec;
        HSSFCellRangeAddress range = fra.removeArrayFormula(cell.getRowIndex(), cell.getColumnIndex());

        CellRange<HSSFCell> result = getCellRange(range);

        for (ICell c : result) {
            c.setCellType(ICell.CELL_TYPE_BLANK);
        }
        return result;
    }

    public DataValidationHelper getDataValidationHelper() {
        return new HSSFDataValidationHelper(this);
    }

    public HSSFAutoFilter setAutoFilter(HSSFCellRangeAddress range) {

        InternalWorkbook workbook = _workbook.getWorkbook();
        int sheetIndex = _workbook.getSheetIndex(this);

        NameRecord name = workbook.getSpecificBuiltinRecord(NameRecord.BUILTIN_FILTER_DB,
                sheetIndex + 1);

        if (name == null) {
            name = workbook.createBuiltInName(NameRecord.BUILTIN_FILTER_DB, sheetIndex + 1);
        }


        Area3DPtg ptg = new Area3DPtg(range.getFirstRow(), range.getLastRow(),
                range.getFirstColumn(), range.getLastColumn(), false, false, false, false, sheetIndex);
        name.setNameDefinition(new Ptg[]{ptg});

        AutoFilterInfoRecord r = new AutoFilterInfoRecord();

        int numcols = 1 + range.getLastColumn() - range.getFirstColumn();
        r.setNumEntries((short) numcols);
        int idx = _sheet.findFirstRecordLocBySid(DimensionsRecord.sid);
        _sheet.getRecords().add(idx, r);


        HSSFPatriarch p = createDrawingPatriarch();
        for (int col = range.getFirstColumn(); col <= range.getLastColumn(); col++) {
            p.createComboBox(new HSSFClientAnchor(0, 0, 0, 0, (short) col, range.getFirstRow(),
                    (short) (col + 1), range.getFirstRow() + 1));
        }

        return new HSSFAutoFilter(this);
    }


    public boolean isInitForDraw() {
        return isInitForDraw;
    }


    public void setInitForDraw(boolean isInitForDraw) {
        this.isInitForDraw = isInitForDraw;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public int getScrollX() {
        return scrollX;
    }

    public int getScrollY() {
        return scrollY;
    }

    public void setScroll(int scrollX, int scrollY) {
        this.scrollX = scrollX;
        this.scrollY = scrollY;
    }


    public int getActiveCellRow() {
        return this.row_activecell;
    }


    public void setActiveCell(int row, int column) {
        row_activecell = row;
        column_activecell = column;
    }


    public int getActiveCellColumn() {
        return this.column_activecell;
    }


    public HSSFCell getActiveCell() {
        if (getRow(row_activecell) != null) {
            return getRow(row_activecell).getCell(column_activecell);
        }
        return null;
    }
}
