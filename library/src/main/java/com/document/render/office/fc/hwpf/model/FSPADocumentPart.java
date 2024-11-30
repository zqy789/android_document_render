

package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.util.Internal;

@Internal
public enum FSPADocumentPart {
    HEADER(FIBFieldHandler.PLCSPAHDR),

    MAIN(FIBFieldHandler.PLCSPAMOM);

    private final int fibFieldsField;

    private FSPADocumentPart(final int fibHandlerField) {
        this.fibFieldsField = fibHandlerField;
    }

    public int getFibFieldsField() {
        return fibFieldsField;
    }
}
