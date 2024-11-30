
package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.ShapeKit;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.ss.model.XLSModel.AWorkbook;


public class HSSFAutoShape extends HSSFTextbox {
    private Float adjusts[];


    public HSSFAutoShape(AWorkbook workbook, EscherContainerRecord escherContainer, HSSFShape parent,
                         HSSFAnchor anchor, int shapeType) {
        super(escherContainer, parent, anchor);

        setShapeType(shapeType);
        processLineWidth();
        processLine(escherContainer, workbook);
        processSimpleBackground(escherContainer, workbook);
        processRotationAndFlip(escherContainer);


        String unicodeText = ShapeKit.getUnicodeGeoText(escherContainer);
        if (unicodeText != null && unicodeText.length() > 0) {
            setString(new HSSFRichTextString(unicodeText));
            setWordArt(true);

            setNoFill(true);
            setFontColor(getFillColor());
        }
    }

    public Float[] getAdjustmentValue() {
        return adjusts;
    }

    public void setAdjustmentValue(EscherContainerRecord escherContainer) {
        adjusts = ShapeKit.getAdjustmentValue(escherContainer);
    }
}
