

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class SheetPropertiesRecord extends StandardRecord {
    public final static short sid = 0x1044;
    public final static byte EMPTY_NOT_PLOTTED = 0;
    public final static byte EMPTY_ZERO = 1;
    public final static byte EMPTY_INTERPOLATED = 2;
    private static final BitField chartTypeManuallyFormatted = BitFieldFactory.getInstance(0x01);
    private static final BitField plotVisibleOnly = BitFieldFactory.getInstance(0x02);
    private static final BitField doNotSizeWithWindow = BitFieldFactory.getInstance(0x04);
    private static final BitField defaultPlotDimensions = BitFieldFactory.getInstance(0x08);
    private static final BitField autoPlotArea = BitFieldFactory.getInstance(0x10);
    private int field_1_flags;
    private int field_2_empty;


    public SheetPropertiesRecord() {

    }

    public SheetPropertiesRecord(RecordInputStream in) {
        field_1_flags = in.readUShort();
        field_2_empty = in.readUShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[SHTPROPS]\n");
        buffer.append("    .flags                = ").append(HexDump.shortToHex(field_1_flags)).append('\n');
        buffer.append("         .chartTypeManuallyFormatted= ").append(isChartTypeManuallyFormatted()).append('\n');
        buffer.append("         .plotVisibleOnly           = ").append(isPlotVisibleOnly()).append('\n');
        buffer.append("         .doNotSizeWithWindow       = ").append(isDoNotSizeWithWindow()).append('\n');
        buffer.append("         .defaultPlotDimensions     = ").append(isDefaultPlotDimensions()).append('\n');
        buffer.append("         .autoPlotArea              = ").append(isAutoPlotArea()).append('\n');
        buffer.append("    .empty                = ").append(HexDump.shortToHex(field_2_empty)).append('\n');

        buffer.append("[/SHTPROPS]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_flags);
        out.writeShort(field_2_empty);
    }

    protected int getDataSize() {
        return 2 + 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        SheetPropertiesRecord rec = new SheetPropertiesRecord();

        rec.field_1_flags = field_1_flags;
        rec.field_2_empty = field_2_empty;
        return rec;
    }


    public int getFlags() {
        return field_1_flags;
    }


    public int getEmpty() {
        return field_2_empty;
    }


    public void setEmpty(byte empty) {
        this.field_2_empty = empty;
    }


    public boolean isChartTypeManuallyFormatted() {
        return chartTypeManuallyFormatted.isSet(field_1_flags);
    }


    public void setChartTypeManuallyFormatted(boolean value) {
        field_1_flags = chartTypeManuallyFormatted.setBoolean(field_1_flags, value);
    }


    public boolean isPlotVisibleOnly() {
        return plotVisibleOnly.isSet(field_1_flags);
    }


    public void setPlotVisibleOnly(boolean value) {
        field_1_flags = plotVisibleOnly.setBoolean(field_1_flags, value);
    }


    public boolean isDoNotSizeWithWindow() {
        return doNotSizeWithWindow.isSet(field_1_flags);
    }


    public void setDoNotSizeWithWindow(boolean value) {
        field_1_flags = doNotSizeWithWindow.setBoolean(field_1_flags, value);
    }


    public boolean isDefaultPlotDimensions() {
        return defaultPlotDimensions.isSet(field_1_flags);
    }


    public void setDefaultPlotDimensions(boolean value) {
        field_1_flags = defaultPlotDimensions.setBoolean(field_1_flags, value);
    }


    public boolean isAutoPlotArea() {
        return autoPlotArea.isSet(field_1_flags);
    }


    public void setAutoPlotArea(boolean value) {
        field_1_flags = autoPlotArea.setBoolean(field_1_flags, value);
    }
}
