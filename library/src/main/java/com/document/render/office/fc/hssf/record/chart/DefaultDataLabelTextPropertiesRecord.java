

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class DefaultDataLabelTextPropertiesRecord extends StandardRecord {
    public final static short sid = 0x1024;
    public final static short CATEGORY_DATA_TYPE_SHOW_LABELS_CHARACTERISTIC = 0;
    public final static short CATEGORY_DATA_TYPE_VALUE_AND_PERCENTAGE_CHARACTERISTIC = 1;
    public final static short CATEGORY_DATA_TYPE_ALL_TEXT_CHARACTERISTIC = 2;
    private short field_1_categoryDataType;


    public DefaultDataLabelTextPropertiesRecord() {

    }

    public DefaultDataLabelTextPropertiesRecord(RecordInputStream in) {
        field_1_categoryDataType = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[DEFAULTTEXT]\n");
        buffer.append("    .categoryDataType     = ")
                .append("0x").append(HexDump.toHex(getCategoryDataType()))
                .append(" (").append(getCategoryDataType()).append(" )");
        buffer.append(System.getProperty("line.separator"));

        buffer.append("[/DEFAULTTEXT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_categoryDataType);
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        DefaultDataLabelTextPropertiesRecord rec = new DefaultDataLabelTextPropertiesRecord();

        rec.field_1_categoryDataType = field_1_categoryDataType;
        return rec;
    }



    public short getCategoryDataType() {
        return field_1_categoryDataType;
    }


    public void setCategoryDataType(short field_1_categoryDataType) {
        this.field_1_categoryDataType = field_1_categoryDataType;
    }
}
