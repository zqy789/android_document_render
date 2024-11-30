
package com.document.render.office.ss.model.style;


public class NumberFormat {


    private short numFmtId;

    private String formatCode;

    public NumberFormat() {
        numFmtId = 0;
        formatCode = "General";
    }

    public NumberFormat(short numFmtId, String formatCode) {
        this.numFmtId = numFmtId;
        this.formatCode = formatCode;
    }


    public short getNumberFormatID() {
        return numFmtId;
    }

    public void setNumberFormatID(short id) {
        numFmtId = id;
    }


    public String getFormatCode() {
        return formatCode;
    }


    public void setFormatCode(String formatCode) {
        this.formatCode = formatCode;
    }


    public void dispose() {
        formatCode = null;
    }
}
