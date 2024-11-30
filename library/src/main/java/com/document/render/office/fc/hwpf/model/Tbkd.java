package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.util.LittleEndian;


public class Tbkd {


    private short itxbxs;

    private short dcpDepend;

    private boolean fMarkDelete;

    private boolean fUnk;

    private boolean fTextOverflow;

    public Tbkd(byte[] data, int offset) {
        itxbxs = LittleEndian.getShort(data, offset);
        dcpDepend = LittleEndian.getShort(data, offset + 2);

        int t = LittleEndian.getShort(data, offset + 4);
        fMarkDelete = ((t & 0x20) != 0);
        fUnk = ((t & 0x10) != 0);
        fTextOverflow = ((t & 0x8) != 0);
    }

    public static int getSize() {
        return 6;
    }

    public int getTxbxIndex() {
        return itxbxs;
    }
}
