
package com.document.render.office.ss.model.XLSModel;

import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.record.BlankRecord;
import com.document.render.office.fc.hssf.record.BoolErrRecord;
import com.document.render.office.fc.hssf.record.CellValueRecordInterface;
import com.document.render.office.fc.hssf.record.FormulaRecord;
import com.document.render.office.fc.hssf.record.LabelRecord;
import com.document.render.office.fc.hssf.record.LabelSSTRecord;
import com.document.render.office.fc.hssf.record.NumberRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.StringRecord;
import com.document.render.office.fc.hssf.record.aggregates.FormulaRecordAggregate;
import com.document.render.office.fc.hssf.record.common.UnicodeString;
import com.document.render.office.fc.hssf.usermodel.HSSFDataFormatter;
import com.document.render.office.fc.hssf.usermodel.HSSFRichTextString;
import com.document.render.office.fc.ss.SpreadsheetVersion;
import com.document.render.office.ss.model.baseModel.Cell;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.ss.util.SectionElementFactory;


public class ACell extends Cell {
    private CellValueRecordInterface record;


    public ACell(Sheet sheet, CellValueRecordInterface cval) {
        super(Cell.CELL_TYPE_BLANK);
        record = cval;

        cellType = (short) determineType(cval);

        this.sheet = sheet;

        this.rowNumber = cval.getRow();
        this.colNumber = cval.getColumn();
        this.styleIndex = cval.getXFIndex();

        switch (cellType) {
            case CELL_TYPE_NUMERIC:
                value = getNumericCellValue();
                break;

            case CELL_TYPE_STRING:
                if (cval instanceof LabelSSTRecord) {
                    value = ((LabelSSTRecord) cval).getSSTIndex();
                    processSST();
                } else if (cval instanceof LabelRecord) {
                    value = sheet.getWorkbook().addSharedString(((LabelRecord) cval).getValue());
                }
                break;

            case CELL_TYPE_FORMULA:
                procellFormulaCellValue((FormulaRecordAggregate) cval);
                break;
            case CELL_TYPE_BLANK:
                break;
            case CELL_TYPE_BOOLEAN:
                value = getBooleanCellValue();
                break;
            case CELL_TYPE_ERROR:
                value = getErrorCellValue();
                break;
        }
    }


    public ACell(AWorkbook book, ASheet sheet, int row, short col) {
        super(CELL_TYPE_ERROR);
        this.sheet = sheet;




        short xfindex = sheet.getInternalSheet().getXFIndexForColAt(col);
        setCellType(CELL_TYPE_BLANK, false, row, col, xfindex);
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


    public static int determineType(CellValueRecordInterface cval) {
        if (cval instanceof FormulaRecordAggregate) {
            return CELL_TYPE_FORMULA;
        }

        Record record = (Record) cval;
        switch (record.getSid()) {
            case NumberRecord.sid:
                return CELL_TYPE_NUMERIC;
            case BlankRecord.sid:
                return CELL_TYPE_BLANK;
            case LabelSSTRecord.sid:
            case LabelRecord.sid:
                return CELL_TYPE_STRING;
            case BoolErrRecord.sid:
                BoolErrRecord boolErrRecord = (BoolErrRecord) record;

                return boolErrRecord.isBoolean()
                        ? CELL_TYPE_BOOLEAN
                        : CELL_TYPE_ERROR;
        }
        throw new RuntimeException("Bad cell value rec (" + cval.getClass().getName() + ")");
    }

    private void processSST() {
        Workbook book = sheet.getWorkbook();
        Object obj = book.getSharedItem((Integer) value);
        if (obj instanceof UnicodeString) {
            UnicodeString unicodeString = (UnicodeString) obj;

            {
                value = book.addSharedString(SectionElementFactory.getSectionElement(book, unicodeString, this));
            }




        }
    }


    private void procellFormulaCellValue(FormulaRecordAggregate cval) {
        StringRecord strRec = cval.getStringRecord();
        if (strRec != null) {
            cellType = Cell.CELL_TYPE_STRING;
            value = sheet.getWorkbook().addSharedString(strRec.getString());
        } else {
            FormulaRecord formulaRec = cval.getFormulaRecord();
            cellType = (short) formulaRec.getCachedResultType();
            switch (cellType) {
                case CELL_TYPE_NUMERIC:
                    value = formulaRec.getValue();
                    break;
                case CELL_TYPE_STRING:

                    break;

                case CELL_TYPE_BOOLEAN:
                    value = formulaRec.getCachedBooleanValue();
                    break;
                case CELL_TYPE_ERROR:
                    value = (byte) formulaRec.getCachedErrorValue();
                    break;
            }
        }
    }


    public void setCellFormula(Ptg[] ptgs) {
        int row = record.getRow();
        short col = record.getColumn();
        short styleIndex = record.getXFIndex();

        setCellType(CELL_TYPE_FORMULA, false, row, col, styleIndex);
        FormulaRecordAggregate agg = (FormulaRecordAggregate) record;
        FormulaRecord frec = agg.getFormulaRecord();
        frec.setOptions((short) 2);
        frec.setValue(0);


        if (agg.getXFIndex() == (short) 0) {
            agg.setXFIndex((short) 0x0f);
        }
        agg.setParsedExpression(ptgs);
    }


    public String getStringCellValue() {
        switch (cellType) {
            case CELL_TYPE_BLANK:
                return "";
            case CELL_TYPE_STRING:
                if (record instanceof LabelSSTRecord) {
                    return sheet.getWorkbook().getSharedString(((LabelSSTRecord) record).getSSTIndex());
                }
                break;
            default:
                throw typeMismatch(CELL_TYPE_STRING, cellType, false);
            case CELL_TYPE_FORMULA:
                break;
        }
        FormulaRecordAggregate fra = ((FormulaRecordAggregate) record);
        checkFormulaCachedValueType(CELL_TYPE_STRING, fra.getFormulaRecord());
        String strVal = fra.getStringValue();
        return strVal;
    }


    public double getNumericCellValue() {
        switch (cellType) {
            case CELL_TYPE_BLANK:
                return 0.0;
            case CELL_TYPE_NUMERIC:
                return ((NumberRecord) record).getValue();
            default:
                throw typeMismatch(CELL_TYPE_NUMERIC, cellType, false);
            case CELL_TYPE_FORMULA:
                break;
        }
        FormulaRecord fr = ((FormulaRecordAggregate) record).getFormulaRecord();
        checkFormulaCachedValueType(CELL_TYPE_NUMERIC, fr);
        return fr.getValue();
    }


    public boolean getBooleanCellValue() {

        switch (cellType) {
            case CELL_TYPE_BLANK:
                return false;
            case CELL_TYPE_BOOLEAN:
                return ((BoolErrRecord) record).getBooleanValue();
            default:
                throw typeMismatch(CELL_TYPE_BOOLEAN, cellType, false);
            case CELL_TYPE_FORMULA:
                break;
        }
        FormulaRecord fr = ((FormulaRecordAggregate) record).getFormulaRecord();
        checkFormulaCachedValueType(CELL_TYPE_BOOLEAN, fr);
        return fr.getCachedBooleanValue();
    }


    public byte getErrorCellValue() {
        switch (cellType) {
            case CELL_TYPE_ERROR:
                return ((BoolErrRecord) record).getErrorValue();
            default:
                throw typeMismatch(CELL_TYPE_ERROR, cellType, false);
            case CELL_TYPE_FORMULA:
                break;
        }
        FormulaRecord fr = ((FormulaRecordAggregate) record).getFormulaRecord();
        checkFormulaCachedValueType(CELL_TYPE_ERROR, fr);
        return (byte) fr.getCachedErrorValue();
    }


    public int getFormulaCachedValueType() {
        return ((FormulaRecordAggregate) record).getFormulaRecord().getCachedResultType();
    }



    public CellValueRecordInterface getCellValueRecord() {
        return record;
    }


    public void setCellType(int cellType, boolean setValue) {
        int row = record.getRow();
        short col = record.getColumn();
        short styleIndex = record.getXFIndex();
        setCellType(cellType, setValue, row, col, styleIndex);
    }



    private void setCellType(int cellType, boolean setValue, int row, short col, short styleIndex) {

        if (cellType > CELL_TYPE_ERROR) {
            throw new RuntimeException("I have no idea what type that is!");
        }
        switch (cellType) {

            case CELL_TYPE_FORMULA:
                FormulaRecordAggregate frec;

                if (this.cellType != cellType) {
                    frec = ((ASheet) sheet).getInternalSheet().getRowsAggregate().createFormula(row, col);
                } else {
                    frec = (FormulaRecordAggregate) record;
                    frec.setRow(row);
                    frec.setColumn(col);
                }
                frec.setXFIndex(styleIndex);
                record = frec;
                break;

            case CELL_TYPE_NUMERIC:
                NumberRecord nrec = null;

                if (cellType != this.cellType) {
                    nrec = new NumberRecord();
                } else {
                    nrec = (NumberRecord) record;
                }
                nrec.setColumn(col);

                nrec.setXFIndex(styleIndex);
                nrec.setRow(row);
                record = nrec;
                break;

            case CELL_TYPE_STRING:
                LabelSSTRecord lrec;

                if (cellType == this.cellType) {
                    lrec = (LabelSSTRecord) this.record;
                } else {
                    lrec = new LabelSSTRecord();
                    lrec.setColumn(col);
                    lrec.setRow(row);
                    lrec.setXFIndex(styleIndex);
                }

                record = lrec;
                break;

            case CELL_TYPE_BLANK:
                BlankRecord brec = null;

                if (this.cellType != cellType) {
                    brec = new BlankRecord();
                } else {
                    brec = (BlankRecord) record;
                }
                brec.setColumn(col);


                brec.setXFIndex(styleIndex);
                brec.setRow(row);
                record = brec;
                break;

            case CELL_TYPE_BOOLEAN:
                BoolErrRecord boolRec = null;

                if (cellType != this.cellType) {
                    boolRec = new BoolErrRecord();
                } else {
                    boolRec = (BoolErrRecord) record;
                }
                boolRec.setColumn(col);

                boolRec.setXFIndex(styleIndex);
                boolRec.setRow(row);
                record = boolRec;
                break;

            case CELL_TYPE_ERROR:
                BoolErrRecord errRec = null;

                if (cellType != this.cellType) {
                    errRec = new BoolErrRecord();
                } else {
                    errRec = (BoolErrRecord) record;
                }
                errRec.setColumn(col);

                errRec.setXFIndex(styleIndex);
                errRec.setRow(row);
                record = errRec;
                break;
        }







        this.cellType = (short) cellType;
    }


    public void setCellValue(double value) {
        switch (cellType) {
            case CELL_TYPE_STRING:
                this.value = (Integer) Math.round((float) value);
                break;
            case CELL_TYPE_NUMERIC:
                ((NumberRecord) record).setValue(value);
                this.value = value;
                break;
            case CELL_TYPE_FORMULA:
                break;
        }
    }


    public void setCellValue(boolean value) {
        switch (cellType) {
            case CELL_TYPE_BOOLEAN:
                ((BoolErrRecord) record).setValue(value);
                this.value = value;
                break;
            case CELL_TYPE_FORMULA:
                break;
        }
    }


    public void setCellValue(String value) {
        HSSFRichTextString richString = value == null ? null : new HSSFRichTextString(value);

        int row = record.getRow();
        short col = record.getColumn();
        short styleIndex = record.getXFIndex();
        if (richString == null) {
            setCellType(CELL_TYPE_BLANK, false, row, col, styleIndex);
            return;
        }

        if (richString.length() > SpreadsheetVersion.EXCEL97.getMaxTextLength()) {
            throw new IllegalArgumentException("The maximum length of cell contents (text) is 32,767 characters");
        }
        int index = 0;

        UnicodeString str = richString.getUnicodeString();
        index = ((AWorkbook) sheet.getWorkbook()).getInternalWorkbook().addSSTString(str);
        ((LabelSSTRecord) record).setSSTIndex(index);

        this.value = index;
    }


    public void setCellErrorValue(byte errorCode) {
        switch (cellType) {
            case CELL_TYPE_ERROR:
                ((BoolErrRecord) record).setValue(errorCode);
                value = errorCode;
                break;
            case CELL_TYPE_FORMULA:
                break;
        }
    }

    public void dispose() {
        super.dispose();
        record = null;
    }
}
