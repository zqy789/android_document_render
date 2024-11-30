

package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.ptg.ExpPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.model.InternalSheet;
import com.document.render.office.fc.hssf.model.InternalWorkbook;
import com.document.render.office.fc.hssf.record.BlankRecord;
import com.document.render.office.fc.hssf.record.BoolErrRecord;
import com.document.render.office.fc.hssf.record.CellValueRecordInterface;
import com.document.render.office.fc.hssf.record.CommonObjectDataSubRecord;
import com.document.render.office.fc.hssf.record.DrawingRecord;
import com.document.render.office.fc.hssf.record.ExtendedFormatRecord;
import com.document.render.office.fc.hssf.record.FormulaRecord;
import com.document.render.office.fc.hssf.record.HyperlinkRecord;
import com.document.render.office.fc.hssf.record.LabelSSTRecord;
import com.document.render.office.fc.hssf.record.NoteRecord;
import com.document.render.office.fc.hssf.record.NumberRecord;
import com.document.render.office.fc.hssf.record.ObjRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.RecordBase;
import com.document.render.office.fc.hssf.record.SubRecord;
import com.document.render.office.fc.hssf.record.TextObjectRecord;
import com.document.render.office.fc.hssf.record.aggregates.FormulaRecordAggregate;
import com.document.render.office.fc.hssf.record.common.UnicodeString;
import com.document.render.office.fc.ss.SpreadsheetVersion;
import com.document.render.office.fc.ss.usermodel.Comment;
import com.document.render.office.fc.ss.usermodel.FormulaError;
import com.document.render.office.fc.ss.usermodel.ICell;
import com.document.render.office.fc.ss.usermodel.ICellStyle;
import com.document.render.office.fc.ss.usermodel.IHyperlink;
import com.document.render.office.fc.ss.usermodel.RichTextString;
import com.document.render.office.fc.ss.util.CellReference;
import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;
import com.document.render.office.fc.ss.util.NumberToTextConverter;
import com.document.render.office.fc.util.POILogFactory;
import com.document.render.office.fc.util.POILogger;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class HSSFCell implements ICell {

    public static final int LAST_COLUMN_NUMBER = SpreadsheetVersion.EXCEL97.getLastColumnIndex();
    public final static short ENCODING_UNCHANGED = -1;
    public final static short ENCODING_COMPRESSED_UNICODE = 0;
    public final static short ENCODING_UTF_16 = 1;
    private static final String FILE_FORMAT_NAME = "BIFF8";
    private static final String LAST_COLUMN_NAME = SpreadsheetVersion.EXCEL97.getLastColumnName();
    private static POILogger log = POILogFactory.getLogger(HSSFCell.class);
    private final HSSFWorkbook _book;
    private final HSSFSheet _sheet;
    private int _cellType;
    private HSSFRichTextString _stringValue;
    private CellValueRecordInterface _record;
    private HSSFComment _comment;


    private int rangeAddressIndex = -1;



    public HSSFCell(HSSFWorkbook book, HSSFSheet sheet, int row, short col) {
        checkBounds(col);
        _stringValue = null;
        _book = book;
        _sheet = sheet;




        short xfindex = sheet.getSheet().getXFIndexForColAt(col);
        setCellType(CELL_TYPE_BLANK, false, row, col, xfindex);
    }


    protected HSSFCell(HSSFWorkbook book, HSSFSheet sheet, int row, short col,
                       int type) {
        checkBounds(col);
        _cellType = -1;
        _stringValue = null;
        _book = book;
        _sheet = sheet;

        short xfindex = sheet.getSheet().getXFIndexForColAt(col);
        setCellType(type, false, row, col, xfindex);
    }


    protected HSSFCell(HSSFWorkbook book, HSSFSheet sheet, CellValueRecordInterface cval) {
        _record = cval;
        _cellType = determineType(cval);
        _stringValue = null;
        _book = book;
        _sheet = sheet;
        switch (_cellType) {
            case CELL_TYPE_STRING:
                _stringValue = new HSSFRichTextString(book.getWorkbook(), (LabelSSTRecord) cval);
                break;

            case CELL_TYPE_BLANK:
                break;

            case CELL_TYPE_FORMULA:
                _stringValue = new HSSFRichTextString(((FormulaRecordAggregate) cval).getStringValue());
                break;
        }
    }


    private static int determineType(CellValueRecordInterface cval) {
        if (cval instanceof FormulaRecordAggregate) {
            return HSSFCell.CELL_TYPE_FORMULA;
        }

        Record record = (Record) cval;
        switch (record.getSid()) {

            case NumberRecord.sid:
                return HSSFCell.CELL_TYPE_NUMERIC;
            case BlankRecord.sid:
                return HSSFCell.CELL_TYPE_BLANK;
            case LabelSSTRecord.sid:
                return HSSFCell.CELL_TYPE_STRING;
            case BoolErrRecord.sid:
                BoolErrRecord boolErrRecord = (BoolErrRecord) record;

                return boolErrRecord.isBoolean()
                        ? HSSFCell.CELL_TYPE_BOOLEAN
                        : HSSFCell.CELL_TYPE_ERROR;
        }
        throw new RuntimeException("Bad cell value rec (" + cval.getClass().getName() + ")");
    }


    private static String getCellTypeName(int cellTypeCode) {
        switch (cellTypeCode) {
            case CELL_TYPE_BLANK:
                return "blank";
            case CELL_TYPE_STRING:
                return "text";
            case CELL_TYPE_BOOLEAN:
                return "boolean";
            case CELL_TYPE_ERROR:
                return "error";
            case CELL_TYPE_NUMERIC:
                return "numeric";
            case CELL_TYPE_FORMULA:
                return "formula";
        }
        return "#unknown cell type (" + cellTypeCode + ")#";
    }

    private static RuntimeException typeMismatch(int expectedTypeCode, int actualTypeCode, boolean isFormulaCell) {
        String msg = "Cannot get a "
                + getCellTypeName(expectedTypeCode) + " value from a "
                + getCellTypeName(actualTypeCode) + " " + (isFormulaCell ? "formula " : "") + "cell";
        return new IllegalStateException(msg);
    }

    private static void checkFormulaCachedValueType(int expectedTypeCode, FormulaRecord fr) {
        int cachedValueType = fr.getCachedResultType();
        if (cachedValueType != expectedTypeCode) {
            throw typeMismatch(expectedTypeCode, cachedValueType, true);
        }
    }


    private static void checkBounds(int cellIndex) {
        if (cellIndex < 0 || cellIndex > LAST_COLUMN_NUMBER) {
            throw new IllegalArgumentException("Invalid column index (" + cellIndex
                    + ").  Allowable column range for " + FILE_FORMAT_NAME + " is (0.."
                    + LAST_COLUMN_NUMBER + ") or ('A'..'" + LAST_COLUMN_NAME + "')");
        }
    }


    protected static HSSFComment findCellComment(InternalSheet sheet, int row, int column) {

        HSSFComment comment = null;
        Map<Integer, TextObjectRecord> noteTxo =
                new HashMap<Integer, TextObjectRecord>();
        int i = 0;
        for (Iterator<RecordBase> it = sheet.getRecords().iterator(); it.hasNext(); ) {
            RecordBase rec = it.next();
            if (rec instanceof NoteRecord) {
                NoteRecord note = (NoteRecord) rec;
                if (note.getRow() == row && note.getColumn() == column) {
                    if (i < noteTxo.size()) {
                        TextObjectRecord txo = noteTxo.get(note.getShapeId());
                        if (txo != null) {
                            comment = new HSSFComment(note, txo);
                            comment.setRow(note.getRow());
                            comment.setColumn(note.getColumn());
                            comment.setAuthor(note.getAuthor());
                            comment.setVisible(note.getFlags() == NoteRecord.NOTE_VISIBLE);
                            comment.setString(txo.getStr());
                        } else {
                            log.log(POILogger.WARN, "Failed to match NoteRecord and TextObjectRecord, row: " + row + ", column: " + column);
                        }
                    } else {
                        log.log(POILogger.WARN, "Failed to match NoteRecord and TextObjectRecord, row: " + row + ", column: " + column);
                    }
                    break;
                }
                i++;
            } else if (rec instanceof ObjRecord) {
                ObjRecord obj = (ObjRecord) rec;
                SubRecord sub = obj.getSubRecords().get(0);
                if (sub instanceof CommonObjectDataSubRecord) {
                    CommonObjectDataSubRecord cmo = (CommonObjectDataSubRecord) sub;
                    if (cmo.getObjectType() == CommonObjectDataSubRecord.OBJECT_TYPE_COMMENT) {


                        while (it.hasNext()) {
                            rec = it.next();
                            if (rec instanceof TextObjectRecord) {
                                noteTxo.put(cmo.getObjectId(), (TextObjectRecord) rec);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return comment;
    }


    public HSSFSheet getSheet() {
        return _sheet;
    }


    public HSSFRow getRow() {
        int rowIndex = getRowIndex();
        return _sheet.getRow(rowIndex);
    }


    protected InternalWorkbook getBoundWorkbook() {
        return _book.getWorkbook();
    }


    public int getRowIndex() {
        return _record.getRow();
    }


    protected void updateCellNum(short num) {
        _record.setColumn(num);
    }


    public short getCellNum() {
        return (short) getColumnIndex();
    }


    public void setCellNum(short num) {
        _record.setColumn(num);
    }

    public int getColumnIndex() {
        return _record.getColumn() & 0xFFFF;
    }


    public void setCellType(int cellType, boolean setValue) {
        notifyFormulaChanging();
        if (isPartOfArrayFormulaGroup()) {
            notifyArrayFormulaChanging();
        }
        int row = _record.getRow();
        short col = _record.getColumn();
        short styleIndex = _record.getXFIndex();
        setCellType(cellType, setValue, row, col, styleIndex);
    }



    private void setCellType(int cellType, boolean setValue, int row, short col, short styleIndex) {

        if (cellType > CELL_TYPE_ERROR) {
            throw new RuntimeException("I have no idea what type that is!");
        }
        switch (cellType) {

            case CELL_TYPE_FORMULA:
                FormulaRecordAggregate frec;

                if (cellType != _cellType) {
                    frec = _sheet.getSheet().getRowsAggregate().createFormula(row, col);
                } else {
                    frec = (FormulaRecordAggregate) _record;
                    frec.setRow(row);
                    frec.setColumn(col);
                }
                if (setValue) {
                    frec.getFormulaRecord().setValue(getNumericCellValue());
                }
                frec.setXFIndex(styleIndex);
                _record = frec;
                break;

            case CELL_TYPE_NUMERIC:
                NumberRecord nrec = null;

                if (cellType != _cellType) {
                    nrec = new NumberRecord();
                } else {
                    nrec = (NumberRecord) _record;
                }
                nrec.setColumn(col);
                if (setValue) {
                    nrec.setValue(getNumericCellValue());
                }
                nrec.setXFIndex(styleIndex);
                nrec.setRow(row);
                _record = nrec;
                break;

            case CELL_TYPE_STRING:
                LabelSSTRecord lrec;

                if (cellType == _cellType) {
                    lrec = (LabelSSTRecord) _record;
                } else {
                    lrec = new LabelSSTRecord();
                    lrec.setColumn(col);
                    lrec.setRow(row);
                    lrec.setXFIndex(styleIndex);
                }
                if (setValue) {
                    String str = convertCellValueToString();
                    int sstIndex = _book.getWorkbook().addSSTString(new UnicodeString(str));
                    lrec.setSSTIndex(sstIndex);
                    UnicodeString us = _book.getWorkbook().getSSTString(sstIndex);
                    _stringValue = new HSSFRichTextString();
                    _stringValue.setUnicodeString(us);
                }
                _record = lrec;
                break;

            case CELL_TYPE_BLANK:
                BlankRecord brec = null;

                if (cellType != _cellType) {
                    brec = new BlankRecord();
                } else {
                    brec = (BlankRecord) _record;
                }
                brec.setColumn(col);


                brec.setXFIndex(styleIndex);
                brec.setRow(row);
                _record = brec;
                break;

            case CELL_TYPE_BOOLEAN:
                BoolErrRecord boolRec = null;

                if (cellType != _cellType) {
                    boolRec = new BoolErrRecord();
                } else {
                    boolRec = (BoolErrRecord) _record;
                }
                boolRec.setColumn(col);
                if (setValue) {
                    boolRec.setValue(convertCellValueToBoolean());
                }
                boolRec.setXFIndex(styleIndex);
                boolRec.setRow(row);
                _record = boolRec;
                break;

            case CELL_TYPE_ERROR:
                BoolErrRecord errRec = null;

                if (cellType != _cellType) {
                    errRec = new BoolErrRecord();
                } else {
                    errRec = (BoolErrRecord) _record;
                }
                errRec.setColumn(col);
                if (setValue) {
                    errRec.setValue((byte) HSSFErrorConstants.ERROR_VALUE);
                }
                errRec.setXFIndex(styleIndex);
                errRec.setRow(row);
                _record = errRec;
                break;
        }
        if (cellType != _cellType &&
                _cellType != -1)
        {
            _sheet.getSheet().replaceValueRecord(_record);
        }
        _cellType = cellType;
    }



    public int getCellType() {
        return _cellType;
    }


    public void setCellType(int cellType) {
        notifyFormulaChanging();
        if (isPartOfArrayFormulaGroup()) {
            notifyArrayFormulaChanging();
        }
        int row = _record.getRow();
        short col = _record.getColumn();
        short styleIndex = _record.getXFIndex();
        setCellType(cellType, true, row, col, styleIndex);
    }


    public void setCellValue(double value) {
        if (Double.isInfinite(value)) {


            setCellErrorValue(FormulaError.DIV0.getCode());
        } else if (Double.isNaN(value)) {


            setCellErrorValue(FormulaError.NUM.getCode());
        } else {
            int row = _record.getRow();
            short col = _record.getColumn();
            short styleIndex = _record.getXFIndex();

            switch (_cellType) {
                default:
                    setCellType(CELL_TYPE_NUMERIC, false, row, col, styleIndex);
                case CELL_TYPE_NUMERIC:
                    ((NumberRecord) _record).setValue(value);
                    break;
                case CELL_TYPE_FORMULA:
                    ((FormulaRecordAggregate) _record).setCachedDoubleResult(value);
                    break;
            }
        }

    }


    public void setCellValue(Date value) {
        setCellValue(HSSFDateUtil.getExcelDate(value, _book.getWorkbook().isUsing1904DateWindowing()));
    }


    public void setCellValue(Calendar value) {
        setCellValue(HSSFDateUtil.getExcelDate(value, _book.getWorkbook().isUsing1904DateWindowing()));
    }


    public void setCellValue(String value) {
        HSSFRichTextString str = value == null ? null : new HSSFRichTextString(value);
        setCellValue(str);
    }



    public void setCellValue(RichTextString value) {
        HSSFRichTextString hvalue = (HSSFRichTextString) value;
        int row = _record.getRow();
        short col = _record.getColumn();
        short styleIndex = _record.getXFIndex();
        if (hvalue == null) {
            notifyFormulaChanging();
            setCellType(CELL_TYPE_BLANK, false, row, col, styleIndex);
            return;
        }

        if (hvalue.length() > SpreadsheetVersion.EXCEL97.getMaxTextLength()) {
            throw new IllegalArgumentException("The maximum length of cell contents (text) is 32,767 characters");
        }

        if (_cellType == CELL_TYPE_FORMULA) {


            FormulaRecordAggregate fr = (FormulaRecordAggregate) _record;
            fr.setCachedStringResult(hvalue.getString());

            _stringValue = new HSSFRichTextString(value.getString());


            return;
        }




        if (_cellType != CELL_TYPE_STRING) {
            setCellType(CELL_TYPE_STRING, false, row, col, styleIndex);
        }
        int index = 0;

        UnicodeString str = hvalue.getUnicodeString();
        index = _book.getWorkbook().addSSTString(str);
        ((LabelSSTRecord) _record).setSSTIndex(index);
        _stringValue = hvalue;
        _stringValue.setWorkbookReferences(_book.getWorkbook(), ((LabelSSTRecord) _record));
        _stringValue.setUnicodeString(_book.getWorkbook().getSSTString(index));
    }


    private void notifyFormulaChanging() {
        if (_record instanceof FormulaRecordAggregate) {
            ((FormulaRecordAggregate) _record).notifyFormulaChanging();
        }
    }

    public String getCellFormula() {
        if (!(_record instanceof FormulaRecordAggregate)) {
            throw typeMismatch(CELL_TYPE_FORMULA, _cellType, true);
        }

        return null;
    }

    public void setCellFormula(String formula) {
        if (isPartOfArrayFormulaGroup()) {
            notifyArrayFormulaChanging();
        }

        int row = _record.getRow();
        short col = _record.getColumn();
        short styleIndex = _record.getXFIndex();

        if (formula == null) {
            notifyFormulaChanging();
            setCellType(CELL_TYPE_BLANK, false, row, col, styleIndex);
            return;
        }
        int sheetIndex = _book.getSheetIndex(_sheet);












    }

    public void setCellFormula(Ptg[] ptgs) {
        if (isPartOfArrayFormulaGroup()) {
            notifyArrayFormulaChanging();
        }

        int row = _record.getRow();
        short col = _record.getColumn();
        short styleIndex = _record.getXFIndex();

        setCellType(CELL_TYPE_FORMULA, false, row, col, styleIndex);
        FormulaRecordAggregate agg = (FormulaRecordAggregate) _record;
        FormulaRecord frec = agg.getFormulaRecord();
        frec.setOptions((short) 2);
        frec.setValue(0);


        if (agg.getXFIndex() == (short) 0) {
            agg.setXFIndex((short) 0x0f);
        }
        agg.setParsedExpression(ptgs);
    }


    public int getFormulaCachedValueType() {
        return ((FormulaRecordAggregate) _record).getFormulaRecord().getCachedResultType();
    }


    public double getNumericCellValue() {

        switch (_cellType) {
            case CELL_TYPE_BLANK:
                return 0.0;
            case CELL_TYPE_NUMERIC:
                return ((NumberRecord) _record).getValue();
            default:
                throw typeMismatch(CELL_TYPE_NUMERIC, _cellType, false);
            case CELL_TYPE_FORMULA:
                break;
        }
        FormulaRecord fr = ((FormulaRecordAggregate) _record).getFormulaRecord();
        checkFormulaCachedValueType(CELL_TYPE_NUMERIC, fr);
        return fr.getValue();
    }


    public Date getDateCellValue() {

        if (_cellType == CELL_TYPE_BLANK) {
            return null;
        }
        double value = getNumericCellValue();
        if (_book.getWorkbook().isUsing1904DateWindowing()) {
            return HSSFDateUtil.getJavaDate(value, true);
        }
        return HSSFDateUtil.getJavaDate(value, false);
    }

    public int getSSTStringIndex() {
        HSSFRichTextString str = getRichStringCellValue();
        return str.getSSTIndex();
    }


    public String getStringCellValue() {
        HSSFRichTextString str = getRichStringCellValue();
        return str.getString();
    }


    public HSSFRichTextString getRichStringCellValue() {

        switch (_cellType) {
            case CELL_TYPE_BLANK:
                return new HSSFRichTextString("");
            case CELL_TYPE_STRING:
                return _stringValue;
            default:
                throw typeMismatch(CELL_TYPE_STRING, _cellType, false);
            case CELL_TYPE_FORMULA:
                break;
        }
        FormulaRecordAggregate fra = ((FormulaRecordAggregate) _record);
        checkFormulaCachedValueType(CELL_TYPE_STRING, fra.getFormulaRecord());
        String strVal = fra.getStringValue();
        return new HSSFRichTextString(strVal == null ? "" : strVal);
    }


    public void setCellValue(boolean value) {
        int row = _record.getRow();
        short col = _record.getColumn();
        short styleIndex = _record.getXFIndex();

        switch (_cellType) {
            default:
                setCellType(CELL_TYPE_BOOLEAN, false, row, col, styleIndex);
            case CELL_TYPE_BOOLEAN:
                ((BoolErrRecord) _record).setValue(value);
                break;
            case CELL_TYPE_FORMULA:
                ((FormulaRecordAggregate) _record).setCachedBooleanResult(value);
                break;
        }
    }


    public void setCellErrorValue(byte errorCode) {
        int row = _record.getRow();
        short col = _record.getColumn();
        short styleIndex = _record.getXFIndex();
        switch (_cellType) {
            default:
                setCellType(CELL_TYPE_ERROR, false, row, col, styleIndex);
            case CELL_TYPE_ERROR:
                ((BoolErrRecord) _record).setValue(errorCode);
                break;
            case CELL_TYPE_FORMULA:
                ((FormulaRecordAggregate) _record).setCachedErrorResult(errorCode);
                break;
        }
    }


    private boolean convertCellValueToBoolean() {

        switch (_cellType) {
            case CELL_TYPE_BOOLEAN:
                return ((BoolErrRecord) _record).getBooleanValue();
            case CELL_TYPE_STRING:
                int sstIndex = ((LabelSSTRecord) _record).getSSTIndex();
                String text = _book.getWorkbook().getSSTString(sstIndex).getString();
                return Boolean.valueOf(text).booleanValue();
            case CELL_TYPE_NUMERIC:
                return ((NumberRecord) _record).getValue() != 0;

            case CELL_TYPE_FORMULA:

                FormulaRecord fr = ((FormulaRecordAggregate) _record).getFormulaRecord();
                checkFormulaCachedValueType(CELL_TYPE_BOOLEAN, fr);
                return fr.getCachedBooleanValue();


            case CELL_TYPE_ERROR:
            case CELL_TYPE_BLANK:
                return false;
        }
        throw new RuntimeException("Unexpected cell type (" + _cellType + ")");
    }

    private String convertCellValueToString() {

        switch (_cellType) {
            case CELL_TYPE_BLANK:
                return "";
            case CELL_TYPE_BOOLEAN:
                return ((BoolErrRecord) _record).getBooleanValue() ? "TRUE" : "FALSE";
            case CELL_TYPE_STRING:
                int sstIndex = ((LabelSSTRecord) _record).getSSTIndex();
                return _book.getWorkbook().getSSTString(sstIndex).getString();
            case CELL_TYPE_NUMERIC:
                return NumberToTextConverter.toText(((NumberRecord) _record).getValue());
            case CELL_TYPE_ERROR:
                return HSSFErrorConstants.getText(((BoolErrRecord) _record).getErrorValue());
            case CELL_TYPE_FORMULA:


                break;
            default:
                throw new IllegalStateException("Unexpected cell type (" + _cellType + ")");
        }
        FormulaRecordAggregate fra = ((FormulaRecordAggregate) _record);
        FormulaRecord fr = fra.getFormulaRecord();
        switch (fr.getCachedResultType()) {
            case CELL_TYPE_BOOLEAN:
                return fr.getCachedBooleanValue() ? "TRUE" : "FALSE";
            case CELL_TYPE_STRING:
                return fra.getStringValue();
            case CELL_TYPE_NUMERIC:
                return NumberToTextConverter.toText(fr.getValue());
            case CELL_TYPE_ERROR:
                return HSSFErrorConstants.getText(fr.getCachedErrorValue());
        }
        throw new IllegalStateException("Unexpected formula result type (" + _cellType + ")");
    }


    public boolean getBooleanCellValue() {

        switch (_cellType) {
            case CELL_TYPE_BLANK:
                return false;
            case CELL_TYPE_BOOLEAN:
                return ((BoolErrRecord) _record).getBooleanValue();
            default:
                throw typeMismatch(CELL_TYPE_BOOLEAN, _cellType, false);
            case CELL_TYPE_FORMULA:
                break;
        }
        FormulaRecord fr = ((FormulaRecordAggregate) _record).getFormulaRecord();
        checkFormulaCachedValueType(CELL_TYPE_BOOLEAN, fr);
        return fr.getCachedBooleanValue();
    }


    public byte getErrorCellValue() {
        switch (_cellType) {
            case CELL_TYPE_ERROR:
                return ((BoolErrRecord) _record).getErrorValue();
            default:
                throw typeMismatch(CELL_TYPE_ERROR, _cellType, false);
            case CELL_TYPE_FORMULA:
                break;
        }
        FormulaRecord fr = ((FormulaRecordAggregate) _record).getFormulaRecord();
        checkFormulaCachedValueType(CELL_TYPE_ERROR, fr);
        return (byte) fr.getCachedErrorValue();
    }


    public HSSFCellStyle getCellStyle() {
        short styleIndex = _record.getXFIndex();
        ExtendedFormatRecord xf = _book.getWorkbook().getExFormatAt(styleIndex);
        return new HSSFCellStyle(styleIndex, xf, _book);
    }


    public void setCellStyle(ICellStyle style) {
        setCellStyle((HSSFCellStyle) style);
    }

    public void setCellStyle(HSSFCellStyle style) {

        style.verifyBelongsToWorkbook(_book);

        short styleIndex;
        if (style.getUserStyleName() != null) {
            styleIndex = applyUserCellStyle(style);
        } else {
            styleIndex = style.getIndex();
        }


        _record.setXFIndex(styleIndex);
    }


    public int getCellStyleIndex() {
        return _record.getXFIndex();
    }



    protected CellValueRecordInterface getCellValueRecord() {
        return _record;
    }


    public void setAsActiveCell() {
        int row = _record.getRow();
        short col = _record.getColumn();
        _sheet.getSheet().setActiveCellRow(row);
        _sheet.getSheet().setActiveCellCol(col);
    }


    public String toString() {
        switch (getCellType()) {
            case CELL_TYPE_BLANK:
                return "";
            case CELL_TYPE_BOOLEAN:
                return getBooleanCellValue() ? "TRUE" : "FALSE";
            case CELL_TYPE_ERROR:
                return ErrorEval.getText(((BoolErrRecord) _record).getErrorValue());
            case CELL_TYPE_FORMULA:
                return getCellFormula();
            case CELL_TYPE_NUMERIC:


                return String.valueOf(getNumericCellValue());
            case CELL_TYPE_STRING:
                return getStringCellValue();
            default:
                return "Unknown Cell Type: " + getCellType();
        }
    }


    public HSSFComment getCellComment() {
        if (_comment == null) {
            _comment = findCellComment(_sheet.getSheet(), _record.getRow(), _record.getColumn());
        }
        return _comment;
    }


    public void setCellComment(Comment comment) {
        if (comment == null) {
            removeCellComment();
            return;
        }

        comment.setRow(_record.getRow());
        comment.setColumn(_record.getColumn());
        _comment = (HSSFComment) comment;
    }


    public void removeCellComment() {
        HSSFComment comment = findCellComment(_sheet.getSheet(), _record.getRow(), _record.getColumn());
        _comment = null;

        if (comment == null) {

            return;
        }


        List<RecordBase> sheetRecords = _sheet.getSheet().getRecords();
        sheetRecords.remove(comment.getNoteRecord());






        if (comment.getTextObjectRecord() != null) {
            TextObjectRecord txo = comment.getTextObjectRecord();
            int txoAt = sheetRecords.indexOf(txo);

            if (sheetRecords.get(txoAt - 3) instanceof DrawingRecord &&
                    sheetRecords.get(txoAt - 2) instanceof ObjRecord &&
                    sheetRecords.get(txoAt - 1) instanceof DrawingRecord) {

                sheetRecords.remove(txoAt - 1);
                sheetRecords.remove(txoAt - 2);
                sheetRecords.remove(txoAt - 3);
            } else {
                throw new IllegalStateException("Found the wrong records before the TextObjectRecord, can't remove comment");
            }


            sheetRecords.remove(txo);
        }
    }


    public HSSFHyperlink getHyperlink() {
        for (Iterator<RecordBase> it = _sheet.getSheet().getRecords().iterator(); it.hasNext(); ) {
            RecordBase rec = it.next();
            if (rec instanceof HyperlinkRecord) {
                HyperlinkRecord link = (HyperlinkRecord) rec;
                if (link.getFirstColumn() == _record.getColumn() && link.getFirstRow() == _record.getRow()) {
                    return new HSSFHyperlink(link);
                }
            }
        }
        return null;
    }


    public void setHyperlink(IHyperlink hyperlink) {
        HSSFHyperlink link = (HSSFHyperlink) hyperlink;

        link.setFirstRow(_record.getRow());
        link.setLastRow(_record.getRow());
        link.setFirstColumn(_record.getColumn());
        link.setLastColumn(_record.getColumn());

        switch (link.getType()) {
            case HSSFHyperlink.LINK_EMAIL:
            case HSSFHyperlink.LINK_URL:
                link.setLabel("url");
                break;
            case HSSFHyperlink.LINK_FILE:
                link.setLabel("file");
                break;
            case HSSFHyperlink.LINK_DOCUMENT:
                link.setLabel("place");
                break;
        }

        List<RecordBase> records = _sheet.getSheet().getRecords();
        int eofLoc = records.size() - 1;
        records.add(eofLoc, link.record);
    }


    public int getCachedFormulaResultType() {
        if (_cellType != CELL_TYPE_FORMULA) {
            throw new IllegalStateException("Only formula cells have cached results");
        }
        return ((FormulaRecordAggregate) _record).getFormulaRecord().getCachedResultType();
    }

    void setCellArrayFormula(HSSFCellRangeAddress range) {
        int row = _record.getRow();
        short col = _record.getColumn();
        short styleIndex = _record.getXFIndex();
        setCellType(CELL_TYPE_FORMULA, false, row, col, styleIndex);


        Ptg[] ptgsForCell = {new ExpPtg(range.getFirstRow(), range.getFirstColumn())};
        FormulaRecordAggregate agg = (FormulaRecordAggregate) _record;
        agg.setParsedExpression(ptgsForCell);
    }

    public HSSFCellRangeAddress getArrayFormulaRange() {
        if (_cellType != CELL_TYPE_FORMULA) {
            String ref = new CellReference(this).formatAsString();
            throw new IllegalStateException("Cell " + ref
                    + " is not part of an array formula.");
        }
        return ((FormulaRecordAggregate) _record).getArrayFormulaRange();
    }

    public boolean isPartOfArrayFormulaGroup() {
        if (_cellType != CELL_TYPE_FORMULA) {
            return false;
        }
        return ((FormulaRecordAggregate) _record).isPartOfArrayFormula();
    }


    void notifyArrayFormulaChanging(String msg) {
        HSSFCellRangeAddress cra = getArrayFormulaRange();
        if (cra.getNumberOfCells() > 1) {
            throw new IllegalStateException(msg);
        }

        getRow().getSheet().removeArrayFormula(this);
    }


    void notifyArrayFormulaChanging() {
        CellReference ref = new CellReference(this);
        String msg = "Cell " + ref.formatAsString() + " is part of a multi-cell array formula. " +
                "You cannot change part of an array.";
        notifyArrayFormulaChanging(msg);
    }


    private short applyUserCellStyle(HSSFCellStyle style) {
        if (style.getUserStyleName() == null) {
            throw new IllegalArgumentException("Expected user-defined style");
        }

        InternalWorkbook iwb = _book.getWorkbook();
        short userXf = -1;
        int numfmt = iwb.getNumExFormats();
        for (short i = 0; i < numfmt; i++) {
            ExtendedFormatRecord xf = iwb.getExFormatAt(i);
            if (xf.getXFType() == ExtendedFormatRecord.XF_CELL && xf.getParentIndex() == style.getIndex()) {
                userXf = i;
                break;
            }
        }
        short styleIndex;
        if (userXf == -1) {
            ExtendedFormatRecord xfr = iwb.createCellXF();
            xfr.cloneStyleFrom(iwb.getExFormatAt(style.getIndex()));
            xfr.setIndentionOptions((short) 0);
            xfr.setXFType(ExtendedFormatRecord.XF_CELL);
            xfr.setParentIndex(style.getIndex());
            styleIndex = (short) numfmt;
        } else {
            styleIndex = userXf;
        }

        return styleIndex;
    }


    public int getRangeAddressIndex() {
        return rangeAddressIndex;
    }


    public void setRangeAddressIndex(int rangeAddressIndex) {
        this.rangeAddressIndex = rangeAddressIndex;
    }
}
