

package com.document.render.office.fc.hssf.formula.ptg;

import com.document.render.office.fc.hssf.formula.EvaluationWorkbook.ExternalSheet;
import com.document.render.office.fc.hssf.formula.FormulaRenderingWorkbook;
import com.document.render.office.fc.hssf.formula.SheetNameFormatter;


final class ExternSheetNameResolver {

    private ExternSheetNameResolver() {

    }

    public static String prependSheetName(FormulaRenderingWorkbook book, int field_1_index_extern_sheet, String cellRefText) {
        ExternalSheet externalSheet = book.getExternalSheet(field_1_index_extern_sheet);
        StringBuffer sb;
        if (externalSheet != null) {
            String wbName = externalSheet.getWorkbookName();
            String sheetName = externalSheet.getSheetName();
            sb = new StringBuffer(wbName.length() + sheetName.length() + cellRefText.length() + 4);
            SheetNameFormatter.appendFormat(sb, wbName, sheetName);
        } else {
            String sheetName = book.getSheetNameByExternSheet(field_1_index_extern_sheet);
            sb = new StringBuffer(sheetName.length() + cellRefText.length() + 4);
            if (sheetName.length() < 1) {

                sb.append("#REF");
            } else {
                SheetNameFormatter.appendFormat(sb, sheetName);
            }
        }
        sb.append('!');
        sb.append(cellRefText);
        return sb.toString();
    }
}
