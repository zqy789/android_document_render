

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;

import java.util.ArrayList;



public class ExObjList extends RecordContainer {
    private static long _type = 1033;
    private byte[] _header;

    private ExObjListAtom exObjListAtom;


    protected ExObjList(byte[] source, int start, int len) {

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        _children = Record.findChildRecords(source, start + 8, len - 8);
        findInterestingChildren();
    }


    public ExObjList() {
        _header = new byte[8];
        _children = new Record[1];


        _header[0] = 0x0f;
        LittleEndian.putShort(_header, 2, (short) _type);


        _children[0] = new ExObjListAtom();
        findInterestingChildren();
    }


    public ExObjListAtom getExObjListAtom() {
        return exObjListAtom;
    }


    public ExHyperlink[] getExHyperlinks() {
        ArrayList<ExHyperlink> links = new ArrayList<ExHyperlink>();
        for (int i = 0; i < _children.length; i++) {
            if (_children[i] instanceof ExHyperlink) {
                links.add((ExHyperlink) _children[i]);
            }
        }

        return links.toArray(new ExHyperlink[links.size()]);
    }


    private void findInterestingChildren() {

        if (_children[0] instanceof ExObjListAtom) {
            exObjListAtom = (ExObjListAtom) _children[0];
        } else {
            throw new IllegalStateException(
                    "First child record wasn't a ExObjListAtom, was of type "
                            + _children[0].getRecordType());
        }
    }


    public long getRecordType() {
        return _type;
    }



    public ExHyperlink get(int id) {
        for (int i = 0; i < _children.length; i++) {
            if (_children[i] instanceof ExHyperlink) {
                ExHyperlink rec = (ExHyperlink) _children[i];
                if (rec.getExHyperlinkAtom().getNumber() == id) {
                    return rec;
                }
            }
        }
        return null;
    }


    public void dispose() {
        super.dispose();
        _header = null;
        if (exObjListAtom != null) {
            exObjListAtom.dispose();
            exObjListAtom = null;
        }
    }
}
