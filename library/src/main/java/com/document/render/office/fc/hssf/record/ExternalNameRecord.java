

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;

import com.document.render.office.constant.fc.ConstantValueParser;
import com.document.render.office.fc.hssf.formula.Formula;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;



public final class ExternalNameRecord extends StandardRecord {

    @Keep
    public final static short sid = 0x0023;

    private static final int OPT_BUILTIN_NAME = 0x0001;
    private static final int OPT_AUTOMATIC_LINK = 0x0002;
    private static final int OPT_PICTURE_LINK = 0x0004;
    private static final int OPT_STD_DOCUMENT_NAME = 0x0008;
    private static final int OPT_OLE_LINK = 0x0010;

    private static final int OPT_ICONIFIED_PICTURE_LINK = 0x8000;


    private short field_1_option_flag;
    private short field_2_ixals;
    private short field_3_not_used;
    private String field_4_name;
    private Formula field_5_name_definition;


    private Object[] _ddeValues;

    private int _nColumns;

    private int _nRows;

    public ExternalNameRecord() {
        field_2_ixals = 0;
    }

    public ExternalNameRecord(RecordInputStream in) {
        field_1_option_flag = in.readShort();
        field_2_ixals = in.readShort();
        field_3_not_used = in.readShort();

        int numChars = in.readUByte();
        field_4_name = StringUtil.readUnicodeString(in, numChars);



        if (!isOLELink() && !isStdDocumentNameIdentifier()) {


            if (isAutomaticLink()) {
                if (in.available() > 0) {

                    int nColumns = in.readUByte() + 1;
                    int nRows = in.readShort() + 1;

                    int totalCount = nRows * nColumns;
                    _ddeValues = ConstantValueParser.parse(in, totalCount);
                    _nColumns = nColumns;
                    _nRows = nRows;
                }
            } else {

                int formulaLen = in.readUShort();
                field_5_name_definition = Formula.read(formulaLen, in);
            }
        }
    }


    public boolean isBuiltInName() {
        return (field_1_option_flag & OPT_BUILTIN_NAME) != 0;
    }


    public boolean isAutomaticLink() {
        return (field_1_option_flag & OPT_AUTOMATIC_LINK) != 0;
    }


    public boolean isPicureLink() {
        return (field_1_option_flag & OPT_PICTURE_LINK) != 0;
    }


    public boolean isStdDocumentNameIdentifier() {
        return (field_1_option_flag & OPT_STD_DOCUMENT_NAME) != 0;
    }

    public boolean isOLELink() {
        return (field_1_option_flag & OPT_OLE_LINK) != 0;
    }

    public boolean isIconifiedPictureLink() {
        return (field_1_option_flag & OPT_ICONIFIED_PICTURE_LINK) != 0;
    }


    public String getText() {
        return field_4_name;
    }

    public void setText(String str) {
        field_4_name = str;
    }


    public short getIx() {
        return field_2_ixals;
    }

    public void setIx(short ix) {
        field_2_ixals = ix;
    }

    public Ptg[] getParsedExpression() {
        return Formula.getTokens(field_5_name_definition);
    }

    public void setParsedExpression(Ptg[] ptgs) {
        field_5_name_definition = Formula.create(ptgs);
    }

    protected int getDataSize() {
        int result = 2 + 4;
        result += StringUtil.getEncodedSize(field_4_name) - 1;

        if (!isOLELink() && !isStdDocumentNameIdentifier()) {
            if (isAutomaticLink()) {
                result += 3;
                result += ConstantValueParser.getEncodedSize(_ddeValues);
            } else {
                result += field_5_name_definition.getEncodedSize();
            }
        }
        return result;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_option_flag);
        out.writeShort(field_2_ixals);
        out.writeShort(field_3_not_used);

        out.writeByte(field_4_name.length());
        StringUtil.writeUnicodeStringFlagAndData(out, field_4_name);

        if (!isOLELink() && !isStdDocumentNameIdentifier()) {
            if (isAutomaticLink()) {
                out.writeByte(_nColumns - 1);
                out.writeShort(_nRows - 1);
                ConstantValueParser.encode(out, _ddeValues);
            } else {
                field_5_name_definition.serialize(out);
            }
        }
    }

    public short getSid() {
        return sid;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[EXTERNALNAME]\n");
        sb.append("    .options      = ").append(field_1_option_flag).append("\n");
        sb.append("    .ix      = ").append(field_2_ixals).append("\n");
        sb.append("    .name    = ").append(field_4_name).append("\n");
        if (field_5_name_definition != null) {
            Ptg[] ptgs = field_5_name_definition.getTokens();
            for (int i = 0; i < ptgs.length; i++) {
                Ptg ptg = ptgs[i];
                sb.append(ptg.toString()).append(ptg.getRVAType()).append("\n");
            }
        }
        sb.append("[/EXTERNALNAME]\n");
        return sb.toString();
    }
}
