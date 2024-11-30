

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class ValueRangeRecord extends StandardRecord {
    public final static short sid = 0x101f;

    private static final BitField automaticMinimum = BitFieldFactory.getInstance(0x0001);
    private static final BitField automaticMaximum = BitFieldFactory.getInstance(0x0002);
    private static final BitField automaticMajor = BitFieldFactory.getInstance(0x0004);
    private static final BitField automaticMinor = BitFieldFactory.getInstance(0x0008);
    private static final BitField automaticCategoryCrossing = BitFieldFactory.getInstance(0x0010);
    private static final BitField logarithmicScale = BitFieldFactory.getInstance(0x0020);
    private static final BitField valuesInReverse = BitFieldFactory.getInstance(0x0040);
    private static final BitField crossCategoryAxisAtMaximum = BitFieldFactory.getInstance(0x0080);
    private static final BitField reserved = BitFieldFactory.getInstance(0x0100);

    private double field_1_minimumAxisValue;
    private double field_2_maximumAxisValue;
    private double field_3_majorIncrement;
    private double field_4_minorIncrement;
    private double field_5_categoryAxisCross;
    private short field_6_options;


    public ValueRangeRecord() {

    }

    public ValueRangeRecord(RecordInputStream in) {
        field_1_minimumAxisValue = in.readDouble();
        field_2_maximumAxisValue = in.readDouble();
        field_3_majorIncrement = in.readDouble();
        field_4_minorIncrement = in.readDouble();
        field_5_categoryAxisCross = in.readDouble();
        field_6_options = in.readShort();

    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[VALUERANGE]\n");
        buffer.append("    .minimumAxisValue     = ")
                .append(" (").append(getMinimumAxisValue()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .maximumAxisValue     = ")
                .append(" (").append(getMaximumAxisValue()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .majorIncrement       = ")
                .append(" (").append(getMajorIncrement()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .minorIncrement       = ")
                .append(" (").append(getMinorIncrement()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .categoryAxisCross    = ")
                .append(" (").append(getCategoryAxisCross()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .options              = ")
                .append("0x").append(HexDump.toHex(getOptions()))
                .append(" (").append(getOptions()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .automaticMinimum         = ").append(isAutomaticMinimum()).append('\n');
        buffer.append("         .automaticMaximum         = ").append(isAutomaticMaximum()).append('\n');
        buffer.append("         .automaticMajor           = ").append(isAutomaticMajor()).append('\n');
        buffer.append("         .automaticMinor           = ").append(isAutomaticMinor()).append('\n');
        buffer.append("         .automaticCategoryCrossing     = ").append(isAutomaticCategoryCrossing()).append('\n');
        buffer.append("         .logarithmicScale         = ").append(isLogarithmicScale()).append('\n');
        buffer.append("         .valuesInReverse          = ").append(isValuesInReverse()).append('\n');
        buffer.append("         .crossCategoryAxisAtMaximum     = ").append(isCrossCategoryAxisAtMaximum()).append('\n');
        buffer.append("         .reserved                 = ").append(isReserved()).append('\n');

        buffer.append("[/VALUERANGE]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeDouble(field_1_minimumAxisValue);
        out.writeDouble(field_2_maximumAxisValue);
        out.writeDouble(field_3_majorIncrement);
        out.writeDouble(field_4_minorIncrement);
        out.writeDouble(field_5_categoryAxisCross);
        out.writeShort(field_6_options);
    }

    protected int getDataSize() {
        return 8 + 8 + 8 + 8 + 8 + 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        ValueRangeRecord rec = new ValueRangeRecord();

        rec.field_1_minimumAxisValue = field_1_minimumAxisValue;
        rec.field_2_maximumAxisValue = field_2_maximumAxisValue;
        rec.field_3_majorIncrement = field_3_majorIncrement;
        rec.field_4_minorIncrement = field_4_minorIncrement;
        rec.field_5_categoryAxisCross = field_5_categoryAxisCross;
        rec.field_6_options = field_6_options;
        return rec;
    }



    public double getMinimumAxisValue() {
        return field_1_minimumAxisValue;
    }


    public void setMinimumAxisValue(double field_1_minimumAxisValue) {
        this.field_1_minimumAxisValue = field_1_minimumAxisValue;
    }


    public double getMaximumAxisValue() {
        return field_2_maximumAxisValue;
    }


    public void setMaximumAxisValue(double field_2_maximumAxisValue) {
        this.field_2_maximumAxisValue = field_2_maximumAxisValue;
    }


    public double getMajorIncrement() {
        return field_3_majorIncrement;
    }


    public void setMajorIncrement(double field_3_majorIncrement) {
        this.field_3_majorIncrement = field_3_majorIncrement;
    }


    public double getMinorIncrement() {
        return field_4_minorIncrement;
    }


    public void setMinorIncrement(double field_4_minorIncrement) {
        this.field_4_minorIncrement = field_4_minorIncrement;
    }


    public double getCategoryAxisCross() {
        return field_5_categoryAxisCross;
    }


    public void setCategoryAxisCross(double field_5_categoryAxisCross) {
        this.field_5_categoryAxisCross = field_5_categoryAxisCross;
    }


    public short getOptions() {
        return field_6_options;
    }


    public void setOptions(short field_6_options) {
        this.field_6_options = field_6_options;
    }


    public boolean isAutomaticMinimum() {
        return automaticMinimum.isSet(field_6_options);
    }


    public void setAutomaticMinimum(boolean value) {
        field_6_options = automaticMinimum.setShortBoolean(field_6_options, value);
    }


    public boolean isAutomaticMaximum() {
        return automaticMaximum.isSet(field_6_options);
    }


    public void setAutomaticMaximum(boolean value) {
        field_6_options = automaticMaximum.setShortBoolean(field_6_options, value);
    }


    public boolean isAutomaticMajor() {
        return automaticMajor.isSet(field_6_options);
    }


    public void setAutomaticMajor(boolean value) {
        field_6_options = automaticMajor.setShortBoolean(field_6_options, value);
    }


    public boolean isAutomaticMinor() {
        return automaticMinor.isSet(field_6_options);
    }


    public void setAutomaticMinor(boolean value) {
        field_6_options = automaticMinor.setShortBoolean(field_6_options, value);
    }


    public boolean isAutomaticCategoryCrossing() {
        return automaticCategoryCrossing.isSet(field_6_options);
    }


    public void setAutomaticCategoryCrossing(boolean value) {
        field_6_options = automaticCategoryCrossing.setShortBoolean(field_6_options, value);
    }


    public boolean isLogarithmicScale() {
        return logarithmicScale.isSet(field_6_options);
    }


    public void setLogarithmicScale(boolean value) {
        field_6_options = logarithmicScale.setShortBoolean(field_6_options, value);
    }


    public boolean isValuesInReverse() {
        return valuesInReverse.isSet(field_6_options);
    }


    public void setValuesInReverse(boolean value) {
        field_6_options = valuesInReverse.setShortBoolean(field_6_options, value);
    }


    public boolean isCrossCategoryAxisAtMaximum() {
        return crossCategoryAxisAtMaximum.isSet(field_6_options);
    }


    public void setCrossCategoryAxisAtMaximum(boolean value) {
        field_6_options = crossCategoryAxisAtMaximum.setShortBoolean(field_6_options, value);
    }


    public boolean isReserved() {
        return reserved.isSet(field_6_options);
    }


    public void setReserved(boolean value) {
        field_6_options = reserved.setShortBoolean(field_6_options, value);
    }
}
