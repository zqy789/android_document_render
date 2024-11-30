
package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.hssf.record.HyperlinkRecord;
import com.document.render.office.fc.ss.usermodel.IHyperlink;



public class HSSFHyperlink implements IHyperlink {


    public static final int LINK_URL = 1;


    public static final int LINK_DOCUMENT = 2;


    public static final int LINK_EMAIL = 3;


    public static final int LINK_FILE = 4;


    protected HyperlinkRecord record = null;


    protected int link_type;


    public HSSFHyperlink(int type) {
        this.link_type = type;
        record = new HyperlinkRecord();
        switch (type) {
            case LINK_URL:
            case LINK_EMAIL:
                record.newUrlLink();
                break;
            case LINK_FILE:
                record.newFileLink();
                break;
            case LINK_DOCUMENT:
                record.newDocumentLink();
                break;
        }
    }


    protected HSSFHyperlink(HyperlinkRecord record) {
        this.record = record;


        if (record.isFileLink()) {
            link_type = LINK_FILE;
        } else if (record.isDocumentLink()) {
            link_type = LINK_DOCUMENT;
        } else {
            if (record.getAddress() != null &&
                    record.getAddress().startsWith("mailto:")) {
                link_type = LINK_EMAIL;
            } else {
                link_type = LINK_URL;
            }
        }
    }


    public int getFirstRow() {
        return record.getFirstRow();
    }


    public void setFirstRow(int row) {
        record.setFirstRow(row);
    }


    public int getLastRow() {
        return record.getLastRow();
    }


    public void setLastRow(int row) {
        record.setLastRow(row);
    }


    public int getFirstColumn() {
        return record.getFirstColumn();
    }


    public void setFirstColumn(int col) {
        record.setFirstColumn((short) col);
    }


    public int getLastColumn() {
        return record.getLastColumn();
    }


    public void setLastColumn(int col) {
        record.setLastColumn((short) col);
    }


    public String getAddress() {
        return record.getAddress();
    }


    public void setAddress(String address) {
        record.setAddress(address);
    }

    public String getTextMark() {
        return record.getTextMark();
    }


    public void setTextMark(String textMark) {
        record.setTextMark(textMark);
    }

    public String getShortFilename() {
        return record.getShortFilename();
    }


    public void setShortFilename(String shortFilename) {
        record.setShortFilename(shortFilename);
    }


    public String getLabel() {
        return record.getLabel();
    }


    public void setLabel(String label) {
        record.setLabel(label);
    }


    public int getType() {
        return link_type;
    }
}
