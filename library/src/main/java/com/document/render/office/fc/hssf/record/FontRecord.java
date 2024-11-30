

package com.document.render.office.fc.hssf.record;

import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;


public final class FontRecord extends StandardRecord {
    public final static short sid = 0x0031;
    public final static short SS_NONE = 0;
    public final static short SS_SUPER = 1;
    public final static short SS_SUB = 2;
    public final static byte U_NONE = 0;
    public final static byte U_SINGLE = 1;
    public final static byte U_DOUBLE = 2;
    public final static byte U_SINGLE_ACCOUNTING = 0x21;
    public final static byte U_DOUBLE_ACCOUNTING = 0x22;

    private static final BitField italic = BitFieldFactory.getInstance(0x02);

    private static final BitField strikeout = BitFieldFactory.getInstance(0x08);
    private static final BitField macoutline = BitFieldFactory.getInstance(0x10);
    private static final BitField macshadow = BitFieldFactory.getInstance(0x20);
    private short field_1_font_height;
    private short field_2_attributes;


    private short field_3_color_palette_index;
    private short field_4_bold_weight;
    private short field_5_super_sub_script;
    private byte field_6_underline;
    private byte field_7_family;
    private byte field_8_charset;
    private byte field_9_zero = 0;

    private String field_11_font_name;

    public FontRecord() {
    }

    public FontRecord(RecordInputStream in) {
        field_1_font_height = in.readShort();
        field_2_attributes = in.readShort();
        field_3_color_palette_index = in.readShort();
        field_4_bold_weight = in.readShort();
        field_5_super_sub_script = in.readShort();
        field_6_underline = in.readByte();
        field_7_family = in.readByte();
        field_8_charset = in.readByte();
        field_9_zero = in.readByte();
        int field_10_font_name_len = in.readUByte();
        int unicodeFlags = in.readUByte();

        if (field_10_font_name_len > 0) {
            if (unicodeFlags == 0) {
                field_11_font_name = in.readCompressedUnicode(field_10_font_name_len);
            } else {
                field_11_font_name = in.readUnicodeLEString(field_10_font_name_len);
            }
        } else {
            field_11_font_name = "";
        }
    }


    public void setStrikeout(boolean strike) {
        field_2_attributes = strikeout.setShortBoolean(field_2_attributes, strike);
    }


    public void setMacoutline(boolean mac) {
        field_2_attributes = macoutline.setShortBoolean(field_2_attributes, mac);
    }




    public void setMacshadow(boolean mac) {
        field_2_attributes = macshadow.setShortBoolean(field_2_attributes, mac);
    }


    public short getFontHeight() {
        return field_1_font_height;
    }


    public void setFontHeight(short height) {
        field_1_font_height = height;
    }


    public short getAttributes() {
        return field_2_attributes;
    }


    public void setAttributes(short attributes) {
        field_2_attributes = attributes;
    }


    public boolean isItalic() {
        return italic.isSet(field_2_attributes);
    }


    public void setItalic(boolean italics) {
        field_2_attributes = italic.setShortBoolean(field_2_attributes, italics);
    }


    public boolean isStruckout() {
        return strikeout.isSet(field_2_attributes);
    }


    public boolean isMacoutlined() {
        return macoutline.isSet(field_2_attributes);
    }


    public boolean isMacshadowed() {
        return macshadow.isSet(field_2_attributes);
    }


    public short getColorPaletteIndex() {
        return field_3_color_palette_index;
    }


    public void setColorPaletteIndex(short cpi) {
        field_3_color_palette_index = cpi;
    }


    public short getBoldWeight() {
        return field_4_bold_weight;
    }


    public void setBoldWeight(short bw) {
        field_4_bold_weight = bw;
    }


    public short getSuperSubScript() {
        return field_5_super_sub_script;
    }


    public void setSuperSubScript(short sss) {
        field_5_super_sub_script = sss;
    }


    public byte getUnderline() {
        return field_6_underline;
    }


    public void setUnderline(byte u) {
        field_6_underline = u;
    }


    public byte getFamily() {
        return field_7_family;
    }


    public void setFamily(byte f) {
        field_7_family = f;
    }


    public byte getCharset() {
        return field_8_charset;
    }


    public void setCharset(byte charset) {
        field_8_charset = charset;
    }


    public String getFontName() {
        return field_11_font_name;
    }


    public void setFontName(String fn) {
        field_11_font_name = fn;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("[FONT]\n");
        sb.append("    .fontheight    = ").append(HexDump.shortToHex(getFontHeight())).append("\n");
        sb.append("    .attributes    = ").append(HexDump.shortToHex(getAttributes())).append("\n");
        sb.append("       .italic     = ").append(isItalic()).append("\n");
        sb.append("       .strikout   = ").append(isStruckout()).append("\n");
        sb.append("       .macoutlined= ").append(isMacoutlined()).append("\n");
        sb.append("       .macshadowed= ").append(isMacshadowed()).append("\n");
        sb.append("    .colorpalette  = ").append(HexDump.shortToHex(getColorPaletteIndex())).append("\n");
        sb.append("    .boldweight    = ").append(HexDump.shortToHex(getBoldWeight())).append("\n");
        sb.append("    .supersubscript= ").append(HexDump.shortToHex(getSuperSubScript())).append("\n");
        sb.append("    .underline     = ").append(HexDump.byteToHex(getUnderline())).append("\n");
        sb.append("    .family        = ").append(HexDump.byteToHex(getFamily())).append("\n");
        sb.append("    .charset       = ").append(HexDump.byteToHex(getCharset())).append("\n");
        sb.append("    .fontname      = ").append(getFontName()).append("\n");
        sb.append("[/FONT]\n");
        return sb.toString();
    }

    public void serialize(LittleEndianOutput out) {

        out.writeShort(getFontHeight());
        out.writeShort(getAttributes());
        out.writeShort(getColorPaletteIndex());
        out.writeShort(getBoldWeight());
        out.writeShort(getSuperSubScript());
        out.writeByte(getUnderline());
        out.writeByte(getFamily());
        out.writeByte(getCharset());
        out.writeByte(field_9_zero);
        int fontNameLen = field_11_font_name.length();
        out.writeByte(fontNameLen);
        boolean hasMultibyte = StringUtil.hasMultibyte(field_11_font_name);
        out.writeByte(hasMultibyte ? 0x01 : 0x00);
        if (fontNameLen > 0) {
            if (hasMultibyte) {
                StringUtil.putUnicodeLE(field_11_font_name, out);
            } else {
                StringUtil.putCompressedUnicode(field_11_font_name, out);
            }
        }
    }

    protected int getDataSize() {
        int size = 16;
        int fontNameLen = field_11_font_name.length();
        if (fontNameLen < 1) {
            return size;
        }

        boolean hasMultibyte = StringUtil.hasMultibyte(field_11_font_name);
        return size + fontNameLen * (hasMultibyte ? 2 : 1);
    }

    public short getSid() {
        return sid;
    }


    public void cloneStyleFrom(FontRecord source) {
        field_1_font_height = source.field_1_font_height;
        field_2_attributes = source.field_2_attributes;
        field_3_color_palette_index = source.field_3_color_palette_index;
        field_4_bold_weight = source.field_4_bold_weight;
        field_5_super_sub_script = source.field_5_super_sub_script;
        field_6_underline = source.field_6_underline;
        field_7_family = source.field_7_family;
        field_8_charset = source.field_8_charset;
        field_9_zero = source.field_9_zero;
        field_11_font_name = source.field_11_font_name;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((field_11_font_name == null) ? 0 : field_11_font_name
                .hashCode());
        result = prime * result + field_1_font_height;
        result = prime * result + field_2_attributes;
        result = prime * result + field_3_color_palette_index;
        result = prime * result + field_4_bold_weight;
        result = prime * result + field_5_super_sub_script;
        result = prime * result + field_6_underline;
        result = prime * result + field_7_family;
        result = prime * result + field_8_charset;
        result = prime * result + field_9_zero;
        return result;
    }


    public boolean sameProperties(FontRecord other) {
        return
                field_1_font_height == other.field_1_font_height &&
                        field_2_attributes == other.field_2_attributes &&
                        field_3_color_palette_index == other.field_3_color_palette_index &&
                        field_4_bold_weight == other.field_4_bold_weight &&
                        field_5_super_sub_script == other.field_5_super_sub_script &&
                        field_6_underline == other.field_6_underline &&
                        field_7_family == other.field_7_family &&
                        field_8_charset == other.field_8_charset &&
                        field_9_zero == other.field_9_zero &&
                        field_11_font_name.equals(other.field_11_font_name)
                ;
    }
}
