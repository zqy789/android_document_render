
package com.document.render.office.ss.util;

import android.graphics.Color;


public class ColorUtil {
    private static ColorUtil util = new ColorUtil();


    public static ColorUtil instance() {
        return util;
    }


    public static int rgb(int red, int green, int blue) {
        return (0xFF << 24) | (red << 16 & 0xFF0000) | (green << 8 & 0xFF00) | (blue & 0xFF);
    }

    public static int rgb(byte red, byte green, byte blue) {
        return (0xFF << 24) | (red << 16 & 0xFF0000) | (green << 8 & 0xFF00) | (blue & 0xFF);
    }


    private static int applyTint(int lum, double tint) {
        if (tint > 0) {
            lum = (int) (lum + (255 - lum) * tint);
        } else if (tint < 0) {
            lum = (int) (lum * (1 + tint));
        }

        lum = lum > 255 ? lum = 255 : lum;

        return lum;
    }


    public int getColorWithTint(int color, double tint) {
        int r = applyTint(Color.red(color) & 0xFF, tint);
        int g = applyTint(Color.green(color) & 0xFF, tint);
        int b = applyTint(Color.blue(color) & 0xFF, tint);

        return Color.rgb(r, g, b);
    }
}
