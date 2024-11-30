

package com.document.render.office.fc.hssf.record;



public interface CellValueRecordInterface {


    int getRow();


    void setRow(int row);


    short getColumn();


    void setColumn(short col);

    short getXFIndex();

    void setXFIndex(short xf);
}
