
package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.ShapeKit;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.ss.model.XLSModel.AWorkbook;


public class HSSFLine extends HSSFSimpleShape {

    private Float adjusts[];

    public HSSFLine(AWorkbook workbook, EscherContainerRecord escherContainer,
                    HSSFShape parent, HSSFAnchor anchor, int shapeType) {
        super(escherContainer, parent, anchor);
        setShapeType(shapeType);
        processLineWidth();
        processLine(escherContainer, workbook);
        processArrow(escherContainer);
        setAdjustmentValue(escherContainer);
        processRotationAndFlip(escherContainer);
    }

    public Float[] getAdjustmentValue() {
        return adjusts;
    }

    public void setAdjustmentValue(EscherContainerRecord escherContainer) {
        adjusts = ShapeKit.getAdjustmentValue(escherContainer);
    }
}
