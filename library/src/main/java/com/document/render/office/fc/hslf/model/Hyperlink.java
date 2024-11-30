

package com.document.render.office.fc.hslf.model;

import com.document.render.office.fc.ddf.EscherClientDataRecord;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.fc.ddf.EscherRecord;
import com.document.render.office.fc.hslf.record.ExHyperlink;
import com.document.render.office.fc.hslf.record.ExObjList;
import com.document.render.office.fc.hslf.record.InteractiveInfo;
import com.document.render.office.fc.hslf.record.InteractiveInfoAtom;
import com.document.render.office.fc.hslf.record.Record;
import com.document.render.office.fc.hslf.record.TxInteractiveInfoAtom;
import com.document.render.office.fc.hslf.usermodel.SlideShow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public final class Hyperlink {
    public static final byte LINK_NEXTSLIDE = InteractiveInfoAtom.LINK_NextSlide;
    public static final byte LINK_PREVIOUSSLIDE = InteractiveInfoAtom.LINK_PreviousSlide;
    public static final byte LINK_FIRSTSLIDE = InteractiveInfoAtom.LINK_FirstSlide;
    public static final byte LINK_LASTSLIDE = InteractiveInfoAtom.LINK_LastSlide;
    public static final byte LINK_URL = InteractiveInfoAtom.LINK_Url;
    public static final byte LINK_NULL = InteractiveInfoAtom.LINK_NULL;

    private int id = -1;
    private int type;
    private String address;
    private String title;
    private int startIndex, endIndex;


    protected static Hyperlink[] find(TextRun run) {
        ArrayList lst = new ArrayList();
        SlideShow ppt = run.getSheet().getSlideShow();

        ExObjList exobj = ppt.getDocumentRecord().getExObjList();
        if (exobj == null) {
            return null;
        }
        Record[] records = run._records;
        if (records != null)
            find(records, exobj, lst);

        Hyperlink[] links = null;
        if (lst.size() > 0) {
            links = new Hyperlink[lst.size()];
            lst.toArray(links);
        }
        return links;
    }


    protected static Hyperlink find(Shape shape) {
        ArrayList lst = new ArrayList();
        SlideShow ppt = shape.getSheet().getSlideShow();

        ExObjList exobj = ppt.getDocumentRecord().getExObjList();
        if (exobj == null) {
            return null;
        }

        EscherContainerRecord spContainer = shape.getSpContainer();
        for (Iterator<EscherRecord> it = spContainer.getChildIterator(); it.hasNext(); ) {
            EscherRecord obj = it.next();
            if (obj.getRecordId() == EscherClientDataRecord.RECORD_ID) {
                byte[] data = obj.serialize();
                Record[] records = Record.findChildRecords(data, 8, data.length - 8);
                if (records != null)
                    find(records, exobj, lst);
            }
        }

        return lst.size() == 1 ? (Hyperlink) lst.get(0) : null;
    }

    private static void find(Record[] records, ExObjList exobj, List out) {
        for (int i = 0; i < records.length; i++) {

            if (records[i] instanceof InteractiveInfo) {
                InteractiveInfo hldr = (InteractiveInfo) records[i];
                InteractiveInfoAtom info = hldr.getInteractiveInfoAtom();
                int id = info.getHyperlinkID();
                ExHyperlink linkRecord = exobj.get(id);
                if (linkRecord != null) {
                    Hyperlink link = new Hyperlink();
                    link.title = linkRecord.getLinkTitle();
                    link.address = linkRecord.getLinkURL();
                    link.type = info.getAction();

                    if (++i < records.length && records[i] instanceof TxInteractiveInfoAtom) {
                        TxInteractiveInfoAtom txinfo = (TxInteractiveInfoAtom) records[i];
                        link.startIndex = txinfo.getStartIndex();
                        link.endIndex = txinfo.getEndIndex();
                    }
                    out.add(link);
                }
            }
        }
    }


    public int getType() {
        return type;
    }

    public void setType(int val) {
        type = val;
        switch (type) {
            case LINK_NEXTSLIDE:
                title = "NEXT";
                address = "1,-1,NEXT";
                break;
            case LINK_PREVIOUSSLIDE:
                title = "PREV";
                address = "1,-1,PREV";
                break;
            case LINK_FIRSTSLIDE:
                title = "FIRST";
                address = "1,-1,FIRST";
                break;
            case LINK_LASTSLIDE:
                title = "LAST";
                address = "1,-1,LAST";
                break;
            default:
                title = "";
                address = "";
                break;
        }
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String str) {
        address = str;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String str) {
        title = str;
    }


    public int getStartIndex() {
        return startIndex;
    }


    public int getEndIndex() {
        return endIndex;
    }
}
