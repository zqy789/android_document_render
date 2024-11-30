
package com.document.render.office.simpletext.view;


public class TableAttr {


    public int topMargin;

    public int leftMargin;

    public int rightMargin;

    public int bottomMargin;

    public int cellWidth;

    public byte cellVerticalAlign;

    public int cellBackground;


    public void reset() {
        topMargin = 0;
        leftMargin = 0;
        rightMargin = 0;
        bottomMargin = 0;
        cellVerticalAlign = 0;
        cellBackground = -1;
    }


}
