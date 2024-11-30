
package com.document.render.office.pg.model;

import java.util.ArrayList;
import java.util.List;


public class PGBulletText {

    private List<String> bulletTexts;


    public PGBulletText() {
        bulletTexts = new ArrayList<String>();
    }


    public int addBulletText(String text) {
        int size = bulletTexts.size();
        bulletTexts.add(text);
        return size;
    }


    public String getBulletText(int index) {
        if (index < 0 || index >= bulletTexts.size()) {
            return null;
        }
        return bulletTexts.get(index);
    }


    public void dispose() {
        if (bulletTexts != null) {
            bulletTexts.clear();
            bulletTexts = null;
        }
    }
}
