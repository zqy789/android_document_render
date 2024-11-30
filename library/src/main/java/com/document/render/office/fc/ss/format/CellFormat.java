

package com.document.render.office.fc.ss.format;

import com.document.render.office.fc.ss.usermodel.ICell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



@SuppressWarnings({"Singleton"})
public class CellFormat {

    public static final CellFormat GENERAL_FORMAT = new CellFormat("General") {
        @Override
        public CellFormatResult apply(Object value) {
            String text;
            if (value == null) {
                text = "";
            } else if (value instanceof Number) {
                text = CellNumberFormatter.SIMPLE_NUMBER.format(value);
            } else {
                text = value.toString();
            }
            return new CellFormatResult(true, text, -1);
        }
    };
    private static final Pattern ONE_PART = Pattern.compile(CellFormatPart.FORMAT_PAT.pattern()
            + "(;|$)", Pattern.COMMENTS | Pattern.CASE_INSENSITIVE);
    private static final CellFormatPart DEFAULT_TEXT_FORMAT = new CellFormatPart("@");

    private static final Map<String, CellFormat> formatCache = new WeakHashMap<String, CellFormat>();
    private final String format;
    private final CellFormatPart posNumFmt;
    private final CellFormatPart zeroNumFmt;
    private final CellFormatPart negNumFmt;
    private final CellFormatPart textFmt;


    private CellFormat(String format) {
        this.format = format;
        Matcher m = ONE_PART.matcher(format);
        List<CellFormatPart> parts = new ArrayList<CellFormatPart>();

        while (m.find()) {
            try {
                String valueDesc = m.group();


                if (valueDesc.endsWith(";"))
                    valueDesc = valueDesc.substring(0, valueDesc.length() - 1);

                parts.add(new CellFormatPart(valueDesc));
            } catch (RuntimeException e) {
                CellFormatter.logger.log(Level.WARNING,
                        "Invalid format: " + CellFormatter.quote(m.group()), e);
                parts.add(null);
            }
        }

        switch (parts.size()) {
            case 1:
                posNumFmt = zeroNumFmt = negNumFmt = parts.get(0);
                textFmt = DEFAULT_TEXT_FORMAT;
                break;
            case 2:
                posNumFmt = zeroNumFmt = parts.get(0);
                negNumFmt = parts.get(1);
                textFmt = DEFAULT_TEXT_FORMAT;
                break;
            case 3:
                posNumFmt = parts.get(0);
                zeroNumFmt = parts.get(1);
                negNumFmt = parts.get(2);
                textFmt = DEFAULT_TEXT_FORMAT;
                break;
            case 4:
            default:
                posNumFmt = parts.get(0);
                zeroNumFmt = parts.get(1);
                negNumFmt = parts.get(2);
                textFmt = parts.get(3);
                break;
        }
    }


    public static CellFormat getInstance(String format) {
        CellFormat fmt = formatCache.get(format);
        if (fmt == null) {
            if (format.equals("General"))
                fmt = GENERAL_FORMAT;
            else
                fmt = new CellFormat(format);
            formatCache.put(format, fmt);
        }
        return fmt;
    }


    public static int ultimateType(ICell cell) {
        int type = cell.getCellType();
        if (type == ICell.CELL_TYPE_FORMULA)
            return cell.getCachedFormulaResultType();
        else
            return type;
    }


    public CellFormatResult apply(Object value) {
        if (value instanceof Number) {
            Number num = (Number) value;
            double val = num.doubleValue();
            if (val > 0)
                return posNumFmt.apply(value);
            else if (val < 0)
                return negNumFmt.apply(-val);
            else
                return zeroNumFmt.apply(value);
        } else {
            return textFmt.apply(value);
        }
    }








    public CellFormatResult apply(ICell c) {
        switch (ultimateType(c)) {
            case ICell.CELL_TYPE_BLANK:
                return apply("");
            case ICell.CELL_TYPE_BOOLEAN:
                return apply(Boolean.toString(c.getBooleanCellValue()));
            case ICell.CELL_TYPE_NUMERIC:
                return apply(c.getNumericCellValue());
            case ICell.CELL_TYPE_STRING:
                return apply(c.getStringCellValue());
            default:
                return apply("?");
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof CellFormat) {
            CellFormat that = (CellFormat) obj;
            return format.equals(that.format);
        }
        return false;
    }


    @Override
    public int hashCode() {
        return format.hashCode();
    }
}
