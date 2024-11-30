

package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.hwpf.model.types.DOPAbstractType;
import com.document.render.office.fc.util.Internal;
import com.document.render.office.fc.util.LittleEndian;



@Internal
public final class DocumentProperties extends DOPAbstractType {

    private byte[] _preserved;


    public DocumentProperties(byte[] tableStream, int offset) {
        this(tableStream, offset, DOPAbstractType.getSize());
    }

    public DocumentProperties(byte[] tableStream, int offset, int length) {
        super.fillFields(tableStream, offset);

        final int supportedSize = DOPAbstractType.getSize();
        if (length != supportedSize) {
            this._preserved = LittleEndian.getByteArray(tableStream, offset
                    + supportedSize, length - supportedSize);
        } else {
            _preserved = new byte[0];
        }
    }

    @Override
    public void serialize(byte[] data, int offset) {
        super.serialize(data, offset);
    }
}
