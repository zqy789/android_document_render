

package com.document.render.office.fc.hssf.model;

import com.document.render.office.fc.hssf.record.Record;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public final class WorkbookRecordList implements Iterable<Record> {
    private List<Record> records = new ArrayList<Record>();

    private int protpos = 0;
    private int bspos = 0;
    private int tabpos = 0;
    private int fontpos = 0;
    private int xfpos = 0;
    private int backuppos = 0;
    private int namepos = 0;
    private int supbookpos = 0;
    private int externsheetPos = 0;
    private int palettepos = -1;

    public int size() {
        return records.size();
    }

    public Record get(int i) {
        return records.get(i);
    }

    public void add(int pos, Record r) {
        records.add(pos, r);
        if (getProtpos() >= pos) setProtpos(protpos + 1);
        if (getBspos() >= pos) setBspos(bspos + 1);
        if (getTabpos() >= pos) setTabpos(tabpos + 1);
        if (getFontpos() >= pos) setFontpos(fontpos + 1);
        if (getXfpos() >= pos) setXfpos(xfpos + 1);
        if (getBackuppos() >= pos) setBackuppos(backuppos + 1);
        if (getNamepos() >= pos) setNamepos(namepos + 1);
        if (getSupbookpos() >= pos) setSupbookpos(supbookpos + 1);
        if ((getPalettepos() != -1) && (getPalettepos() >= pos)) setPalettepos(palettepos + 1);
        if (getExternsheetPos() >= pos) setExternsheetPos(getExternsheetPos() + 1);
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public Iterator<Record> iterator() {
        return records.iterator();
    }

    public void remove(Object record) {
        int i = records.indexOf(record);
        this.remove(i);
    }

    public void remove(int pos) {
        records.remove(pos);
        if (getProtpos() >= pos) setProtpos(protpos - 1);
        if (getBspos() >= pos) setBspos(bspos - 1);
        if (getTabpos() >= pos) setTabpos(tabpos - 1);
        if (getFontpos() >= pos) setFontpos(fontpos - 1);
        if (getXfpos() >= pos) setXfpos(xfpos - 1);
        if (getBackuppos() >= pos) setBackuppos(backuppos - 1);
        if (getNamepos() >= pos) setNamepos(getNamepos() - 1);
        if (getSupbookpos() >= pos) setSupbookpos(getSupbookpos() - 1);
        if ((getPalettepos() != -1) && (getPalettepos() >= pos)) setPalettepos(palettepos - 1);
        if (getExternsheetPos() >= pos) setExternsheetPos(getExternsheetPos() - 1);
    }

    public int getProtpos() {
        return protpos;
    }

    public void setProtpos(int protpos) {
        this.protpos = protpos;
    }

    public int getBspos() {
        return bspos;
    }

    public void setBspos(int bspos) {
        this.bspos = bspos;
    }

    public int getTabpos() {
        return tabpos;
    }

    public void setTabpos(int tabpos) {
        this.tabpos = tabpos;
    }

    public int getFontpos() {
        return fontpos;
    }

    public void setFontpos(int fontpos) {
        this.fontpos = fontpos;
    }

    public int getXfpos() {
        return xfpos;
    }

    public void setXfpos(int xfpos) {
        this.xfpos = xfpos;
    }

    public int getBackuppos() {
        return backuppos;
    }

    public void setBackuppos(int backuppos) {
        this.backuppos = backuppos;
    }

    public int getPalettepos() {
        return palettepos;
    }

    public void setPalettepos(int palettepos) {
        this.palettepos = palettepos;
    }



    public int getNamepos() {
        return namepos;
    }


    public void setNamepos(int namepos) {
        this.namepos = namepos;
    }


    public int getSupbookpos() {
        return supbookpos;
    }


    public void setSupbookpos(int supbookpos) {
        this.supbookpos = supbookpos;
    }


    public int getExternsheetPos() {
        return externsheetPos;
    }


    public void setExternsheetPos(int externsheetPos) {
        this.externsheetPos = externsheetPos;
    }
}
