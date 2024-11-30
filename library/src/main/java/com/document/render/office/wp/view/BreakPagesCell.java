
package com.document.render.office.wp.view;

import com.document.render.office.wp.model.CellElement;


public class BreakPagesCell {


    private CellElement cell;

    private long breakOffset;

    public BreakPagesCell(CellElement cell, long breakOffset) {
        this.cell = cell;
        this.breakOffset = breakOffset;
    }


    public CellElement getCell() {
        return cell;
    }


    public long getBreakOffset() {
        return breakOffset;
    }

}
