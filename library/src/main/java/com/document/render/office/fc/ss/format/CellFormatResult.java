

package com.document.render.office.fc.ss.format;


public class CellFormatResult {

    public final boolean applies;


    public final String text;


    public final int textColor;


    public CellFormatResult(boolean applies, String text, int textColor) {
        this.applies = applies;
        this.text = text;
        this.textColor = (applies ? textColor : -1);
    }
}
