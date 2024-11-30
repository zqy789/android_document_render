

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class BOFRecord extends StandardRecord {

    @Keep
    public final static short sid = 0x809;


    public final static int VERSION = 0x0600;

    public final static int BUILD = 0x10d3;

    public final static int BUILD_YEAR = 0x07CC;

    public final static int HISTORY_MASK = 0x41;

    public final static int TYPE_WORKBOOK = 0x05;
    public final static int TYPE_VB_MODULE = 0x06;
    public final static int TYPE_WORKSHEET = 0x10;
    public final static int TYPE_CHART = 0x20;
    public final static int TYPE_EXCEL_4_MACRO = 0x40;
    public final static int TYPE_WORKSPACE_FILE = 0x100;

    private int field_1_version;
    private int field_2_type;
    private int field_3_build;
    private int field_4_year;
    private int field_5_history;
    private int field_6_rversion;


    public BOFRecord() {
    }

    private BOFRecord(int type) {
        field_1_version = VERSION;
        field_2_type = type;
        field_3_build = BUILD;
        field_4_year = BUILD_YEAR;
        field_5_history = 0x01;
        field_6_rversion = VERSION;
    }

    public BOFRecord(RecordInputStream in) {
        field_1_version = in.readShort();
        field_2_type = in.readShort();



        if (in.remaining() >= 2) {
            field_3_build = in.readShort();
        }
        if (in.remaining() >= 2) {
            field_4_year = in.readShort();
        }
        if (in.remaining() >= 4) {
            field_5_history = in.readInt();
        }
        if (in.remaining() >= 4) {
            field_6_rversion = in.readInt();
        }
    }

    public static BOFRecord createSheetBOF() {
        return new BOFRecord(TYPE_WORKSHEET);
    }


    public int getVersion() {
        return field_1_version;
    }


    public void setVersion(int version) {
        field_1_version = version;
    }


    public int getType() {
        return field_2_type;
    }


    public void setType(int type) {
        field_2_type = type;
    }


    public int getBuild() {
        return field_3_build;
    }


    public void setBuild(int build) {
        field_3_build = build;
    }


    public int getBuildYear() {
        return field_4_year;
    }


    public void setBuildYear(int year) {
        field_4_year = year;
    }


    public int getHistoryBitMask() {
        return field_5_history;
    }


    public void setHistoryBitMask(int bitmask) {
        field_5_history = bitmask;
    }


    public int getRequiredVersion() {
        return field_6_rversion;
    }


    public void setRequiredVersion(int version) {
        field_6_rversion = version;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[BOF RECORD]\n");
        buffer.append("    .version  = ").append(HexDump.shortToHex(getVersion())).append("\n");
        buffer.append("    .type     = ").append(HexDump.shortToHex(getType()));
        buffer.append(" (").append(getTypeName()).append(")").append("\n");
        buffer.append("    .build    = ").append(HexDump.shortToHex(getBuild())).append("\n");
        buffer.append("    .buildyear= ").append(getBuildYear()).append("\n");
        buffer.append("    .history  = ").append(HexDump.intToHex(getHistoryBitMask())).append("\n");
        buffer.append("    .reqver   = ").append(HexDump.intToHex(getRequiredVersion())).append("\n");
        buffer.append("[/BOF RECORD]\n");
        return buffer.toString();
    }

    private String getTypeName() {
        switch (field_2_type) {
            case TYPE_CHART:
                return "chart";
            case TYPE_EXCEL_4_MACRO:
                return "excel 4 macro";
            case TYPE_VB_MODULE:
                return "vb module";
            case TYPE_WORKBOOK:
                return "workbook";
            case TYPE_WORKSHEET:
                return "worksheet";
            case TYPE_WORKSPACE_FILE:
                return "workspace file";
        }
        return "#error unknown type#";
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getVersion());
        out.writeShort(getType());
        out.writeShort(getBuild());
        out.writeShort(getBuildYear());
        out.writeInt(getHistoryBitMask());
        out.writeInt(getRequiredVersion());
    }

    protected int getDataSize() {
        return 16;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        BOFRecord rec = new BOFRecord();
        rec.field_1_version = field_1_version;
        rec.field_2_type = field_2_type;
        rec.field_3_build = field_3_build;
        rec.field_4_year = field_4_year;
        rec.field_5_history = field_5_history;
        rec.field_6_rversion = field_6_rversion;
        return rec;
    }
}
