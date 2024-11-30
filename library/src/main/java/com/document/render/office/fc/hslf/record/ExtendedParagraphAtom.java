
package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.hslf.model.textproperties.AutoNumberTextProp;
import com.document.render.office.fc.hslf.model.textproperties.TextProp;
import com.document.render.office.fc.util.LittleEndian;

import java.util.LinkedList;


public final class ExtendedParagraphAtom extends RecordAtom {

    public static TextProp[] extendedParagraphPropTypes = new TextProp[]
            {

                    new TextProp(2, 0x01000000, "NumberingType"),
                    new TextProp(2, 0x00800000, "Start")
            };

    private static long _type = 4012;

    private byte[] _header;

    private LinkedList<AutoNumberTextProp> autoNumberList = new LinkedList<AutoNumberTextProp>();


    public ExtendedParagraphAtom(byte[] source, int start, int len) {

        if (len < 8) {
            len = 8;
        }

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        int pos = start + 8;
        while (pos < (start + len) && len >= 28) {
            if ((len - pos) < 4) {
                break;
            }
            AutoNumberTextProp paraProp = new AutoNumberTextProp();
            int mask = LittleEndian.getInt(source, pos);
            if (mask == 0x03000000) {
                mask >>= 1;
            }
            pos += 4;

            if (mask != 0) {

                if (mask == 0x01800000) {
                    pos += 2;
                } else {
                    pos += 4;
                }
                for (int i = 0; i < extendedParagraphPropTypes.length; i++) {
                    int val = 0;
                    if ((mask & extendedParagraphPropTypes[i].getMask()) != 0) {
                        val = LittleEndian.getShort(source, pos);
                        if ("NumberingType".equals(extendedParagraphPropTypes[i].getName())) {
                            paraProp.setNumberingType(val);
                        } else if ("Start".equals(extendedParagraphPropTypes[i].getName())) {
                            paraProp.setStart(val);
                        }
                        pos += extendedParagraphPropTypes[i].getSize();
                    } else {
                        break;
                    }
                }
                if (mask == 0x01800000) {
                    pos += 2;
                }
            }
            autoNumberList.add(paraProp);
            pos += 8;
        }
    }


    protected ExtendedParagraphAtom() {

    }


    public LinkedList<AutoNumberTextProp> getExtendedParagraphPropList() {
        return autoNumberList;
    }


    public long getRecordType() {
        return _type;
    }


    public void dispose() {
        _header = null;
        if (autoNumberList != null) {
            for (AutoNumberTextProp an : autoNumberList) {
                an.dispose();
            }
            autoNumberList.clear();
            autoNumberList = null;
        }
    }
}
