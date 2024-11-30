

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.record.cf.CellRangeUtil;
import com.document.render.office.fc.ss.util.CellRangeAddressList;
import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class CFHeaderRecord extends StandardRecord {
    @Keep
    public static final short sid = 0x01B0;

    private int field_1_numcf;
    private int field_2_need_recalculation;
    private HSSFCellRangeAddress field_3_enclosing_cell_range;
    private CellRangeAddressList field_4_cell_ranges;


    public CFHeaderRecord() {
        field_4_cell_ranges = new CellRangeAddressList();
    }

    public CFHeaderRecord(HSSFCellRangeAddress[] regions, int nRules) {
        HSSFCellRangeAddress[] unmergedRanges = regions;
        HSSFCellRangeAddress[] mergeCellRanges = CellRangeUtil.mergeCellRanges(unmergedRanges);
        setCellRanges(mergeCellRanges);
        field_1_numcf = nRules;
    }

    public CFHeaderRecord(RecordInputStream in) {
        field_1_numcf = in.readShort();
        field_2_need_recalculation = in.readShort();
        field_3_enclosing_cell_range = new HSSFCellRangeAddress(in);
        field_4_cell_ranges = new CellRangeAddressList(in);
    }

    public int getNumberOfConditionalFormats() {
        return field_1_numcf;
    }

    public void setNumberOfConditionalFormats(int n) {
        field_1_numcf = n;
    }

    public boolean getNeedRecalculation() {
        return field_2_need_recalculation == 1 ? true : false;
    }

    public void setNeedRecalculation(boolean b) {
        field_2_need_recalculation = b ? 1 : 0;
    }

    public HSSFCellRangeAddress getEnclosingCellRange() {
        return field_3_enclosing_cell_range;
    }

    public void setEnclosingCellRange(HSSFCellRangeAddress cr) {
        field_3_enclosing_cell_range = cr;
    }

    public HSSFCellRangeAddress[] getCellRanges() {
        return field_4_cell_ranges.getCellRangeAddresses();
    }


    public void setCellRanges(HSSFCellRangeAddress[] cellRanges) {
        if (cellRanges == null) {
            throw new IllegalArgumentException("cellRanges must not be null");
        }
        CellRangeAddressList cral = new CellRangeAddressList();
        HSSFCellRangeAddress enclosingRange = null;
        for (int i = 0; i < cellRanges.length; i++) {
            HSSFCellRangeAddress cr = cellRanges[i];
            enclosingRange = CellRangeUtil.createEnclosingCellRange(cr, enclosingRange);
            cral.addCellRangeAddress(cr);
        }
        field_3_enclosing_cell_range = enclosingRange;
        field_4_cell_ranges = cral;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[CFHEADER]\n");
        buffer.append("	.id		= ").append(Integer.toHexString(sid)).append("\n");
        buffer.append("	.numCF			= ").append(getNumberOfConditionalFormats()).append("\n");
        buffer.append("	.needRecalc	   = ").append(getNeedRecalculation()).append("\n");
        buffer.append("	.enclosingCellRange= ").append(getEnclosingCellRange()).append("\n");
        buffer.append("	.cfranges=[");
        for (int i = 0; i < field_4_cell_ranges.countRanges(); i++) {
            buffer.append(i == 0 ? "" : ",").append(field_4_cell_ranges.getCellRangeAddress(i).toString());
        }
        buffer.append("]\n");
        buffer.append("[/CFHEADER]\n");
        return buffer.toString();
    }

    protected int getDataSize() {
        return 4
                + HSSFCellRangeAddress.ENCODED_SIZE
                + field_4_cell_ranges.getSize();
    }

    public void serialize(LittleEndianOutput out) {

        out.writeShort(field_1_numcf);
        out.writeShort(field_2_need_recalculation);
        field_3_enclosing_cell_range.serialize(out);
        field_4_cell_ranges.serialize(out);
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        CFHeaderRecord result = new CFHeaderRecord();
        result.field_1_numcf = field_1_numcf;
        result.field_2_need_recalculation = field_2_need_recalculation;
        result.field_3_enclosing_cell_range = field_3_enclosing_cell_range;
        result.field_4_cell_ranges = field_4_cell_ranges.copy();
        return result;
    }
}
