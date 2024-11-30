

package com.document.render.office.fc.hslf.record;

import androidx.annotation.Keep;

import com.document.render.office.common.shape.ShapeTypes;
import com.document.render.office.fc.ddf.DefaultEscherRecordFactory;
import com.document.render.office.fc.ddf.EscherBoolProperty;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.fc.ddf.EscherDgRecord;
import com.document.render.office.fc.ddf.EscherOptRecord;
import com.document.render.office.fc.ddf.EscherProperties;
import com.document.render.office.fc.ddf.EscherRGBProperty;
import com.document.render.office.fc.ddf.EscherRecord;
import com.document.render.office.fc.ddf.EscherSimpleProperty;
import com.document.render.office.fc.ddf.EscherSpRecord;
import com.document.render.office.fc.ddf.EscherSpgrRecord;
import com.document.render.office.fc.ddf.EscherTextboxRecord;
import com.document.render.office.fc.util.LittleEndian;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;






public final class PPDrawing extends RecordAtom {

    private byte[] _header;
    private long _type;


    private EscherRecord[] childRecords;
    private EscherTextboxWrapper[] textboxWrappers;

    private EscherDgRecord dg;


    protected PPDrawing(byte[] source, int start, int len) {

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        _type = LittleEndian.getUShort(_header, 2);


        byte[] contents = new byte[len];
        System.arraycopy(source, start, contents, 0, len);


        DefaultEscherRecordFactory erf = new DefaultEscherRecordFactory();
        Vector escherChildren = new Vector();
        findEscherChildren(erf, contents, 8, len - 8, escherChildren);

        childRecords = new EscherRecord[escherChildren.size()];
        for (int i = 0; i < childRecords.length; i++) {
            childRecords[i] = (EscherRecord) escherChildren.get(i);
        }


        Vector textboxes = new Vector();
        findEscherTextboxRecord(childRecords, textboxes);
        textboxWrappers = new EscherTextboxWrapper[textboxes.size()];
        for (int i = 0; i < textboxWrappers.length; i++) {
            textboxWrappers[i] = (EscherTextboxWrapper) textboxes.get(i);
        }
    }


    public PPDrawing() {
        _header = new byte[8];
        LittleEndian.putUShort(_header, 0, 15);
        LittleEndian.putUShort(_header, 2, RecordTypes.PPDrawing.typeID);
        LittleEndian.putInt(_header, 4, 0);

        textboxWrappers = new EscherTextboxWrapper[]{};
        create();
    }


    public EscherRecord[] getEscherRecords() {
        return childRecords;
    }


    public EscherTextboxWrapper[] getTextboxWrappers() {
        return textboxWrappers;
    }


    private void findEscherChildren(DefaultEscherRecordFactory erf, byte[] source, int startPos,
                                    int lenToGo, Vector found) {

        int escherBytes = LittleEndian.getInt(source, startPos + 4) + 8;


        EscherRecord r = erf.createRecord(source, startPos);

        r.fillFields(source, startPos, erf);

        found.add(r);


        int size = r.getRecordSize();



        if (size != escherBytes) {

            size = escherBytes;
        }
        startPos += size;
        lenToGo -= size;
        if (lenToGo >= 8) {
            findEscherChildren(erf, source, startPos, lenToGo, found);
        }
    }


    private void findEscherTextboxRecord(EscherRecord[] toSearch, Vector found) {
        for (int i = 0; i < toSearch.length; i++) {
            if (toSearch[i] instanceof EscherTextboxRecord) {
                EscherTextboxRecord tbr = (EscherTextboxRecord) toSearch[i];
                EscherTextboxWrapper w = new EscherTextboxWrapper(tbr);
                found.add(w);
                if ("BinaryTagData".equals(toSearch[i].getRecordName())) {
                    w.setShapeId(toSearch[i].getRecordId());
                } else {
                    for (int j = i; j >= 0; j--) {
                        if (toSearch[j] instanceof EscherSpRecord) {
                            EscherSpRecord sp = (EscherSpRecord) toSearch[j];
                            w.setShapeId(sp.getShapeId());
                            break;
                        }
                    }
                }
            } else {

                if (toSearch[i].isContainerRecord()) {
                    List<EscherRecord> childrenL = toSearch[i].getChildRecords();
                    EscherRecord[] children = new EscherRecord[childrenL.size()];
                    childrenL.toArray(children);
                    findEscherTextboxRecord(children, found);
                }
            }
        }
    }


    public long getRecordType() {
        return _type;
    }


    public Record[] getChildRecords() {
        return null;
    }


    public void writeOut(OutputStream out) throws IOException {

        for (int i = 0; i < textboxWrappers.length; i++) {

        }


        int newSize = 0;
        for (int i = 0; i < childRecords.length; i++) {
            newSize += childRecords[i].getRecordSize();
        }


        LittleEndian.putInt(_header, 4, newSize);


        out.write(_header);


        byte[] b = new byte[newSize];
        int done = 0;
        for (int i = 0; i < childRecords.length; i++) {
            int written = childRecords[i].serialize(done, b);
            done += written;
        }


        out.write(b);
    }


    @Keep
    private void create() {
        EscherContainerRecord dgContainer = new EscherContainerRecord();
        dgContainer.setRecordId(EscherContainerRecord.DG_CONTAINER);
        dgContainer.setOptions((short) 15);

        EscherDgRecord dg = new EscherDgRecord();
        dg.setOptions((short) 16);
        dg.setNumShapes(1);
        dgContainer.addChildRecord(dg);

        EscherContainerRecord spgrContainer = new EscherContainerRecord();
        spgrContainer.setOptions((short) 15);
        spgrContainer.setRecordId(EscherContainerRecord.SPGR_CONTAINER);

        EscherContainerRecord spContainer = new EscherContainerRecord();
        spContainer.setOptions((short) 15);
        spContainer.setRecordId(EscherContainerRecord.SP_CONTAINER);

        EscherSpgrRecord spgr = new EscherSpgrRecord();
        spgr.setOptions((short) 1);
        spContainer.addChildRecord(spgr);

        EscherSpRecord sp = new EscherSpRecord();
        sp.setOptions((short) ((ShapeTypes.NotPrimitive << 4) + 2));
        sp.setFlags(EscherSpRecord.FLAG_PATRIARCH | EscherSpRecord.FLAG_GROUP);
        spContainer.addChildRecord(sp);
        spgrContainer.addChildRecord(spContainer);
        dgContainer.addChildRecord(spgrContainer);

        spContainer = new EscherContainerRecord();
        spContainer.setOptions((short) 15);
        spContainer.setRecordId(EscherContainerRecord.SP_CONTAINER);
        sp = new EscherSpRecord();
        sp.setOptions((short) ((ShapeTypes.Rectangle << 4) + 2));
        sp.setFlags(EscherSpRecord.FLAG_BACKGROUND | EscherSpRecord.FLAG_HASSHAPETYPE);
        spContainer.addChildRecord(sp);

        EscherOptRecord opt = new EscherOptRecord();
        opt.setRecordId(EscherOptRecord.RECORD_ID);
        opt.addEscherProperty(new EscherRGBProperty(EscherProperties.FILL__FILLCOLOR, 134217728));
        opt.addEscherProperty(new EscherRGBProperty(EscherProperties.FILL__FILLBACKCOLOR, 134217733));
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.FILL__RECTRIGHT, 10064750));
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.FILL__RECTBOTTOM, 7778750));
        opt.addEscherProperty(new EscherBoolProperty(EscherProperties.FILL__NOFILLHITTEST, 1179666));
        opt.addEscherProperty(new EscherBoolProperty(EscherProperties.LINESTYLE__NOLINEDRAWDASH,
                524288));
        opt.addEscherProperty(new EscherSimpleProperty(
                EscherProperties.SHAPE__BLACKANDWHITESETTINGS, 9));
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.SHAPE__BACKGROUNDSHAPE,
                65537));
        spContainer.addChildRecord(opt);

        dgContainer.addChildRecord(spContainer);

        childRecords = new EscherRecord[]{dgContainer};
    }


    public void addTextboxWrapper(EscherTextboxWrapper txtbox) {
        EscherTextboxWrapper[] tw = new EscherTextboxWrapper[textboxWrappers.length + 1];
        System.arraycopy(textboxWrappers, 0, tw, 0, textboxWrappers.length);

        tw[textboxWrappers.length] = txtbox;
        textboxWrappers = tw;
    }


    public EscherDgRecord getEscherDgRecord() {
        if (dg == null) {
            EscherContainerRecord dgContainer = (EscherContainerRecord) childRecords[0];
            for (Iterator<EscherRecord> it = dgContainer.getChildIterator(); it.hasNext(); ) {
                EscherRecord r = it.next();
                if (r instanceof EscherDgRecord) {
                    dg = (EscherDgRecord) r;
                    break;
                }
            }
        }
        return dg;
    }


    public void dispose() {
        _header = null;
        if (childRecords != null) {
            for (EscherRecord er : childRecords) {
                er.dispose();
            }
            childRecords = null;
        }
        if (textboxWrappers != null) {
            for (EscherTextboxWrapper etw : textboxWrappers) {
                etw.dispose();
            }
            textboxWrappers = null;
        }
        if (dg != null) {
            dg.dispose();
            dg = null;
        }
    }

}
