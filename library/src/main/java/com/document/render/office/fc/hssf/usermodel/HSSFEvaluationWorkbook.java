

package com.document.render.office.fc.hssf.usermodel;


import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.formula.EvaluationCell;
import com.document.render.office.fc.hssf.formula.EvaluationName;
import com.document.render.office.fc.hssf.formula.EvaluationSheet;
import com.document.render.office.fc.hssf.formula.EvaluationWorkbook;
import com.document.render.office.fc.hssf.formula.FormulaParsingWorkbook;
import com.document.render.office.fc.hssf.formula.FormulaRenderingWorkbook;
import com.document.render.office.fc.hssf.formula.ptg.NamePtg;
import com.document.render.office.fc.hssf.formula.ptg.NameXPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.formula.udf.UDFFinder;
import com.document.render.office.fc.hssf.model.InternalWorkbook;
import com.document.render.office.fc.hssf.record.NameRecord;
import com.document.render.office.fc.hssf.record.aggregates.FormulaRecordAggregate;
import com.document.render.office.fc.ss.SpreadsheetVersion;
import com.document.render.office.ss.model.XLSModel.ACell;
import com.document.render.office.ss.model.XLSModel.ASheet;
import com.document.render.office.ss.model.XLSModel.AWorkbook;



public final class HSSFEvaluationWorkbook implements FormulaRenderingWorkbook, EvaluationWorkbook, FormulaParsingWorkbook {

    private final AWorkbook _uBook;
    private final InternalWorkbook _iBook;

    private HSSFEvaluationWorkbook(AWorkbook book) {
        _uBook = book;
        _iBook = book.getInternalWorkbook();
    }

    @Keep
    public static HSSFEvaluationWorkbook create(AWorkbook book) {
        if (book == null) {
            return null;
        }
        return new HSSFEvaluationWorkbook(book);
    }

    public int getExternalSheetIndex(String sheetName) {
        int sheetIndex = _uBook.getSheetIndex(sheetName);
        return _iBook.checkExternSheet(sheetIndex);
    }

    public int getExternalSheetIndex(String workbookName, String sheetName) {
        return _iBook.getExternalSheetIndex(workbookName, sheetName);
    }

    public NameXPtg getNameXPtg(String name) {
        return _iBook.getNameXPtg(name, _uBook.getUDFFinder());
    }


    public EvaluationName getName(String name, int sheetIndex) {
        for (int i = 0; i < _iBook.getNumNames(); i++) {
            NameRecord nr = _iBook.getNameRecord(i);
            if (nr.getSheetNumber() == sheetIndex + 1 && name.equalsIgnoreCase(nr.getNameText())) {
                return new Name(nr, i);
            }
        }
        return sheetIndex == -1 ? null : getName(name, -1);
    }

    public int getSheetIndex(EvaluationSheet evalSheet) {
        ASheet sheet = ((HSSFEvaluationSheet) evalSheet).getASheet();
        return _uBook.getSheetIndex(sheet);
    }

    public int getSheetIndex(String sheetName) {
        return _uBook.getSheetIndex(sheetName);
    }

    public String getSheetName(int sheetIndex) {
        return _uBook.getSheet(sheetIndex).getSheetName();

    }

    public EvaluationSheet getSheet(int sheetIndex) {
        return new HSSFEvaluationSheet(_uBook.getSheetAt(sheetIndex));
    }

    public int convertFromExternSheetIndex(int externSheetIndex) {
        return _iBook.getSheetIndexFromExternSheetIndex(externSheetIndex);
    }

    public ExternalSheet getExternalSheet(int externSheetIndex) {
        return _iBook.getExternalSheet(externSheetIndex);
    }

    public ExternalName getExternalName(int externSheetIndex, int externNameIndex) {
        return _iBook.getExternalName(externSheetIndex, externNameIndex);
    }

    public String resolveNameXText(NameXPtg n) {
        return _iBook.resolveNameXText(n.getSheetRefIndex(), n.getNameIndex());
    }

    public String getSheetNameByExternSheet(int externSheetIndex) {
        return _iBook.findSheetNameFromExternSheet(externSheetIndex);
    }

    public String getNameText(NamePtg namePtg) {
        return _iBook.getNameRecord(namePtg.getIndex()).getNameText();
    }

    public EvaluationName getName(NamePtg namePtg) {
        int ix = namePtg.getIndex();
        return new Name(_iBook.getNameRecord(ix), ix);
    }

    public EvaluationName getName(NameXPtg nameXPtg) {
        int ix = nameXPtg.getNameIndex();
        return new Name(_iBook.getNameRecord(ix), ix);
    }

    public Ptg[] getFormulaTokens(EvaluationCell evalCell) {
        ACell cell = ((HSSFEvaluationCell) evalCell).getACell();













        FormulaRecordAggregate fra = (FormulaRecordAggregate) cell.getCellValueRecord();
        return fra.getFormulaTokens();
    }

    public UDFFinder getUDFFinder() {
        return _uBook.getUDFFinder();
    }

    public SpreadsheetVersion getSpreadsheetVersion() {
        return SpreadsheetVersion.EXCEL97;
    }

    private static final class Name implements EvaluationName {

        private final NameRecord _nameRecord;
        private final int _index;

        public Name(NameRecord nameRecord, int index) {
            _nameRecord = nameRecord;
            _index = index;
        }

        public Ptg[] getNameDefinition() {
            return _nameRecord.getNameDefinition();
        }

        public String getNameText() {
            return _nameRecord.getNameText();
        }

        public boolean hasFormula() {
            return _nameRecord.hasFormula();
        }

        public boolean isFunctionName() {
            return _nameRecord.isFunctionName();
        }

        public boolean isRange() {
            return _nameRecord.hasFormula();
        }

        public NamePtg createPtg() {
            return new NamePtg(_index);
        }
    }
}
