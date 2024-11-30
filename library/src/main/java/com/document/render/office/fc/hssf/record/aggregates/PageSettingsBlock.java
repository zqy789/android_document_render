

package com.document.render.office.fc.hssf.record.aggregates;

import com.document.render.office.fc.hssf.model.InternalSheet;
import com.document.render.office.fc.hssf.model.RecordStream;
import com.document.render.office.fc.hssf.record.BottomMarginRecord;
import com.document.render.office.fc.hssf.record.ContinueRecord;
import com.document.render.office.fc.hssf.record.FooterRecord;
import com.document.render.office.fc.hssf.record.HCenterRecord;
import com.document.render.office.fc.hssf.record.HeaderFooterRecord;
import com.document.render.office.fc.hssf.record.HeaderRecord;
import com.document.render.office.fc.hssf.record.HorizontalPageBreakRecord;
import com.document.render.office.fc.hssf.record.LeftMarginRecord;
import com.document.render.office.fc.hssf.record.Margin;
import com.document.render.office.fc.hssf.record.PageBreakRecord;
import com.document.render.office.fc.hssf.record.PrintSetupRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.RecordBase;
import com.document.render.office.fc.hssf.record.RecordFormatException;
import com.document.render.office.fc.hssf.record.RightMarginRecord;
import com.document.render.office.fc.hssf.record.TopMarginRecord;
import com.document.render.office.fc.hssf.record.UnknownRecord;
import com.document.render.office.fc.hssf.record.UserSViewBegin;
import com.document.render.office.fc.hssf.record.VCenterRecord;
import com.document.render.office.fc.hssf.record.VerticalPageBreakRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;



public final class PageSettingsBlock extends RecordAggregate {

    private final List<PLSAggregate> _plsRecords;


    private PageBreakRecord _rowBreaksRecord;
    private PageBreakRecord _columnBreaksRecord;
    private HeaderRecord _header;
    private FooterRecord _footer;
    private HCenterRecord _hCenter;
    private VCenterRecord _vCenter;
    private LeftMarginRecord _leftMargin;
    private RightMarginRecord _rightMargin;
    private TopMarginRecord _topMargin;
    private BottomMarginRecord _bottomMargin;
    private PrintSetupRecord _printSetup;
    private Record _bitmap;
    private HeaderFooterRecord _headerFooter;

    private List<HeaderFooterRecord> _sviewHeaderFooters = new ArrayList<HeaderFooterRecord>();
    private Record _printSize;
    public PageSettingsBlock(RecordStream rs) {
        _plsRecords = new ArrayList<PLSAggregate>();
        while (true) {
            if (!readARecord(rs)) {
                break;
            }
        }
    }


    public PageSettingsBlock() {
        _plsRecords = new ArrayList<PLSAggregate>();
        _rowBreaksRecord = new HorizontalPageBreakRecord();
        _columnBreaksRecord = new VerticalPageBreakRecord();
        _header = new HeaderRecord("");
        _footer = new FooterRecord("");
        _hCenter = createHCenter();
        _vCenter = createVCenter();
        _printSetup = createPrintSetup();
    }


    public static boolean isComponentRecord(int sid) {
        switch (sid) {
            case HorizontalPageBreakRecord.sid:
            case VerticalPageBreakRecord.sid:
            case HeaderRecord.sid:
            case FooterRecord.sid:
            case HCenterRecord.sid:
            case VCenterRecord.sid:
            case LeftMarginRecord.sid:
            case RightMarginRecord.sid:
            case TopMarginRecord.sid:
            case BottomMarginRecord.sid:
            case UnknownRecord.PLS_004D:
            case PrintSetupRecord.sid:
            case UnknownRecord.BITMAP_00E9:
            case UnknownRecord.PRINTSIZE_0033:
            case HeaderFooterRecord.sid:
                return true;
        }
        return false;
    }

    private static void visitIfPresent(Record r, RecordVisitor rv) {
        if (r != null) {
            rv.visitRecord(r);
        }
    }

    private static void visitIfPresent(PageBreakRecord r, RecordVisitor rv) {
        if (r != null) {
            if (r.isEmpty()) {

                return;
            }
            rv.visitRecord(r);
        }
    }


    private static HCenterRecord createHCenter() {
        HCenterRecord retval = new HCenterRecord();

        retval.setHCenter(false);
        return retval;
    }


    private static VCenterRecord createVCenter() {
        VCenterRecord retval = new VCenterRecord();

        retval.setVCenter(false);
        return retval;
    }


    private static PrintSetupRecord createPrintSetup() {
        PrintSetupRecord retval = new PrintSetupRecord();

        retval.setPaperSize((short) 1);
        retval.setScale((short) 100);
        retval.setPageStart((short) 1);
        retval.setFitWidth((short) 1);
        retval.setFitHeight((short) 1);
        retval.setOptions((short) 2);
        retval.setHResolution((short) 300);
        retval.setVResolution((short) 300);
        retval.setHeaderMargin(0.5);
        retval.setFooterMargin(0.5);
        retval.setCopies((short) 1);
        return retval;
    }


    private static void shiftBreaks(PageBreakRecord breaks, int start, int stop, int count) {

        Iterator<PageBreakRecord.Break> iterator = breaks.getBreaksIterator();
        List<PageBreakRecord.Break> shiftedBreak = new ArrayList<PageBreakRecord.Break>();
        while (iterator.hasNext()) {
            PageBreakRecord.Break breakItem = iterator.next();
            int breakLocation = breakItem.main;
            boolean inStart = (breakLocation >= start);
            boolean inEnd = (breakLocation <= stop);
            if (inStart && inEnd)
                shiftedBreak.add(breakItem);
        }

        iterator = shiftedBreak.iterator();
        while (iterator.hasNext()) {
            PageBreakRecord.Break breakItem = iterator.next();
            breaks.removeBreak(breakItem.main);
            breaks.addBreak((short) (breakItem.main + count), breakItem.subFrom, breakItem.subTo);
        }
    }

    private boolean readARecord(RecordStream rs) {
        switch (rs.peekNextSid()) {
            case HorizontalPageBreakRecord.sid:
                checkNotPresent(_rowBreaksRecord);
                _rowBreaksRecord = (PageBreakRecord) rs.getNext();
                break;
            case VerticalPageBreakRecord.sid:
                checkNotPresent(_columnBreaksRecord);
                _columnBreaksRecord = (PageBreakRecord) rs.getNext();
                break;
            case HeaderRecord.sid:
                checkNotPresent(_header);
                _header = (HeaderRecord) rs.getNext();
                break;
            case FooterRecord.sid:
                checkNotPresent(_footer);
                _footer = (FooterRecord) rs.getNext();
                break;
            case HCenterRecord.sid:
                checkNotPresent(_hCenter);
                _hCenter = (HCenterRecord) rs.getNext();
                break;
            case VCenterRecord.sid:
                checkNotPresent(_vCenter);
                _vCenter = (VCenterRecord) rs.getNext();
                break;
            case LeftMarginRecord.sid:
                checkNotPresent(_leftMargin);
                _leftMargin = (LeftMarginRecord) rs.getNext();
                break;
            case RightMarginRecord.sid:
                checkNotPresent(_rightMargin);
                _rightMargin = (RightMarginRecord) rs.getNext();
                break;
            case TopMarginRecord.sid:
                checkNotPresent(_topMargin);
                _topMargin = (TopMarginRecord) rs.getNext();
                break;
            case BottomMarginRecord.sid:
                checkNotPresent(_bottomMargin);
                _bottomMargin = (BottomMarginRecord) rs.getNext();
                break;
            case UnknownRecord.PLS_004D:
                _plsRecords.add(new PLSAggregate(rs));
                break;
            case PrintSetupRecord.sid:
                checkNotPresent(_printSetup);
                _printSetup = (PrintSetupRecord) rs.getNext();
                break;
            case UnknownRecord.BITMAP_00E9:
                checkNotPresent(_bitmap);
                _bitmap = rs.getNext();
                break;
            case UnknownRecord.PRINTSIZE_0033:
                checkNotPresent(_printSize);
                _printSize = rs.getNext();
                break;
            case HeaderFooterRecord.sid:

                HeaderFooterRecord hf = (HeaderFooterRecord) rs.getNext();
                if (hf.isCurrentSheet()) _headerFooter = hf;
                else {
                    _sviewHeaderFooters.add(hf);
                }
                break;
            default:

                return false;
        }
        return true;
    }

    private void checkNotPresent(Record rec) {
        if (rec != null) {
            throw new RecordFormatException("Duplicate PageSettingsBlock record (sid=0x"
                    + Integer.toHexString(rec.getSid()) + ")");
        }
    }

    private PageBreakRecord getRowBreaksRecord() {
        if (_rowBreaksRecord == null) {
            _rowBreaksRecord = new HorizontalPageBreakRecord();
        }
        return _rowBreaksRecord;
    }

    private PageBreakRecord getColumnBreaksRecord() {
        if (_columnBreaksRecord == null) {
            _columnBreaksRecord = new VerticalPageBreakRecord();
        }
        return _columnBreaksRecord;
    }


    public void setColumnBreak(short column, short fromRow, short toRow) {
        getColumnBreaksRecord().addBreak(column, fromRow, toRow);
    }


    public void removeColumnBreak(int column) {
        getColumnBreaksRecord().removeBreak(column);
    }

    public void visitContainedRecords(RecordVisitor rv) {


        visitIfPresent(_rowBreaksRecord, rv);
        visitIfPresent(_columnBreaksRecord, rv);

        if (_header == null) {
            rv.visitRecord(new HeaderRecord(""));
        } else {
            rv.visitRecord(_header);
        }
        if (_footer == null) {
            rv.visitRecord(new FooterRecord(""));
        } else {
            rv.visitRecord(_footer);
        }
        visitIfPresent(_hCenter, rv);
        visitIfPresent(_vCenter, rv);
        visitIfPresent(_leftMargin, rv);
        visitIfPresent(_rightMargin, rv);
        visitIfPresent(_topMargin, rv);
        visitIfPresent(_bottomMargin, rv);
        for (RecordAggregate pls : _plsRecords) {
            pls.visitContainedRecords(rv);
        }
        visitIfPresent(_printSetup, rv);
        visitIfPresent(_bitmap, rv);
        visitIfPresent(_printSize, rv);
        visitIfPresent(_headerFooter, rv);
    }


    public HeaderRecord getHeader() {
        return _header;
    }


    public void setHeader(HeaderRecord newHeader) {
        _header = newHeader;
    }


    public FooterRecord getFooter() {
        return _footer;
    }


    public void setFooter(FooterRecord newFooter) {
        _footer = newFooter;
    }


    public PrintSetupRecord getPrintSetup() {
        return _printSetup;
    }


    public void setPrintSetup(PrintSetupRecord newPrintSetup) {
        _printSetup = newPrintSetup;
    }


    private Margin getMarginRec(int marginIndex) {
        switch (marginIndex) {
            case InternalSheet.LeftMargin:
                return _leftMargin;
            case InternalSheet.RightMargin:
                return _rightMargin;
            case InternalSheet.TopMargin:
                return _topMargin;
            case InternalSheet.BottomMargin:
                return _bottomMargin;
        }
        throw new IllegalArgumentException("Unknown margin constant:  " + marginIndex);
    }



    public double getMargin(short margin) {
        Margin m = getMarginRec(margin);
        if (m != null) {
            return m.getMargin();
        }
        switch (margin) {
            case InternalSheet.LeftMargin:
                return .75;
            case InternalSheet.RightMargin:
                return .75;
            case InternalSheet.TopMargin:
                return 1.0;
            case InternalSheet.BottomMargin:
                return 1.0;
        }
        throw new IllegalArgumentException("Unknown margin constant:  " + margin);
    }


    public void setMargin(short margin, double size) {
        Margin m = getMarginRec(margin);
        if (m == null) {
            switch (margin) {
                case InternalSheet.LeftMargin:
                    _leftMargin = new LeftMarginRecord();
                    m = _leftMargin;
                    break;
                case InternalSheet.RightMargin:
                    _rightMargin = new RightMarginRecord();
                    m = _rightMargin;
                    break;
                case InternalSheet.TopMargin:
                    _topMargin = new TopMarginRecord();
                    m = _topMargin;
                    break;
                case InternalSheet.BottomMargin:
                    _bottomMargin = new BottomMarginRecord();
                    m = _bottomMargin;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown margin constant:  " + margin);
            }
        }
        m.setMargin(size);
    }


    public void setRowBreak(int row, short fromCol, short toCol) {
        getRowBreaksRecord().addBreak((short) row, fromCol, toCol);
    }


    public void removeRowBreak(int row) {
        if (getRowBreaksRecord().getBreaks().length < 1)
            throw new IllegalArgumentException("Sheet does not define any row breaks");
        getRowBreaksRecord().removeBreak((short) row);
    }


    public boolean isRowBroken(int row) {
        return getRowBreaksRecord().getBreak(row) != null;
    }


    public boolean isColumnBroken(int column) {
        return getColumnBreaksRecord().getBreak(column) != null;
    }


    public void shiftRowBreaks(int startingRow, int endingRow, int count) {
        shiftBreaks(getRowBreaksRecord(), startingRow, endingRow, count);
    }


    public void shiftColumnBreaks(short startingCol, short endingCol, short count) {
        shiftBreaks(getColumnBreaksRecord(), startingCol, endingCol, count);
    }


    public int[] getRowBreaks() {
        return getRowBreaksRecord().getBreaks();
    }


    public int getNumRowBreaks() {
        return getRowBreaksRecord().getNumBreaks();
    }


    public int[] getColumnBreaks() {
        return getColumnBreaksRecord().getBreaks();
    }


    public int getNumColumnBreaks() {
        return getColumnBreaksRecord().getNumBreaks();
    }

    public VCenterRecord getVCenter() {
        return _vCenter;
    }

    public HCenterRecord getHCenter() {
        return _hCenter;
    }


    public void addLateHeaderFooter(HeaderFooterRecord rec) {
        if (_headerFooter != null) {
            throw new IllegalStateException("This page settings block already has a header/footer record");
        }
        if (rec.getSid() != HeaderFooterRecord.sid) {
            throw new RecordFormatException("Unexpected header-footer record sid: 0x" + Integer.toHexString(rec.getSid()));
        }
        _headerFooter = rec;
    }


    public void addLateRecords(RecordStream rs) {
        while (true) {
            if (!readARecord(rs)) {
                break;
            }
        }
    }


    public void positionRecords(List<RecordBase> sheetRecords) {


        List<HeaderFooterRecord> hfRecordsToIterate = new ArrayList<HeaderFooterRecord>(_sviewHeaderFooters);



        for (final HeaderFooterRecord hf : hfRecordsToIterate) {
            for (RecordBase rb : sheetRecords) {
                if (rb instanceof CustomViewSettingsRecordAggregate) {
                    final CustomViewSettingsRecordAggregate cv = (CustomViewSettingsRecordAggregate) rb;
                    cv.visitContainedRecords(new RecordVisitor() {
                        public void visitRecord(Record r) {
                            if (r.getSid() == UserSViewBegin.sid) {
                                byte[] guid1 = ((UserSViewBegin) r).getGuid();
                                byte[] guid2 = hf.getGuid();
                                if (Arrays.equals(guid1, guid2)) {
                                    cv.append(hf);
                                    _sviewHeaderFooters.remove(hf);
                                }
                            }
                        }
                    });
                }
            }
        }
    }


    private static final class PLSAggregate extends RecordAggregate {
        private static final ContinueRecord[] EMPTY_CONTINUE_RECORD_ARRAY = {};
        private final Record _pls;

        private ContinueRecord[] _plsContinues;

        public PLSAggregate(RecordStream rs) {
            _pls = rs.getNext();
            if (rs.peekNextSid() == ContinueRecord.sid) {
                List<ContinueRecord> temp = new ArrayList<ContinueRecord>();
                while (rs.peekNextSid() == ContinueRecord.sid) {
                    temp.add((ContinueRecord) rs.getNext());
                }
                _plsContinues = new ContinueRecord[temp.size()];
                temp.toArray(_plsContinues);
            } else {
                _plsContinues = EMPTY_CONTINUE_RECORD_ARRAY;
            }
        }

        @Override
        public void visitContainedRecords(RecordVisitor rv) {
            rv.visitRecord(_pls);
            for (int i = 0; i < _plsContinues.length; i++) {
                rv.visitRecord(_plsContinues[i]);
            }
        }
    }
}
