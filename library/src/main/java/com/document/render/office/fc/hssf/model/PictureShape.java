

package com.document.render.office.fc.hssf.model;

import com.document.render.office.common.shape.ShapeTypes;
import com.document.render.office.fc.ddf.EscherClientDataRecord;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.fc.ddf.EscherOptRecord;
import com.document.render.office.fc.ddf.EscherProperties;
import com.document.render.office.fc.ddf.EscherRecord;
import com.document.render.office.fc.ddf.EscherSimpleProperty;
import com.document.render.office.fc.ddf.EscherSpRecord;
import com.document.render.office.fc.hssf.record.CommonObjectDataSubRecord;
import com.document.render.office.fc.hssf.record.EndSubRecord;
import com.document.render.office.fc.hssf.record.ObjRecord;
import com.document.render.office.fc.hssf.usermodel.HSSFAnchor;
import com.document.render.office.fc.hssf.usermodel.HSSFPicture;
import com.document.render.office.fc.hssf.usermodel.HSSFShape;
import com.document.render.office.fc.hssf.usermodel.HSSFSimpleShape;



public class PictureShape
        extends AbstractShape {
    private EscherContainerRecord spContainer;
    private ObjRecord objRecord;


    PictureShape(HSSFSimpleShape hssfShape, int shapeId) {
        spContainer = createSpContainer(hssfShape, shapeId);
        objRecord = createObjRecord(hssfShape, shapeId);
    }


    private EscherContainerRecord createSpContainer(HSSFSimpleShape hssfShape, int shapeId) {
        HSSFPicture shape = (HSSFPicture) hssfShape;

        EscherContainerRecord spContainer = new EscherContainerRecord();
        EscherSpRecord sp = new EscherSpRecord();
        EscherOptRecord opt = new EscherOptRecord();
        EscherRecord anchor;
        EscherClientDataRecord clientData = new EscherClientDataRecord();

        spContainer.setRecordId(EscherContainerRecord.SP_CONTAINER);
        spContainer.setOptions((short) 0x000F);
        sp.setRecordId(EscherSpRecord.RECORD_ID);
        sp.setOptions((short) ((ShapeTypes.PictureFrame << 4) | 0x2));

        sp.setShapeId(shapeId);
        sp.setFlags(EscherSpRecord.FLAG_HAVEANCHOR | EscherSpRecord.FLAG_HASSHAPETYPE);
        opt.setRecordId(EscherOptRecord.RECORD_ID);

        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.BLIP__BLIPTODISPLAY, false, true, shape.getPictureIndex()));


        addStandardOptions(shape, opt);
        HSSFAnchor userAnchor = shape.getAnchor();
        if (userAnchor.isHorizontallyFlipped())
            sp.setFlags(sp.getFlags() | EscherSpRecord.FLAG_FLIPHORIZ);
        if (userAnchor.isVerticallyFlipped())
            sp.setFlags(sp.getFlags() | EscherSpRecord.FLAG_FLIPVERT);
        anchor = createAnchor(userAnchor);
        clientData.setRecordId(EscherClientDataRecord.RECORD_ID);
        clientData.setOptions((short) 0x0000);

        spContainer.addChildRecord(sp);
        spContainer.addChildRecord(opt);
        spContainer.addChildRecord(anchor);
        spContainer.addChildRecord(clientData);

        return spContainer;
    }


    private ObjRecord createObjRecord(HSSFShape hssfShape, int shapeId) {
        HSSFShape shape = hssfShape;

        ObjRecord obj = new ObjRecord();
        CommonObjectDataSubRecord c = new CommonObjectDataSubRecord();
        c.setObjectType((short) ((HSSFSimpleShape) shape).getShapeType());
        c.setObjectId(getCmoObjectId(shapeId));
        c.setLocked(true);
        c.setPrintable(true);
        c.setAutofill(true);
        c.setAutoline(true);
        c.setReserved2(0x0);
        EndSubRecord e = new EndSubRecord();

        obj.addSubRecord(c);
        obj.addSubRecord(e);

        return obj;
    }

    public EscherContainerRecord getSpContainer() {
        return spContainer;
    }

    public ObjRecord getObjRecord() {
        return objRecord;
    }

}
