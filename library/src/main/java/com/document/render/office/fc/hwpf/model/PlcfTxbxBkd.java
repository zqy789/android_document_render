package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.util.LittleEndian;


public class PlcfTxbxBkd {

    private int[] CPs;

    private Tbkd[] tbkds;

    public PlcfTxbxBkd(byte[] data, int offset, int length) {
        int tbkdSize = Tbkd.getSize();

        int tbkdCnt = (length - 4) / (tbkdSize + 4);
        int CPCnt = (length - tbkdCnt * tbkdSize) / 4;

        CPs = new int[CPCnt];
        tbkds = new Tbkd[tbkdCnt];

        for (int i = 0; i < CPCnt; i++) {
            CPs[i] = LittleEndian.getUShort(data, offset);
            offset += 4;
        }

        for (int i = 0; i < tbkdCnt; i++) {
            tbkds[i] = new Tbkd(data, offset);
            offset += tbkdSize;
        }
    }

    public int[] getCharPositions() {
        return CPs;
    }


    public int getCharPosition(int index) {
        if (CPs != null && CPs.length > index) {
            return CPs[index];
        }

        return -1;
    }

    public Tbkd[] getTbkds() {
        return tbkds;
    }
}
