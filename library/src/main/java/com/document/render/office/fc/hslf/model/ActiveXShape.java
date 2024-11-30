

package com.document.render.office.fc.hslf.model;

import com.document.render.office.fc.ShapeKit;
import com.document.render.office.fc.ddf.EscherClientDataRecord;
import com.document.render.office.fc.ddf.EscherComplexProperty;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.fc.ddf.EscherOptRecord;
import com.document.render.office.fc.ddf.EscherProperties;
import com.document.render.office.fc.ddf.EscherRecord;
import com.document.render.office.fc.hslf.exceptions.HSLFException;
import com.document.render.office.fc.hslf.record.Document;
import com.document.render.office.fc.hslf.record.ExControl;
import com.document.render.office.fc.hslf.record.ExObjList;
import com.document.render.office.fc.hslf.record.OEShapeAtom;
import com.document.render.office.fc.hslf.record.Record;
import com.document.render.office.fc.hslf.record.RecordTypes;
import com.document.render.office.fc.util.LittleEndian;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;



public final class ActiveXShape extends Picture {
    public static final int DEFAULT_ACTIVEX_THUMBNAIL = -1;


    public ActiveXShape(int movieIdx, int pictureIdx) {
        super(pictureIdx, null);
        setActiveXIndex(movieIdx);
    }


    protected ActiveXShape(EscherContainerRecord escherRecord, Shape parent) {
        super(escherRecord, parent);
    }


    protected EscherContainerRecord createSpContainer(int idx, boolean isChild) {
        _escherContainer = super.createSpContainer(idx, isChild);


        return _escherContainer;
    }


    public void setActiveXIndex(int idx) {
        EscherContainerRecord spContainer = getSpContainer();
        for (Iterator<EscherRecord> it = spContainer.getChildIterator(); it.hasNext(); ) {
            EscherRecord obj = it.next();
            if (obj.getRecordId() == EscherClientDataRecord.RECORD_ID) {
                EscherClientDataRecord clientRecord = (EscherClientDataRecord) obj;
                byte[] recdata = clientRecord.getRemainingData();
                LittleEndian.putInt(recdata, 8, idx);
            }
        }
    }

    public int getControlIndex() {
        int idx = -1;
        OEShapeAtom oe = (OEShapeAtom) getClientDataRecord(RecordTypes.OEShapeAtom.typeID);
        if (oe != null)
            idx = oe.getOptions();
        return idx;
    }


    public void setProperty(String key, String value) {

    }


    public ExControl getExControl() {
        int idx = getControlIndex();
        ExControl ctrl = null;
        Document doc = getSheet().getSlideShow().getDocumentRecord();
        ExObjList lst = (ExObjList) doc.findFirstOfType(RecordTypes.ExObjList.typeID);
        if (lst != null) {
            Record[] ch = lst.getChildRecords();
            for (int i = 0; i < ch.length; i++) {
                if (ch[i] instanceof ExControl) {
                    ExControl c = (ExControl) ch[i];
                    if (c.getExOleObjAtom().getObjID() == idx) {
                        ctrl = c;
                        break;
                    }
                }
            }
        }
        return ctrl;
    }

    protected void afterInsert(Sheet sheet) {
        ExControl ctrl = getExControl();
        ctrl.getExControlAtom().setSlideId(sheet._getSheetNumber());

        try {
            String name = ctrl.getProgId() + "-" + getControlIndex();
            byte[] data = (name + '\u0000').getBytes("UTF-16LE");
            EscherComplexProperty prop = new EscherComplexProperty(
                    EscherProperties.GROUPSHAPE__SHAPENAME, false, data);
            EscherOptRecord opt = (EscherOptRecord) ShapeKit.getEscherChild(_escherContainer,
                    EscherOptRecord.RECORD_ID);
            opt.addEscherProperty(prop);
        } catch (UnsupportedEncodingException e) {
            throw new HSLFException(e);
        }
    }
}
