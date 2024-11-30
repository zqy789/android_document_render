
package com.document.render.office.fc.hssf.usermodel;

import android.graphics.Path;
import android.graphics.PointF;

import com.document.render.office.common.autoshape.pathbuilder.ArrowPathAndTail;
import com.document.render.office.fc.ShapeKit;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.ss.model.XLSModel.AWorkbook;


public class HSSFFreeform extends HSSFAutoShape {
    public HSSFFreeform(AWorkbook workbook, EscherContainerRecord escherContainer,
                        HSSFShape parent, HSSFAnchor anchor, int shapeType) {
        super(workbook, escherContainer, parent, anchor, shapeType);
        processLineWidth();
        processArrow(escherContainer);
    }


    public Path[] getFreeformPath(Rectangle rect, PointF startArrowTailCenter, byte startArrowType, PointF endArrowTailCenter, byte endArrowType) {
        return ShapeKit.getFreeformPath(escherContainer, rect, startArrowTailCenter, startArrowType, endArrowTailCenter, endArrowType);
    }

    public ArrowPathAndTail getStartArrowPath(Rectangle rect) {
        return ShapeKit.getStartArrowPathAndTail(escherContainer, rect);
    }

    public ArrowPathAndTail getEndArrowPath(Rectangle rect) {
        return ShapeKit.getEndArrowPathAndTail(escherContainer, rect);
    }
}
