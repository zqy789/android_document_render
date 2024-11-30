

package com.document.render.office.fc.hwpf.sprm;

import com.document.render.office.fc.util.Internal;


@Internal
public final class SprmIterator {
    int _offset;
    private byte[] _grpprl;

    public SprmIterator(byte[] grpprl, int offset) {
        _grpprl = grpprl;
        _offset = offset;
    }

    public boolean hasNext() {

        return _offset < (_grpprl.length - 1);
    }

    public SprmOperation next() {
        SprmOperation op = new SprmOperation(_grpprl, _offset);
        _offset += op.size();
        return op;
    }

}
