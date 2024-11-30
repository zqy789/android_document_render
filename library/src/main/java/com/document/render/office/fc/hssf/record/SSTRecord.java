

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.record.common.UnicodeString;
import com.document.render.office.fc.hssf.record.cont.ContinuableRecord;
import com.document.render.office.fc.hssf.record.cont.ContinuableRecordOutput;
import com.document.render.office.fc.util.IntMapper;
import com.document.render.office.fc.util.LittleEndianConsts;

import java.util.Iterator;



public final class SSTRecord extends ContinuableRecord {
    @Keep
    public static final short sid = 0x00FC;

    static final int STD_RECORD_OVERHEAD = 2 * LittleEndianConsts.SHORT_SIZE;



    static final int SST_RECORD_OVERHEAD = STD_RECORD_OVERHEAD + 2 * LittleEndianConsts.INT_SIZE;

    static final int MAX_DATA_SPACE = RecordInputStream.MAX_RECORD_DATA_SIZE - 8;
    private static final UnicodeString EMPTY_STRING = new UnicodeString("");

    int[] bucketAbsoluteOffsets;

    int[] bucketRelativeOffsets;

    private int field_1_num_strings;

    private int field_2_num_unique_strings;
    private IntMapper<UnicodeString> field_3_strings;
    private SSTDeserializer deserializer;

    public SSTRecord() {
        field_1_num_strings = 0;
        field_2_num_unique_strings = 0;
        field_3_strings = new IntMapper<UnicodeString>();
        deserializer = new SSTDeserializer(field_3_strings);
    }


    public SSTRecord(RecordInputStream in) {



        field_1_num_strings = in.readInt();
        field_2_num_unique_strings = in.readInt();
        field_3_strings = new IntMapper<UnicodeString>();
        deserializer = new SSTDeserializer(field_3_strings);
        deserializer.manufactureStrings(field_2_num_unique_strings, in);
    }


    public int addString(UnicodeString string) {
        field_1_num_strings++;
        UnicodeString ucs = (string == null) ? EMPTY_STRING
                : string;
        int rval;
        int index = field_3_strings.getIndex(ucs);

        if (index != -1) {
            rval = index;
        } else {


            rval = field_3_strings.size();
            field_2_num_unique_strings++;
            SSTDeserializer.addToStringTable(field_3_strings, ucs);
        }
        return rval;
    }


    public int getNumStrings() {
        return field_1_num_strings;
    }


    public int getNumUniqueStrings() {
        return field_2_num_unique_strings;
    }


    public UnicodeString getString(int id) {
        return field_3_strings.get(id);
    }


    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[SST]\n");
        buffer.append("    .numstrings     = ")
                .append(Integer.toHexString(getNumStrings())).append("\n");
        buffer.append("    .uniquestrings  = ")
                .append(Integer.toHexString(getNumUniqueStrings())).append("\n");
        for (int k = 0; k < field_3_strings.size(); k++) {
            UnicodeString s = field_3_strings.get(k);
            buffer.append("    .string_" + k + "      = ")
                    .append(s.getDebugInfo()).append("\n");
        }
        buffer.append("[/SST]\n");
        return buffer.toString();
    }

    public short getSid() {
        return sid;
    }


    Iterator<UnicodeString> getStrings() {
        return field_3_strings.iterator();
    }


    int countStrings() {
        return field_3_strings.size();
    }

    protected void serialize(ContinuableRecordOutput out) {
        SSTSerializer serializer = new SSTSerializer(field_3_strings, getNumStrings(), getNumUniqueStrings());
        serializer.serialize(out);
        bucketAbsoluteOffsets = serializer.getBucketAbsoluteOffsets();
        bucketRelativeOffsets = serializer.getBucketRelativeOffsets();
    }

    SSTDeserializer getDeserializer() {
        return deserializer;
    }


    public ExtSSTRecord createExtSSTRecord(int sstOffset) {
        if (bucketAbsoluteOffsets == null || bucketAbsoluteOffsets == null)
            throw new IllegalStateException("SST record has not yet been serialized.");

        ExtSSTRecord extSST = new ExtSSTRecord();
        extSST.setNumStringsPerBucket((short) 8);
        int[] absoluteOffsets = bucketAbsoluteOffsets.clone();
        int[] relativeOffsets = bucketRelativeOffsets.clone();
        for (int i = 0; i < absoluteOffsets.length; i++)
            absoluteOffsets[i] += sstOffset;
        extSST.setBucketOffsets(absoluteOffsets, relativeOffsets);
        return extSST;
    }


    public int calcExtSSTRecordSize() {
        return ExtSSTRecord.getRecordSizeForStrings(field_3_strings.size());
    }
}
