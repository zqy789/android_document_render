

package com.document.render.office.fc.ss.usermodel;

import com.document.render.office.fc.hssf.formula.FormulaParseException;
import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;

import java.util.Calendar;
import java.util.Date;



public interface ICell {


    public final static int CELL_TYPE_NUMERIC = 0;


    public final static int CELL_TYPE_STRING = 1;


    public final static int CELL_TYPE_FORMULA = 2;


    public final static int CELL_TYPE_BLANK = 3;


    public final static int CELL_TYPE_BOOLEAN = 4;


    public final static int CELL_TYPE_ERROR = 5;


    int getColumnIndex();


    int getRowIndex();


    Sheet getSheet();


    IRow getRow();


    int getCellType();


    void setCellType(int cellType);


    int getCachedFormulaResultType();


    void setCellValue(double value);


    void setCellValue(Date value);


    void setCellValue(Calendar value);


    void setCellValue(RichTextString value);


    void setCellValue(String value);


    String getCellFormula();


    void setCellFormula(String formula) throws FormulaParseException;


    double getNumericCellValue();


    Date getDateCellValue();


    RichTextString getRichStringCellValue();


    String getStringCellValue();


    void setCellValue(boolean value);


    void setCellErrorValue(byte value);


    boolean getBooleanCellValue();


    byte getErrorCellValue();


    ICellStyle getCellStyle();


    void setCellStyle(ICellStyle style);


    void setAsActiveCell();


    Comment getCellComment();


    void setCellComment(Comment comment);


    void removeCellComment();


    IHyperlink getHyperlink();


    void setHyperlink(IHyperlink link);


    HSSFCellRangeAddress getArrayFormulaRange();


    boolean isPartOfArrayFormulaGroup();
}
