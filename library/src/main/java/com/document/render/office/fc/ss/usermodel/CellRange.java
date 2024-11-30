

package com.document.render.office.fc.ss.usermodel;

import java.util.Iterator;



public interface CellRange<C extends ICell> extends Iterable<C> {

    int getWidth();

    int getHeight();


    int size();

    String getReferenceText();


    C getTopLeftCell();


    C getCell(int relativeRowIndex, int relativeColumnIndex);


    C[] getFlattenedCells();


    C[][] getCells();


    Iterator<C> iterator();
}
