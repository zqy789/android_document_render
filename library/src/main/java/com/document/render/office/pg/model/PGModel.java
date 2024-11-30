
package com.document.render.office.pg.model;

import com.document.render.office.java.awt.Dimension;
import com.document.render.office.pg.model.tableStyle.TableStyle;
import com.document.render.office.simpletext.model.IDocument;
import com.document.render.office.simpletext.model.STDocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PGModel {

    private IDocument doc;

    private List<PGSlide> slides;

    private Dimension pageSize;

    private List<PGSlide> slideMasters;

    private int total = 0;
    private Map<String, TableStyle> tableStyleMap;
    private int slideNumberOffset;
    private boolean omitTitleSlide;


    public PGModel() {
        doc = new STDocument();
        slides = new ArrayList<PGSlide>();
        slideMasters = new ArrayList<PGSlide>();
        slideNumberOffset = 0;
        omitTitleSlide = false;
    }


    public synchronized void appendSlide(PGSlide slide) {
        if (slides == null) {
            return;
        }
        slides.add(slide);
    }


    public PGSlide getSlide(int index) {
        if (index < 0 || index >= slides.size()) {
            return null;
        }
        return slides.get(index);
    }


    public PGSlide getSlideForSlideNo(int slideNo) {
        for (PGSlide slide : slides) {
            if (slide.getSlideNo() == slideNo) {
                return slide;
            }
        }
        return null;
    }


    public int getRealSlideCount() {
        if (slides != null) {
            return slides.size();
        }
        return 0;
    }


    public int getSlideCount() {
        return total;
    }


    public void setSlideCount(int total) {
        this.total = total;
    }


    public IDocument getRenderersDoc() {
        return doc;
    }


    public Dimension getPageSize() {
        return pageSize;
    }


    public void setPageSize(Dimension pageSize) {
        this.pageSize = pageSize;
    }


    public int appendSlideMaster(PGSlide master) {
        int size = slideMasters.size();
        slideMasters.add(master);
        return size;
    }


    public PGSlide getSlideMaster(int index) {
        if (index < 0 || index >= slideMasters.size()) {
            return null;
        }
        return slideMasters.get(index);
    }


    public int getSlideMasterCount() {
        if (slideMasters == null) {
            return 0;
        }
        return slideMasters.size();
    }


    public void putTableStyle(String styleID, TableStyle tableStyle) {
        if (tableStyleMap == null) {
            tableStyleMap = new HashMap<String, TableStyle>();
        }

        if (styleID != null && tableStyle != null) {
            tableStyleMap.put(styleID, tableStyle);
        }
    }


    public TableStyle getTableStyle(String styleID) {
        if (tableStyleMap != null && styleID != null) {
            return tableStyleMap.get(styleID);
        }

        return null;
    }


    public int getSlideNumberOffset() {
        return slideNumberOffset;
    }


    public void setSlideNumberOffset(int slideNumberOffset) {
        this.slideNumberOffset = slideNumberOffset;
    }


    public boolean isOmitTitleSlide() {
        return omitTitleSlide;
    }

    public void setOmitTitleSlide(boolean omitTitleSlide) {
        this.omitTitleSlide = omitTitleSlide;
    }


    public synchronized void dispose() {
        if (doc != null) {
            doc.dispose();
            doc = null;
        }
        if (slides != null) {
            for (PGSlide slide : slides) {
                slide.dispose();
            }
            slides.clear();
            slides = null;
        }
        if (slideMasters != null) {
            for (PGSlide master : slideMasters) {
                master.dispose();
            }
            slideMasters.clear();
            slideMasters = null;
        }

        if (tableStyleMap != null) {
            tableStyleMap.clear();
            tableStyleMap = null;
        }
    }
}
