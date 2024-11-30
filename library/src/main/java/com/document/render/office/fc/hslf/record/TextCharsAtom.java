

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.StringUtil;




public final class TextCharsAtom extends RecordAtom {
    private static long _type = 4000l;
    private byte[] _header;

    private byte[] _text;


    protected TextCharsAtom(byte[] source, int start, int len) {

        if (len < 8) {
            len = 8;
        }


        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        _text = new byte[len - 8];
        System.arraycopy(source, start + 8, _text, 0, len - 8);
    }


    public TextCharsAtom() {

        _header = new byte[]{0, 0, 0xA0 - 256, 0x0f, 0, 0, 0, 0};

        _text = new byte[0];
    }




    public String getText() {
        return StringUtil.getFromUnicodeLE(_text);
    }


    public void setText(String text) {

        _text = new byte[text.length() * 2];
        StringUtil.putUnicodeLE(text, _text, 0);


        LittleEndian.putInt(_header, 4, _text.length);
    }


    public long getRecordType() {
        return _type;
    }



    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("TextCharsAtom:\n");
        out.append(HexDump.dump(_text, 0, 0));
        return out.toString();
    }


    public void dispose() {
        _header = null;
        _text = null;
    }
}
