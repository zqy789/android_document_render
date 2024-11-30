

package com.document.render.office.fc.ss.usermodel;


public interface Name {


    String getSheetName();


    String getNameName();


    void setNameName(String name);


    String getRefersToFormula();


    void setRefersToFormula(String formulaText);


    boolean isFunctionName();


    boolean isDeleted();


    public int getSheetIndex();


    public void setSheetIndex(int sheetId);


    public String getComment();


    public void setComment(String comment);


    void setFunction(boolean value);
}
