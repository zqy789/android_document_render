

package com.document.render.office.fc.hssf.formula.eval;

import com.document.render.office.fc.ss.usermodel.FormulaEvaluator;


public final class NotImplementedException extends RuntimeException {

    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException(String message, NotImplementedException cause) {
        super(message, cause);
    }
}
