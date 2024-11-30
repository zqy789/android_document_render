


package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.hssf.record.EmbeddedObjectRefSubRecord;
import com.document.render.office.fc.hssf.record.ObjRecord;
import com.document.render.office.fc.hssf.record.SubRecord;
import com.document.render.office.fc.poifs.filesystem.DirectoryEntry;
import com.document.render.office.fc.poifs.filesystem.Entry;
import com.document.render.office.fc.util.HexDump;

import java.io.IOException;
import java.util.Iterator;



public final class HSSFObjectData {

    private final ObjRecord _record;


    private final DirectoryEntry _root;


    public HSSFObjectData(ObjRecord record, DirectoryEntry root) {
        _record = record;
        _root = root;
    }


    public String getOLE2ClassName() {
        return findObjectRecord().getOLEClassName();
    }


    public DirectoryEntry getDirectory() throws IOException {
        EmbeddedObjectRefSubRecord subRecord = findObjectRecord();

        int streamId = subRecord.getStreamId().intValue();
        String streamName = "MBD" + HexDump.toHex(streamId);

        Entry entry = _root.getEntry(streamName);
        if (entry instanceof DirectoryEntry) {
            return (DirectoryEntry) entry;
        }
        throw new IOException("Stream " + streamName + " was not an OLE2 directory");
    }


    public byte[] getObjectData() {
        return findObjectRecord().getObjectData();
    }


    public boolean hasDirectoryEntry() {
        EmbeddedObjectRefSubRecord subRecord = findObjectRecord();


        Integer streamId = subRecord.getStreamId();
        return streamId != null && streamId.intValue() != 0;
    }


    protected EmbeddedObjectRefSubRecord findObjectRecord() {
        Iterator<SubRecord> subRecordIter = _record.getSubRecords().iterator();

        while (subRecordIter.hasNext()) {
            Object subRecord = subRecordIter.next();
            if (subRecord instanceof EmbeddedObjectRefSubRecord) {
                return (EmbeddedObjectRefSubRecord) subRecord;
            }
        }

        throw new IllegalStateException("Object data does not contain a reference to an embedded object OLE2 directory");
    }
}
