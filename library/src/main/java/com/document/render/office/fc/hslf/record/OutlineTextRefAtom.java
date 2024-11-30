

package com.document.render.office.fc.hslf.record;


import com.document.render.office.fc.util.LittleEndian;

import java.io.IOException;
import java.io.OutputStream;



public final class OutlineTextRefAtom extends RecordAtom {
    
    private byte[] _header;

    
    private int _index;

    
    protected OutlineTextRefAtom(byte[] source, int start, int len) {
        
        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);

        
        _index = LittleEndian.getInt(source, start + 8);
    }

    
    protected OutlineTextRefAtom() {
        _index = 0;

        _header = new byte[8];
        LittleEndian.putUShort(_header, 0, 0);
        LittleEndian.putUShort(_header, 2, (int) getRecordType());
        LittleEndian.putInt(_header, 4, 4);
    }

    public long getRecordType() {
        return RecordTypes.OutlineTextRefAtom.typeID;
    }

    
    public void writeOut(OutputStream out) throws IOException {
        out.write(_header);

        byte[] recdata = new byte[4];
        LittleEndian.putInt(recdata, 0, _index);
        out.write(recdata);
    }

    
    public int getTextIndex() {
        return _index;
    }

    
    public void setTextIndex(int idx) {
        _index = idx;
    }

    
    public void dispose() {
        _header = null;
    }

}
