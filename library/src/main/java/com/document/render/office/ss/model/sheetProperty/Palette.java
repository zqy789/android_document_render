

package com.document.render.office.ss.model.sheetProperty;

import java.util.ArrayList;
import java.util.List;


public final class Palette {

    public final static byte STANDARD_PALETTE_SIZE = (byte) 56;

    public final static short FIRST_COLOR_INDEX = (short) 0x8;

    private final List<PColor> _colors;

    public Palette() {
        PColor[] defaultPalette = createDefaultPalette();
        _colors = new ArrayList<PColor>(defaultPalette.length);

        for (int i = 0; i < defaultPalette.length; i++) {
            _colors.add(defaultPalette[i]);
        }
    }


    private static PColor[] createDefaultPalette() {
        return new PColor[]{
                pc(0, 0, 0),
                pc(255, 255, 255),
                pc(255, 0, 0),
                pc(0, 255, 0),
                pc(0, 0, 255),
                pc(255, 255, 0),
                pc(255, 0, 255),
                pc(0, 255, 255),
                pc(128, 0, 0),
                pc(0, 128, 0),
                pc(0, 0, 128),
                pc(128, 128, 0),
                pc(128, 0, 128),
                pc(0, 128, 128),
                pc(192, 192, 192),
                pc(128, 128, 128),
                pc(153, 153, 255),
                pc(153, 51, 102),
                pc(255, 255, 204),
                pc(204, 255, 255),
                pc(102, 0, 102),
                pc(255, 128, 128),
                pc(0, 102, 204),
                pc(204, 204, 255),
                pc(0, 0, 128),
                pc(255, 0, 255),
                pc(255, 255, 0),
                pc(0, 255, 255),
                pc(128, 0, 128),
                pc(128, 0, 0),
                pc(0, 128, 128),
                pc(0, 0, 255),
                pc(0, 204, 255),
                pc(204, 255, 255),
                pc(204, 255, 204),
                pc(255, 255, 153),
                pc(153, 204, 255),
                pc(255, 153, 204),
                pc(204, 153, 255),
                pc(255, 204, 153),
                pc(51, 102, 255),
                pc(51, 204, 204),
                pc(153, 204, 0),
                pc(255, 204, 0),
                pc(255, 153, 0),
                pc(255, 102, 0),
                pc(102, 102, 153),
                pc(150, 150, 150),
                pc(0, 51, 102),
                pc(51, 153, 102),
                pc(0, 51, 0),
                pc(51, 51, 0),
                pc(153, 51, 0),
                pc(153, 51, 102),
                pc(51, 51, 153),
                pc(51, 51, 51),
        };
    }

    private static PColor pc(int r, int g, int b) {
        return new PColor(r, g, b);
    }


    public byte[] getColor(int byteIndex) {
        int i = byteIndex - FIRST_COLOR_INDEX;
        if (i < 0 || i >= _colors.size()) {
            return null;
        }
        return _colors.get(i).getTriplet();
    }


    public void setColor(short byteIndex, byte red, byte green, byte blue) {
        int i = byteIndex - FIRST_COLOR_INDEX;
        if (i < 0 || i >= STANDARD_PALETTE_SIZE) {
            return;
        }

        while (_colors.size() <= i) {
            _colors.add(new PColor(0, 0, 0));
        }
        PColor custColor = new PColor(red, green, blue);
        _colors.set(i, custColor);
    }

    public void dispose() {
        if (_colors != null) {
            _colors.clear();
        }
    }


    private static final class PColor {

        private int _red;
        private int _green;
        private int _blue;

        public PColor(int red, int green, int blue) {
            _red = red;
            _green = green;
            _blue = blue;
        }

        public byte[] getTriplet() {
            return new byte[]{(byte) _red, (byte) _green, (byte) _blue};
        }
    }
}
