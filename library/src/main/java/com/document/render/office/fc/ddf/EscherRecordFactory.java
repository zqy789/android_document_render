

package com.document.render.office.fc.ddf;


public interface EscherRecordFactory {

    EscherRecord createRecord(byte[] data, int offset);
}
