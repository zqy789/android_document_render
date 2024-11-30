
package com.document.render.office.fc.ss.usermodel;


public interface IHyperlink extends com.document.render.office.fc.usermodel.Hyperlink {

    public int getFirstRow();


    public void setFirstRow(int row);


    public int getLastRow();


    public void setLastRow(int row);


    public int getFirstColumn();


    public void setFirstColumn(int col);


    public int getLastColumn();


    public void setLastColumn(int col);
}
