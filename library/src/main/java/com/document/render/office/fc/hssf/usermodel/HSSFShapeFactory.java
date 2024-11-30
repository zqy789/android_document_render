
package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.common.shape.ShapeTypes;
import com.document.render.office.fc.ShapeKit;
import com.document.render.office.fc.ddf.EscherChildAnchorRecord;
import com.document.render.office.fc.ddf.EscherClientAnchorRecord;
import com.document.render.office.fc.ddf.EscherClientDataRecord;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.fc.ddf.EscherOptRecord;
import com.document.render.office.fc.ddf.EscherProperties;
import com.document.render.office.fc.ddf.EscherProperty;
import com.document.render.office.fc.ddf.EscherPropertyFactory;
import com.document.render.office.fc.ddf.EscherRecord;
import com.document.render.office.fc.ddf.EscherSimpleProperty;
import com.document.render.office.fc.ddf.EscherSpRecord;
import com.document.render.office.fc.ddf.EscherSpgrRecord;
import com.document.render.office.fc.hssf.record.CommonObjectDataSubRecord;
import com.document.render.office.fc.hssf.record.EmbeddedObjectRefSubRecord;
import com.document.render.office.fc.hssf.record.ObjRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.SubRecord;
import com.document.render.office.ss.model.XLSModel.AWorkbook;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


public final class HSSFShapeFactory {
    public static HSSFShape createShape(AWorkbook workbook, Map<EscherRecord, Record> shapeToObj,
                                        EscherContainerRecord spContainer, HSSFShape parent) {
        if (spContainer.getRecordId() == EscherContainerRecord.SPGR_CONTAINER) {
            return createShapeGroup(workbook, shapeToObj, spContainer, parent);
        }
        return createSimpeShape(workbook, shapeToObj, spContainer, parent);
    }

    public static HSSFShapeGroup createShapeGroup(AWorkbook workbook, Map<EscherRecord, Record> shapeToObj,
                                                  EscherContainerRecord spContainer, HSSFShape parent) {
        HSSFShapeGroup group = null;
        List<EscherRecord> childRecords = spContainer.getChildRecords();
        if (childRecords.size() > 0) {
            EscherContainerRecord groupContainer = (EscherContainerRecord) childRecords.get(0);

            HSSFAnchor anchor = null;
            if (parent == null) {
                EscherClientAnchorRecord anchorRecord = (EscherClientAnchorRecord) ShapeKit.getEscherChild(
                        groupContainer, EscherClientAnchorRecord.RECORD_ID);
                if (anchorRecord != null && anchorRecord.getCol2() <= 255 && anchorRecord.getRow2() <= 65535) {
                    anchor = HSSFShape.toClientAnchor(anchorRecord);
                }
            } else {
                EscherChildAnchorRecord childRecord = (EscherChildAnchorRecord) ShapeKit.getEscherChild(
                        groupContainer, EscherChildAnchorRecord.RECORD_ID);
                if (childRecord != null) {
                    anchor = HSSFShape.toChildAnchor(childRecord);
                }
            }
            if (anchor == null) {
                anchor = new HSSFClientAnchor();
            }


            EscherRecord opt = ShapeKit.getEscherChild(groupContainer, (short) 0xF122);
            if (opt != null) {
                EscherPropertyFactory f = new EscherPropertyFactory();
                List<EscherProperty> props = f.createProperties(opt.serialize(), 8, opt.getInstance());
                EscherSimpleProperty p = (EscherSimpleProperty) props.get(0);
                if (p.getPropertyNumber() != 0x39F || p.getPropertyValue() != 1) {
                    group = new HSSFShapeGroup(groupContainer, parent, anchor);
                }
            } else {
                group = new HSSFShapeGroup(groupContainer, parent, anchor);
            }

            EscherSpgrRecord spgrRecord = (EscherSpgrRecord) ShapeKit.getEscherChild(
                    groupContainer, EscherSpgrRecord.RECORD_ID);
            if (spgrRecord != null) {
                group.setCoordinates(spgrRecord.getRectX1(),
                        spgrRecord.getRectY1(), spgrRecord.getRectX2(), spgrRecord.getRectY2());
            }

            for (int i = 1; i < childRecords.size(); i++) {
                HSSFShape shape = createShape(workbook, shapeToObj, (EscherContainerRecord) childRecords.get(i), group);
                group.addChildShape(shape);
            }
        }
        return group;
    }

    public static HSSFShape createSimpeShape(AWorkbook workbook, Map<EscherRecord, Record> shapeToObj,
                                             EscherContainerRecord spContainer, HSSFShape parent) {
        HSSFShape shape = null;
        HSSFAnchor anchor = null;

        if (parent == null) {
            EscherClientAnchorRecord anchorRecord = (EscherClientAnchorRecord) ShapeKit.getEscherChild(
                    spContainer, EscherClientAnchorRecord.RECORD_ID);
            if (anchorRecord != null && anchorRecord.getCol2() <= 255 && anchorRecord.getRow2() <= 65535) {
                anchor = HSSFShape.toClientAnchor(anchorRecord);
            }
        } else {
            EscherChildAnchorRecord childRecord = (EscherChildAnchorRecord) ShapeKit.getEscherChild(
                    spContainer, EscherChildAnchorRecord.RECORD_ID);
            if (childRecord != null) {
                anchor = HSSFShape.toChildAnchor(childRecord);
            }
        }


        EscherSpRecord spRecord = spContainer.getChildById(EscherSpRecord.RECORD_ID);
        if (spRecord == null) {
            return null;
        }

        int type = spRecord.getOptions() >> 4;
        switch (type) {
            case ShapeTypes.TextBox:
                if (shapeToObj != null && shapeToObj.size() > 0) {
                    EscherClientDataRecord escherClientDataRecord =
                            (EscherClientDataRecord) ShapeKit.getEscherChild(spContainer, EscherClientDataRecord.RECORD_ID);
                    Record record = shapeToObj.get(escherClientDataRecord);

                    if (record instanceof ObjRecord && ((ObjRecord) record).getSubRecords().get(0) instanceof CommonObjectDataSubRecord) {
                        CommonObjectDataSubRecord commonObjectDataSubRecord = (CommonObjectDataSubRecord) ((ObjRecord) record).getSubRecords().get(0);
                        if (commonObjectDataSubRecord.getObjectType() != CommonObjectDataSubRecord.OBJECT_TYPE_COMMENT) {

                            shape = new HSSFAutoShape(workbook, spContainer, parent, anchor, type);
                        }
                    }
                    break;
                }

            case ShapeTypes.PictureFrame:
                EscherOptRecord opt = (EscherOptRecord) ShapeKit.getEscherChild(
                        spContainer, EscherOptRecord.RECORD_ID);
                EscherSimpleProperty prop = (EscherSimpleProperty) opt.lookup(
                        EscherProperties.BLIP__BLIPTODISPLAY);
                shape = new HSSFPicture(workbook, spContainer, parent, anchor, opt);
                if (prop != null) {
                    ((HSSFPicture) shape).setPictureIndex(prop.getPropertyValue());
                }
                break;

            case ShapeTypes.HostControl:

                shape = new HSSFChart(workbook, spContainer, parent, anchor);
                break;

            case ShapeTypes.Line:
            case ShapeTypes.StraightConnector1:
            case ShapeTypes.BentConnector2:
            case ShapeTypes.BentConnector3:
            case ShapeTypes.CurvedConnector3:
                shape = new HSSFLine(workbook, spContainer, parent, anchor, type);
                break;

            case ShapeTypes.NotPrimitive:
            case ShapeTypes.NotchedCircularArrow:
                shape = new HSSFFreeform(workbook, spContainer, parent, anchor, type);
                break;

            default:
                shape = new HSSFAutoShape(workbook, spContainer, parent, anchor, type);
                ((HSSFAutoShape) shape).setAdjustmentValue(spContainer);
                break;
        }
        return shape;
    }































































































    private static boolean isEmbeddedObject(ObjRecord obj) {
        Iterator<SubRecord> subRecordIter = obj.getSubRecords().iterator();
        while (subRecordIter.hasNext()) {
            SubRecord sub = subRecordIter.next();
            if (sub instanceof EmbeddedObjectRefSubRecord) {
                return true;
            }
        }
        return false;
    }

}
