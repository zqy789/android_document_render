
package com.document.render.office.simpletext.model;

import com.document.render.office.ss.model.baseModel.Cell;
import com.document.render.office.ss.model.baseModel.Workbook;


public class CellLeafElement extends LeafElement {

    private Workbook book;
    private int sharedStringIndex;

















    private int offStart;
    private int offEnd;
    private boolean appendNewline;

    public CellLeafElement(Cell cell, int offStart, int offEnd) {
        super(null);

        book = cell.getSheet().getWorkbook();

        this.sharedStringIndex = cell.getStringCellValueIndex();
        this.offStart = offStart;
        this.offEnd = offEnd;
    }


    public String getText(IDocument doc) {
        if (appendNewline) {
            return book.getSharedString(sharedStringIndex).substring(offStart, offEnd) + "\n";
        } else {
            return book.getSharedString(sharedStringIndex).substring(offStart, offEnd);
        }

    }


    public void appendNewlineFlag() {
        appendNewline = true;
    }


    public void dispose() {
        book = null;
    }
}
