

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.record.cont.ContinuableRecord;
import com.document.render.office.fc.hssf.record.cont.ContinuableRecordOutput;
import com.document.render.office.fc.util.LittleEndianOutput;

import java.util.ArrayList;


public final class ExtSSTRecord extends ContinuableRecord {
    @Keep
    public final static short sid = 0x00FF;
    public static final int DEFAULT_BUCKET_SIZE = 8;


    public static final int MAX_BUCKETS = 128;
    private short _stringsPerBucket;
    private InfoSubRecord[] _sstInfos;
    public ExtSSTRecord() {
        _stringsPerBucket = DEFAULT_BUCKET_SIZE;
        _sstInfos = new InfoSubRecord[0];
    }


    public ExtSSTRecord(RecordInputStream in) {
        _stringsPerBucket = in.readShort();

        int nInfos = in.remaining() / InfoSubRecord.ENCODED_SIZE;
        ArrayList<InfoSubRecord> lst = new ArrayList<InfoSubRecord>(nInfos);

        while (in.available() > 0) {
            InfoSubRecord info = new InfoSubRecord(in);
            lst.add(info);

            if (in.available() == 0 && in.hasNextRecord() && in.getNextSid() == ContinueRecord.sid) {
                in.nextRecord();
            }
        }
        _sstInfos = lst.toArray(new InfoSubRecord[lst.size()]);
    }

    public static final int getNumberOfInfoRecsForStrings(int numStrings) {
        int infoRecs = (numStrings / DEFAULT_BUCKET_SIZE);
        if ((numStrings % DEFAULT_BUCKET_SIZE) != 0)
            infoRecs++;


        if (infoRecs > MAX_BUCKETS)
            infoRecs = MAX_BUCKETS;
        return infoRecs;
    }


    public static final int getRecordSizeForStrings(int numStrings) {
        return 4 + 2 + getNumberOfInfoRecsForStrings(numStrings) * 8;
    }

    public void setNumStringsPerBucket(short numStrings) {
        _stringsPerBucket = numStrings;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[EXTSST]\n");
        buffer.append("    .dsst           = ")
                .append(Integer.toHexString(_stringsPerBucket))
                .append("\n");
        buffer.append("    .numInfoRecords = ").append(_sstInfos.length)
                .append("\n");
        for (int k = 0; k < _sstInfos.length; k++) {
            buffer.append("    .inforecord     = ").append(k).append("\n");
            buffer.append("    .streampos      = ")
                    .append(Integer
                            .toHexString(_sstInfos[k].getStreamPos())).append("\n");
            buffer.append("    .sstoffset      = ")
                    .append(Integer
                            .toHexString(_sstInfos[k].getBucketSSTOffset()))
                    .append("\n");
        }
        buffer.append("[/EXTSST]\n");
        return buffer.toString();
    }

    public void serialize(ContinuableRecordOutput out) {
        out.writeShort(_stringsPerBucket);
        for (int k = 0; k < _sstInfos.length; k++) {
            _sstInfos[k].serialize(out);
        }
    }

    protected int getDataSize() {
        return 2 + InfoSubRecord.ENCODED_SIZE * _sstInfos.length;
    }

    protected InfoSubRecord[] getInfoSubRecords() {
        return _sstInfos;
    }

    public short getSid() {
        return sid;
    }

    public void setBucketOffsets(int[] bucketAbsoluteOffsets, int[] bucketRelativeOffsets) {

        _sstInfos = new InfoSubRecord[bucketAbsoluteOffsets.length];
        for (int i = 0; i < bucketAbsoluteOffsets.length; i++) {
            _sstInfos[i] = new InfoSubRecord(bucketAbsoluteOffsets[i], bucketRelativeOffsets[i]);
        }
    }

    public static final class InfoSubRecord {
        public static final int ENCODED_SIZE = 8;
        private int field_1_stream_pos;
        private int field_2_bucket_sst_offset;

        private short field_3_zero;



        public InfoSubRecord(int streamPos, int bucketSstOffset) {
            field_1_stream_pos = streamPos;
            field_2_bucket_sst_offset = bucketSstOffset;
        }

        public InfoSubRecord(RecordInputStream in) {
            field_1_stream_pos = in.readInt();
            field_2_bucket_sst_offset = in.readShort();
            field_3_zero = in.readShort();
        }

        public int getStreamPos() {
            return field_1_stream_pos;
        }

        public int getBucketSSTOffset() {
            return field_2_bucket_sst_offset;
        }

        public void serialize(LittleEndianOutput out) {
            out.writeInt(field_1_stream_pos);
            out.writeShort(field_2_bucket_sst_offset);
            out.writeShort(field_3_zero);
        }
    }
}
