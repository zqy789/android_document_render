

package com.document.render.office.fc.hssf.record.aggregates;

import com.document.render.office.fc.hssf.model.RecordStream;
import com.document.render.office.fc.hssf.record.ColumnInfoRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public final class ColumnInfoRecordsAggregate extends RecordAggregate {

    private final List records;



    public ColumnInfoRecordsAggregate() {
        records = new ArrayList();
    }

    public ColumnInfoRecordsAggregate(RecordStream rs) {
        this();

        boolean isInOrder = true;
        ColumnInfoRecord cirPrev = null;
        while (rs.peekNextClass() == ColumnInfoRecord.class) {
            ColumnInfoRecord cir = (ColumnInfoRecord) rs.getNext();
            records.add(cir);
            if (cirPrev != null && CIRComparator.compareColInfos(cirPrev, cir) > 0) {
                isInOrder = false;
            }
            cirPrev = cir;
        }
        if (records.size() < 1) {
            throw new RuntimeException("No column info records found");
        }
        if (!isInOrder) {
            Collections.sort(records, CIRComparator.instance);
        }
    }

    private static ColumnInfoRecord copyColInfo(ColumnInfoRecord ci) {
        return (ColumnInfoRecord) ci.clone();
    }


    private static void setColumnInfoFields(ColumnInfoRecord ci, Short xfStyle, Integer width,
                                            Integer level, Boolean hidden, Boolean collapsed) {
        if (xfStyle != null) {
            ci.setXFIndex(xfStyle.shortValue());
        }
        if (width != null) {
            ci.setColumnWidth(width.intValue());
        }
        if (level != null) {
            ci.setOutlineLevel(level.shortValue());
        }
        if (hidden != null) {
            ci.setHidden(hidden.booleanValue());
        }
        if (collapsed != null) {
            ci.setCollapsed(collapsed.booleanValue());
        }
    }


    private static boolean mergeColInfoRecords(ColumnInfoRecord ciA, ColumnInfoRecord ciB) {
        if (ciA.isAdjacentBefore(ciB) && ciA.formatMatches(ciB)) {
            ciA.setLastColumn(ciB.getLastColumn());
            return true;
        }
        return false;
    }


    public Object clone() {
        ColumnInfoRecordsAggregate rec = new ColumnInfoRecordsAggregate();
        for (int k = 0; k < records.size(); k++) {
            ColumnInfoRecord ci = (ColumnInfoRecord) records.get(k);
            rec.records.add(ci.clone());
        }
        return rec;
    }


    public void insertColumn(ColumnInfoRecord col) {
        records.add(col);
        Collections.sort(records, CIRComparator.instance);
    }


    private void insertColumn(int idx, ColumnInfoRecord col) {
        records.add(idx, col);
    }

    public int getNumColumns() {
        return records.size();
    }

    public void visitContainedRecords(RecordVisitor rv) {
        int nItems = records.size();
        if (nItems < 1) {
            return;
        }
        ColumnInfoRecord cirPrev = null;
        for (int i = 0; i < nItems; i++) {
            ColumnInfoRecord cir = (ColumnInfoRecord) records.get(i);
            rv.visitRecord(cir);
            if (cirPrev != null && CIRComparator.compareColInfos(cirPrev, cir) > 0) {


                throw new RuntimeException("Column info records are out of order");
            }
            cirPrev = cir;
        }
    }

    private int findStartOfColumnOutlineGroup(int pIdx) {

        ColumnInfoRecord columnInfo = (ColumnInfoRecord) records.get(pIdx);
        int level = columnInfo.getOutlineLevel();
        int idx = pIdx;
        while (idx != 0) {
            ColumnInfoRecord prevColumnInfo = (ColumnInfoRecord) records.get(idx - 1);
            if (!prevColumnInfo.isAdjacentBefore(columnInfo)) {
                break;
            }
            if (prevColumnInfo.getOutlineLevel() < level) {
                break;
            }
            idx--;
            columnInfo = prevColumnInfo;
        }

        return idx;
    }

    private int findEndOfColumnOutlineGroup(int colInfoIndex) {

        ColumnInfoRecord columnInfo = (ColumnInfoRecord) records.get(colInfoIndex);
        int level = columnInfo.getOutlineLevel();
        int idx = colInfoIndex;
        while (idx < records.size() - 1) {
            ColumnInfoRecord nextColumnInfo = (ColumnInfoRecord) records.get(idx + 1);
            if (!columnInfo.isAdjacentBefore(nextColumnInfo)) {
                break;
            }
            if (nextColumnInfo.getOutlineLevel() < level) {
                break;
            }
            idx++;
            columnInfo = nextColumnInfo;
        }
        return idx;
    }

    public ColumnInfoRecord getColInfo(int idx) {
        return (ColumnInfoRecord) records.get(idx);
    }


    private boolean isColumnGroupCollapsed(int idx) {
        int endOfOutlineGroupIdx = findEndOfColumnOutlineGroup(idx);
        int nextColInfoIx = endOfOutlineGroupIdx + 1;
        if (nextColInfoIx >= records.size()) {
            return false;
        }
        ColumnInfoRecord nextColInfo = getColInfo(nextColInfoIx);
        if (!getColInfo(endOfOutlineGroupIdx).isAdjacentBefore(nextColInfo)) {
            return false;
        }
        return nextColInfo.getCollapsed();
    }

    private boolean isColumnGroupHiddenByParent(int idx) {

        int endLevel = 0;
        boolean endHidden = false;
        int endOfOutlineGroupIdx = findEndOfColumnOutlineGroup(idx);
        if (endOfOutlineGroupIdx < records.size()) {
            ColumnInfoRecord nextInfo = getColInfo(endOfOutlineGroupIdx + 1);
            if (getColInfo(endOfOutlineGroupIdx).isAdjacentBefore(nextInfo)) {
                endLevel = nextInfo.getOutlineLevel();
                endHidden = nextInfo.getHidden();
            }
        }

        int startLevel = 0;
        boolean startHidden = false;
        int startOfOutlineGroupIdx = findStartOfColumnOutlineGroup(idx);
        if (startOfOutlineGroupIdx > 0) {
            ColumnInfoRecord prevInfo = getColInfo(startOfOutlineGroupIdx - 1);
            if (prevInfo.isAdjacentBefore(getColInfo(startOfOutlineGroupIdx))) {
                startLevel = prevInfo.getOutlineLevel();
                startHidden = prevInfo.getHidden();
            }
        }
        if (endLevel > startLevel) {
            return endHidden;
        }
        return startHidden;
    }

    public void collapseColumn(int columnIndex) {
        int colInfoIx = findColInfoIdx(columnIndex, 0);
        if (colInfoIx == -1) {
            return;
        }


        int groupStartColInfoIx = findStartOfColumnOutlineGroup(colInfoIx);
        ColumnInfoRecord columnInfo = getColInfo(groupStartColInfoIx);


        int lastColIx = setGroupHidden(groupStartColInfoIx, columnInfo.getOutlineLevel(), true);


        setColumn(lastColIx + 1, null, null, null, null, Boolean.TRUE);
    }


    private int setGroupHidden(int pIdx, int level, boolean hidden) {
        int idx = pIdx;
        ColumnInfoRecord columnInfo = getColInfo(idx);
        while (idx < records.size()) {
            columnInfo.setHidden(hidden);
            if (idx + 1 < records.size()) {
                ColumnInfoRecord nextColumnInfo = getColInfo(idx + 1);
                if (!columnInfo.isAdjacentBefore(nextColumnInfo)) {
                    break;
                }
                if (nextColumnInfo.getOutlineLevel() < level) {
                    break;
                }
                columnInfo = nextColumnInfo;
            }
            idx++;
        }
        return columnInfo.getLastColumn();
    }

    public void expandColumn(int columnIndex) {
        int idx = findColInfoIdx(columnIndex, 0);
        if (idx == -1) {
            return;
        }


        if (!isColumnGroupCollapsed(idx)) {
            return;
        }


        int startIdx = findStartOfColumnOutlineGroup(idx);
        int endIdx = findEndOfColumnOutlineGroup(idx);








        ColumnInfoRecord columnInfo = getColInfo(endIdx);
        if (!isColumnGroupHiddenByParent(idx)) {
            int outlineLevel = columnInfo.getOutlineLevel();
            for (int i = startIdx; i <= endIdx; i++) {
                ColumnInfoRecord ci = getColInfo(i);
                if (outlineLevel == ci.getOutlineLevel())
                    ci.setHidden(false);
            }
        }


        setColumn(columnInfo.getLastColumn() + 1, null, null, null, null, Boolean.FALSE);
    }

    public void setColumn(int targetColumnIx, Short xfIndex, Integer width,
                          Integer level, Boolean hidden, Boolean collapsed) {
        ColumnInfoRecord ci = null;
        int k = 0;

        for (k = 0; k < records.size(); k++) {
            ColumnInfoRecord tci = (ColumnInfoRecord) records.get(k);
            if (tci.containsColumn(targetColumnIx)) {
                ci = tci;
                break;
            }
            if (tci.getFirstColumn() > targetColumnIx) {

                break;
            }
        }

        if (ci == null) {

            ColumnInfoRecord nci = new ColumnInfoRecord();

            nci.setFirstColumn(targetColumnIx);
            nci.setLastColumn(targetColumnIx);
            setColumnInfoFields(nci, xfIndex, width, level, hidden, collapsed);
            insertColumn(k, nci);
            attemptMergeColInfoRecords(k);
            return;
        }

        boolean styleChanged = xfIndex != null && ci.getXFIndex() != xfIndex.shortValue();
        boolean widthChanged = width != null && ci.getColumnWidth() != width.shortValue();
        boolean levelChanged = level != null && ci.getOutlineLevel() != level.intValue();
        boolean hiddenChanged = hidden != null && ci.getHidden() != hidden.booleanValue();
        boolean collapsedChanged = collapsed != null && ci.getCollapsed() != collapsed.booleanValue();

        boolean columnChanged = styleChanged || widthChanged || levelChanged || hiddenChanged || collapsedChanged;
        if (!columnChanged) {

            return;
        }

        if (ci.getFirstColumn() == targetColumnIx && ci.getLastColumn() == targetColumnIx) {

            setColumnInfoFields(ci, xfIndex, width, level, hidden, collapsed);
            attemptMergeColInfoRecords(k);
            return;
        }

        if (ci.getFirstColumn() == targetColumnIx || ci.getLastColumn() == targetColumnIx) {


            if (ci.getFirstColumn() == targetColumnIx) {
                ci.setFirstColumn(targetColumnIx + 1);
            } else {
                ci.setLastColumn(targetColumnIx - 1);
                k++;
            }
            ColumnInfoRecord nci = copyColInfo(ci);

            nci.setFirstColumn(targetColumnIx);
            nci.setLastColumn(targetColumnIx);
            setColumnInfoFields(nci, xfIndex, width, level, hidden, collapsed);

            insertColumn(k, nci);
            attemptMergeColInfoRecords(k);
        } else {

            ColumnInfoRecord ciStart = ci;
            ColumnInfoRecord ciMid = copyColInfo(ci);
            ColumnInfoRecord ciEnd = copyColInfo(ci);
            int lastcolumn = ci.getLastColumn();

            ciStart.setLastColumn(targetColumnIx - 1);

            ciMid.setFirstColumn(targetColumnIx);
            ciMid.setLastColumn(targetColumnIx);
            setColumnInfoFields(ciMid, xfIndex, width, level, hidden, collapsed);
            insertColumn(++k, ciMid);

            ciEnd.setFirstColumn(targetColumnIx + 1);
            ciEnd.setLastColumn(lastcolumn);
            insertColumn(++k, ciEnd);


        }
    }

    private int findColInfoIdx(int columnIx, int fromColInfoIdx) {
        if (columnIx < 0) {
            throw new IllegalArgumentException("column parameter out of range: " + columnIx);
        }
        if (fromColInfoIdx < 0) {
            throw new IllegalArgumentException("fromIdx parameter out of range: " + fromColInfoIdx);
        }

        for (int k = fromColInfoIdx; k < records.size(); k++) {
            ColumnInfoRecord ci = getColInfo(k);
            if (ci.containsColumn(columnIx)) {
                return k;
            }
            if (ci.getFirstColumn() > columnIx) {
                break;
            }
        }
        return -1;
    }


    private void attemptMergeColInfoRecords(int colInfoIx) {
        int nRecords = records.size();
        if (colInfoIx < 0 || colInfoIx >= nRecords) {
            throw new IllegalArgumentException("colInfoIx " + colInfoIx
                    + " is out of range (0.." + (nRecords - 1) + ")");
        }
        ColumnInfoRecord currentCol = getColInfo(colInfoIx);
        int nextIx = colInfoIx + 1;
        if (nextIx < nRecords) {
            if (mergeColInfoRecords(currentCol, getColInfo(nextIx))) {
                records.remove(nextIx);
            }
        }
        if (colInfoIx > 0) {
            if (mergeColInfoRecords(getColInfo(colInfoIx - 1), currentCol)) {
                records.remove(colInfoIx);
            }
        }
    }


    public void groupColumnRange(int fromColumnIx, int toColumnIx, boolean indent) {

        int colInfoSearchStartIdx = 0;
        for (int i = fromColumnIx; i <= toColumnIx; i++) {
            int level = 1;
            int colInfoIdx = findColInfoIdx(i, colInfoSearchStartIdx);
            if (colInfoIdx != -1) {
                level = getColInfo(colInfoIdx).getOutlineLevel();
                if (indent) {
                    level++;
                } else {
                    level--;
                }
                level = Math.max(0, level);
                level = Math.min(7, level);
                colInfoSearchStartIdx = Math.max(0, colInfoIdx - 1);
            }
            setColumn(i, null, null, Integer.valueOf(level), null, null);
        }
    }


    public ColumnInfoRecord findColumnInfo(int columnIndex) {
        int nInfos = records.size();
        for (int i = 0; i < nInfos; i++) {
            ColumnInfoRecord ci = getColInfo(i);
            if (ci.containsColumn(columnIndex)) {
                return ci;
            }
        }
        return null;
    }

    public int getMaxOutlineLevel() {
        int result = 0;
        int count = records.size();
        for (int i = 0; i < count; i++) {
            ColumnInfoRecord columnInfoRecord = getColInfo(i);
            result = Math.max(columnInfoRecord.getOutlineLevel(), result);
        }
        return result;
    }

    private static final class CIRComparator implements Comparator {
        public static final Comparator instance = new CIRComparator();

        private CIRComparator() {

        }

        public static int compareColInfos(ColumnInfoRecord a, ColumnInfoRecord b) {
            return a.getFirstColumn() - b.getFirstColumn();
        }

        public int compare(Object a, Object b) {
            return compareColInfos((ColumnInfoRecord) a, (ColumnInfoRecord) b);
        }
    }
}
