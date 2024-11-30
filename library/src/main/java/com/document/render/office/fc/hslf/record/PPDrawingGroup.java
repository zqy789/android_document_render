

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.ddf.DefaultEscherRecordFactory;
import com.document.render.office.fc.ddf.EscherBSERecord;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.fc.ddf.EscherDggRecord;
import com.document.render.office.fc.ddf.EscherRecord;
import com.document.render.office.fc.util.LittleEndian;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;



public final class PPDrawingGroup extends RecordAtom {

    private byte[] _header;
    private EscherContainerRecord dggContainer;

    private EscherDggRecord dgg;

    protected PPDrawingGroup(byte[] source, int start, int len) {

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        byte[] contents = new byte[len];
        System.arraycopy(source, start, contents, 0, len);

        DefaultEscherRecordFactory erf = new DefaultEscherRecordFactory();
        EscherRecord child = erf.createRecord(contents, 0);
        child.fillFields(contents, 0, erf);
        dggContainer = (EscherContainerRecord) child.getChild(0);
    }


    public long getRecordType() {
        return RecordTypes.PPDrawingGroup.typeID;
    }


    public Record[] getChildRecords() {
        return null;
    }

    public void writeOut(OutputStream out) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Iterator<EscherRecord> iter = dggContainer.getChildIterator();
        while (iter.hasNext()) {
            EscherRecord r = iter.next();
            if (r.getRecordId() == EscherContainerRecord.BSTORE_CONTAINER) {
                EscherContainerRecord bstore = (EscherContainerRecord) r;

                ByteArrayOutputStream b2 = new ByteArrayOutputStream();
                for (Iterator<EscherRecord> it = bstore.getChildIterator(); it.hasNext(); ) {
                    EscherBSERecord bse = (EscherBSERecord) it.next();
                    byte[] b = new byte[36 + 8];
                    bse.serialize(0, b);
                    b2.write(b);
                }
                byte[] bstorehead = new byte[8];
                LittleEndian.putShort(bstorehead, 0, bstore.getOptions());
                LittleEndian.putShort(bstorehead, 2, bstore.getRecordId());
                LittleEndian.putInt(bstorehead, 4, b2.size());
                bout.write(bstorehead);
                bout.write(b2.toByteArray());

            } else {
                bout.write(r.serialize());
            }
        }
        int size = bout.size();


        LittleEndian.putInt(_header, 4, size + 8);


        out.write(_header);

        byte[] dgghead = new byte[8];
        LittleEndian.putShort(dgghead, 0, dggContainer.getOptions());
        LittleEndian.putShort(dgghead, 2, dggContainer.getRecordId());
        LittleEndian.putInt(dgghead, 4, size);
        out.write(dgghead);


        out.write(bout.toByteArray());

    }

    public EscherContainerRecord getDggContainer() {
        return dggContainer;
    }

    public EscherDggRecord getEscherDggRecord() {
        if (dgg == null) {
            for (Iterator<EscherRecord> it = dggContainer.getChildIterator(); it.hasNext(); ) {
                EscherRecord r = it.next();
                if (r instanceof EscherDggRecord) {
                    dgg = (EscherDggRecord) r;
                    break;
                }
            }
        }
        return dgg;
    }


    public void dispose() {
        _header = null;
        if (dggContainer != null) {
            dggContainer.dispose();
        }
        if (dgg != null) {
            dgg.dispose();
            dgg = null;
        }
    }

}
