

package com.document.render.office.fc.hssf.util;


import com.document.render.office.fc.ss.util.CellRangeAddressBase;
import com.document.render.office.fc.util.LittleEndianByteArrayOutputStream;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class CellRangeAddress8Bit extends CellRangeAddressBase {

    public static final int ENCODED_SIZE = 6;

    public CellRangeAddress8Bit(int firstRow, int lastRow, int firstCol, int lastCol) {
        super(firstRow, lastRow, firstCol, lastCol);
    }

    public CellRangeAddress8Bit(LittleEndianInput in) {
        super(readUShortAndCheck(in), in.readUShort(), in.readUByte(), in.readUByte());
    }

    private static int readUShortAndCheck(LittleEndianInput in) {
        if (in.available() < ENCODED_SIZE) {

            throw new RuntimeException("Ran out of data reading CellRangeAddress");
        }
        return in.readUShort();
    }

    public static int getEncodedSize(int numberOfItems) {
        return numberOfItems * ENCODED_SIZE;
    }


    public int serialize(int offset, byte[] data) {
        serialize(new LittleEndianByteArrayOutputStream(data, offset, ENCODED_SIZE));
        return ENCODED_SIZE;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getFirstRow());
        out.writeShort(getLastRow());
        out.writeByte(getFirstColumn());
        out.writeByte(getLastColumn());
    }

    public CellRangeAddress8Bit copy() {
        return new CellRangeAddress8Bit(getFirstRow(), getLastRow(), getFirstColumn(), getLastColumn());
    }
}
