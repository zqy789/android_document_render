
package com.document.render.office.fc.ddf;

import androidx.annotation.Keep;


public class EscherBinaryTagRecord extends EscherTextboxRecord {
    @Keep
    public static final short RECORD_ID = (short) 0x138B;


    public EscherBinaryTagRecord() {

    }


    public String getRecordName() {
        return "BinaryTagData";
    }
}
