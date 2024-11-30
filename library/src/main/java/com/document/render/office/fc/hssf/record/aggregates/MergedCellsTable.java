

package com.document.render.office.fc.hssf.record.aggregates;

import com.document.render.office.fc.hssf.model.RecordStream;
import com.document.render.office.fc.hssf.record.MergeCellsRecord;
import com.document.render.office.fc.ss.util.CellRangeAddressList;
import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;

import java.util.ArrayList;
import java.util.List;



public final class MergedCellsTable extends RecordAggregate {
    private static int MAX_MERGED_REGIONS = 1027;

    private final List _mergedRegions;


    public MergedCellsTable() {
        _mergedRegions = new ArrayList();
    }


    public void read(RecordStream rs) {
        List temp = _mergedRegions;
        while (rs.peekNextClass() == MergeCellsRecord.class) {
            MergeCellsRecord mcr = (MergeCellsRecord) rs.getNext();
            int nRegions = mcr.getNumAreas();
            for (int i = 0; i < nRegions; i++) {
                HSSFCellRangeAddress cra = mcr.getAreaAt(i);
                temp.add(cra);
            }
        }
    }

    public int getRecordSize() {

        int nRegions = _mergedRegions.size();
        if (nRegions < 1) {

            return 0;
        }
        int nMergedCellsRecords = nRegions / MAX_MERGED_REGIONS;
        int nLeftoverMergedRegions = nRegions % MAX_MERGED_REGIONS;

        int result = nMergedCellsRecords
                * (4 + CellRangeAddressList.getEncodedSize(MAX_MERGED_REGIONS)) + 4
                + CellRangeAddressList.getEncodedSize(nLeftoverMergedRegions);
        return result;
    }

    public void visitContainedRecords(RecordVisitor rv) {
        int nRegions = _mergedRegions.size();
        if (nRegions < 1) {

            return;
        }

        int nFullMergedCellsRecords = nRegions / MAX_MERGED_REGIONS;
        int nLeftoverMergedRegions = nRegions % MAX_MERGED_REGIONS;
        HSSFCellRangeAddress[] cras = new HSSFCellRangeAddress[nRegions];
        _mergedRegions.toArray(cras);

        for (int i = 0; i < nFullMergedCellsRecords; i++) {
            int startIx = i * MAX_MERGED_REGIONS;
            rv.visitRecord(new MergeCellsRecord(cras, startIx, MAX_MERGED_REGIONS));
        }
        if (nLeftoverMergedRegions > 0) {
            int startIx = nFullMergedCellsRecords * MAX_MERGED_REGIONS;
            rv.visitRecord(new MergeCellsRecord(cras, startIx, nLeftoverMergedRegions));
        }
    }

    public void addRecords(MergeCellsRecord[] mcrs) {
        for (int i = 0; i < mcrs.length; i++) {
            addMergeCellsRecord(mcrs[i]);
        }
    }

    private void addMergeCellsRecord(MergeCellsRecord mcr) {
        int nRegions = mcr.getNumAreas();
        for (int i = 0; i < nRegions; i++) {
            HSSFCellRangeAddress cra = mcr.getAreaAt(i);
            _mergedRegions.add(cra);
        }
    }

    public HSSFCellRangeAddress get(int index) {
        checkIndex(index);
        return (HSSFCellRangeAddress) _mergedRegions.get(index);
    }

    public void remove(int index) {
        checkIndex(index);
        _mergedRegions.remove(index);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= _mergedRegions.size()) {
            throw new IllegalArgumentException("Specified CF index " + index
                    + " is outside the allowable range (0.." + (_mergedRegions.size() - 1) + ")");
        }
    }

    public void addArea(int rowFrom, int colFrom, int rowTo, int colTo) {
        _mergedRegions.add(new HSSFCellRangeAddress(rowFrom, rowTo, colFrom, colTo));
    }

    public int getNumberOfMergedRegions() {
        return _mergedRegions.size();
    }
}
