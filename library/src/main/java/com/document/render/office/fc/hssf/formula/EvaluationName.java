

package com.document.render.office.fc.hssf.formula;

import com.document.render.office.fc.hssf.formula.ptg.NamePtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;


public interface EvaluationName {

    String getNameText();

    boolean isFunctionName();

    boolean hasFormula();

    Ptg[] getNameDefinition();

    boolean isRange();

    NamePtg createPtg();
}
