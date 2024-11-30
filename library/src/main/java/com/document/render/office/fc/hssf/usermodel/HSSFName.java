

package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.model.InternalWorkbook;
import com.document.render.office.fc.hssf.record.NameCommentRecord;
import com.document.render.office.fc.hssf.record.NameRecord;
import com.document.render.office.fc.ss.usermodel.Name;
import com.document.render.office.ss.model.XLSModel.AWorkbook;



public final class HSSFName implements Name {
    private AWorkbook _book;
    private NameRecord _definedNameRec;
    private NameCommentRecord _commentRec;


     HSSFName(AWorkbook book, NameRecord name) {
        this(book, name, null);
    }


    public HSSFName(final AWorkbook book, final NameRecord name, final NameCommentRecord comment) {
        _book = book;
        _definedNameRec = name;
        _commentRec = comment;
    }

    private static void validateName(String name) {
        if (name.length() == 0) throw new IllegalArgumentException("Name cannot be blank");

        char c = name.charAt(0);
        if (!(c == '_' || Character.isLetter(c)) || name.indexOf(' ') != -1) {
            throw new IllegalArgumentException("Invalid name: '" + name + "'; Names must begin with a letter or underscore and not contain spaces");
        }
    }


    public String getSheetName() {
        int indexToExternSheet = _definedNameRec.getExternSheetNumber();

        return _book.getInternalWorkbook().findSheetNameFromExternSheet(indexToExternSheet);
    }


    public String getNameName() {
        return _definedNameRec.getNameText();
    }


    public void setNameName(String nameName) {
        validateName(nameName);

        InternalWorkbook wb = _book.getInternalWorkbook();
        _definedNameRec.setNameText(nameName);

        int sheetNumber = _definedNameRec.getSheetNumber();


        for (int i = wb.getNumNames() - 1; i >= 0; i--) {
            NameRecord rec = wb.getNameRecord(i);
            if (rec != _definedNameRec) {
                if (rec.getNameText().equalsIgnoreCase(nameName) && sheetNumber == rec.getSheetNumber()) {
                    String msg = "The " + (sheetNumber == 0 ? "workbook" : "sheet") + " already contains this name: " + nameName;
                    _definedNameRec.setNameText(nameName + "(2)");
                    throw new IllegalArgumentException(msg);
                }
            }
        }


        if (_commentRec != null) {
            String oldName = _commentRec.getNameText();
            _commentRec.setNameText(nameName);
            _book.getInternalWorkbook().updateNameCommentRecordCache(_commentRec);
        }
    }


    public String getReference() {
        return getRefersToFormula();
    }


    public void setReference(String ref) {
        setRefersToFormula(ref);
    }

    public String getRefersToFormula() {









        return null;
    }

    public void setRefersToFormula(String formulaText) {


    }

    public Ptg[] getRefersToFormulaDefinition() {
        if (_definedNameRec.isFunctionName()) {
            throw new IllegalStateException("Only applicable to named ranges");
        }
        return _definedNameRec.getNameDefinition();
    }

    public boolean isDeleted() {
        Ptg[] ptgs = _definedNameRec.getNameDefinition();
        return Ptg.doesFormulaReferToDeletedCell(ptgs);
    }


    public boolean isFunctionName() {
        return _definedNameRec.isFunctionName();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName()).append(" [");
        sb.append(_definedNameRec.getNameText());
        sb.append("]");
        return sb.toString();
    }


    public int getSheetIndex() {
        return _definedNameRec.getSheetNumber() - 1;
    }


    public void setSheetIndex(int index) {
        int lastSheetIx = _book.getNumberOfSheets() - 1;
        if (index < -1 || index > lastSheetIx) {
            throw new IllegalArgumentException("Sheet index (" + index + ") is out of range" +
                    (lastSheetIx == -1 ? "" : (" (0.." + lastSheetIx + ")")));
        }

        _definedNameRec.setSheetNumber(index + 1);
    }


    public String getComment() {
        if (_commentRec != null) {

            if (_commentRec.getCommentText() != null &&
                    _commentRec.getCommentText().length() > 0) {
                return _commentRec.getCommentText();
            }
        }
        return _definedNameRec.getDescriptionText();
    }


    public void setComment(String comment) {

        _definedNameRec.setDescriptionText(comment);

        if (_commentRec != null) {
            _commentRec.setCommentText(comment);
        }
    }


    public void setFunction(boolean value) {
        _definedNameRec.setFunction(value);
    }

    public void dispose() {
        _book = null;
        _definedNameRec = null;
        _commentRec = null;
    }
}
