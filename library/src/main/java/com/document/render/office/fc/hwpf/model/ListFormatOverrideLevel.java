

package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.Internal;
import com.document.render.office.fc.util.LittleEndian;

import java.util.Arrays;


@Internal
public final class ListFormatOverrideLevel {
    private static final int BASE_SIZE = 8;
    private static BitField _ilvl = BitFieldFactory.getInstance(0xf);
    private static BitField _fStartAt = BitFieldFactory.getInstance(0x10);
    private static BitField _fFormatting = BitFieldFactory.getInstance(0x20);
    int _iStartAt;
    byte _info;
    byte[] _reserved = new byte[3];
    POIListLevel _lvl;

    public ListFormatOverrideLevel(byte[] buf, int offset) {
        _iStartAt = LittleEndian.getInt(buf, offset);
        offset += LittleEndian.INT_SIZE;
        _info = buf[offset++];
        System.arraycopy(buf, offset, _reserved, 0, _reserved.length);
        offset += _reserved.length;

        if (_fFormatting.getValue(_info) > 0) {
            _lvl = new POIListLevel(buf, offset);
        }
    }

    public POIListLevel getLevel() {
        return _lvl;
    }

    public int getLevelNum() {
        return _ilvl.getValue(_info);
    }

    public boolean isFormatting() {
        return _fFormatting.getValue(_info) != 0;
    }

    public boolean isStartAt() {
        return _fStartAt.getValue(_info) != 0;
    }

    public int getSizeInBytes() {
        return (_lvl == null ? BASE_SIZE : BASE_SIZE + _lvl.getSizeInBytes());
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        ListFormatOverrideLevel lfolvl = (ListFormatOverrideLevel) obj;
        boolean lvlEquality = false;
        if (_lvl != null) {
            lvlEquality = _lvl.equals(lfolvl._lvl);
        } else {
            lvlEquality = lfolvl._lvl == null;
        }

        return lvlEquality && lfolvl._iStartAt == _iStartAt && lfolvl._info == _info
                && Arrays.equals(lfolvl._reserved, _reserved);
    }

    public byte[] toByteArray() {
        byte[] buf = new byte[getSizeInBytes()];

        int offset = 0;
        LittleEndian.putInt(buf, _iStartAt);
        offset += LittleEndian.INT_SIZE;
        buf[offset++] = _info;
        System.arraycopy(_reserved, 0, buf, offset, 3);
        offset += 3;

        if (_lvl != null) {
            byte[] levelBuf = _lvl.toByteArray();
            System.arraycopy(levelBuf, 0, buf, offset, levelBuf.length);
        }

        return buf;
    }

    public int getIStartAt() {
        return _iStartAt;
    }
}
