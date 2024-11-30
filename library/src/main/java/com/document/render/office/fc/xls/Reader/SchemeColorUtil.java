
package com.document.render.office.fc.xls.Reader;

import com.document.render.office.constant.SchemeClrConstant;
import com.document.render.office.ss.model.baseModel.Workbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class SchemeColorUtil {
    private static List<String> schemeClrName;
    private static Map<String, Integer> schemeColor;

    public static int getThemeColor(Workbook workbook, int index) {
        init(workbook);
        if (index < 0 || index >= schemeClrName.size()) {
            return -1;
        }

        return workbook.getSchemeColor(schemeClrName.get(index));
    }


    public static Map<String, Integer> getSchemeColor(Workbook workbook) {
        init(workbook);

        return schemeColor;
    }

    private static void init(Workbook workbook) {
        if (schemeColor == null) {
            schemeClrName = new ArrayList<String>();
            schemeClrName.add(SchemeClrConstant.SCHEME_BG1);
            schemeClrName.add(SchemeClrConstant.SCHEME_TX1);
            schemeClrName.add(SchemeClrConstant.SCHEME_BG2);
            schemeClrName.add(SchemeClrConstant.SCHEME_TX2);

            schemeClrName.add(SchemeClrConstant.SCHEME_ACCENT1);
            schemeClrName.add(SchemeClrConstant.SCHEME_ACCENT2);
            schemeClrName.add(SchemeClrConstant.SCHEME_ACCENT3);
            schemeClrName.add(SchemeClrConstant.SCHEME_ACCENT4);
            schemeClrName.add(SchemeClrConstant.SCHEME_ACCENT5);
            schemeClrName.add(SchemeClrConstant.SCHEME_ACCENT6);

            schemeClrName.add(SchemeClrConstant.SCHEME_HLINK);
            schemeClrName.add(SchemeClrConstant.SCHEME_FOLHLINK);

            schemeClrName.add(SchemeClrConstant.SCHEME_DK1);
            schemeClrName.add(SchemeClrConstant.SCHEME_LT1);
            schemeClrName.add(SchemeClrConstant.SCHEME_DK2);
            schemeClrName.add(SchemeClrConstant.SCHEME_LT2);

            schemeColor = new HashMap<String, Integer>();
        }


        schemeColor.clear();

        Iterator<String> iter = schemeClrName.iterator();
        String key;
        while (iter.hasNext()) {
            key = iter.next();
            schemeColor.put(key, workbook.getSchemeColor(key));
        }
    }
}
