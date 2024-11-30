

package com.document.render.office.fc.hssf.model;

import com.document.render.office.fc.hssf.formula.FormulaShifter;
import com.document.render.office.fc.hssf.record.BOFRecord;
import com.document.render.office.fc.hssf.record.CFHeaderRecord;
import com.document.render.office.fc.hssf.record.CalcCountRecord;
import com.document.render.office.fc.hssf.record.CalcModeRecord;
import com.document.render.office.fc.hssf.record.CellValueRecordInterface;
import com.document.render.office.fc.hssf.record.ColumnInfoRecord;
import com.document.render.office.fc.hssf.record.ContinueRecord;
import com.document.render.office.fc.hssf.record.DVALRecord;
import com.document.render.office.fc.hssf.record.DefaultColWidthRecord;
import com.document.render.office.fc.hssf.record.DefaultRowHeightRecord;
import com.document.render.office.fc.hssf.record.DeltaRecord;
import com.document.render.office.fc.hssf.record.DimensionsRecord;
import com.document.render.office.fc.hssf.record.DrawingRecord;
import com.document.render.office.fc.hssf.record.EOFRecord;
import com.document.render.office.fc.hssf.record.EscherAggregate;
import com.document.render.office.fc.hssf.record.FeatHdrRecord;
import com.document.render.office.fc.hssf.record.FeatRecord;
import com.document.render.office.fc.hssf.record.GridsetRecord;
import com.document.render.office.fc.hssf.record.GutsRecord;
import com.document.render.office.fc.hssf.record.IndexRecord;
import com.document.render.office.fc.hssf.record.IterationRecord;
import com.document.render.office.fc.hssf.record.MergeCellsRecord;
import com.document.render.office.fc.hssf.record.NoteRecord;
import com.document.render.office.fc.hssf.record.ObjRecord;
import com.document.render.office.fc.hssf.record.PaneRecord;
import com.document.render.office.fc.hssf.record.PrintGridlinesRecord;
import com.document.render.office.fc.hssf.record.PrintHeadersRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.RecordBase;
import com.document.render.office.fc.hssf.record.RefModeRecord;
import com.document.render.office.fc.hssf.record.RowRecord;
import com.document.render.office.fc.hssf.record.SCLRecord;
import com.document.render.office.fc.hssf.record.SaveRecalcRecord;
import com.document.render.office.fc.hssf.record.SelectionRecord;
import com.document.render.office.fc.hssf.record.TextObjectRecord;
import com.document.render.office.fc.hssf.record.UncalcedRecord;
import com.document.render.office.fc.hssf.record.WSBoolRecord;
import com.document.render.office.fc.hssf.record.WindowTwoRecord;
import com.document.render.office.fc.hssf.record.aggregates.ChartSubstreamRecordAggregate;
import com.document.render.office.fc.hssf.record.aggregates.ColumnInfoRecordsAggregate;
import com.document.render.office.fc.hssf.record.aggregates.ConditionalFormattingTable;
import com.document.render.office.fc.hssf.record.aggregates.CustomViewSettingsRecordAggregate;
import com.document.render.office.fc.hssf.record.aggregates.DataValidityTable;
import com.document.render.office.fc.hssf.record.aggregates.MergedCellsTable;
import com.document.render.office.fc.hssf.record.aggregates.PageSettingsBlock;
import com.document.render.office.fc.hssf.record.aggregates.RecordAggregate;
import com.document.render.office.fc.hssf.record.aggregates.RecordAggregate.PositionTrackingVisitor;
import com.document.render.office.fc.hssf.record.aggregates.RecordAggregate.RecordVisitor;
import com.document.render.office.fc.hssf.record.aggregates.RowRecordsAggregate;
import com.document.render.office.fc.hssf.record.aggregates.WorksheetProtectionBlock;
import com.document.render.office.fc.hssf.record.chart.ChartRecord;
import com.document.render.office.fc.hssf.usermodel.HSSFChart;
import com.document.render.office.fc.hssf.util.ColumnInfo;
import com.document.render.office.fc.hssf.util.HSSFPaneInformation;
import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;
import com.document.render.office.fc.util.Internal;
import com.document.render.office.fc.util.POILogFactory;
import com.document.render.office.fc.util.POILogger;
import com.document.render.office.system.AbortReaderError;
import com.document.render.office.system.AbstractReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Internal
public final class InternalSheet {
    public static final short LeftMargin = 0;
    public static final short RightMargin = 1;
    public static final short TopMargin = 2;
    public static final short BottomMargin = 3;
    public static final byte PANE_LOWER_RIGHT = (byte) 0;
    public static final byte PANE_UPPER_RIGHT = (byte) 1;
    public static final byte PANE_LOWER_LEFT = (byte) 2;
    public static final byte PANE_UPPER_LEFT = (byte) 3;
    private static POILogger log = POILogFactory.getLogger(InternalSheet.class);

    protected final RowRecordsAggregate _rowsAggregate;

    private final WorksheetProtectionBlock _protectionBlock = new WorksheetProtectionBlock();

    private final MergedCellsTable _mergedCellsTable;
    protected PrintGridlinesRecord printGridlines = null;
    protected GridsetRecord gridset = null;
    protected DefaultColWidthRecord defaultcolwidth = new DefaultColWidthRecord();
    protected DefaultRowHeightRecord defaultrowheight = new DefaultRowHeightRecord();
    protected WindowTwoRecord windowTwo = null;
    protected SelectionRecord _selection = null;

    protected boolean _isUncalced = false;

     ColumnInfoRecordsAggregate _columnInfos;
    private List<RecordBase> _records;
    private GutsRecord _gutsRecord;
    private PageSettingsBlock _psBlock;

    private DimensionsRecord _dimensions;
    private DataValidityTable _dataValidityTable = null;
    private ConditionalFormattingTable condFormatting;
    private Iterator<RowRecord> rowRecIterator = null;

    private int sheetType = BOFRecord.TYPE_WORKSHEET;

    private InternalSheet(RecordStream rs, AbstractReader iAbortListener) {
        _mergedCellsTable = new MergedCellsTable();
        RowRecordsAggregate rra = null;

        List<RecordBase> records = new ArrayList<RecordBase>(128);
        _records = records;
        int dimsloc = -1;

        if (rs.peekNextSid() != BOFRecord.sid) {
            throw new RuntimeException("BOF record expected");
        }
        BOFRecord bof = (BOFRecord) rs.getNext();
        if (bof.getType() != BOFRecord.TYPE_WORKSHEET) {

            sheetType = bof.getType();
        }

        records.add(bof);
        while (rs.hasNext()) {
            if (iAbortListener != null && iAbortListener.isAborted()) {
                throw new AbortReaderError("abort Reader");
            }

            int recSid = rs.peekNextSid();

            if (recSid == CFHeaderRecord.sid) {
                condFormatting = new ConditionalFormattingTable(rs);
                records.add(condFormatting);
                continue;
            }

            if (recSid == ColumnInfoRecord.sid) {
                _columnInfos = new ColumnInfoRecordsAggregate(rs);
                records.add(_columnInfos);
                continue;
            }
            if (recSid == DVALRecord.sid) {
                _dataValidityTable = new DataValidityTable(rs);
                records.add(_dataValidityTable);
                continue;
            }

            if (RecordOrderer.isRowBlockRecord(recSid)) {

                if (rra != null) {
                    throw new RuntimeException("row/cell records found in the wrong place");
                }
                RowBlocksReader rbr = new RowBlocksReader(rs);
                _mergedCellsTable.addRecords(rbr.getLooseMergedCells());
                rra = new RowRecordsAggregate(rbr.getPlainRecordStream(),
                        rbr.getSharedFormulaManager());
                records.add(rra);
                continue;
            }

            if (CustomViewSettingsRecordAggregate.isBeginRecord(recSid)) {


                records.add(new CustomViewSettingsRecordAggregate(rs));
                continue;
            }

            if (PageSettingsBlock.isComponentRecord(recSid)) {
                if (_psBlock == null) {

                    _psBlock = new PageSettingsBlock(rs);
                    records.add(_psBlock);
                } else {

                    _psBlock.addLateRecords(rs);
                }


                _psBlock.positionRecords(records);
                continue;
            }

            if (WorksheetProtectionBlock.isComponentRecord(recSid)) {
                _protectionBlock.addRecords(rs);
                continue;
            }

            if (recSid == MergeCellsRecord.sid) {

                _mergedCellsTable.read(rs);
                continue;
            }

            if (recSid == BOFRecord.sid) {
                ChartSubstreamRecordAggregate chartAgg = new ChartSubstreamRecordAggregate(rs);
                if (false) {

                    records.add(chartAgg);
                } else {
                    spillAggregate(chartAgg, records);
                }
                continue;
            }

            Record rec = rs.getNext();
            if (recSid == IndexRecord.sid) {


                continue;
            }

            if (recSid == UncalcedRecord.sid) {

                _isUncalced = true;
                continue;
            }

            if (recSid == FeatRecord.sid || recSid == FeatHdrRecord.sid) {
                records.add(rec);
                continue;
            }

            if (recSid == EOFRecord.sid) {
                records.add(rec);
                break;
            }

            if (recSid == DimensionsRecord.sid) {

                if (_columnInfos == null) {
                    _columnInfos = new ColumnInfoRecordsAggregate();
                    records.add(_columnInfos);
                }

                _dimensions = (DimensionsRecord) rec;
                dimsloc = records.size();
            } else if (recSid == DefaultColWidthRecord.sid) {
                defaultcolwidth = (DefaultColWidthRecord) rec;
            } else if (recSid == DefaultRowHeightRecord.sid) {
                defaultrowheight = (DefaultRowHeightRecord) rec;
            } else if (recSid == PrintGridlinesRecord.sid) {
                printGridlines = (PrintGridlinesRecord) rec;
            } else if (recSid == GridsetRecord.sid) {
                gridset = (GridsetRecord) rec;
            } else if (recSid == SelectionRecord.sid) {
                _selection = (SelectionRecord) rec;
            } else if (recSid == WindowTwoRecord.sid) {
                windowTwo = (WindowTwoRecord) rec;
            } else if (recSid == GutsRecord.sid) {
                _gutsRecord = (GutsRecord) rec;
            }

            records.add(rec);
        }
        if (windowTwo == null) {
            throw new RuntimeException("WINDOW2 was not found");
        }
        if (_dimensions == null) {


            if (rra == null) {



                rra = new RowRecordsAggregate();
            } else {
                log.log(POILogger.WARN, "DIMENSION record not found even though row/cells present");

            }
            dimsloc = findFirstRecordLocBySid(WindowTwoRecord.sid);
            _dimensions = rra.createDimensions();
            records.add(dimsloc, _dimensions);
        }
        if (rra == null) {
            rra = new RowRecordsAggregate();
            records.add(dimsloc + 1, rra);
        }
        _rowsAggregate = rra;

        RecordOrderer.addNewSheetRecord(records, _mergedCellsTable);
        RecordOrderer.addNewSheetRecord(records, _protectionBlock);
        if (log.check(POILogger.DEBUG))
            log.log(POILogger.DEBUG, "sheet createSheet (existing file) exited");
    }

    private InternalSheet() {
        _mergedCellsTable = new MergedCellsTable();
        List<RecordBase> records = new ArrayList<RecordBase>(32);

        if (log.check(POILogger.DEBUG))
            log.log(POILogger.DEBUG, "Sheet createsheet from scratch called");

        records.add(createBOF());

        records.add(createCalcMode());
        records.add(createCalcCount());
        records.add(createRefMode());
        records.add(createIteration());
        records.add(createDelta());
        records.add(createSaveRecalc());
        records.add(createPrintHeaders());
        printGridlines = createPrintGridlines();
        records.add(printGridlines);
        gridset = createGridset();
        records.add(gridset);
        _gutsRecord = createGuts();
        records.add(_gutsRecord);
        defaultrowheight = createDefaultRowHeight();
        records.add(defaultrowheight);
        records.add(createWSBool());


        _psBlock = new PageSettingsBlock();
        records.add(_psBlock);


        records.add(_protectionBlock);

        defaultcolwidth = createDefaultColWidth();
        records.add(defaultcolwidth);
        ColumnInfoRecordsAggregate columns = new ColumnInfoRecordsAggregate();
        records.add(columns);
        _columnInfos = columns;
        _dimensions = createDimensions();
        records.add(_dimensions);
        _rowsAggregate = new RowRecordsAggregate();
        records.add(_rowsAggregate);

        records.add(windowTwo = createWindowTwo());
        _selection = createSelection();
        records.add(_selection);

        records.add(_mergedCellsTable);
        records.add(EOFRecord.instance);

        _records = records;
        if (log.check(POILogger.DEBUG))
            log.log(POILogger.DEBUG, "Sheet createsheet from scratch exit");
    }


    public static InternalSheet createSheet(RecordStream rs) {
        return new InternalSheet(rs, null);
    }

    public static InternalSheet createSheet(RecordStream rs, AbstractReader iAbortListener) {
        return new InternalSheet(rs, iAbortListener);
    }

    private static void spillAggregate(RecordAggregate ra, final List<RecordBase> recs) {
        ra.visitContainedRecords(new RecordVisitor() {
            public void visitRecord(Record r) {
                recs.add(r);
            }
        });
    }


    public static InternalSheet createSheet() {
        return new InternalSheet();
    }



    static BOFRecord createBOF() {
        BOFRecord retval = new BOFRecord();

        retval.setVersion((short) 0x600);
        retval.setType((short) 0x010);

        retval.setBuild((short) 0x0dbb);
        retval.setBuildYear((short) 1996);
        retval.setHistoryBitMask(0xc1);
        retval.setRequiredVersion(0x6);
        return retval;
    }


    private static CalcModeRecord createCalcMode() {
        CalcModeRecord retval = new CalcModeRecord();

        retval.setCalcMode((short) 1);
        return retval;
    }


    private static CalcCountRecord createCalcCount() {
        CalcCountRecord retval = new CalcCountRecord();

        retval.setIterations((short) 100);
        return retval;
    }


    private static RefModeRecord createRefMode() {
        RefModeRecord retval = new RefModeRecord();

        retval.setMode(RefModeRecord.USE_A1_MODE);
        return retval;
    }


    private static IterationRecord createIteration() {
        return new IterationRecord(false);
    }


    private static DeltaRecord createDelta() {
        return new DeltaRecord(DeltaRecord.DEFAULT_VALUE);
    }


    private static SaveRecalcRecord createSaveRecalc() {
        SaveRecalcRecord retval = new SaveRecalcRecord();

        retval.setRecalc(true);
        return retval;
    }


    private static PrintHeadersRecord createPrintHeaders() {
        PrintHeadersRecord retval = new PrintHeadersRecord();

        retval.setPrintHeaders(false);
        return retval;
    }


    private static PrintGridlinesRecord createPrintGridlines() {
        PrintGridlinesRecord retval = new PrintGridlinesRecord();

        retval.setPrintGridlines(false);
        return retval;
    }


    private static GridsetRecord createGridset() {
        GridsetRecord retval = new GridsetRecord();

        retval.setGridset(true);
        return retval;
    }


    private static GutsRecord createGuts() {
        GutsRecord retval = new GutsRecord();

        retval.setLeftRowGutter((short) 0);
        retval.setTopColGutter((short) 0);
        retval.setRowLevelMax((short) 0);
        retval.setColLevelMax((short) 0);
        return retval;
    }


    private static DefaultRowHeightRecord createDefaultRowHeight() {
        DefaultRowHeightRecord retval = new DefaultRowHeightRecord();

        retval.setOptionFlags((short) 0);
        retval.setRowHeight(DefaultRowHeightRecord.DEFAULT_ROW_HEIGHT);
        return retval;
    }


    private static WSBoolRecord createWSBool() {
        WSBoolRecord retval = new WSBoolRecord();

        retval.setWSBool1((byte) 0x4);
        retval.setWSBool2((byte) 0xffffffc1);
        return retval;
    }


    private static DefaultColWidthRecord createDefaultColWidth() {
        DefaultColWidthRecord retval = new DefaultColWidthRecord();
        retval.setColWidth(DefaultColWidthRecord.DEFAULT_COLUMN_WIDTH);
        return retval;
    }


    private static DimensionsRecord createDimensions() {
        DimensionsRecord retval = new DimensionsRecord();

        retval.setFirstCol((short) 0);
        retval.setLastRow(1);
        retval.setFirstRow(0);
        retval.setLastCol((short) 1);
        return retval;
    }


    private static WindowTwoRecord createWindowTwo() {
        WindowTwoRecord retval = new WindowTwoRecord();

        retval.setOptions((short) 0x6b6);
        retval.setTopRow((short) 0);
        retval.setLeftCol((short) 0);
        retval.setHeaderColor(0x40);
        retval.setPageBreakZoom((short) 0);
        retval.setNormalZoom((short) 0);
        return retval;
    }


    private static SelectionRecord createSelection() {
        return new SelectionRecord(0, 0);
    }

    public boolean isChartSheet() {
        return sheetType == BOFRecord.TYPE_CHART;
    }


    public InternalSheet cloneSheet() {
        List<Record> clonedRecords = new ArrayList<Record>(_records.size());
        for (int i = 0; i < _records.size(); i++) {
            RecordBase rb = _records.get(i);
            if (rb instanceof RecordAggregate) {
                ((RecordAggregate) rb).visitContainedRecords(new RecordCloner(clonedRecords));
                continue;
            }
            Record rec = (Record) ((Record) rb).clone();
            clonedRecords.add(rec);
        }
        return createSheet(new RecordStream(clonedRecords, 0));
    }

    public RowRecordsAggregate getRowsAggregate() {
        return _rowsAggregate;
    }

    private MergedCellsTable getMergedRecords() {

        return _mergedCellsTable;
    }


    public void updateFormulasAfterCellShift(FormulaShifter shifter, int externSheetIndex) {
        getRowsAggregate().updateFormulasAfterRowShift(shifter, externSheetIndex);
        if (condFormatting != null) {
            getConditionalFormattingTable().updateFormulasAfterCellShift(shifter, externSheetIndex);
        }

    }

    public int addMergedRegion(int rowFrom, int colFrom, int rowTo, int colTo) {

        if (rowTo < rowFrom) {
            throw new IllegalArgumentException("The 'to' row (" + rowTo
                    + ") must not be less than the 'from' row (" + rowFrom + ")");
        }
        if (colTo < colFrom) {
            throw new IllegalArgumentException("The 'to' col (" + colTo
                    + ") must not be less than the 'from' col (" + colFrom + ")");
        }

        MergedCellsTable mrt = getMergedRecords();
        mrt.addArea(rowFrom, colFrom, rowTo, colTo);
        return mrt.getNumberOfMergedRegions() - 1;
    }

    public void removeMergedRegion(int index) {

        MergedCellsTable mrt = getMergedRecords();
        if (index >= mrt.getNumberOfMergedRegions()) {
            return;
        }
        mrt.remove(index);
    }

    public HSSFCellRangeAddress getMergedRegionAt(int index) {

        MergedCellsTable mrt = getMergedRecords();
        if (index >= mrt.getNumberOfMergedRegions()) {
            return null;
        }
        return mrt.get(index);
    }

    public int getNumMergedRegions() {
        return getMergedRecords().getNumberOfMergedRegions();
    }

    public ConditionalFormattingTable getConditionalFormattingTable() {
        if (condFormatting == null) {
            condFormatting = new ConditionalFormattingTable();
            RecordOrderer.addNewSheetRecord(_records, condFormatting);
        }
        return condFormatting;
    }


    public void setDimensions(int firstrow, short firstcol, int lastrow, short lastcol) {
        if (log.check(POILogger.DEBUG)) {
            log.log(POILogger.DEBUG, "Sheet.setDimensions");
            log.log(POILogger.DEBUG,
                    (new StringBuffer("firstrow")).append(firstrow).append("firstcol").append(firstcol)
                            .append("lastrow").append(lastrow).append("lastcol").append(lastcol).toString());
        }
        _dimensions.setFirstCol(firstcol);
        _dimensions.setFirstRow(firstrow);
        _dimensions.setLastCol(lastcol);
        _dimensions.setLastRow(lastrow);
        if (log.check(POILogger.DEBUG))
            log.log(POILogger.DEBUG, "Sheet.setDimensions exiting");
    }

    public void visitContainedRecords(RecordVisitor rv, int offset) {

        PositionTrackingVisitor ptv = new PositionTrackingVisitor(rv, offset);

        boolean haveSerializedIndex = false;

        for (int k = 0; k < _records.size(); k++) {
            RecordBase record = _records.get(k);

            if (record instanceof RecordAggregate) {
                RecordAggregate agg = (RecordAggregate) record;
                agg.visitContainedRecords(ptv);
            } else {
                ptv.visitRecord((Record) record);
            }


            if (record instanceof BOFRecord) {
                if (!haveSerializedIndex) {
                    haveSerializedIndex = true;




                    if (_isUncalced) {
                        ptv.visitRecord(new UncalcedRecord());
                    }


                    if (_rowsAggregate != null) {

                        int initRecsSize = getSizeOfInitialSheetRecords(k);
                        int currentPos = ptv.getPosition();
                        ptv.visitRecord(_rowsAggregate.createIndexRecord(currentPos, initRecsSize));
                    }
                }
            }
        }
    }


    private int getSizeOfInitialSheetRecords(int bofRecordIndex) {

        int result = 0;

        for (int j = bofRecordIndex + 1; j < _records.size(); j++) {
            RecordBase tmpRec = _records.get(j);
            if (tmpRec instanceof RowRecordsAggregate) {
                break;
            }
            result += tmpRec.getRecordSize();
        }
        if (_isUncalced) {
            result += UncalcedRecord.getStaticRecordSize();
        }
        return result;
    }


    public void addValueRecord(int row, CellValueRecordInterface col) {

        if (log.check(POILogger.DEBUG)) {
            log.log(POILogger.DEBUG, "add value record  row" + row);
        }
        DimensionsRecord d = _dimensions;

        if (col.getColumn() > d.getLastCol()) {
            d.setLastCol((short) (col.getColumn() + 1));
        }
        if (col.getColumn() < d.getFirstCol()) {
            d.setFirstCol(col.getColumn());
        }
        _rowsAggregate.insertCell(col);
    }


    public void removeValueRecord(int row, CellValueRecordInterface col) {

        log.logFormatted(POILogger.DEBUG, "remove value record row %", new int[]{row});
        _rowsAggregate.removeCell(col);
    }



    public void replaceValueRecord(CellValueRecordInterface newval) {

        if (log.check(POILogger.DEBUG))
            log.log(POILogger.DEBUG, "replaceValueRecord ");





        _rowsAggregate.removeCell(newval);
        _rowsAggregate.insertCell(newval);
    }



    public void addRow(RowRecord row) {
        if (log.check(POILogger.DEBUG))
            log.log(POILogger.DEBUG, "addRow ");
        DimensionsRecord d = _dimensions;

        if (row.getRowNumber() >= d.getLastRow()) {
            d.setLastRow(row.getRowNumber() + 1);
        }
        if (row.getRowNumber() < d.getFirstRow()) {
            d.setFirstRow(row.getRowNumber());
        }


        RowRecord existingRow = _rowsAggregate.getRow(row.getRowNumber());
        if (existingRow != null) {
            _rowsAggregate.removeRow(existingRow);
        }

        _rowsAggregate.insertRow(row);

        if (log.check(POILogger.DEBUG))
            log.log(POILogger.DEBUG, "exit addRow");
    }


    public void removeRow(RowRecord row) {
        _rowsAggregate.removeRow(row);
    }


    public Iterator<CellValueRecordInterface> getCellValueIterator() {
        return _rowsAggregate.getCellValueIterator();
    }


    @Deprecated
    public CellValueRecordInterface[] getValueRecords() {
        return _rowsAggregate.getValueRecords();
    }


    public RowRecord getNextRow() {
        if (rowRecIterator == null) {
            rowRecIterator = _rowsAggregate.getIterator();
        }
        if (!rowRecIterator.hasNext()) {
            return null;
        }
        RowRecord rec = rowRecIterator.next();
        rowRecIterator.remove();
        return rec;
    }


    public RowRecord getRow(int rownum) {
        return _rowsAggregate.getRow(rownum);
    }

    private GutsRecord getGutsRecord() {
        if (_gutsRecord == null) {
            GutsRecord result = createGuts();
            RecordOrderer.addNewSheetRecord(_records, result);
            _gutsRecord = result;
        }

        return _gutsRecord;
    }


    public int getDefaultColumnWidth() {
        return defaultcolwidth.getColWidth();
    }


    public void setDefaultColumnWidth(int dcw) {
        defaultcolwidth.setColWidth(dcw);
    }


    public boolean isGridsPrinted() {
        if (gridset == null) {
            gridset = createGridset();

            int loc = findFirstRecordLocBySid(EOFRecord.sid);
            _records.add(loc, gridset);
        }
        return !gridset.getGridset();
    }


    public void setGridsPrinted(boolean value) {
        gridset.setGridset(!value);
    }


    public short getDefaultRowHeight() {
        return defaultrowheight.getRowHeight();
    }


    public void setDefaultRowHeight(short dch) {
        defaultrowheight.setRowHeight(dch);
    }


    public int getColumnWidth(int columnIndex) {
        ColumnInfoRecord ci = _columnInfos.findColumnInfo(columnIndex);
        if (ci != null) {
            return ci.getColumnWidth();
        }


        return (256 * defaultcolwidth.getColWidth());
    }


    public int getColumnPixelWidth(int columnIndex) {
        ColumnInfoRecord ci = _columnInfos.findColumnInfo(columnIndex);
        if (ci != null) {
            return ci.getColPixelWidth();
        }

        return 80;
    }


    public void setColumnPixelWidth(int columnIndex, int width) {
        ColumnInfoRecord ci = _columnInfos.findColumnInfo(columnIndex);
        if (ci != null) {
            ci.setColPixelWidth(width);
        }
    }


    public short getXFIndexForColAt(short columnIndex) {
        ColumnInfoRecord ci = _columnInfos.findColumnInfo(columnIndex);
        if (ci != null) {
            return (short) ci.getXFIndex();
        }
        return 0xF;
    }


    public void setColumnWidth(int column, int width) {
        if (width > 255 * 256)
            throw new IllegalArgumentException(
                    "The maximum column width for an individual cell is 255 characters.");

        setColumn(column, null, Integer.valueOf(width), null, null, null);
    }


    public boolean isColumnHidden(int columnIndex) {
        ColumnInfoRecord cir = _columnInfos.findColumnInfo(columnIndex);
        if (cir == null) {
            return false;
        }
        return cir.getHidden();
    }


    public List<ColumnInfo> getColumnInfo() {
        if (_columnInfos == null) {
            return null;
        }

        int size = _columnInfos.getNumColumns();
        List<ColumnInfo> colInfoList = new ArrayList<ColumnInfo>(5);
        ColumnInfoRecord columnInfo;
        for (int i = 0; i < size; i++) {
            columnInfo = _columnInfos.getColInfo(i);
            colInfoList.add(
                    new ColumnInfo(
                            columnInfo.getFirstColumn(),
                            columnInfo.getLastColumn(),
                            columnInfo.getColumnWidth(),
                            columnInfo.getXFIndex(),
                            columnInfo.getHidden()));
        }

        return colInfoList;
    }


    public void setColumnHidden(int column, boolean hidden) {
        setColumn(column, null, null, null, Boolean.valueOf(hidden), null);
    }

    public void setDefaultColumnStyle(int column, int styleIndex) {
        setColumn(column, Short.valueOf((short) styleIndex), null, null, null, null);
    }

    private void setColumn(int column, Short xfStyle, Integer width, Integer level, Boolean hidden,
                           Boolean collapsed) {
        _columnInfos.setColumn(column, xfStyle, width, level, hidden, collapsed);
    }


    public void groupColumnRange(int fromColumn, int toColumn, boolean indent) {


        _columnInfos.groupColumnRange(fromColumn, toColumn, indent);


        int maxLevel = _columnInfos.getMaxOutlineLevel();

        GutsRecord guts = getGutsRecord();
        guts.setColLevelMax((short) (maxLevel + 1));
        if (maxLevel == 0) {
            guts.setTopColGutter((short) 0);
        } else {
            guts.setTopColGutter((short) (29 + (12 * (maxLevel - 1))));
        }
    }

    public short getTopRow() {
        return (windowTwo == null) ? (short) 0 : windowTwo.getTopRow();
    }

    public void setTopRow(short topRow) {
        if (windowTwo != null) {
            windowTwo.setTopRow(topRow);
        }
    }

    public short getLeftCol() {
        return (windowTwo == null) ? (short) 0 : windowTwo.getLeftCol();
    }


    public void setLeftCol(short leftCol) {
        if (windowTwo != null) {
            windowTwo.setLeftCol(leftCol);
        }
    }


    public int getActiveCellRow() {
        if (_selection == null) {
            return 0;
        }
        return _selection.getActiveCellRow();
    }


    public void setActiveCellRow(int row) {

        if (_selection != null) {
            _selection.setActiveCellRow(row);
        }
    }


    public short getActiveCellCol() {
        if (_selection == null) {
            return 0;
        }
        return (short) _selection.getActiveCellCol();
    }


    public void setActiveCellCol(short col) {

        if (_selection != null) {
            _selection.setActiveCellCol(col);
        }
    }

    public List<RecordBase> getRecords() {
        return _records;
    }


    public GridsetRecord getGridsetRecord() {
        return gridset;
    }


    public Record findFirstRecordBySid(short sid) {
        int ix = findFirstRecordLocBySid(sid);
        if (ix < 0) {
            return null;
        }
        return (Record) _records.get(ix);
    }


    public void setSCLRecord(SCLRecord sclRecord) {
        int oldRecordLoc = findFirstRecordLocBySid(SCLRecord.sid);
        if (oldRecordLoc == -1) {

            int windowRecordLoc = findFirstRecordLocBySid(WindowTwoRecord.sid);
            _records.add(windowRecordLoc + 1, sclRecord);
        } else {
            _records.set(oldRecordLoc, sclRecord);
        }
    }


    public int findFirstRecordLocBySid(short sid) {
        int max = _records.size();
        for (int i = 0; i < max; i++) {
            Object rb = _records.get(i);
            if (!(rb instanceof Record)) {
                continue;
            }
            Record record = (Record) rb;
            if (record.getSid() == sid) {
                return i;
            }
        }
        return -1;
    }

    public WindowTwoRecord getWindowTwo() {
        return windowTwo;
    }


    public PrintGridlinesRecord getPrintGridlines() {
        return printGridlines;
    }


    public void setPrintGridlines(PrintGridlinesRecord newPrintGridlines) {
        printGridlines = newPrintGridlines;
    }


    public void setSelected(boolean sel) {
        windowTwo.setSelected(sel);
    }


    public void createFreezePane(int colSplit, int rowSplit, int topRow, int leftmostColumn) {
        int paneLoc = findFirstRecordLocBySid(PaneRecord.sid);
        if (paneLoc != -1)
            _records.remove(paneLoc);


        if (colSplit == 0 && rowSplit == 0) {
            windowTwo.setFreezePanes(false);
            windowTwo.setFreezePanesNoSplit(false);
            SelectionRecord sel = (SelectionRecord) findFirstRecordBySid(SelectionRecord.sid);
            sel.setPane(HSSFPaneInformation.PANE_UPPER_LEFT);
            return;
        }

        int loc = findFirstRecordLocBySid(WindowTwoRecord.sid);
        PaneRecord pane = new PaneRecord();
        pane.setX((short) colSplit);
        pane.setY((short) rowSplit);
        pane.setTopRow((short) topRow);
        pane.setLeftColumn((short) leftmostColumn);
        if (rowSplit == 0) {
            pane.setTopRow((short) 0);
            pane.setActivePane((short) 1);
        } else if (colSplit == 0) {
            pane.setLeftColumn((short) 0);
            pane.setActivePane((short) 2);
        } else {
            pane.setActivePane((short) 0);
        }
        _records.add(loc + 1, pane);

        windowTwo.setFreezePanes(true);
        windowTwo.setFreezePanesNoSplit(true);

        SelectionRecord sel = (SelectionRecord) findFirstRecordBySid(SelectionRecord.sid);
        sel.setPane((byte) pane.getActivePane());

    }


    public void createSplitPane(int xSplitPos, int ySplitPos, int topRow, int leftmostColumn,
                                int activePane) {
        int paneLoc = findFirstRecordLocBySid(PaneRecord.sid);
        if (paneLoc != -1)
            _records.remove(paneLoc);

        int loc = findFirstRecordLocBySid(WindowTwoRecord.sid);
        PaneRecord r = new PaneRecord();
        r.setX((short) xSplitPos);
        r.setY((short) ySplitPos);
        r.setTopRow((short) topRow);
        r.setLeftColumn((short) leftmostColumn);
        r.setActivePane((short) activePane);
        _records.add(loc + 1, r);

        windowTwo.setFreezePanes(false);
        windowTwo.setFreezePanesNoSplit(false);

        SelectionRecord sel = (SelectionRecord) findFirstRecordBySid(SelectionRecord.sid);
        sel.setPane(PANE_LOWER_RIGHT);

    }


    public HSSFPaneInformation getPaneInformation() {
        PaneRecord rec = (PaneRecord) findFirstRecordBySid(PaneRecord.sid);
        if (rec == null)
            return null;

        return new HSSFPaneInformation(rec.getX(), rec.getY(), rec.getTopRow(), rec.getLeftColumn(),
                (byte) rec.getActivePane(), windowTwo.getFreezePanes());
    }

    public SelectionRecord getSelection() {
        return _selection;
    }

    public void setSelection(SelectionRecord selection) {
        _selection = selection;
    }


    public WorksheetProtectionBlock getProtectionBlock() {
        return _protectionBlock;
    }


    public boolean isDisplayGridlines() {
        return windowTwo.getDisplayGridlines();
    }


    public void setDisplayGridlines(boolean show) {
        windowTwo.setDisplayGridlines(show);
    }


    public boolean isDisplayFormulas() {
        return windowTwo.getDisplayFormulas();
    }


    public void setDisplayFormulas(boolean show) {
        windowTwo.setDisplayFormulas(show);
    }


    public boolean isDisplayRowColHeadings() {
        return windowTwo.getDisplayRowColHeadings();
    }


    public void setDisplayRowColHeadings(boolean show) {
        windowTwo.setDisplayRowColHeadings(show);
    }


    public boolean getUncalced() {
        return _isUncalced;
    }


    public void setUncalced(boolean uncalced) {
        this._isUncalced = uncalced;
    }


    public HSSFChart getChart() {
        if (sheetType == BOFRecord.TYPE_CHART) {
            int loc = findFirstRecordLocBySid(ChartRecord.sid);
            if (loc >= 0) {
                List<Record> chartRecordsList = new ArrayList<Record>();
                RecordBase record = _records.get(loc);
                while (!(record instanceof WorksheetProtectionBlock)) {
                    chartRecordsList.add((Record) record);

                    loc++;
                    record = _records.get(loc);
                }

                HSSFChart chart = new HSSFChart(null, null, null, null);
                HSSFChart.convertRecordsToChart(chartRecordsList, chart);
                return chart;
            }
        }

        return null;
    }


    public int aggregateDrawingRecords(DrawingManager2 drawingManager, boolean createIfMissing) {
        int loc = findFirstRecordLocBySid(DrawingRecord.sid);
        boolean noDrawingRecordsFound = (loc == -1);
        if (noDrawingRecordsFound) {
            if (!createIfMissing) {

                return -1;
            }

            EscherAggregate aggregate = new EscherAggregate(drawingManager);
            loc = findFirstRecordLocBySid(EscherAggregate.sid);
            if (loc == -1) {
                loc = findFirstRecordLocBySid(WindowTwoRecord.sid);
            } else {
                getRecords().remove(loc);
            }
            getRecords().add(loc, aggregate);
            return loc;
        }
        List<RecordBase> records = getRecords();
        EscherAggregate r = EscherAggregate.createAggregate(records, loc, drawingManager);

        int startloc = loc;
        while (loc + 1 < records.size()
                && (records.get(loc) instanceof DrawingRecord || records.get(loc) instanceof ContinueRecord)
                && (records.get(loc + 1) instanceof ObjRecord || records.get(loc + 1) instanceof TextObjectRecord)) {



            loc += EscherAggregate.shapeContainRecords(records, loc);
        }

        int endloc = loc - 1;
        for (int i = 0; i < (endloc - startloc + 1); i++)
            records.remove(startloc);
        records.add(startloc, r);

        return startloc;
    }


    public void preSerialize() {
        for (RecordBase r : getRecords()) {
            if (r instanceof EscherAggregate) {

                r.getRecordSize();
            }
        }
    }

    public PageSettingsBlock getPageSettings() {
        if (_psBlock == null) {
            _psBlock = new PageSettingsBlock();
            RecordOrderer.addNewSheetRecord(_records, _psBlock);
        }
        return _psBlock;
    }

    public void setColumnGroupCollapsed(int columnNumber, boolean collapsed) {
        if (collapsed) {
            _columnInfos.collapseColumn(columnNumber);
        } else {
            _columnInfos.expandColumn(columnNumber);
        }
    }

    public void groupRowRange(int fromRow, int toRow, boolean indent) {
        for (int rowNum = fromRow; rowNum <= toRow; rowNum++) {
            RowRecord row = getRow(rowNum);
            if (row == null) {
                row = RowRecordsAggregate.createRow(rowNum);
                addRow(row);
            }
            int level = row.getOutlineLevel();
            if (indent)
                level++;
            else
                level--;
            level = Math.max(0, level);
            level = Math.min(7, level);
            row.setOutlineLevel((short) (level));
        }

        recalcRowGutter();
    }

    private void recalcRowGutter() {
        int maxLevel = 0;
        Iterator iterator = _rowsAggregate.getIterator();
        while (iterator.hasNext()) {
            RowRecord rowRecord = (RowRecord) iterator.next();
            maxLevel = Math.max(rowRecord.getOutlineLevel(), maxLevel);
        }


        GutsRecord guts = getGutsRecord();

        guts.setRowLevelMax((short) (maxLevel + 1));
        guts.setLeftRowGutter((short) (29 + (12 * (maxLevel))));
    }

    public DataValidityTable getOrCreateDataValidityTable() {
        if (_dataValidityTable == null) {
            DataValidityTable result = new DataValidityTable();
            RecordOrderer.addNewSheetRecord(_records, result);
            _dataValidityTable = result;
        }
        return _dataValidityTable;
    }


    public NoteRecord[] getNoteRecords() {
        List<NoteRecord> temp = new ArrayList<NoteRecord>();
        for (int i = _records.size() - 1; i >= 0; i--) {
            RecordBase rec = _records.get(i);
            if (rec instanceof NoteRecord) {
                temp.add((NoteRecord) rec);
            }
        }
        if (temp.size() < 1) {
            return NoteRecord.EMPTY_ARRAY;
        }
        NoteRecord[] result = new NoteRecord[temp.size()];
        temp.toArray(result);
        return result;
    }

    public void dispose() {
        _records.clear();

        printGridlines = null;
        gridset = null;
        _gutsRecord = null;
        defaultcolwidth = null;
        defaultrowheight = null;
        _psBlock = null;


        windowTwo = null;
        _selection = null;

        _dimensions = null;

        _dataValidityTable = null;
        condFormatting = null;

        rowRecIterator = null;

        _rowsAggregate.dispose();
    }

    private static final class RecordCloner implements RecordVisitor {

        private final List<Record> _destList;

        public RecordCloner(List<Record> destList) {
            _destList = destList;
        }

        public void visitRecord(Record r) {
            _destList.add((Record) r.clone());
        }
    }
}
