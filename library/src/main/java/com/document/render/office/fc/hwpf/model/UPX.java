

package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.util.Internal;

import java.util.Arrays;


@Internal
public final class UPX {
    private byte[] _upx;

    public UPX(byte[] upx) {
        _upx = upx;
    }

    public byte[] getUPX() {
        return _upx;
    }

    public int size() {
        return _upx.length;
    }

    public boolean equals(Object o) {
        UPX upx = (UPX) o;
        return Arrays.equals(_upx, upx._upx);
    }
}
