

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;


public final class StyleRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x0293;

    private static final BitField styleIndexMask = BitFieldFactory.getInstance(0x0FFF);
    private static final BitField isBuiltinFlag = BitFieldFactory.getInstance(0x8000);


    private int field_1_xf_index;


    private int field_2_builtin_style;
    private int field_3_outline_style_level;


    private boolean field_3_stringHasMultibyte;
    private String field_4_name;


    public StyleRecord() {
        field_1_xf_index = isBuiltinFlag.set(field_1_xf_index);
    }

    public StyleRecord(RecordInputStream in) {
        field_1_xf_index = in.readShort();
        if (isBuiltin()) {
            field_2_builtin_style = in.readByte();
            field_3_outline_style_level = in.readByte();
        } else {
            int field_2_name_length = in.readShort();

            if (in.remaining() < 1) {


                if (field_2_name_length != 0) {
                    throw new RecordFormatException("Ran out of data reading style record");
                }

                field_4_name = "";
            } else {

                field_3_stringHasMultibyte = in.readByte() != 0x00;
                if (field_3_stringHasMultibyte) {
                    field_4_name = StringUtil.readUnicodeLE(in, field_2_name_length);
                } else {
                    field_4_name = StringUtil.readCompressedUnicode(in, field_2_name_length);
                }
            }
        }
    }


    public int getXFIndex() {
        return styleIndexMask.getValue(field_1_xf_index);
    }


    public void setXFIndex(int xfIndex) {
        field_1_xf_index = styleIndexMask.setValue(field_1_xf_index, xfIndex);
    }


    public void setBuiltinStyle(int builtinStyleId) {
        field_1_xf_index = isBuiltinFlag.set(field_1_xf_index);
        field_2_builtin_style = builtinStyleId;
    }


    public void setOutlineStyleLevel(int level) {
        field_3_outline_style_level = level & 0x00FF;
    }

    public boolean isBuiltin() {
        return isBuiltinFlag.isSet(field_1_xf_index);
    }


    public String getName() {
        return field_4_name;
    }


    public void setName(String name) {
        field_4_name = name;
        field_3_stringHasMultibyte = StringUtil.hasMultibyte(name);
        field_1_xf_index = isBuiltinFlag.clear(field_1_xf_index);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("[STYLE]\n");
        sb.append("    .xf_index_raw =").append(HexDump.shortToHex(field_1_xf_index)).append("\n");
        sb.append("        .type     =").append(isBuiltin() ? "built-in" : "user-defined").append("\n");
        sb.append("        .xf_index =").append(HexDump.shortToHex(getXFIndex())).append("\n");
        if (isBuiltin()) {
            sb.append("    .builtin_style=").append(HexDump.byteToHex(field_2_builtin_style)).append("\n");
            sb.append("    .outline_level=").append(HexDump.byteToHex(field_3_outline_style_level)).append("\n");
        } else {
            sb.append("    .name        =").append(getName()).append("\n");
        }
        sb.append("[/STYLE]\n");
        return sb.toString();
    }


    protected int getDataSize() {
        if (isBuiltin()) {
            return 4;
        }
        return 2
                + 3
                + field_4_name.length() * (field_3_stringHasMultibyte ? 2 : 1);
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_xf_index);
        if (isBuiltin()) {
            out.writeByte(field_2_builtin_style);
            out.writeByte(field_3_outline_style_level);
        } else {
            out.writeShort(field_4_name.length());
            out.writeByte(field_3_stringHasMultibyte ? 0x01 : 0x00);
            if (field_3_stringHasMultibyte) {
                StringUtil.putUnicodeLE(getName(), out);
            } else {
                StringUtil.putCompressedUnicode(getName(), out);
            }
        }
    }

    public short getSid() {
        return sid;
    }
}
