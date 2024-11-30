

package com.document.render.office.fc.hssf.record.common;

import com.document.render.office.fc.hssf.record.FeatRecord;
import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class FeatSmartTag implements SharedFeature {

    private byte[] data;

    public FeatSmartTag() {
        data = new byte[0];
    }

    public FeatSmartTag(RecordInputStream in) {
        data = in.readRemainder();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" [FEATURE SMART TAGS]\n");
        buffer.append(" [/FEATURE SMART TAGS]\n");
        return buffer.toString();
    }

    public int getDataSize() {
        return data.length;
    }

    public void serialize(LittleEndianOutput out) {
        out.write(data);
    }
}
