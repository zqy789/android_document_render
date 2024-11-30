

package com.document.render.office.fc.hssf.record;


import com.document.render.office.fc.hssf.record.common.UnicodeString;
import com.document.render.office.fc.hssf.record.cont.ContinuableRecordOutput;
import com.document.render.office.fc.util.IntMapper;



final class SSTSerializer {

    private final int _numStrings;
    private final int _numUniqueStrings;

    private final IntMapper<UnicodeString> strings;


    private final int[] bucketAbsoluteOffsets;

    private final int[] bucketRelativeOffsets;

    public SSTSerializer(IntMapper<UnicodeString> strings, int numStrings, int numUniqueStrings) {
        this.strings = strings;
        _numStrings = numStrings;
        _numUniqueStrings = numUniqueStrings;

        int infoRecs = ExtSSTRecord.getNumberOfInfoRecsForStrings(strings.size());
        this.bucketAbsoluteOffsets = new int[infoRecs];
        this.bucketRelativeOffsets = new int[infoRecs];
    }

    private static UnicodeString getUnicodeString(IntMapper<UnicodeString> strings, int index) {
        return (strings.get(index));
    }

    public void serialize(ContinuableRecordOutput out) {
        out.writeInt(_numStrings);
        out.writeInt(_numUniqueStrings);

        for (int k = 0; k < strings.size(); k++) {
            if (k % ExtSSTRecord.DEFAULT_BUCKET_SIZE == 0) {
                int rOff = out.getTotalSize();
                int index = k / ExtSSTRecord.DEFAULT_BUCKET_SIZE;
                if (index < ExtSSTRecord.MAX_BUCKETS) {

                    bucketAbsoluteOffsets[index] = rOff;
                    bucketRelativeOffsets[index] = rOff;
                }
            }
            UnicodeString s = getUnicodeString(k);
            s.serialize(out);
        }
    }

    private UnicodeString getUnicodeString(int index) {
        return getUnicodeString(strings, index);
    }

    public int[] getBucketAbsoluteOffsets() {
        return bucketAbsoluteOffsets;
    }

    public int[] getBucketRelativeOffsets() {
        return bucketRelativeOffsets;
    }
}
