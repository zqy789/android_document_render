
package com.document.render.office.fc.hssf.eventusermodel;

import com.document.render.office.fc.hssf.record.CellValueRecordInterface;
import com.document.render.office.fc.hssf.record.ExtendedFormatRecord;
import com.document.render.office.fc.hssf.record.FormatRecord;
import com.document.render.office.fc.hssf.record.FormulaRecord;
import com.document.render.office.fc.hssf.record.NumberRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.usermodel.HSSFDataFormat;
import com.document.render.office.fc.hssf.usermodel.HSSFDataFormatter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;



public class FormatTrackingHSSFListener implements HSSFListener {
    private final HSSFListener _childListener;
    private final HSSFDataFormatter _formatter;
    private final NumberFormat _defaultFormat;
    private final Map<Integer, FormatRecord> _customFormatRecords = new Hashtable<Integer, FormatRecord>();
    private final List<ExtendedFormatRecord> _xfRecords = new ArrayList<ExtendedFormatRecord>();


    public FormatTrackingHSSFListener(HSSFListener childListener) {
        this(childListener, Locale.getDefault());
    }


    public FormatTrackingHSSFListener(
            HSSFListener childListener, Locale locale) {
        _childListener = childListener;
        _formatter = new HSSFDataFormatter(locale);
        _defaultFormat = NumberFormat.getInstance(locale);
    }

    protected int getNumberOfCustomFormats() {
        return _customFormatRecords.size();
    }

    protected int getNumberOfExtendedFormats() {
        return _xfRecords.size();
    }


    public void processRecord(Record record) {

        processRecordInternally(record);


        _childListener.processRecord(record);
    }


    public void processRecordInternally(Record record) {
        if (record instanceof FormatRecord) {
            FormatRecord fr = (FormatRecord) record;
            _customFormatRecords.put(Integer.valueOf(fr.getIndexCode()), fr);
        }
        if (record instanceof ExtendedFormatRecord) {
            ExtendedFormatRecord xr = (ExtendedFormatRecord) record;
            _xfRecords.add(xr);
        }
    }


    public String formatNumberDateCell(CellValueRecordInterface cell) {
        double value;
        if (cell instanceof NumberRecord) {
            value = ((NumberRecord) cell).getValue();
        } else if (cell instanceof FormulaRecord) {
            value = ((FormulaRecord) cell).getValue();
        } else {
            throw new IllegalArgumentException("Unsupported CellValue Record passed in " + cell);
        }


        int formatIndex = getFormatIndex(cell);
        String formatString = getFormatString(cell);

        if (formatString == null) {
            return _defaultFormat.format(value);
        }


        return _formatter.formatRawCellContents(value, formatIndex, formatString);
    }


    public String getFormatString(int formatIndex) {
        String format = null;
        if (formatIndex >= HSSFDataFormat.getNumberOfBuiltinBuiltinFormats()) {
            FormatRecord tfr = _customFormatRecords.get(Integer.valueOf(formatIndex));
            if (tfr == null) {
                System.err.println("Requested format at index " + formatIndex
                        + ", but it wasn't found");
            } else {
                format = tfr.getFormatString();
            }
        } else {
            format = HSSFDataFormat.getBuiltinFormat((short) formatIndex);
        }
        return format;
    }


    public String getFormatString(CellValueRecordInterface cell) {
        int formatIndex = getFormatIndex(cell);
        if (formatIndex == -1) {

            return null;
        }
        return getFormatString(formatIndex);
    }


    public int getFormatIndex(CellValueRecordInterface cell) {
        ExtendedFormatRecord xfr = _xfRecords.get(cell.getXFIndex());
        if (xfr == null) {
            System.err.println("Cell " + cell.getRow() + "," + cell.getColumn()
                    + " uses XF with index " + cell.getXFIndex() + ", but we don't have that");
            return -1;
        }
        return xfr.getFormatIndex();
    }
}
