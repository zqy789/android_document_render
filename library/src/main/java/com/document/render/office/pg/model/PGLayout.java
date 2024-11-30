
package com.document.render.office.pg.model;

import com.document.render.office.common.bg.BackgroundAndFill;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.simpletext.model.IAttributeSet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class PGLayout {

    private BackgroundAndFill bgFill;

    private Map<String, PGStyle> styleByType;

    private Map<Integer, PGStyle> styleByIdx;

    private Map<Integer, String> shapeType;

    private Map<Integer, Integer> titlebodyID;

    private int index = -1;

    private boolean addShapes = true;


    public PGLayout() {
        styleByType = new HashMap<String, PGStyle>();
        styleByIdx = new HashMap<Integer, PGStyle>();
    }


    public Rectangle getAnchor(String type, int idx) {
        if (!PGPlaceholderUtil.instance().isBody(type)) {
            PGStyle style = styleByType.get(type);
            if (style != null) {
                return style.getAnchor();
            }
        } else if (idx > 0) {
            PGStyle style = styleByIdx.get(idx);
            if (style != null) {
                return style.getAnchor();
            }
        }
        return null;
    }


    public IAttributeSet getSectionAttr(String type, int idx) {
        if (!PGPlaceholderUtil.instance().isBody(type)) {
            PGStyle style = styleByType.get(type);
            if (style != null) {
                return style.getSectionAttr();
            }
        } else if (idx > 0) {
            PGStyle style = styleByIdx.get(idx);
            if (style != null) {
                return style.getSectionAttr();
            }
        }
        return null;
    }


    public int getStyleID(String type, int idx, int lvl) {
        if (!PGPlaceholderUtil.instance().isBody(type)) {
            PGStyle style = styleByType.get(type);
            if (style != null) {
                return style.getStyle(lvl);
            }
        } else if (idx > 0) {
            PGStyle style = styleByIdx.get(idx);
            if (style != null) {
                return style.getStyle(lvl);
            }
        }
        return -1;
    }


    public void setStyleByType(String type, PGStyle style) {
        styleByType.put(type, style);
    }


    public void setStyleByIdx(int idx, PGStyle style) {
        styleByIdx.put(idx, style);
    }


    public BackgroundAndFill getBackgroundAndFill() {
        return bgFill;
    }


    public void setBackgroundAndFill(BackgroundAndFill bgFill) {
        this.bgFill = bgFill;
    }


    public int getSlideMasterIndex() {
        return index;
    }


    public void setSlideMasterIndex(int index) {
        this.index = index;
    }


    public boolean isAddShapes() {
        return addShapes;
    }


    public void setAddShapes(boolean addShapes) {
        this.addShapes = addShapes;
    }


    public void addShapeType(int idx, String type) {
        if (shapeType == null) {
            shapeType = new HashMap<Integer, String>();
        }
        shapeType.put(idx, type);
    }


    public String getShapeType(int idx) {
        if (shapeType != null) {
            return shapeType.get(idx);
        }
        return null;
    }

    public void addTitleBodyID(int idx, int id) {
        if (titlebodyID == null) {
            titlebodyID = new HashMap<Integer, Integer>();
        }
        titlebodyID.put(idx, id);
    }

    public Integer getTitleBodyID(int idx) {
        if (titlebodyID != null) {
            return titlebodyID.get(idx);
        }
        return null;
    }


    public void disposs() {
        if (bgFill != null) {
            bgFill.dispose();
            bgFill = null;
        }
        if (styleByType != null) {
            Iterator<String> iter = styleByType.keySet().iterator();
            while (iter.hasNext()) {
                styleByType.get(iter.next()).dispose();
            }
            styleByType.clear();
            styleByType = null;
        }
        if (styleByIdx != null) {
            Iterator<Integer> iter = styleByIdx.keySet().iterator();
            while (iter.hasNext()) {
                styleByIdx.get(iter.next()).dispose();
            }
            styleByIdx.clear();
            styleByIdx = null;
        }

        if (shapeType != null) {
            shapeType.clear();
            shapeType = null;
        }

        if (titlebodyID != null) {
            titlebodyID.clear();
            titlebodyID = null;
        }
    }
}
