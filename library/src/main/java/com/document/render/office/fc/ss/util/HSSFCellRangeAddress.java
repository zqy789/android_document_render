

package com.document.render.office.fc.ss.util;


import com.document.render.office.fc.hssf.formula.SheetNameFormatter;
import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.SelectionRecord;
import com.document.render.office.fc.util.LittleEndianByteArrayOutputStream;
import com.document.render.office.fc.util.LittleEndianOutput;



public class HSSFCellRangeAddress extends CellRangeAddressBase {
    
    public static final int ENCODED_SIZE = 8;

    public HSSFCellRangeAddress(int firstRow, int lastRow, int firstCol, int lastCol) {
        super(firstRow, lastRow, firstCol, lastCol);
    }

    public HSSFCellRangeAddress(RecordInputStream in) {
        super(readUShortAndCheck(in), in.readUShort(), in.readUShort(), in.readUShort());
    }

    private static int readUShortAndCheck(RecordInputStream in) {
        if (in.remaining() < ENCODED_SIZE) {
            
            throw new RuntimeException("Ran out of data reading CellRangeAddress");
        }
        return in.readUShort();
    }

    public static int getEncodedSize(int numberOfItems) {
        return numberOfItems * ENCODED_SIZE;
    }

    
    public static HSSFCellRangeAddress valueOf(String ref) {
        int sep = ref.indexOf(":");
        CellReference a;
        CellReference b;
        if (sep == -1) {
            a = new CellReference(ref);
            b = a;
        } else {
            a = new CellReference(ref.substring(0, sep));
            b = new CellReference(ref.substring(sep + 1));
        }
        return new HSSFCellRangeAddress(a.getRow(), b.getRow(), a.getCol(), b.getCol());
    }

    
    public int serialize(int offset, byte[] data) {
        serialize(new LittleEndianByteArrayOutputStream(data, offset, ENCODED_SIZE));
        return ENCODED_SIZE;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getFirstRow());
        out.writeShort(getLastRow());
        out.writeShort(getFirstColumn());
        out.writeShort(getLastColumn());
    }

    public HSSFCellRangeAddress copy() {
        return new HSSFCellRangeAddress(getFirstRow(), getLastRow(), getFirstColumn(), getLastColumn());
    }

    
    public String formatAsString() {
        return formatAsString(null, false);
    }

    
    public String formatAsString(String sheetName, boolean useAbsoluteAddress) {
        StringBuffer sb = new StringBuffer();
        if (sheetName != null) {
            sb.append(SheetNameFormatter.format(sheetName));
            sb.append("!");
        }
        CellReference cellRefFrom = new CellReference(getFirstRow(), getFirstColumn(),
                useAbsoluteAddress, useAbsoluteAddress);
        CellReference cellRefTo = new CellReference(getLastRow(), getLastColumn(),
                useAbsoluteAddress, useAbsoluteAddress);
        sb.append(cellRefFrom.formatAsString());

        
        if (!cellRefFrom.equals(cellRefTo)) {
            sb.append(':');
            sb.append(cellRefTo.formatAsString());
        }
        return sb.toString();
    }
}
