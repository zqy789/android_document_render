
package com.document.render.office.fc.ss.usermodel;

import com.document.render.office.fc.hssf.usermodel.IClientAnchor;


public interface CreationHelper {

    RichTextString createRichTextString(String text);


    DataFormat createDataFormat();


    IHyperlink createHyperlink(int type);


    FormulaEvaluator createFormulaEvaluator();

    IClientAnchor createClientAnchor();
}
