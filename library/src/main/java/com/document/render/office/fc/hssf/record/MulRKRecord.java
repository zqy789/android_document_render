

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.util.RKUtil;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class MulRKRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x00BD;

    private int field_1_row;
    private short field_2_first_col;
    private RkRec[] field_3_rks;
    private short field_4_last_col;


    public MulRKRecord(RecordInputStream in) {
        field_1_row = in.readUShort();
        field_2_first_col = in.readShort();
        field_3_rks = RkRec.parseRKs(in);
        field_4_last_col = in.readShort();
    }

    public int getRow() {
        return field_1_row;
    }


    public short getFirstColumn() {
        return field_2_first_col;
    }


    public short getLastColumn() {
        return field_4_last_col;
    }


    public int getNumColumns() {
        return field_4_last_col - field_2_first_col + 1;
    }


    public short getXFAt(int coffset) {
        return field_3_rks[coffset].xf;
    }


    public double getRKNumberAt(int coffset) {
        return RKUtil.decodeNumber(field_3_rks[coffset].rk);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[MULRK]\n");
        buffer.append("	.row	 = ").append(HexDump.shortToHex(getRow())).append("\n");
        buffer.append("	.firstcol= ").append(HexDump.shortToHex(getFirstColumn())).append("\n");
        buffer.append("	.lastcol = ").append(HexDump.shortToHex(getLastColumn())).append("\n");

        for (int k = 0; k < getNumColumns(); k++) {
            buffer.append("	xf[").append(k).append("] = ").append(HexDump.shortToHex(getXFAt(k))).append("\n");
            buffer.append("	rk[").append(k).append("] = ").append(getRKNumberAt(k)).append("\n");
        }
        buffer.append("[/MULRK]\n");
        return buffer.toString();
    }

    public short getSid() {
        return sid;
    }

    public void serialize(LittleEndianOutput out) {
        throw new RecordFormatException("Sorry, you can't serialize MulRK in this release");
    }

    protected int getDataSize() {
        throw new RecordFormatException("Sorry, you can't serialize MulRK in this release");
    }

    private static final class RkRec {
        public static final int ENCODED_SIZE = 6;
        public final short xf;
        public final int rk;

        private RkRec(RecordInputStream in) {
            xf = in.readShort();
            rk = in.readInt();
        }

        public static RkRec[] parseRKs(RecordInputStream in) {
            int nItems = (in.remaining() - 2) / ENCODED_SIZE;
            RkRec[] retval = new RkRec[nItems];
            for (int i = 0; i < nItems; i++) {
                retval[i] = new RkRec(in);
            }
            return retval;
        }
    }
}
