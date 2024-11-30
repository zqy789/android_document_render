
package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.util.Internal;


@Internal
public enum SubdocumentType {
    MAIN(FIBLongHandler.CCPTEXT),

    FOOTNOTE(FIBLongHandler.CCPFTN),

    HEADER(FIBLongHandler.CCPHDD),

    MACRO(FIBLongHandler.CCPMCR),

    ANNOTATION(FIBLongHandler.CCPATN),

    ENDNOTE(FIBLongHandler.CCPEDN),

    TEXTBOX(FIBLongHandler.CCPTXBX),

    HEADER_TEXTBOX(FIBLongHandler.CCPHDRTXBX);


    public static final SubdocumentType[] ORDERED = new SubdocumentType[]{
            MAIN, FOOTNOTE, HEADER, MACRO, ANNOTATION, ENDNOTE, TEXTBOX,
            HEADER_TEXTBOX};

    private final int fibLongFieldIndex;

    private SubdocumentType(int fibLongFieldIndex) {
        this.fibLongFieldIndex = fibLongFieldIndex;
    }

    public int getFibLongFieldIndex() {
        return fibLongFieldIndex;
    }

}
