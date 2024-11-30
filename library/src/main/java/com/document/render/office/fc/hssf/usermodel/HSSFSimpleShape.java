

package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.constant.MainConstant;
import com.document.render.office.fc.ShapeKit;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.java.awt.Color;
import com.document.render.office.ss.model.XLSModel.AWorkbook;



public class HSSFSimpleShape extends HSSFShape {



    public final static short OBJECT_TYPE_LINE = 1;
    public final static short OBJECT_TYPE_RECTANGLE = 2;
    public final static short OBJECT_TYPE_OVAL = 3;




    public final static short OBJECT_TYPE_PICTURE = 8;










    public final static short OBJECT_TYPE_COMBO_BOX = 20;
    public final static short OBJECT_TYPE_COMMENT = 25;


    public HSSFSimpleShape(EscherContainerRecord escherContainer, HSSFShape parent, HSSFAnchor anchor) {
        super(escherContainer, parent, anchor);
    }

    public void processLine(EscherContainerRecord escherContainer, AWorkbook workbook) {
        if (ShapeKit.hasLine(escherContainer)) {
            Color color = ShapeKit.getLineColor(escherContainer, workbook, MainConstant.APPLICATION_TYPE_SS);
            if (color != null) {
                setLineStyleColor(color.getRGB());
            } else {
                setNoBorder(true);
            }

            setLineStyle(ShapeKit.getLineDashing(escherContainer));
        } else {
            setNoBorder(true);
        }
    }

    public void processArrow(EscherContainerRecord escherContainer) {
        setStartArrow((byte) ShapeKit.getStartArrowType(escherContainer),
                ShapeKit.getStartArrowWidth(escherContainer),
                ShapeKit.getStartArrowLength(escherContainer));

        setEndArrow((byte) ShapeKit.getEndArrowType(escherContainer),
                ShapeKit.getEndArrowWidth(escherContainer),
                ShapeKit.getEndArrowLength(escherContainer));
    }

    public void processRotationAndFlip(EscherContainerRecord escherContainer) {
        setRotation(ShapeKit.getRotation(escherContainer));
        setFilpH(ShapeKit.getFlipHorizontal(escherContainer));
        setFlipV(ShapeKit.getFlipVertical(escherContainer));
    }
}
