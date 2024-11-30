
package com.document.render.office.simpletext.model;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;



public class StyleManage {
    private static StyleManage kit = new StyleManage();
    private Map<Integer, Style> styles = new Hashtable<Integer, Style>();


    public static StyleManage instance() {
        return kit;
    }


    public Style getStyle(int styleID) {
        return styles.get(styleID);
    }


    public Style getStyleForName(String styleName) {
        Iterator<Style> itor = styles.values().iterator();
        while (itor.hasNext()) {
            Style s = itor.next();
            if (s.getName().equals(styleName)) {
                return s;
            }
        }
        return null;
    }


    public void addStyle(Style style) {
        styles.put(style.getId(), style);
    }

    public void dispose() {
        Iterator<Style> itor = styles.values().iterator();
        while (itor.hasNext()) {
            ((Style) (itor.next())).dispose();
        }
        styles.clear();
        styles = null;
    }
}
