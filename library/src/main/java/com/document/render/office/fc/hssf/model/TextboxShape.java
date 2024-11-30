


package com.document.render.office.fc.hssf.model;

import com.document.render.office.common.shape.ShapeTypes;
import com.document.render.office.fc.ddf.EscherClientAnchorRecord;
import com.document.render.office.fc.ddf.EscherClientDataRecord;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.fc.ddf.EscherOptRecord;
import com.document.render.office.fc.ddf.EscherProperties;
import com.document.render.office.fc.ddf.EscherRecord;
import com.document.render.office.fc.ddf.EscherSimpleProperty;
import com.document.render.office.fc.ddf.EscherSpRecord;
import com.document.render.office.fc.ddf.EscherTextboxRecord;
import com.document.render.office.fc.hssf.record.CommonObjectDataSubRecord;
import com.document.render.office.fc.hssf.record.EndSubRecord;
import com.document.render.office.fc.hssf.record.ObjRecord;
import com.document.render.office.fc.hssf.record.TextObjectRecord;
import com.document.render.office.fc.hssf.usermodel.HSSFAnchor;
import com.document.render.office.fc.hssf.usermodel.HSSFShape;
import com.document.render.office.fc.hssf.usermodel.HSSFSimpleShape;
import com.document.render.office.fc.hssf.usermodel.HSSFTextbox;



public class TextboxShape
        extends AbstractShape {
    private EscherContainerRecord spContainer;
    private TextObjectRecord textObjectRecord;
    private ObjRecord objRecord;
    private EscherTextboxRecord escherTextbox;


    TextboxShape(HSSFTextbox hssfShape, int shapeId) {
        spContainer = createSpContainer(hssfShape, shapeId);
        objRecord = createObjRecord(hssfShape, shapeId);
        textObjectRecord = createTextObjectRecord(hssfShape, shapeId);
    }


    private ObjRecord createObjRecord(HSSFTextbox hssfShape, int shapeId) {
        HSSFShape shape = hssfShape;

        ObjRecord obj = new ObjRecord();
        CommonObjectDataSubRecord c = new CommonObjectDataSubRecord();
        c.setObjectType((short) ((HSSFSimpleShape) shape).getShapeType());
        c.setObjectId(getCmoObjectId(shapeId));
        c.setLocked(true);
        c.setPrintable(true);
        c.setAutofill(true);
        c.setAutoline(true);
        EndSubRecord e = new EndSubRecord();

        obj.addSubRecord(c);
        obj.addSubRecord(e);

        return obj;
    }


    private EscherContainerRecord createSpContainer(HSSFTextbox hssfShape, int shapeId) {
        HSSFTextbox shape = hssfShape;

        EscherContainerRecord spContainer = new EscherContainerRecord();
        EscherSpRecord sp = new EscherSpRecord();
        EscherOptRecord opt = new EscherOptRecord();
        EscherRecord anchor = new EscherClientAnchorRecord();
        EscherClientDataRecord clientData = new EscherClientDataRecord();
        escherTextbox = new EscherTextboxRecord();

        spContainer.setRecordId(EscherContainerRecord.SP_CONTAINER);
        spContainer.setOptions((short) 0x000F);
        sp.setRecordId(EscherSpRecord.RECORD_ID);
        sp.setOptions((short) ((ShapeTypes.TextBox << 4) | 0x2));

        sp.setShapeId(shapeId);
        sp.setFlags(EscherSpRecord.FLAG_HAVEANCHOR | EscherSpRecord.FLAG_HASSHAPETYPE);
        opt.setRecordId(EscherOptRecord.RECORD_ID);

        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.TEXT__TEXTID, 0));
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.TEXT__TEXTLEFT, shape.getMarginLeft()));
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.TEXT__TEXTRIGHT, shape.getMarginRight()));
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.TEXT__TEXTBOTTOM, shape.getMarginBottom()));
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.TEXT__TEXTTOP, shape.getMarginTop()));

        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.TEXT__WRAPTEXT, 0));
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.TEXT__ANCHORTEXT, 0));
        opt.addEscherProperty(new EscherSimpleProperty(EscherProperties.GROUPSHAPE__PRINT, 0x00080000));

        addStandardOptions(shape, opt);
        HSSFAnchor userAnchor = shape.getAnchor();




        anchor = createAnchor(userAnchor);
        clientData.setRecordId(EscherClientDataRecord.RECORD_ID);
        clientData.setOptions((short) 0x0000);
        escherTextbox.setRecordId(EscherTextboxRecord.RECORD_ID);
        escherTextbox.setOptions((short) 0x0000);

        spContainer.addChildRecord(sp);
        spContainer.addChildRecord(opt);
        spContainer.addChildRecord(anchor);
        spContainer.addChildRecord(clientData);
        spContainer.addChildRecord(escherTextbox);

        return spContainer;
    }


    private TextObjectRecord createTextObjectRecord(HSSFTextbox hssfShape, int shapeId) {
        HSSFTextbox shape = hssfShape;

        TextObjectRecord obj = new TextObjectRecord();
        obj.setHorizontalTextAlignment(hssfShape.getHorizontalAlignment());
        obj.setVerticalTextAlignment(hssfShape.getVerticalAlignment());
        obj.setTextLocked(true);
        obj.setTextOrientation(TextObjectRecord.TEXT_ORIENTATION_NONE);
        obj.setStr(shape.getString());

        return obj;
    }

    public EscherContainerRecord getSpContainer() {
        return spContainer;
    }

    public ObjRecord getObjRecord() {
        return objRecord;
    }

    public TextObjectRecord getTextObjectRecord() {
        return textObjectRecord;
    }

    public EscherRecord getEscherTextbox() {
        return escherTextbox;
    }

}
