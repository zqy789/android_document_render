



package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.hssf.model.InternalWorkbook;
import com.document.render.office.fc.hssf.record.FormatRecord;
import com.document.render.office.fc.ss.usermodel.BuiltinFormats;
import com.document.render.office.fc.ss.usermodel.DataFormat;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;



public final class HSSFDataFormat implements DataFormat {
    private static final String[] _builtinFormats = BuiltinFormats.getAll();

    private final Vector<String> _formats = new Vector<String>();
    private final InternalWorkbook _workbook;
    private boolean _movedBuiltins = false;





    HSSFDataFormat(InternalWorkbook workbook) {
        _workbook = workbook;

        @SuppressWarnings("unchecked")
        Iterator<FormatRecord> i = workbook.getFormats().iterator();
        while (i.hasNext()) {
            FormatRecord r = i.next();
            ensureFormatsSize(r.getIndexCode());
            _formats.set(r.getIndexCode(), r.getFormatString());
        }
    }

    public static List<String> getBuiltinFormats() {
        return Arrays.asList(_builtinFormats);
    }


    public static short getBuiltinFormat(String format) {
        return (short) BuiltinFormats.getBuiltinFormat(format);
    }

    public static String getFormatCode(InternalWorkbook workbook, short index) {
        if (index == -1) {


            return null;
        }

        @SuppressWarnings("unchecked")
        Iterator<FormatRecord> i = workbook.getFormats().iterator();
        while (i.hasNext()) {
            FormatRecord r = i.next();
            if (index == r.getIndexCode()) {
                return r.getFormatString();
            }
        }

        if (_builtinFormats.length > index && _builtinFormats[index] != null) {

            return _builtinFormats[index];
        }

        return null;

    }


    public static String getBuiltinFormat(short index) {
        return BuiltinFormats.getBuiltinFormat(index);
    }


    public static int getNumberOfBuiltinBuiltinFormats() {
        return _builtinFormats.length;
    }


    public short getFormat(String pFormat) {

        String format;
        if (pFormat.toUpperCase().equals("TEXT")) {
            format = "@";
        } else {
            format = pFormat;
        }


        if (!_movedBuiltins) {
            for (int i = 0; i < _builtinFormats.length; i++) {
                ensureFormatsSize(i);
                if (_formats.get(i) == null) {
                    _formats.set(i, _builtinFormats[i]);
                } else {

                }
            }
            _movedBuiltins = true;
        }


        for (int i = 0; i < _formats.size(); i++) {
            if (format.equals(_formats.get(i))) {
                return (short) i;
            }
        }


        short index = _workbook.getFormat(format, true);
        ensureFormatsSize(index);
        _formats.set(index, format);
        return index;
    }


    public String getFormat(short index) {
        if (_movedBuiltins) {
            return _formats.get(index);
        }

        if (index == -1) {


            return null;
        }

        String fmt = _formats.size() > index ? _formats.get(index) : null;
        if (_builtinFormats.length > index && _builtinFormats[index] != null) {

            if (fmt != null) {

                return fmt;
            } else {

                return _builtinFormats[index];
            }
        }
        return fmt;
    }


    private void ensureFormatsSize(int index) {
        if (_formats.size() <= index) {
            _formats.setSize(index + 1);
        }
    }
}
