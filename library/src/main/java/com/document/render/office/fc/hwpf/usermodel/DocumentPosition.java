

package com.document.render.office.fc.hwpf.usermodel;

import com.document.render.office.fc.hwpf.HWPFDocument;

public final class DocumentPosition extends Range {
    public DocumentPosition(HWPFDocument doc, int pos) {
        super(pos, pos, doc);
    }

}
