

package com.document.render.office.fc.hslf.model;

import com.document.render.office.common.shape.ShapeTypes;
import com.document.render.office.fc.ShapeKit;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.fc.ddf.EscherOptRecord;
import com.document.render.office.fc.ddf.EscherProperties;
import com.document.render.office.fc.ddf.EscherSpRecord;
import com.document.render.office.java.awt.geom.Line2D;
import com.document.render.office.java.awt.geom.Rectangle2D;



public final class Line extends SimpleShape {
    protected Line(EscherContainerRecord escherRecord, Shape parent) {
        super(escherRecord, parent);
    }


    public Line(Shape parent) {
        super(null, parent);
        _escherContainer = createSpContainer(parent instanceof ShapeGroup);
    }


    public Line() {
        this(null);
    }

    public Float[] getAdjustmentValue() {
        return ShapeKit.getAdjustmentValue(getSpContainer());
    }

    protected EscherContainerRecord createSpContainer(boolean isChild) {
        _escherContainer = super.createSpContainer(isChild);

        EscherSpRecord spRecord = _escherContainer.getChildById(EscherSpRecord.RECORD_ID);
        short type = (ShapeTypes.Line << 4) | 0x2;
        spRecord.setOptions(type);


        EscherOptRecord opt = (EscherOptRecord) ShapeKit.getEscherChild(_escherContainer,
                EscherOptRecord.RECORD_ID);


        setEscherProperty(opt, EscherProperties.GEOMETRY__SHAPEPATH, 4);
        setEscherProperty(opt, EscherProperties.GEOMETRY__FILLOK, 0x10000);
        setEscherProperty(opt, EscherProperties.FILL__NOFILLHITTEST, 0x100000);
        setEscherProperty(opt, EscherProperties.LINESTYLE__COLOR, 0x8000001);
        setEscherProperty(opt, EscherProperties.LINESTYLE__NOLINEDRAWDASH, 0xA0008);
        setEscherProperty(opt, EscherProperties.SHADOWSTYLE__COLOR, 0x8000002);

        return _escherContainer;
    }

    public com.document.render.office.java.awt.Shape getOutline() {
        Rectangle2D anchor = getLogicalAnchor2D();
        return new Line2D.Double(anchor.getX(), anchor.getY(), anchor.getX() + anchor.getWidth(),
                anchor.getY() + anchor.getHeight());
    }

    public void dispose() {
        super.dispose();
    }
}
