

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.formula.Formula;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class LinkedDataRecord extends StandardRecord {
    public final static short sid = 0x1051;
    public final static byte LINK_TYPE_TITLE_OR_TEXT = 0;
    public final static byte LINK_TYPE_VALUES = 1;
    public final static byte LINK_TYPE_CATEGORIES = 2;
    public final static byte REFERENCE_TYPE_DEFAULT_CATEGORIES = 0;
    public final static byte REFERENCE_TYPE_DIRECT = 1;
    public final static byte REFERENCE_TYPE_WORKSHEET = 2;
    public final static byte REFERENCE_TYPE_NOT_USED = 3;
    public final static byte REFERENCE_TYPE_ERROR_REPORTED = 4;
    private static final BitField customNumberFormat = BitFieldFactory.getInstance(0x1);
    private byte field_1_linkType;
    private byte field_2_referenceType;
    private short field_3_options;
    private short field_4_indexNumberFmtRecord;
    private Formula field_5_formulaOfLink;


    public LinkedDataRecord() {

    }

    public LinkedDataRecord(RecordInputStream in) {
        field_1_linkType = in.readByte();
        field_2_referenceType = in.readByte();
        field_3_options = in.readShort();
        field_4_indexNumberFmtRecord = in.readShort();
        int encodedTokenLen = in.readUShort();
        field_5_formulaOfLink = Formula.read(encodedTokenLen, in);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[AI]\n");
        buffer.append("    .linkType             = ").append(HexDump.byteToHex(getLinkType())).append('\n');
        buffer.append("    .referenceType        = ").append(HexDump.byteToHex(getReferenceType())).append('\n');
        buffer.append("    .options              = ").append(HexDump.shortToHex(getOptions())).append('\n');
        buffer.append("    .customNumberFormat   = ").append(isCustomNumberFormat()).append('\n');
        buffer.append("    .indexNumberFmtRecord = ").append(HexDump.shortToHex(getIndexNumberFmtRecord())).append('\n');
        buffer.append("    .formulaOfLink        = ").append('\n');
        Ptg[] ptgs = field_5_formulaOfLink.getTokens();
        for (int i = 0; i < ptgs.length; i++) {
            Ptg ptg = ptgs[i];
            buffer.append(ptg.toString()).append(ptg.getRVAType()).append('\n');
        }

        buffer.append("[/AI]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeByte(field_1_linkType);
        out.writeByte(field_2_referenceType);
        out.writeShort(field_3_options);
        out.writeShort(field_4_indexNumberFmtRecord);
        field_5_formulaOfLink.serialize(out);
    }

    protected int getDataSize() {
        return 1 + 1 + 2 + 2 + field_5_formulaOfLink.getEncodedSize();
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        LinkedDataRecord rec = new LinkedDataRecord();

        rec.field_1_linkType = field_1_linkType;
        rec.field_2_referenceType = field_2_referenceType;
        rec.field_3_options = field_3_options;
        rec.field_4_indexNumberFmtRecord = field_4_indexNumberFmtRecord;
        rec.field_5_formulaOfLink = field_5_formulaOfLink.copy();
        return rec;
    }



    public byte getLinkType() {
        return field_1_linkType;
    }


    public void setLinkType(byte field_1_linkType) {
        this.field_1_linkType = field_1_linkType;
    }


    public byte getReferenceType() {
        return field_2_referenceType;
    }


    public void setReferenceType(byte field_2_referenceType) {
        this.field_2_referenceType = field_2_referenceType;
    }


    public short getOptions() {
        return field_3_options;
    }


    public void setOptions(short field_3_options) {
        this.field_3_options = field_3_options;
    }


    public short getIndexNumberFmtRecord() {
        return field_4_indexNumberFmtRecord;
    }


    public void setIndexNumberFmtRecord(short field_4_indexNumberFmtRecord) {
        this.field_4_indexNumberFmtRecord = field_4_indexNumberFmtRecord;
    }


    public Ptg[] getFormulaOfLink() {
        return field_5_formulaOfLink.getTokens();
    }


    public void setFormulaOfLink(Ptg[] ptgs) {
        this.field_5_formulaOfLink = Formula.create(ptgs);
    }


    public boolean isCustomNumberFormat() {
        return customNumberFormat.isSet(field_3_options);
    }


    public void setCustomNumberFormat(boolean value) {
        field_3_options = customNumberFormat.setShortBoolean(field_3_options, value);
    }
}
