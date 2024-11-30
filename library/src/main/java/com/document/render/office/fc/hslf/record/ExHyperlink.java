
package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.POILogger;



public class ExHyperlink extends RecordContainer {
    private static long _type = 4055;
    private byte[] _header;

    private ExHyperlinkAtom linkAtom;
    private CString linkDetailsA;
    private CString linkDetailsB;


    protected ExHyperlink(byte[] source, int start, int len) {

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        _children = Record.findChildRecords(source, start + 8, len - 8);
        findInterestingChildren();
    }


    public ExHyperlink() {
        _header = new byte[8];
        _children = new Record[3];


        _header[0] = 0x0f;
        LittleEndian.putShort(_header, 2, (short) _type);


        CString csa = new CString();
        CString csb = new CString();
        csa.setOptions(0x00);
        csb.setOptions(0x10);
        _children[0] = new ExHyperlinkAtom();
        _children[1] = csa;
        _children[2] = csb;
        findInterestingChildren();
    }


    public ExHyperlinkAtom getExHyperlinkAtom() {
        return linkAtom;
    }


    public String getLinkURL() {
        return linkDetailsB == null ? null : linkDetailsB.getText();
    }


    public void setLinkURL(String url) {
        if (linkDetailsB != null) {
            linkDetailsB.setText(url);
        }
    }


    public String getLinkTitle() {
        return linkDetailsA == null ? null : linkDetailsA.getText();
    }

    public void setLinkTitle(String title) {
        if (linkDetailsA != null) {
            linkDetailsA.setText(title);
        }
    }


    public String _getDetailsA() {
        return linkDetailsA == null ? null : linkDetailsA.getText();
    }


    public String _getDetailsB() {
        return linkDetailsB == null ? null : linkDetailsB.getText();
    }


    private void findInterestingChildren() {


        if (_children[0] instanceof ExHyperlinkAtom) {
            linkAtom = (ExHyperlinkAtom) _children[0];
        } else {
            logger.log(POILogger.ERROR, "First child record wasn't a ExHyperlinkAtom, was of type " + _children[0].getRecordType());
        }

        for (int i = 1; i < _children.length; i++) {
            if (_children[i] instanceof CString) {
                if (linkDetailsA == null) linkDetailsA = (CString) _children[i];
                else linkDetailsB = (CString) _children[i];
            } else {
                logger.log(POILogger.ERROR, "Record after ExHyperlinkAtom wasn't a CString, was of type " + _children[1].getRecordType());
            }

        }
    }


    public long getRecordType() {
        return _type;
    }


    public void dispose() {
        _header = null;
        if (linkDetailsA != null) {
            linkDetailsA.dispose();
            linkDetailsA = null;
        }
        if (linkDetailsB != null) {
            linkDetailsB.dispose();
            linkDetailsB = null;
        }
        if (linkAtom != null) {
            linkAtom.dispose();
            linkAtom = null;
        }
    }
}
