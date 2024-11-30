

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;


public final class SupBookRecord extends StandardRecord {

    @Keep
    public final static short sid = 0x01AE;

    private static final short SMALL_RECORD_SIZE = 4;
    private static final short TAG_INTERNAL_REFERENCES = 0x0401;
    private static final short TAG_ADD_IN_FUNCTIONS = 0x3A01;

    private short field_1_number_of_sheets;
    private String field_2_encoded_url;
    private String[] field_3_sheet_names;
    private boolean _isAddInFunctions;


    private SupBookRecord(boolean isAddInFuncs, short numberOfSheets) {

        field_1_number_of_sheets = numberOfSheets;
        field_2_encoded_url = null;
        field_3_sheet_names = null;
        _isAddInFunctions = isAddInFuncs;
    }

    public SupBookRecord(String url, String[] sheetNames) {
        field_1_number_of_sheets = (short) sheetNames.length;
        field_2_encoded_url = url;
        field_3_sheet_names = sheetNames;
        _isAddInFunctions = false;
    }


    public SupBookRecord(RecordInputStream in) {
        int recLen = in.remaining();

        field_1_number_of_sheets = in.readShort();

        if (recLen > SMALL_RECORD_SIZE) {

            _isAddInFunctions = false;

            field_2_encoded_url = in.readString();
            String[] sheetNames = new String[field_1_number_of_sheets];
            for (int i = 0; i < sheetNames.length; i++) {
                sheetNames[i] = in.readString();
            }
            field_3_sheet_names = sheetNames;
            return;
        }

        field_2_encoded_url = null;
        field_3_sheet_names = null;

        short nextShort = in.readShort();
        if (nextShort == TAG_INTERNAL_REFERENCES) {

            _isAddInFunctions = false;
        } else if (nextShort == TAG_ADD_IN_FUNCTIONS) {

            _isAddInFunctions = true;
            if (field_1_number_of_sheets != 1) {
                throw new RuntimeException("Expected 0x0001 for number of sheets field in 'Add-In Functions' but got ("
                        + field_1_number_of_sheets + ")");
            }
        } else {
            throw new RuntimeException("invalid EXTERNALBOOK code ("
                    + Integer.toHexString(nextShort) + ")");
        }
    }

    public static SupBookRecord createInternalReferences(short numberOfSheets) {
        return new SupBookRecord(false, numberOfSheets);
    }

    public static SupBookRecord createAddInFunctions() {
        return new SupBookRecord(true, (short) 1 );
    }

    public static SupBookRecord createExternalReferences(String url, String[] sheetNames) {
        return new SupBookRecord(url, sheetNames);
    }

    private static String decodeFileName(String encodedUrl) {
        return encodedUrl.substring(1);


    }

    public boolean isExternalReferences() {
        return field_3_sheet_names != null;
    }

    public boolean isInternalReferences() {
        return field_3_sheet_names == null && !_isAddInFunctions;
    }

    public boolean isAddInFunctions() {
        return field_3_sheet_names == null && _isAddInFunctions;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName()).append(" [SUPBOOK ");

        if (isExternalReferences()) {
            sb.append("External References");
            sb.append(" nSheets=").append(field_1_number_of_sheets);
            sb.append(" url=").append(field_2_encoded_url);
        } else if (_isAddInFunctions) {
            sb.append("Add-In Functions");
        } else {
            sb.append("Internal References ");
            sb.append(" nSheets= ").append(field_1_number_of_sheets);
        }
        sb.append("]");
        return sb.toString();
    }

    protected int getDataSize() {
        if (!isExternalReferences()) {
            return SMALL_RECORD_SIZE;
        }
        int sum = 2;

        sum += StringUtil.getEncodedSize(field_2_encoded_url);

        for (int i = 0; i < field_3_sheet_names.length; i++) {
            sum += StringUtil.getEncodedSize(field_3_sheet_names[i]);
        }
        return sum;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_number_of_sheets);

        if (isExternalReferences()) {
            StringUtil.writeUnicodeString(out, field_2_encoded_url);

            for (int i = 0; i < field_3_sheet_names.length; i++) {
                StringUtil.writeUnicodeString(out, field_3_sheet_names[i]);
            }
        } else {
            int field2val = _isAddInFunctions ? TAG_ADD_IN_FUNCTIONS : TAG_INTERNAL_REFERENCES;

            out.writeShort(field2val);
        }
    }

    public short getNumberOfSheets() {
        return field_1_number_of_sheets;
    }

    public void setNumberOfSheets(short number) {
        field_1_number_of_sheets = number;
    }

    public short getSid() {
        return sid;
    }

    public String getURL() {
        String encodedUrl = field_2_encoded_url;
        switch (encodedUrl.charAt(0)) {
            case 0:
                return encodedUrl.substring(1);
            case 1:
                return decodeFileName(encodedUrl);
            case 2:
                return encodedUrl.substring(1);

        }
        return encodedUrl;
    }

    public String[] getSheetNames() {
        return field_3_sheet_names.clone();
    }
}
