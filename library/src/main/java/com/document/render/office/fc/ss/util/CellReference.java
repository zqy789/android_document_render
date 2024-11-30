

package com.document.render.office.fc.ss.util;

import com.document.render.office.fc.hssf.formula.SheetNameFormatter;
import com.document.render.office.fc.ss.SpreadsheetVersion;
import com.document.render.office.fc.ss.usermodel.ICell;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class CellReference {

    private static final char ABSOLUTE_REFERENCE_MARKER = '$';

    private static final char SHEET_NAME_DELIMITER = '!';

    private static final char SPECIAL_NAME_DELIMITER = '\'';

    private static final Pattern CELL_REF_PATTERN = Pattern.compile("\\$?([A-Za-z]+)\\$?([0-9]+)");

    private static final Pattern COLUMN_REF_PATTERN = Pattern.compile("\\$?([A-Za-z]+)");

    private static final Pattern ROW_REF_PATTERN = Pattern.compile("\\$?([0-9]+)");

    private static final Pattern NAMED_RANGE_NAME_PATTERN = Pattern.compile("[_A-Za-z][_.A-Za-z0-9]*");
    private final int _rowIndex;




    private final int _colIndex;
    private final String _sheetName;
    private final boolean _isRowAbs;
    private final boolean _isColAbs;

    public CellReference(String cellRef) {
        if (cellRef.endsWith("#REF!")) {
            throw new IllegalArgumentException("Cell reference invalid: " + cellRef);
        }

        String[] parts = separateRefParts(cellRef);
        _sheetName = parts[0];
        String colRef = parts[1];
        if (colRef.length() < 1) {
            throw new IllegalArgumentException("Invalid Formula cell reference: '" + cellRef + "'");
        }
        _isColAbs = colRef.charAt(0) == '$';
        if (_isColAbs) {
            colRef = colRef.substring(1);
        }
        _colIndex = convertColStringToIndex(colRef);

        String rowRef = parts[2];
        if (rowRef.length() < 1) {
            throw new IllegalArgumentException("Invalid Formula cell reference: '" + cellRef + "'");
        }
        _isRowAbs = rowRef.charAt(0) == '$';
        if (_isRowAbs) {
            rowRef = rowRef.substring(1);
        }
        _rowIndex = Integer.parseInt(rowRef) - 1;
    }

    public CellReference(int pRow, int pCol) {
        this(pRow, pCol, false, false);
    }

    public CellReference(int pRow, short pCol) {
        this(pRow, pCol & 0xFFFF, false, false);
    }

    public CellReference(ICell cell) {
        this(cell.getRowIndex(), cell.getColumnIndex(), false, false);
    }

    public CellReference(int pRow, int pCol, boolean pAbsRow, boolean pAbsCol) {
        this(null, pRow, pCol, pAbsRow, pAbsCol);
    }

    public CellReference(String pSheetName, int pRow, int pCol, boolean pAbsRow, boolean pAbsCol) {


        if (pRow < -1) {
            throw new IllegalArgumentException("row index may not be negative");
        }
        if (pCol < -1) {
            throw new IllegalArgumentException("column index may not be negative");
        }
        _sheetName = pSheetName;
        _rowIndex = pRow;
        _colIndex = pCol;
        _isRowAbs = pAbsRow;
        _isColAbs = pAbsCol;
    }

    public static boolean isPartAbsolute(String part) {
        return part.charAt(0) == ABSOLUTE_REFERENCE_MARKER;
    }


    public static int convertColStringToIndex(String ref) {

        int pos = 0;
        int retval = 0;
        for (int k = ref.length() - 1; k >= 0; k--) {
            char thechar = ref.charAt(k);
            if (thechar == ABSOLUTE_REFERENCE_MARKER) {
                if (k != 0) {
                    throw new IllegalArgumentException("Bad col ref format '" + ref + "'");
                }
                break;
            }


            int shift = (int) Math.pow(26, pos);
            retval += (Character.getNumericValue(thechar) - 9) * shift;
            pos++;
        }
        return retval - 1;
    }


    public static NameType classifyCellReference(String str, SpreadsheetVersion ssVersion) {
        int len = str.length();
        if (len < 1) {
            throw new IllegalArgumentException("Empty string not allowed");
        }
        char firstChar = str.charAt(0);
        switch (firstChar) {
            case ABSOLUTE_REFERENCE_MARKER:
            case '.':
            case '_':
                break;
            default:
                if (!Character.isLetter(firstChar) && !Character.isDigit(firstChar)) {
                    throw new IllegalArgumentException("Invalid first char (" + firstChar
                            + ") of cell reference or named range.  Letter expected");
                }
        }
        if (!Character.isDigit(str.charAt(len - 1))) {

            return validateNamedRangeName(str, ssVersion);
        }
        Matcher cellRefPatternMatcher = CELL_REF_PATTERN.matcher(str);
        if (!cellRefPatternMatcher.matches()) {
            return validateNamedRangeName(str, ssVersion);
        }
        String lettersGroup = cellRefPatternMatcher.group(1);
        String digitsGroup = cellRefPatternMatcher.group(2);
        if (cellReferenceIsWithinRange(lettersGroup, digitsGroup, ssVersion)) {

            return NameType.CELL;
        }





        if (str.indexOf(ABSOLUTE_REFERENCE_MARKER) >= 0) {

            return NameType.BAD_CELL_OR_NAMED_RANGE;
        }
        return NameType.NAMED_RANGE;
    }

    private static NameType validateNamedRangeName(String str, SpreadsheetVersion ssVersion) {
        Matcher colMatcher = COLUMN_REF_PATTERN.matcher(str);
        if (colMatcher.matches()) {
            String colStr = colMatcher.group(1);
            if (isColumnWithnRange(colStr, ssVersion)) {
                return NameType.COLUMN;
            }
        }
        Matcher rowMatcher = ROW_REF_PATTERN.matcher(str);
        if (rowMatcher.matches()) {
            String rowStr = rowMatcher.group(1);
            if (isRowWithnRange(rowStr, ssVersion)) {
                return NameType.ROW;
            }
        }
        if (!NAMED_RANGE_NAME_PATTERN.matcher(str).matches()) {
            return NameType.BAD_CELL_OR_NAMED_RANGE;
        }
        return NameType.NAMED_RANGE;
    }


    public static boolean cellReferenceIsWithinRange(String colStr, String rowStr, SpreadsheetVersion ssVersion) {
        if (!isColumnWithnRange(colStr, ssVersion)) {
            return false;
        }
        return isRowWithnRange(rowStr, ssVersion);
    }

    public static boolean isColumnWithnRange(String colStr, SpreadsheetVersion ssVersion) {
        String lastCol = ssVersion.getLastColumnName();
        int lastColLength = lastCol.length();

        int numberOfLetters = colStr.length();
        if (numberOfLetters > lastColLength) {

            return false;
        }
        if (numberOfLetters == lastColLength) {
            if (colStr.toUpperCase().compareTo(lastCol) > 0) {
                return false;
            }
        } else {


        }
        return true;
    }

    public static boolean isRowWithnRange(String rowStr, SpreadsheetVersion ssVersion) {
        int rowNum = Integer.parseInt(rowStr);

        if (rowNum < 0) {
            throw new IllegalStateException("Invalid rowStr '" + rowStr + "'.");
        }
        if (rowNum == 0) {


            return false;
        }
        return rowNum <= ssVersion.getMaxRows();
    }


    private static String[] separateRefParts(String reference) {
        int plingPos = reference.lastIndexOf(SHEET_NAME_DELIMITER);
        String sheetName = parseSheetName(reference, plingPos);
        int start = plingPos + 1;

        int length = reference.length();


        int loc = start;

        if (reference.charAt(loc) == ABSOLUTE_REFERENCE_MARKER) {
            loc++;
        }

        for (; loc < length; loc++) {
            char ch = reference.charAt(loc);
            if (Character.isDigit(ch) || ch == ABSOLUTE_REFERENCE_MARKER) {
                break;
            }
        }
        return new String[]{
                sheetName,
                reference.substring(start, loc),
                reference.substring(loc),
        };
    }

    private static String parseSheetName(String reference, int indexOfSheetNameDelimiter) {
        if (indexOfSheetNameDelimiter < 0) {
            return null;
        }

        boolean isQuoted = reference.charAt(0) == SPECIAL_NAME_DELIMITER;
        if (!isQuoted) {
            return reference.substring(0, indexOfSheetNameDelimiter);
        }
        int lastQuotePos = indexOfSheetNameDelimiter - 1;
        if (reference.charAt(lastQuotePos) != SPECIAL_NAME_DELIMITER) {
            throw new RuntimeException("Mismatched quotes: (" + reference + ")");
        }








        StringBuffer sb = new StringBuffer(indexOfSheetNameDelimiter);

        for (int i = 1; i < lastQuotePos; i++) {
            char ch = reference.charAt(i);
            if (ch != SPECIAL_NAME_DELIMITER) {
                sb.append(ch);
                continue;
            }
            if (i < lastQuotePos) {
                if (reference.charAt(i + 1) == SPECIAL_NAME_DELIMITER) {

                    i++;
                    sb.append(ch);
                    continue;
                }
            }
            throw new RuntimeException("Bad sheet name quote escaping: (" + reference + ")");
        }
        return sb.toString();
    }


    public static String convertNumToColString(int col) {


        int excelColNum = col + 1;

        String colRef = "";
        int colRemain = excelColNum;

        while (colRemain > 0) {
            int thisPart = colRemain % 26;
            if (thisPart == 0) {
                thisPart = 26;
            }
            colRemain = (colRemain - thisPart) / 26;


            char colChar = (char) (thisPart + 64);
            colRef = colChar + colRef;
        }

        return colRef;
    }

    public int getRow() {
        return _rowIndex;
    }

    public short getCol() {
        return (short) _colIndex;
    }

    public boolean isRowAbsolute() {
        return _isRowAbs;
    }

    public boolean isColAbsolute() {
        return _isColAbs;
    }


    public String getSheetName() {
        return _sheetName;
    }


    public String formatAsString() {
        StringBuffer sb = new StringBuffer(32);
        if (_sheetName != null) {
            SheetNameFormatter.appendFormat(sb, _sheetName);
            sb.append(SHEET_NAME_DELIMITER);
        }
        appendCellReference(sb);
        return sb.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName()).append(" [");
        sb.append(formatAsString());
        sb.append("]");
        return sb.toString();
    }


    public String[] getCellRefParts() {
        return new String[]{
                _sheetName,
                Integer.toString(_rowIndex + 1),
                convertNumToColString(_colIndex)
        };
    }


     void appendCellReference(StringBuffer sb) {
        if (_isColAbs) {
            sb.append(ABSOLUTE_REFERENCE_MARKER);
        }
        sb.append(convertNumToColString(_colIndex));
        if (_isRowAbs) {
            sb.append(ABSOLUTE_REFERENCE_MARKER);
        }
        sb.append(_rowIndex + 1);
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CellReference)) {
            return false;
        }
        CellReference cr = (CellReference) o;
        return _rowIndex == cr._rowIndex
                && _colIndex == cr._colIndex
                && _isRowAbs == cr._isColAbs
                && _isColAbs == cr._isColAbs;
    }


    public enum NameType {
        CELL,
        NAMED_RANGE,
        COLUMN,
        ROW,
        BAD_CELL_OR_NAMED_RANGE;
    }
}
