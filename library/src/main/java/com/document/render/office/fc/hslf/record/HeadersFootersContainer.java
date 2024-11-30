

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.POILogger;



public final class HeadersFootersContainer extends RecordContainer {


    public static final short SlideHeadersFootersContainer = 0x3F;

    public static final short NotesHeadersFootersContainer = 0x4F;

    public static final int USERDATEATOM = 0;
    public static final int HEADERATOM = 1;
    public static final int FOOTERATOM = 2;
    private byte[] _header;
    private HeadersFootersAtom hdAtom;
    private CString csDate, csHeader, csFooter;

    protected HeadersFootersContainer(byte[] source, int start, int len) {

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);

        _children = Record.findChildRecords(source, start + 8, len - 8);
        for (int i = 0; i < _children.length; i++) {
            if (_children[i] instanceof HeadersFootersAtom)
                hdAtom = (HeadersFootersAtom) _children[i];
            else if (_children[i] instanceof CString) {
                CString cs = (CString) _children[i];
                int opts = cs.getOptions() >> 4;
                switch (opts) {
                    case USERDATEATOM:
                        csDate = cs;
                        break;
                    case HEADERATOM:
                        csHeader = cs;
                        break;
                    case FOOTERATOM:
                        csFooter = cs;
                        break;
                    default:
                        logger.log(POILogger.WARN, "Unexpected CString.Options in HeadersFootersContainer: " + opts);
                        break;
                }
            } else {
                logger.log(POILogger.WARN, "Unexpected record in HeadersFootersContainer: " + _children[i]);
            }
        }

    }

    public HeadersFootersContainer(short options) {
        _header = new byte[8];
        LittleEndian.putShort(_header, 0, options);
        LittleEndian.putShort(_header, 2, (short) getRecordType());

        hdAtom = new HeadersFootersAtom();
        _children = new Record[]{
                hdAtom
        };
        csDate = csHeader = csFooter = null;

    }


    public long getRecordType() {
        return RecordTypes.HeadersFooters.typeID;
    }


    public int getOptions() {
        return LittleEndian.getShort(_header, 0);
    }


    public HeadersFootersAtom getHeadersFootersAtom() {
        return hdAtom;
    }


    public CString getUserDateAtom() {
        return csDate;
    }


    public CString getHeaderAtom() {
        return csHeader;
    }


    public CString getFooterAtom() {
        return csFooter;
    }


    public CString addUserDateAtom() {
        if (csDate != null) return csDate;

        csDate = new CString();
        csDate.setOptions(USERDATEATOM << 4);

        addChildAfter(csDate, hdAtom);

        return csDate;
    }


    public CString addHeaderAtom() {
        if (csHeader != null) return csHeader;

        csHeader = new CString();
        csHeader.setOptions(HEADERATOM << 4);

        Record r = hdAtom;
        if (csDate != null) r = hdAtom;
        addChildAfter(csHeader, r);

        return csHeader;
    }


    public CString addFooterAtom() {
        if (csFooter != null) return csFooter;

        csFooter = new CString();
        csFooter.setOptions(FOOTERATOM << 4);

        Record r = hdAtom;
        if (csHeader != null) r = csHeader;
        else if (csDate != null) r = csDate;
        addChildAfter(csFooter, r);

        return csFooter;
    }


    public void dispose() {
        _header = null;
        if (hdAtom != null) {
            hdAtom.dispose();
            hdAtom = null;
        }
        if (csDate != null) {
            csDate.dispose();
            csDate = null;
        }
        if (csHeader != null) {
            csHeader.dispose();
            csHeader = null;
        }
        if (csFooter != null) {
            csFooter.dispose();
            csFooter = null;
        }
    }
}
