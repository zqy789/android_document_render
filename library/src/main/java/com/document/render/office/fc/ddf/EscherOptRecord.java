
package com.document.render.office.fc.ddf;

import androidx.annotation.Keep;


public class EscherOptRecord extends AbstractEscherOptRecord {
    @Keep
    public static final short RECORD_ID = (short) 0xF00B;
    public static final String RECORD_DESCRIPTION = "msofbtOPT";


    public short getOptions() {
        setOptions((short) ((properties.size() << 4) | 0x3));
        return super.getOptions();
    }

    public String getRecordName() {
        return "Opt";
    }


    public void dispose() {
    }
}
