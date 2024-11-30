

package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.ss.usermodel.CreationHelper;

public class HSSFCreationHelper implements CreationHelper {
    private HSSFWorkbook workbook;
    private HSSFDataFormat dataFormat;

    HSSFCreationHelper(HSSFWorkbook wb) {
        workbook = wb;


        dataFormat = new HSSFDataFormat(workbook.getWorkbook());
    }

    public HSSFRichTextString createRichTextString(String text) {
        return new HSSFRichTextString(text);
    }

    public HSSFDataFormat createDataFormat() {
        return dataFormat;
    }

    public HSSFHyperlink createHyperlink(int type) {
        return new HSSFHyperlink(type);
    }


    public HSSFFormulaEvaluator createFormulaEvaluator() {

        return null;
    }


    public HSSFClientAnchor createClientAnchor() {
        return new HSSFClientAnchor();
    }
}
