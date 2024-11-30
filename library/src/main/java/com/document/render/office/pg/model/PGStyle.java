
package com.document.render.office.pg.model;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.simpletext.model.IAttributeSet;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class PGStyle {

    private Rectangle anchor;

    private IAttributeSet attr;

    private Map<Integer, Integer> lvlStyleIDs;

    private Map<Integer, String> defaultFontColor;


    public PGStyle() {
        lvlStyleIDs = new HashMap<Integer, Integer>();
    }


    public Rectangle getAnchor() {
        return anchor;
    }


    public void setAnchor(Rectangle anchor) {
        this.anchor = anchor;
    }


    public IAttributeSet getSectionAttr() {
        return attr;
    }


    public void setSectionAttr(IAttributeSet attr) {
        this.attr = attr;
    }


    public int getStyle(int lvl) {
        if (!lvlStyleIDs.isEmpty()) {
            Integer index = lvlStyleIDs.get(lvl);
            if (index != null) {
                return index;
            }
        }
        return -1;
    }


    public void addStyle(int lvl, int style) {
        lvlStyleIDs.put(lvl, style);
    }

    public void addDefaultFontColor(int lvl, String fontColor) {
        if (lvl > 0 && fontColor != null) {
            if (defaultFontColor == null) {
                defaultFontColor = new Hashtable<Integer, String>();
            }
            defaultFontColor.put(lvl, fontColor);
        }
    }

    public String getDefaultFontColor(int lvl) {
        if (defaultFontColor != null) {
            return defaultFontColor.get(lvl);
        }
        return null;
    }


    public void dispose() {
        anchor = null;
        if (attr != null) {
            attr.dispose();
            attr = null;
        }
        if (lvlStyleIDs != null) {
            lvlStyleIDs.clear();
            lvlStyleIDs = null;
        }
        if (defaultFontColor != null) {
            defaultFontColor.clear();
            defaultFontColor = null;
        }
    }
}
