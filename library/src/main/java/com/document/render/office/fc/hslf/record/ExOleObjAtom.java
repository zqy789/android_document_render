

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;



public class ExOleObjAtom extends RecordAtom {


    public static final int DRAW_ASPECT_VISIBLE = 1;

    public static final int DRAW_ASPECT_THUMBNAIL = 2;

    public static final int DRAW_ASPECT_ICON = 4;

    public static final int DRAW_ASPECT_DOCPRINT = 8;


    public static final int TYPE_EMBEDDED = 0;

    public static final int TYPE_LINKED = 1;

    public static final int TYPE_CONTROL = 2;

    public static final int SUBTYPE_DEFAULT = 0;
    public static final int SUBTYPE_CLIPART_GALLERY = 1;
    public static final int SUBTYPE_WORD_TABLE = 2;
    public static final int SUBTYPE_EXCEL = 3;
    public static final int SUBTYPE_GRAPH = 4;
    public static final int SUBTYPE_ORGANIZATION_CHART = 5;
    public static final int SUBTYPE_EQUATION = 6;
    public static final int SUBTYPE_WORDART = 7;
    public static final int SUBTYPE_SOUND = 8;
    public static final int SUBTYPE_IMAGE = 9;
    public static final int SUBTYPE_POWERPOINT_PRESENTATION = 10;
    public static final int SUBTYPE_POWERPOINT_SLIDE = 11;
    public static final int SUBTYPE_PROJECT = 12;
    public static final int SUBTYPE_NOTEIT = 13;
    public static final int SUBTYPE_EXCEL_CHART = 14;
    public static final int SUBTYPE_MEDIA_PLAYER = 15;


    private byte[] _header;


    private byte[] _data;


    public ExOleObjAtom() {
        _header = new byte[8];
        _data = new byte[24];

        LittleEndian.putShort(_header, 0, (short) 1);
        LittleEndian.putShort(_header, 2, (short) getRecordType());
        LittleEndian.putInt(_header, 4, _data.length);
    }


    protected ExOleObjAtom(byte[] source, int start, int len) {

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        _data = new byte[len - 8];
        System.arraycopy(source, start + 8, _data, 0, len - 8);


        if (_data.length < 24) {
            throw new IllegalArgumentException("The length of the data for a ExOleObjAtom must be at least 24 bytes, but was only " + _data.length);
        }
    }


    public int getDrawAspect() {
        return LittleEndian.getInt(_data, 0);
    }


    public void setDrawAspect(int aspect) {
        LittleEndian.putInt(_data, 0, aspect);
    }


    public int getType() {
        return LittleEndian.getInt(_data, 4);
    }


    public void setType(int type) {
        LittleEndian.putInt(_data, 4, type);
    }


    public int getObjID() {
        return LittleEndian.getInt(_data, 8);
    }


    public void setObjID(int id) {
        LittleEndian.putInt(_data, 8, id);
    }


    public int getSubType() {
        return LittleEndian.getInt(_data, 12);
    }


    public void setSubType(int type) {
        LittleEndian.putInt(_data, 12, type);
    }


    public int getObjStgDataRef() {
        return LittleEndian.getInt(_data, 16);
    }


    public void setObjStgDataRef(int ref) {
        LittleEndian.putInt(_data, 16, ref);
    }


    public boolean getIsBlank() {

        return LittleEndian.getInt(_data, 20) != 0;
    }


    public int getOptions() {

        return LittleEndian.getInt(_data, 20);
    }


    public void setOptions(int opts) {

        LittleEndian.putInt(_data, 20, opts);
    }


    public long getRecordType() {
        return RecordTypes.ExOleObjAtom.typeID;
    }


    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("ExOleObjAtom\n");
        buf.append("  drawAspect: " + getDrawAspect() + "\n");
        buf.append("  type: " + getType() + "\n");
        buf.append("  objID: " + getObjID() + "\n");
        buf.append("  subType: " + getSubType() + "\n");
        buf.append("  objStgDataRef: " + getObjStgDataRef() + "\n");
        buf.append("  options: " + getOptions() + "\n");
        return buf.toString();
    }


    public void dispose() {
        _header = null;
        _data = null;
    }
}
