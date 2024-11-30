

package com.document.render.office.fc.hssf.record.cf;

import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;

import java.util.ArrayList;
import java.util.List;



public final class CellRangeUtil {

    public static final int NO_INTERSECTION = 1;
    public static final int OVERLAP = 2;

    public static final int INSIDE = 3;

    public static final int ENCLOSES = 4;
    private CellRangeUtil() {

    }


    public static int intersect(HSSFCellRangeAddress crA, HSSFCellRangeAddress crB) {

        int firstRow = crB.getFirstRow();
        int lastRow = crB.getLastRow();
        int firstCol = crB.getFirstColumn();
        int lastCol = crB.getLastColumn();

        if
        (
                gt(crA.getFirstRow(), lastRow) ||
                        lt(crA.getLastRow(), firstRow) ||
                        gt(crA.getFirstColumn(), lastCol) ||
                        lt(crA.getLastColumn(), firstCol)
        ) {
            return NO_INTERSECTION;
        } else if (contains(crA, crB)) {
            return INSIDE;
        } else if (contains(crB, crA)) {
            return ENCLOSES;
        } else {
            return OVERLAP;
        }

    }


    public static HSSFCellRangeAddress[] mergeCellRanges(HSSFCellRangeAddress[] cellRanges) {
        if (cellRanges.length < 1) {
            return cellRanges;
        }

        List<HSSFCellRangeAddress> lst = new ArrayList<HSSFCellRangeAddress>();
        for (HSSFCellRangeAddress cr : cellRanges) lst.add(cr);
        List temp = mergeCellRanges(lst);
        return toArray(temp);
    }

    private static List mergeCellRanges(List cellRangeList) {

        while (cellRangeList.size() > 1) {
            boolean somethingGotMerged = false;

            for (int i = 0; i < cellRangeList.size(); i++) {
                HSSFCellRangeAddress range1 = (HSSFCellRangeAddress) cellRangeList.get(i);
                for (int j = i + 1; j < cellRangeList.size(); j++) {
                    HSSFCellRangeAddress range2 = (HSSFCellRangeAddress) cellRangeList.get(j);

                    HSSFCellRangeAddress[] mergeResult = mergeRanges(range1, range2);
                    if (mergeResult == null) {
                        continue;
                    }
                    somethingGotMerged = true;

                    cellRangeList.set(i, mergeResult[0]);

                    cellRangeList.remove(j--);

                    for (int k = 1; k < mergeResult.length; k++) {
                        j++;
                        cellRangeList.add(j, mergeResult[k]);
                    }
                }
            }
            if (!somethingGotMerged) {
                break;
            }
        }


        return cellRangeList;
    }


    private static HSSFCellRangeAddress[] mergeRanges(HSSFCellRangeAddress range1, HSSFCellRangeAddress range2) {

        int x = intersect(range1, range2);
        switch (x) {
            case CellRangeUtil.NO_INTERSECTION:
                if (hasExactSharedBorder(range1, range2)) {
                    return new HSSFCellRangeAddress[]{createEnclosingCellRange(range1, range2),};
                }

                return null;
            case CellRangeUtil.OVERLAP:
                return resolveRangeOverlap(range1, range2);
            case CellRangeUtil.INSIDE:

                return new HSSFCellRangeAddress[]{range1,};
            case CellRangeUtil.ENCLOSES:

                return new HSSFCellRangeAddress[]{range2,};
        }
        throw new RuntimeException("unexpected intersection result (" + x + ")");
    }


    static HSSFCellRangeAddress[] resolveRangeOverlap(HSSFCellRangeAddress rangeA, HSSFCellRangeAddress rangeB) {

        if (rangeA.isFullColumnRange()) {
            if (rangeA.isFullRowRange()) {

                return null;
            }
            return sliceUp(rangeA, rangeB);
        }
        if (rangeA.isFullRowRange()) {
            if (rangeB.isFullColumnRange()) {

                return null;
            }
            return sliceUp(rangeA, rangeB);
        }
        if (rangeB.isFullColumnRange()) {
            return sliceUp(rangeB, rangeA);
        }
        if (rangeB.isFullRowRange()) {
            return sliceUp(rangeB, rangeA);
        }
        return sliceUp(rangeA, rangeB);
    }


    private static HSSFCellRangeAddress[] sliceUp(HSSFCellRangeAddress crA, HSSFCellRangeAddress crB) {

        List temp = new ArrayList();


        temp.add(crB);
        if (!crA.isFullColumnRange()) {
            temp = cutHorizontally(crA.getFirstRow(), temp);
            temp = cutHorizontally(crA.getLastRow() + 1, temp);
        }
        if (!crA.isFullRowRange()) {
            temp = cutVertically(crA.getFirstColumn(), temp);
            temp = cutVertically(crA.getLastColumn() + 1, temp);
        }
        HSSFCellRangeAddress[] crParts = toArray(temp);


        temp.clear();
        temp.add(crA);

        for (int i = 0; i < crParts.length; i++) {
            HSSFCellRangeAddress crPart = crParts[i];

            if (intersect(crA, crPart) != ENCLOSES) {
                temp.add(crPart);
            }
        }
        return toArray(temp);
    }

    private static List cutHorizontally(int cutRow, List input) {

        List result = new ArrayList();
        HSSFCellRangeAddress[] crs = toArray(input);
        for (int i = 0; i < crs.length; i++) {
            HSSFCellRangeAddress cr = crs[i];
            if (cr.getFirstRow() < cutRow && cutRow < cr.getLastRow()) {
                result.add(new HSSFCellRangeAddress(cr.getFirstRow(), cutRow, cr.getFirstColumn(), cr.getLastColumn()));
                result.add(new HSSFCellRangeAddress(cutRow + 1, cr.getLastRow(), cr.getFirstColumn(), cr.getLastColumn()));
            } else {
                result.add(cr);
            }
        }
        return result;
    }

    private static List cutVertically(int cutColumn, List input) {

        List result = new ArrayList();
        HSSFCellRangeAddress[] crs = toArray(input);
        for (int i = 0; i < crs.length; i++) {
            HSSFCellRangeAddress cr = crs[i];
            if (cr.getFirstColumn() < cutColumn && cutColumn < cr.getLastColumn()) {
                result.add(new HSSFCellRangeAddress(cr.getFirstRow(), cr.getLastRow(), cr.getFirstColumn(), cutColumn));
                result.add(new HSSFCellRangeAddress(cr.getFirstRow(), cr.getLastRow(), cutColumn + 1, cr.getLastColumn()));
            } else {
                result.add(cr);
            }
        }
        return result;
    }


    private static HSSFCellRangeAddress[] toArray(List temp) {
        HSSFCellRangeAddress[] result = new HSSFCellRangeAddress[temp.size()];
        temp.toArray(result);
        return result;
    }



    public static boolean contains(HSSFCellRangeAddress crA, HSSFCellRangeAddress crB) {
        int firstRow = crB.getFirstRow();
        int lastRow = crB.getLastRow();
        int firstCol = crB.getFirstColumn();
        int lastCol = crB.getLastColumn();
        return le(crA.getFirstRow(), firstRow) && ge(crA.getLastRow(), lastRow)
                && le(crA.getFirstColumn(), firstCol) && ge(crA.getLastColumn(), lastCol);
    }


    public static boolean hasExactSharedBorder(HSSFCellRangeAddress crA, HSSFCellRangeAddress crB) {
        int oFirstRow = crB.getFirstRow();
        int oLastRow = crB.getLastRow();
        int oFirstCol = crB.getFirstColumn();
        int oLastCol = crB.getLastColumn();

        if (crA.getFirstRow() > 0 && crA.getFirstRow() - 1 == oLastRow ||
                oFirstRow > 0 && oFirstRow - 1 == crA.getLastRow()) {


            return crA.getFirstColumn() == oFirstCol && crA.getLastColumn() == oLastCol;
        }

        if (crA.getFirstColumn() > 0 && crA.getFirstColumn() - 1 == oLastCol ||
                oFirstCol > 0 && crA.getLastColumn() == oFirstCol - 1) {


            return crA.getFirstRow() == oFirstRow && crA.getLastRow() == oLastRow;
        }
        return false;
    }


    public static HSSFCellRangeAddress createEnclosingCellRange(HSSFCellRangeAddress crA, HSSFCellRangeAddress crB) {
        if (crB == null) {
            return crA.copy();
        }

        return
                new HSSFCellRangeAddress(
                        lt(crB.getFirstRow(), crA.getFirstRow()) ? crB.getFirstRow() : crA.getFirstRow(),
                        gt(crB.getLastRow(), crA.getLastRow()) ? crB.getLastRow() : crA.getLastRow(),
                        lt(crB.getFirstColumn(), crA.getFirstColumn()) ? crB.getFirstColumn() : crA.getFirstColumn(),
                        gt(crB.getLastColumn(), crA.getLastColumn()) ? crB.getLastColumn() : crA.getLastColumn()
                );

    }


    private static boolean lt(int a, int b) {
        return a == -1 ? false : (b == -1 ? true : a < b);
    }


    private static boolean le(int a, int b) {
        return a == b || lt(a, b);
    }


    private static boolean gt(int a, int b) {
        return lt(b, a);
    }


    private static boolean ge(int a, int b) {
        return !lt(a, b);
    }
}
