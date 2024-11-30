
package com.document.render.office.pg.model;

import com.document.render.office.common.bg.BackgroundAndFill;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.simpletext.model.IAttributeSet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class PGMaster {

    private BackgroundAndFill bgFill;

    private Map<String, Integer> schemeColor;

    private Map<String, PGStyle> styleByType;

    private Map<Integer, PGStyle> styleByIdx;

    private Map<Integer, Integer> titlebodyID;

    private PGStyle titleStyle;

    private PGStyle bodyStyle;

    private PGStyle otherStyle;

    private int index = -1;


    public PGMaster() {
        schemeColor = new HashMap<String, Integer>();
        styleByType = new HashMap<String, PGStyle>();
        styleByIdx = new HashMap<Integer, PGStyle>();
    }


    public int getColor(String schemeClr) {
        return schemeColor.get(schemeClr);
    }


    public void addColor(String schemeClr, int color) {
        schemeColor.put(schemeClr, color);
    }


    public BackgroundAndFill getBackgroundAndFill() {
        return bgFill;
    }


    public void setBackgroundAndFill(BackgroundAndFill bgFill) {
        this.bgFill = bgFill;
    }


    public void addStyleByType(String type, PGStyle style) {
        styleByType.put(type, style);
    }


    public void addStyleByIdx(int idx, PGStyle style) {
        styleByIdx.put(idx, style);
    }


    public void setTitleStyle(PGStyle style) {
        titleStyle = style;
    }


    public void setBodyStyle(PGStyle style) {
        bodyStyle = style;
    }


    public void setDefaultStyle(PGStyle style) {
        otherStyle = style;
    }


    public Rectangle getAnchor(String type, int idx) {
        type = PGPlaceholderUtil.instance().checkTypeName(type);
        if (!PGPlaceholderUtil.instance().isBody(type)) {
            PGStyle style = styleByType.get(type);
            if (style != null) {
                return style.getAnchor();
            }
        } else if (idx > 0) {
            PGStyle style = styleByIdx.get(idx);
            if (style == null) {
                Iterator<Integer> iter = styleByIdx.keySet().iterator();
                if (iter.hasNext()) {
                    style = styleByIdx.get(iter.next());
                }
            }
            if (style != null) {
                return style.getAnchor();
            }
        }
        return null;
    }


    public IAttributeSet getSectionAttr(String type, int idx) {
        type = PGPlaceholderUtil.instance().checkTypeName(type);
        if (!PGPlaceholderUtil.instance().isBody(type)) {
            PGStyle style = styleByType.get(type);
            if (style != null) {
                return style.getSectionAttr();
            }
        } else if (idx > 0) {
            PGStyle style = styleByIdx.get(idx);
            if (style == null) {
                Iterator<Integer> iter = styleByIdx.keySet().iterator();
                if (iter.hasNext()) {
                    style = styleByIdx.get(iter.next());
                }
            }
            if (style != null) {
                return style.getSectionAttr();
            }
        }
        return null;
    }


    public int getTextStyle(String type, int idx, int lvl) {
        int styleID = -1;
        type = PGPlaceholderUtil.instance().checkTypeName(type);
        if (!PGPlaceholderUtil.instance().isBody(type)) {
            PGStyle style = styleByType.get(type);
            if (style != null) {
                styleID = style.getStyle(lvl);
                if (styleID >= 0) {
                    return styleID;
                }
            }

            if (PGPlaceholderUtil.TITLE.equals(type)) {
                if (titleStyle != null) {
                    return titleStyle.getStyle(lvl);
                }
            } else {
                if (otherStyle != null) {
                    return otherStyle.getStyle(lvl);
                }
            }
        } else if (idx > 0) {
            PGStyle style = styleByIdx.get(idx);
            if (style == null) {
                Iterator<Integer> iter = styleByIdx.keySet().iterator();
                if (iter.hasNext()) {
                    style = styleByIdx.get(iter.next());
                }
            }

            if (style != null) {
                styleID = style.getStyle(lvl);
                if (styleID >= 0) {
                    return styleID;
                }
            }

            if (bodyStyle != null) {
                return bodyStyle.getStyle(lvl);
            }
        }
        return -1;
    }


    public Map<String, Integer> getSchemeColor() {
        return schemeColor;
    }


    public int getSlideMasterIndex() {
        return index;
    }


    public void setSlideMasterIndex(int index) {
        this.index = index;
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


    public void dispose() {
        if (bgFill != null) {
            bgFill.dispose();
            bgFill = null;
        }
        schemeColor.clear();
        schemeColor = null;
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
        if (titleStyle != null) {
            titleStyle.dispose();
            titleStyle = null;
        }
        if (bodyStyle != null) {
            bodyStyle.dispose();
            bodyStyle = null;
        }
        if (otherStyle != null) {
            otherStyle.dispose();
            otherStyle = null;
        }
        if (titlebodyID != null) {
            titlebodyID.clear();
            titlebodyID = null;
        }
    }
}
