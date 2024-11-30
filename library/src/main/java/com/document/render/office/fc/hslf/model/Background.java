

package com.document.render.office.fc.hslf.model;

import com.document.render.office.fc.ddf.EscherContainerRecord;


public final class Background extends Shape {

    protected Background(EscherContainerRecord escherRecord, Shape parent) {
        super(escherRecord, parent);
    }

    protected EscherContainerRecord createSpContainer(boolean isChild) {
        return null;
    }


}
