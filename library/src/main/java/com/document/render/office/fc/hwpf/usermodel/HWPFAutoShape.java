
package com.document.render.office.fc.hwpf.usermodel;

import com.document.render.office.fc.ShapeKit;
import com.document.render.office.fc.ddf.EscherContainerRecord;


public class HWPFAutoShape extends HWPFShape {
    public HWPFAutoShape(EscherContainerRecord escherRecord, HWPFShape parent) {
        super(escherRecord, parent);
    }

    public String getShapeName() {
        return ShapeKit.getShapeName(escherContainer);
    }
}
