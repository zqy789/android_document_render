

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class SeriesRecord extends StandardRecord {
    public final static short sid = 0x1003;
    public final static short CATEGORY_DATA_TYPE_DATES = 0;
    public final static short CATEGORY_DATA_TYPE_NUMERIC = 1;
    public final static short CATEGORY_DATA_TYPE_SEQUENCE = 2;
    public final static short CATEGORY_DATA_TYPE_TEXT = 3;
    public final static short VALUES_DATA_TYPE_DATES = 0;
    public final static short VALUES_DATA_TYPE_NUMERIC = 1;
    public final static short VALUES_DATA_TYPE_SEQUENCE = 2;
    public final static short VALUES_DATA_TYPE_TEXT = 3;
    public final static short BUBBLE_SERIES_TYPE_DATES = 0;
    public final static short BUBBLE_SERIES_TYPE_NUMERIC = 1;
    public final static short BUBBLE_SERIES_TYPE_SEQUENCE = 2;
    public final static short BUBBLE_SERIES_TYPE_TEXT = 3;
    private short field_1_categoryDataType;
    private short field_2_valuesDataType;
    private short field_3_numCategories;
    private short field_4_numValues;
    private short field_5_bubbleSeriesType;
    private short field_6_numBubbleValues;


    public SeriesRecord() {

    }

    public SeriesRecord(RecordInputStream in) {
        field_1_categoryDataType = in.readShort();
        field_2_valuesDataType = in.readShort();
        field_3_numCategories = in.readShort();
        field_4_numValues = in.readShort();
        field_5_bubbleSeriesType = in.readShort();
        field_6_numBubbleValues = in.readShort();

    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[SERIES]\n");
        buffer.append("    .categoryDataType     = ")
                .append("0x").append(HexDump.toHex(getCategoryDataType()))
                .append(" (").append(getCategoryDataType()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .valuesDataType       = ")
                .append("0x").append(HexDump.toHex(getValuesDataType()))
                .append(" (").append(getValuesDataType()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .numCategories        = ")
                .append("0x").append(HexDump.toHex(getNumCategories()))
                .append(" (").append(getNumCategories()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .numValues            = ")
                .append("0x").append(HexDump.toHex(getNumValues()))
                .append(" (").append(getNumValues()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .bubbleSeriesType     = ")
                .append("0x").append(HexDump.toHex(getBubbleSeriesType()))
                .append(" (").append(getBubbleSeriesType()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .numBubbleValues      = ")
                .append("0x").append(HexDump.toHex(getNumBubbleValues()))
                .append(" (").append(getNumBubbleValues()).append(" )");
        buffer.append(System.getProperty("line.separator"));

        buffer.append("[/SERIES]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_categoryDataType);
        out.writeShort(field_2_valuesDataType);
        out.writeShort(field_3_numCategories);
        out.writeShort(field_4_numValues);
        out.writeShort(field_5_bubbleSeriesType);
        out.writeShort(field_6_numBubbleValues);
    }

    protected int getDataSize() {
        return 2 + 2 + 2 + 2 + 2 + 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        SeriesRecord rec = new SeriesRecord();

        rec.field_1_categoryDataType = field_1_categoryDataType;
        rec.field_2_valuesDataType = field_2_valuesDataType;
        rec.field_3_numCategories = field_3_numCategories;
        rec.field_4_numValues = field_4_numValues;
        rec.field_5_bubbleSeriesType = field_5_bubbleSeriesType;
        rec.field_6_numBubbleValues = field_6_numBubbleValues;
        return rec;
    }



    public short getCategoryDataType() {
        return field_1_categoryDataType;
    }


    public void setCategoryDataType(short field_1_categoryDataType) {
        this.field_1_categoryDataType = field_1_categoryDataType;
    }


    public short getValuesDataType() {
        return field_2_valuesDataType;
    }


    public void setValuesDataType(short field_2_valuesDataType) {
        this.field_2_valuesDataType = field_2_valuesDataType;
    }


    public short getNumCategories() {
        return field_3_numCategories;
    }


    public void setNumCategories(short field_3_numCategories) {
        this.field_3_numCategories = field_3_numCategories;
    }


    public short getNumValues() {
        return field_4_numValues;
    }


    public void setNumValues(short field_4_numValues) {
        this.field_4_numValues = field_4_numValues;
    }


    public short getBubbleSeriesType() {
        return field_5_bubbleSeriesType;
    }


    public void setBubbleSeriesType(short field_5_bubbleSeriesType) {
        this.field_5_bubbleSeriesType = field_5_bubbleSeriesType;
    }


    public short getNumBubbleValues() {
        return field_6_numBubbleValues;
    }


    public void setNumBubbleValues(short field_6_numBubbleValues) {
        this.field_6_numBubbleValues = field_6_numBubbleValues;
    }
}
