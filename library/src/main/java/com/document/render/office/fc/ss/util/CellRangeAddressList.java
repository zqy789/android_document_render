

package com.document.render.office.fc.ss.util;

import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.util.LittleEndianByteArrayOutputStream;
import com.document.render.office.fc.util.LittleEndianOutput;

import java.util.ArrayList;
import java.util.List;



public class CellRangeAddressList {


    protected final List _list;

    public CellRangeAddressList() {
        _list = new ArrayList();
    }


    public CellRangeAddressList(int firstRow, int lastRow, int firstCol, int lastCol) {
        this();
        addCellRangeAddress(firstRow, firstCol, lastRow, lastCol);
    }


    public CellRangeAddressList(RecordInputStream in) {
        this();
        int nItems = in.readUShort();

        for (int k = 0; k < nItems; k++) {
            _list.add(new HSSFCellRangeAddress(in));
        }
    }


    public static int getEncodedSize(int numberOfRanges) {
        return 2 + HSSFCellRangeAddress.getEncodedSize(numberOfRanges);
    }


    public int countRanges() {
        return _list.size();
    }


    public void addCellRangeAddress(int firstRow, int firstCol, int lastRow, int lastCol) {
        HSSFCellRangeAddress region = new HSSFCellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        addCellRangeAddress(region);
    }

    public void addCellRangeAddress(HSSFCellRangeAddress cra) {
        _list.add(cra);
    }

    public HSSFCellRangeAddress remove(int rangeIndex) {
        if (_list.isEmpty()) {
            throw new RuntimeException("List is empty");
        }
        if (rangeIndex < 0 || rangeIndex >= _list.size()) {
            throw new RuntimeException("Range index (" + rangeIndex
                    + ") is outside allowable range (0.." + (_list.size() - 1) + ")");
        }
        return (HSSFCellRangeAddress) _list.remove(rangeIndex);
    }


    public HSSFCellRangeAddress getCellRangeAddress(int index) {
        return (HSSFCellRangeAddress) _list.get(index);
    }

    public int getSize() {
        return getEncodedSize(_list.size());
    }

    public int serialize(int offset, byte[] data) {
        int totalSize = getSize();
        serialize(new LittleEndianByteArrayOutputStream(data, offset, totalSize));
        return totalSize;
    }

    public void serialize(LittleEndianOutput out) {
        int nItems = _list.size();
        out.writeShort(nItems);
        for (int k = 0; k < nItems; k++) {
            HSSFCellRangeAddress region = (HSSFCellRangeAddress) _list.get(k);
            region.serialize(out);
        }
    }


    public CellRangeAddressList copy() {
        CellRangeAddressList result = new CellRangeAddressList();

        int nItems = _list.size();
        for (int k = 0; k < nItems; k++) {
            HSSFCellRangeAddress region = (HSSFCellRangeAddress) _list.get(k);
            result.addCellRangeAddress(region.copy());
        }
        return result;
    }

    public HSSFCellRangeAddress[] getCellRangeAddresses() {
        HSSFCellRangeAddress[] result = new HSSFCellRangeAddress[_list.size()];
        _list.toArray(result);
        return result;
    }
}
