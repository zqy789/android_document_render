
package com.document.render.office.ss.util;


public class ReferenceUtil {

    private static ReferenceUtil util = new ReferenceUtil();


    public static ReferenceUtil instance() {
        return util;
    }

    public int getColumnIndex(String ref) {
        int end = 0;
        for (; end < ref.length(); end++) {
            if (ref.charAt(end) >= '0' && ref.charAt(end) <= '9') {
                break;
            }
        }
        String colString = ref.substring(0, end);
        return HeaderUtil.instance().getColumnHeaderIndexByText(colString);
    }

    public int getRowIndex(String ref) {
        if (ref.indexOf(":") > 0) {
            ref = ref.substring(0, ref.indexOf(":"));
        }

        int end = 0;
        for (; end < ref.length(); end++) {
            if (ref.charAt(end) >= '0' && ref.charAt(end) <= '9') {
                break;
            }
        }
        String rowString = ref.substring(end, ref.length());
        return Integer.parseInt(rowString) - 1;
    }
}
