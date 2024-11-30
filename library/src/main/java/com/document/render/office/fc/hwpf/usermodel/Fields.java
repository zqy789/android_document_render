
package com.document.render.office.fc.hwpf.usermodel;

import com.document.render.office.fc.hwpf.model.FieldsDocumentPart;

import java.util.Collection;



public interface Fields {
    Field getFieldByStartOffset(FieldsDocumentPart documentPart, int offset);

    Collection<Field> getFields(FieldsDocumentPart part);
}
