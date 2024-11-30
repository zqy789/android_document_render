

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;

import com.document.render.office.fc.ss.util.CellRangeAddressList;
import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class MergeCellsRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x00E5;
    private final int _startIndex;
    private final int _numberOfRegions;

    private HSSFCellRangeAddress[] _regions;

    public MergeCellsRecord(HSSFCellRangeAddress[] regions, int startIndex, int numberOfRegions) {
        _regions = regions;
        _startIndex = startIndex;
        _numberOfRegions = numberOfRegions;
    }


    public MergeCellsRecord(RecordInputStream in) {
        int nRegions = in.readUShort();
        HSSFCellRangeAddress[] cras = new HSSFCellRangeAddress[nRegions];
        for (int i = 0; i < nRegions; i++) {
            cras[i] = new HSSFCellRangeAddress(in);
        }
        _numberOfRegions = nRegions;
        _startIndex = 0;
        _regions = cras;
    }


    public short getNumAreas() {
        return (short) _numberOfRegions;
    }


    public HSSFCellRangeAddress getAreaAt(int index) {
        return _regions[_startIndex + index];
    }

    protected int getDataSize() {
        return CellRangeAddressList.getEncodedSize(_numberOfRegions);
    }

    public short getSid() {
        return sid;
    }

    public void serialize(LittleEndianOutput out) {
        int nItems = _numberOfRegions;
        out.writeShort(nItems);
        for (int i = 0; i < _numberOfRegions; i++) {
            _regions[_startIndex + i].serialize(out);
        }
    }

    public String toString() {
        StringBuffer retval = new StringBuffer();

        retval.append("[MERGEDCELLS]").append("\n");
        retval.append("     .numregions =").append(getNumAreas()).append("\n");
        for (int k = 0; k < _numberOfRegions; k++) {
            HSSFCellRangeAddress r = _regions[_startIndex + k];

            retval.append("     .rowfrom =").append(r.getFirstRow()).append("\n");
            retval.append("     .rowto   =").append(r.getLastRow()).append("\n");
            retval.append("     .colfrom =").append(r.getFirstColumn()).append("\n");
            retval.append("     .colto   =").append(r.getLastColumn()).append("\n");
        }
        retval.append("[MERGEDCELLS]").append("\n");
        return retval.toString();
    }

    public Object clone() {
        int nRegions = _numberOfRegions;
        HSSFCellRangeAddress[] clonedRegions = new HSSFCellRangeAddress[nRegions];
        for (int i = 0; i < clonedRegions.length; i++) {
            clonedRegions[i] = _regions[_startIndex + i].copy();
        }
        return new MergeCellsRecord(clonedRegions, 0, nRegions);
    }
}
