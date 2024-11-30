
package com.document.render.office.fc.ddf;

import androidx.annotation.Keep;


public class EscherTertiaryOptRecord extends AbstractEscherOptRecord {
    @Keep
    public static final short RECORD_ID = (short) 0xF122;

    public String getRecordName() {
        return "TertiaryOpt";
    }


    public void dispose() {
    }
}
