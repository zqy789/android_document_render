
package com.document.render.office.simpletext.font;

import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class FontTypefaceManage {

    private static FontTypefaceManage kit;



    private List<String> sysFontName;

    private LinkedHashMap<String, Typeface> tfs;


    public FontTypefaceManage() {

    }


    public static FontTypefaceManage instance() {
        if (kit == null) {
            kit = new FontTypefaceManage();
        }
        return kit;
    }


    public int addFontName(String fontName) {

        if (sysFontName == null) {
            sysFontName = new ArrayList<String>();
        }
        int a = sysFontName.indexOf(fontName);
        if (a < 0) {
            a = sysFontName.size();
            sysFontName.add(fontName);
        }
        return a;

    }


    public Typeface getFontTypeface(int index) {

        if (tfs == null) {
            tfs = new LinkedHashMap<String, Typeface>();
        }
        String fontName = index < 0 ? "sans-serif" : sysFontName.get(index);
        if (fontName == null) {
            fontName = "sans-serif";
        }

        Typeface tf = tfs.get(fontName);
        if (tf == null) {
            tf = Typeface.create(fontName, Typeface.NORMAL);
            if (tf == null) {
                tf = Typeface.DEFAULT;
            }
            tfs.put(fontName, tf);
        }
        return tf;
    }


    public void dispose() {

    }

}
