
package com.document.render.office.fc;

import android.graphics.Color;


public class FCKit {

    public static int convertColor(String val) {
        if ("yellow".equals(val)) {
            return Color.YELLOW;
        } else if ("green".equals(val)) {
            return Color.GREEN;
        } else if ("cyan".equals(val)) {
            return Color.CYAN;
        } else if ("magenta".equals(val)) {
            return Color.MAGENTA;
        } else if ("blue".equals(val)) {
            return Color.BLUE;
        } else if ("red".equals(val)) {
            return Color.RED;
        } else if ("darkBlue".equals(val)) {
            return 0xFF00008B;
        } else if ("darkCyan".equals(val)) {
            return 0xFF008B8B;
        } else if ("darkGreen".equals(val)) {
            return 0xFF006400;
        } else if ("darkMagenta".equals(val)) {
            return 0xFF800080;
        } else if ("darkRed".equals(val)) {
            return 0xFF8B0000;
        } else if ("darkYellow".equals(val)) {
            return 0xFF808000;
        } else if ("darkGray".equals(val)) {
            return Color.DKGRAY;
        } else if ("lightGray".equals(val)) {
            return Color.LTGRAY;
        } else if ("black".equals(val)) {
            return Color.BLACK;
        }
        return -1;
    }


    public static int BGRtoRGB(int color) {

        int argbValue = color;
        if (argbValue == -1 || argbValue == 0xFFFFFF) {
            argbValue = Color.BLACK;
        } else {
            int bgrValue = argbValue & 0x00FFFFFF;
            argbValue = 0xFF000000 | (bgrValue & 0x0000FF) << 16 | (bgrValue & 0x00FF00) | (bgrValue & 0xFF0000) >> 16;
        }
        return argbValue;
    }
}
