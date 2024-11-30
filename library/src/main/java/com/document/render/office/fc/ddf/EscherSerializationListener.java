

package com.document.render.office.fc.ddf;


public interface EscherSerializationListener {

    void beforeRecordSerialize(int offset, short recordId, EscherRecord record);


    void afterRecordSerialize(int offset, short recordId, int size, EscherRecord record);
}
