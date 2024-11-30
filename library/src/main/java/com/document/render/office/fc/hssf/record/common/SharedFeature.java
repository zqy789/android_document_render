

package com.document.render.office.fc.hssf.record.common;

import com.document.render.office.fc.util.LittleEndianOutput;


public interface SharedFeature {
    public String toString();

    public void serialize(LittleEndianOutput out);

    public int getDataSize();
}
