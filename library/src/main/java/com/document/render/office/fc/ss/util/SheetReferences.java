

package com.document.render.office.fc.ss.util;

import java.util.HashMap;
import java.util.Map;


public class SheetReferences {
    Map map;

    public SheetReferences() {
        map = new HashMap(5);
    }

    public void addSheetReference(String sheetName, int number) {
        map.put(Integer.valueOf(number), sheetName);
    }

    public String getSheetName(int number) {
        return (String) map.get(Integer.valueOf(number));
    }
}
