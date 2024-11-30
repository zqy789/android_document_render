
package com.document.render.office.fc.hwpf.usermodel;

import com.document.render.office.fc.ShapeKit;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.fc.ddf.EscherProperty;
import com.document.render.office.fc.ddf.EscherPropertyFactory;
import com.document.render.office.fc.ddf.EscherRecord;
import com.document.render.office.fc.ddf.EscherSimpleProperty;
import com.document.render.office.fc.ddf.EscherSpRecord;

import java.util.List;


public final class HWPFShapeFactory {
    public static HWPFShape createShape(EscherContainerRecord spContainer, HWPFShape parent) {
        if (spContainer.getRecordId() == EscherContainerRecord.SPGR_CONTAINER) {
            return createShapeGroup(spContainer, parent);
        }
        return createSimpeShape(spContainer, parent);
    }

    public static HWPFShapeGroup createShapeGroup(EscherContainerRecord spContainer, HWPFShape parent) {
        HWPFShapeGroup group = null;
        EscherRecord opt = ShapeKit.getEscherChild((EscherContainerRecord) spContainer.getChild(0), (short) 0xF122);
        if (opt != null) {
            try {
                EscherPropertyFactory f = new EscherPropertyFactory();
                List<EscherProperty> props = f.createProperties(opt.serialize(), 8, opt.getInstance());
                EscherSimpleProperty p = (EscherSimpleProperty) props.get(0);
                if (p.getPropertyNumber() == 0x39F && p.getPropertyValue() == 1) {

                } else {
                    group = new HWPFShapeGroup(spContainer, parent);
                }
            } catch (Exception e) {
                group = new HWPFShapeGroup(spContainer, parent);
            }
        } else {
            group = new HWPFShapeGroup(spContainer, parent);
        }

        return group;
    }

    public static HWPFAutoShape createSimpeShape(EscherContainerRecord spContainer, HWPFShape parent) {
        EscherSpRecord spRecord = spContainer.getChildById(EscherSpRecord.RECORD_ID);
        if (spRecord != null) {
            return new HWPFAutoShape(spContainer, parent);
        }
        return null;
    }
}
