
package com.document.render.office.common.borders;

import java.util.ArrayList;
import java.util.List;

public class BordersManage {
    private List<Borders> borders = new ArrayList<Borders>();


    public int addBorders(Borders bs) {
        int size = borders.size();
        borders.add(bs);
        return size;
    }


    public Borders getBorders(int index) {
        return borders.get(index);
    }


    public void dispose() {
        if (borders != null) {
            borders.clear();
            borders = null;
        }
    }
}
