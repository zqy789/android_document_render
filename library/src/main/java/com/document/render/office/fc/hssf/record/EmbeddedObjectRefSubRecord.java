

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.formula.ptg.Area3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.AreaPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.formula.ptg.Ref3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.RefPtg;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianInputStream;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;

import java.io.ByteArrayInputStream;



public final class EmbeddedObjectRefSubRecord extends SubRecord {
    @Keep
    public static final short sid = 0x0009;

    private static final byte[] EMPTY_BYTE_ARRAY = {};

    private int field_1_unknown_int;

    private Ptg field_2_refPtg;

    private byte[] field_2_unknownFormulaData;

    private boolean field_3_unicode_flag;
    private String field_4_ole_classname;

    private Byte field_4_unknownByte;
    private Integer field_5_stream_id;
    private byte[] field_6_unknown;



    EmbeddedObjectRefSubRecord() {
        field_2_unknownFormulaData = new byte[]{0x02, 0x6C, 0x6A, 0x16, 0x01,};
        field_6_unknown = EMPTY_BYTE_ARRAY;
        field_4_ole_classname = null;
    }

    public EmbeddedObjectRefSubRecord(LittleEndianInput in, int size) {






        int streamIdOffset = in.readShort();
        int remaining = size - LittleEndian.SHORT_SIZE;

        int dataLenAfterFormula = remaining - streamIdOffset;
        int formulaSize = in.readUShort();
        remaining -= LittleEndian.SHORT_SIZE;
        field_1_unknown_int = in.readInt();
        remaining -= LittleEndian.INT_SIZE;
        byte[] formulaRawBytes = readRawData(in, formulaSize);
        remaining -= formulaSize;
        field_2_refPtg = readRefPtg(formulaRawBytes);
        if (field_2_refPtg == null) {



            field_2_unknownFormulaData = formulaRawBytes;
        } else {
            field_2_unknownFormulaData = null;
        }

        int stringByteCount;
        if (remaining >= dataLenAfterFormula + 3) {
            int tag = in.readByte();
            stringByteCount = LittleEndian.BYTE_SIZE;
            if (tag != 0x03) {
                throw new RecordFormatException("Expected byte 0x03 here");
            }
            int nChars = in.readUShort();
            stringByteCount += LittleEndian.SHORT_SIZE;
            if (nChars > 0) {

                field_3_unicode_flag = (in.readByte() & 0x01) != 0;
                stringByteCount += LittleEndian.BYTE_SIZE;
                if (field_3_unicode_flag) {
                    field_4_ole_classname = StringUtil.readUnicodeLE(in, nChars);
                    stringByteCount += nChars * 2;
                } else {
                    field_4_ole_classname = StringUtil.readCompressedUnicode(in, nChars);
                    stringByteCount += nChars;
                }
            } else {
                field_4_ole_classname = "";
            }
        } else {
            field_4_ole_classname = null;
            stringByteCount = 0;
        }
        remaining -= stringByteCount;

        if (((stringByteCount + formulaSize) % 2) != 0) {
            int b = in.readByte();
            remaining -= LittleEndian.BYTE_SIZE;
            if (field_2_refPtg != null && field_4_ole_classname == null) {
                field_4_unknownByte = Byte.valueOf((byte) b);
            }
        }
        int nUnexpectedPadding = remaining - dataLenAfterFormula;

        if (nUnexpectedPadding > 0) {
            System.err.println("Discarding " + nUnexpectedPadding + " unexpected padding bytes ");
            readRawData(in, nUnexpectedPadding);
            remaining -= nUnexpectedPadding;
        }


        if (dataLenAfterFormula >= 4) {
            field_5_stream_id = Integer.valueOf(in.readInt());
            remaining -= LittleEndian.INT_SIZE;
        } else {
            field_5_stream_id = null;
        }
        field_6_unknown = readRawData(in, remaining);
    }

    private static Ptg readRefPtg(byte[] formulaRawBytes) {
        LittleEndianInput in = new LittleEndianInputStream(new ByteArrayInputStream(formulaRawBytes));
        byte ptgSid = in.readByte();
        switch (ptgSid) {
            case AreaPtg.sid:
                return new AreaPtg(in);
            case Area3DPtg.sid:
                return new Area3DPtg(in);
            case RefPtg.sid:
                return new RefPtg(in);
            case Ref3DPtg.sid:
                return new Ref3DPtg(in);
        }
        return null;
    }

    private static byte[] readRawData(LittleEndianInput in, int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative size (" + size + ")");
        }
        if (size == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] result = new byte[size];
        in.readFully(result);
        return result;
    }

    public short getSid() {
        return sid;
    }

    private int getStreamIDOffset(int formulaSize) {
        int result = 2 + 4;
        result += formulaSize;

        int stringLen;
        if (field_4_ole_classname == null) {

            stringLen = 0;
        } else {
            result += 1 + 2;
            stringLen = field_4_ole_classname.length();
            if (stringLen > 0) {
                result += 1;
                if (field_3_unicode_flag) {
                    result += stringLen * 2;
                } else {
                    result += stringLen;
                }
            }
        }

        if ((result % 2) != 0) {
            result++;
        }
        return result;
    }

    private int getDataSize(int idOffset) {

        int result = 2 + idOffset;
        if (field_5_stream_id != null) {
            result += 4;
        }
        return result + field_6_unknown.length;
    }

    protected int getDataSize() {
        int formulaSize = field_2_refPtg == null ? field_2_unknownFormulaData.length : field_2_refPtg.getSize();
        int idOffset = getStreamIDOffset(formulaSize);
        return getDataSize(idOffset);
    }

    public void serialize(LittleEndianOutput out) {

        int formulaSize = field_2_refPtg == null ? field_2_unknownFormulaData.length : field_2_refPtg.getSize();
        int idOffset = getStreamIDOffset(formulaSize);
        int dataSize = getDataSize(idOffset);


        out.writeShort(sid);
        out.writeShort(dataSize);

        out.writeShort(idOffset);
        out.writeShort(formulaSize);
        out.writeInt(field_1_unknown_int);

        int pos = 12;

        if (field_2_refPtg == null) {
            out.write(field_2_unknownFormulaData);
        } else {
            field_2_refPtg.write(out);
        }
        pos += formulaSize;

        int stringLen;
        if (field_4_ole_classname == null) {

            stringLen = 0;
        } else {
            out.writeByte(0x03);
            pos += 1;
            stringLen = field_4_ole_classname.length();
            out.writeShort(stringLen);
            pos += 2;
            if (stringLen > 0) {
                out.writeByte(field_3_unicode_flag ? 0x01 : 0x00);
                pos += 1;

                if (field_3_unicode_flag) {
                    StringUtil.putUnicodeLE(field_4_ole_classname, out);
                    pos += stringLen * 2;
                } else {
                    StringUtil.putCompressedUnicode(field_4_ole_classname, out);
                    pos += stringLen;
                }
            }
        }


        switch (idOffset - (pos - 6)) {
            case 1:
                out.writeByte(field_4_unknownByte == null ? 0x00 : field_4_unknownByte.intValue());
                pos++;
            case 0:
                break;
            default:
                throw new IllegalStateException("Bad padding calculation (" + idOffset + ", " + pos + ")");
        }

        if (field_5_stream_id != null) {
            out.writeInt(field_5_stream_id.intValue());
            pos += 4;
        }
        out.write(field_6_unknown);
    }


    public Integer getStreamId() {
        return field_5_stream_id;
    }

    public String getOLEClassName() {
        return field_4_ole_classname;
    }

    public byte[] getObjectData() {
        return field_6_unknown;
    }

    public Object clone() {
        return this;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[ftPictFmla]\n");
        sb.append("    .f2unknown     = ").append(HexDump.intToHex(field_1_unknown_int)).append("\n");
        if (field_2_refPtg == null) {
            sb.append("    .f3unknown     = ").append(HexDump.toHex(field_2_unknownFormulaData)).append("\n");
        } else {
            sb.append("    .formula       = ").append(field_2_refPtg.toString()).append("\n");
        }
        if (field_4_ole_classname != null) {
            sb.append("    .unicodeFlag   = ").append(field_3_unicode_flag).append("\n");
            sb.append("    .oleClassname  = ").append(field_4_ole_classname).append("\n");
        }
        if (field_4_unknownByte != null) {
            sb.append("    .f4unknown   = ").append(HexDump.byteToHex(field_4_unknownByte.intValue())).append("\n");
        }
        if (field_5_stream_id != null) {
            sb.append("    .streamId      = ").append(HexDump.intToHex(field_5_stream_id.intValue())).append("\n");
        }
        if (field_6_unknown.length > 0) {
            sb.append("    .f7unknown     = ").append(HexDump.toHex(field_6_unknown)).append("\n");
        }
        sb.append("[/ftPictFmla]");
        return sb.toString();
    }
}
