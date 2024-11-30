
package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.Color;
import com.document.render.office.simpletext.font.Font;
import com.document.render.office.thirdpart.emf.EMFConstants;



public class StockObjects {

    private static final int WHITE_BRUSH = 0;
    private static final int LTGRAY_BRUSH = 1;
    private static final int GRAY_BRUSH = 2;
    private static final int DKGRAY_BRUSH = 3;
    private static final int BLACK_BRUSH = 4;
    private static final int NULL_BRUSH = 5;
    private static final int WHITE_PEN = 6;
    private static final int BLACK_PEN = 7;
    private static final int NULL_PEN = 8;
    private static final int OEM_FIXED_FONT = 10;
    private static final int ANSI_FIXED_FONT = 11;
    private static final int ANSI_VAR_FONT = 12;
    private static final int SYSTEM_FONT = 13;
    private static final int DEVICE_DEFAULT_FONT = 14;
    private static final int DEFAULT_PALETTE = 15;
    private static final int SYSTEM_FIXED_FONT = 16;


    private static final int DEFAULT_GUI_FONT = 17;




    private static final int DC_BRUSH = 18;
    private static final int DC_PEN = 19;

    private static final GDIObject[] objects = new GDIObject[20];

    static {
        Color nullColor = new Color(0, 0, 0, 0);

        objects[WHITE_BRUSH] = new LogBrush32(EMFConstants.BS_SOLID, Color.WHITE, 0);
        objects[LTGRAY_BRUSH] = new LogBrush32(EMFConstants.BS_SOLID, Color.LIGHT_GRAY, 0);
        objects[GRAY_BRUSH] = new LogBrush32(EMFConstants.BS_SOLID, Color.GRAY, 0);
        objects[DKGRAY_BRUSH] = new LogBrush32(EMFConstants.BS_SOLID, Color.DARK_GRAY, 0);
        objects[BLACK_BRUSH] = new LogBrush32(EMFConstants.BS_SOLID, Color.BLACK, 0);
        objects[NULL_BRUSH] = new LogBrush32(EMFConstants.BS_NULL, nullColor, 0);

        objects[WHITE_PEN] = new LogPen(EMFConstants.PS_SOLID, 1, Color.WHITE);
        objects[BLACK_PEN] = new LogPen(EMFConstants.PS_SOLID, 1, Color.BLACK);
        objects[NULL_PEN] = new LogPen(EMFConstants.PS_NULL, 1, nullColor);


        objects[OEM_FIXED_FONT] = new LogFontW(new Font("Monospaced", Font.PLAIN, 12));
        objects[ANSI_FIXED_FONT] = objects[OEM_FIXED_FONT];
        objects[ANSI_VAR_FONT] = new LogFontW(new Font("SansSerif", Font.PLAIN, 12));
        objects[SYSTEM_FONT] = new LogFontW(new Font("Dialog", Font.PLAIN, 12));
        objects[DEVICE_DEFAULT_FONT] = objects[ANSI_VAR_FONT];
        objects[SYSTEM_FIXED_FONT] = objects[OEM_FIXED_FONT];
        objects[DEFAULT_GUI_FONT] = objects[SYSTEM_FONT];


    }


    public static GDIObject getStockObject(int value) {
        if (value >= 0) {
            throw new IllegalArgumentException("Value does not represent a stock object: " + value);
        }

        value ^= 0x80000000;

        if (value >= objects.length) {
            throw new IllegalArgumentException("Stock object is out of bounds: " + value);
        }

        GDIObject object = objects[value];
        if (object == null) {
            throw new UnsupportedOperationException("Stock object not yet supported: " + value);
        }

        return object;
    }
}
