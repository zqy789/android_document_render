

package com.document.render.office.fc.ss.util;

import com.document.render.office.fc.ss.usermodel.ICell;
import com.document.render.office.fc.ss.usermodel.ICellStyle;
import com.document.render.office.fc.ss.usermodel.IFont;
import com.document.render.office.fc.ss.usermodel.IRow;
import com.document.render.office.fc.ss.usermodel.Sheet;
import com.document.render.office.fc.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;



public final class CellUtil {

    public static final String ALIGNMENT = "alignment";
    public static final String BORDER_BOTTOM = "borderBottom";
    public static final String BORDER_LEFT = "borderLeft";
    public static final String BORDER_RIGHT = "borderRight";
    public static final String BORDER_TOP = "borderTop";
    public static final String BOTTOM_BORDER_COLOR = "bottomBorderColor";
    public static final String DATA_FORMAT = "dataFormat";
    public static final String FILL_BACKGROUND_COLOR = "fillBackgroundColor";
    public static final String FILL_FOREGROUND_COLOR = "fillForegroundColor";
    public static final String FILL_PATTERN = "fillPattern";
    public static final String FONT = "font";
    public static final String HIDDEN = "hidden";
    public static final String INDENTION = "indention";
    public static final String LEFT_BORDER_COLOR = "leftBorderColor";
    public static final String LOCKED = "locked";
    public static final String RIGHT_BORDER_COLOR = "rightBorderColor";
    public static final String ROTATION = "rotation";
    public static final String TOP_BORDER_COLOR = "topBorderColor";
    public static final String VERTICAL_ALIGNMENT = "verticalAlignment";
    public static final String WRAP_TEXT = "wrapText";

    private static UnicodeMapping unicodeMappings[];

    static {
        unicodeMappings = new UnicodeMapping[]{
                um("alpha", "\u03B1"),
                um("beta", "\u03B2"),
                um("gamma", "\u03B3"),
                um("delta", "\u03B4"),
                um("epsilon", "\u03B5"),
                um("zeta", "\u03B6"),
                um("eta", "\u03B7"),
                um("theta", "\u03B8"),
                um("iota", "\u03B9"),
                um("kappa", "\u03BA"),
                um("lambda", "\u03BB"),
                um("mu", "\u03BC"),
                um("nu", "\u03BD"),
                um("xi", "\u03BE"),
                um("omicron", "\u03BF"),
        };
    }

    private CellUtil() {

    }


    public static IRow getRow(int rowIndex, Sheet sheet) {





        return null;
    }



    public static ICell getCell(IRow row, int columnIndex) {
        ICell cell = row.getCell(columnIndex);

        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        return cell;
    }



    public static ICell createCell(IRow row, int column, String value, ICellStyle style) {








        return null;
    }



    public static ICell createCell(IRow row, int column, String value) {
        return createCell(row, column, value, null);
    }



    public static void setAlignment(ICell cell, Workbook workbook, short align) {
        setCellStyleProperty(cell, workbook, ALIGNMENT, Short.valueOf(align));
    }


    public static void setFont(ICell cell, Workbook workbook, IFont font) {
        setCellStyleProperty(cell, workbook, FONT, font.getIndex());
    }


    public static void setCellStyleProperty(ICell cell, Workbook workbook, String propertyName,
                                            Object propertyValue) {

























    }


    private static Map<String, Object> getFormatProperties(ICellStyle style) {
        Map<String, Object> properties = new HashMap<String, Object>();
        putShort(properties, ALIGNMENT, style.getAlignment());
        putShort(properties, BORDER_BOTTOM, style.getBorderBottom());
        putShort(properties, BORDER_LEFT, style.getBorderLeft());
        putShort(properties, BORDER_RIGHT, style.getBorderRight());
        putShort(properties, BORDER_TOP, style.getBorderTop());
        putShort(properties, BOTTOM_BORDER_COLOR, style.getBottomBorderColor());
        putShort(properties, DATA_FORMAT, style.getDataFormat());
        putShort(properties, FILL_BACKGROUND_COLOR, style.getFillBackgroundColor());
        putShort(properties, FILL_FOREGROUND_COLOR, style.getFillForegroundColor());
        putShort(properties, FILL_PATTERN, style.getFillPattern());
        putShort(properties, FONT, style.getFontIndex());
        putBoolean(properties, HIDDEN, style.getHidden());
        putShort(properties, INDENTION, style.getIndention());
        putShort(properties, LEFT_BORDER_COLOR, style.getLeftBorderColor());
        putBoolean(properties, LOCKED, style.getLocked());
        putShort(properties, RIGHT_BORDER_COLOR, style.getRightBorderColor());
        putShort(properties, ROTATION, style.getRotation());
        putShort(properties, TOP_BORDER_COLOR, style.getTopBorderColor());
        putShort(properties, VERTICAL_ALIGNMENT, style.getVerticalAlignment());
        putBoolean(properties, WRAP_TEXT, style.getWrapText());
        return properties;
    }


    private static void setFormatProperties(ICellStyle style, Workbook workbook, Map<String, Object> properties) {




















    }


    private static short getShort(Map<String, Object> properties, String name) {
        Object value = properties.get(name);
        if (value instanceof Short) {
            return ((Short) value).shortValue();
        }
        return 0;
    }


    private static boolean getBoolean(Map<String, Object> properties, String name) {
        Object value = properties.get(name);
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        return false;
    }


    private static void putShort(Map<String, Object> properties, String name, short value) {
        properties.put(name, Short.valueOf(value));
    }


    private static void putBoolean(Map<String, Object> properties, String name, boolean value) {
        properties.put(name, Boolean.valueOf(value));
    }


    public static ICell translateUnicodeValues(ICell cell) {

        String s = cell.getRichStringCellValue().getString();
        boolean foundUnicode = false;
        String lowerCaseStr = s.toLowerCase();

        for (int i = 0; i < unicodeMappings.length; i++) {
            UnicodeMapping entry = unicodeMappings[i];
            String key = entry.entityName;
            if (lowerCaseStr.indexOf(key) != -1) {
                s = s.replaceAll(key, entry.resolvedValue);
                foundUnicode = true;
            }
        }
        if (foundUnicode) {


        }
        return cell;
    }

    private static UnicodeMapping um(String entityName, String resolvedValue) {
        return new UnicodeMapping(entityName, resolvedValue);
    }

    private static final class UnicodeMapping {

        public final String entityName;
        public final String resolvedValue;

        public UnicodeMapping(String pEntityName, String pResolvedValue) {
            entityName = "&" + pEntityName + ";";
            resolvedValue = pResolvedValue;
        }
    }
}
