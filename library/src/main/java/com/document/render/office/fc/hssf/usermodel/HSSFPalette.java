

package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.hssf.record.PaletteRecord;
import com.document.render.office.fc.hssf.util.HSSFColor;



public final class HSSFPalette {
    private PaletteRecord _palette;

    protected HSSFPalette(PaletteRecord palette) {
        _palette = palette;
    }


    public HSSFColor getColor(short index) {

        if (index == HSSFColor.AUTOMATIC.index) {
            return HSSFColor.AUTOMATIC.getInstance();
        }
        byte[] b = _palette.getColor(index);
        if (b != null) {
            return new CustomColor(index, b);
        }
        return null;
    }


    public HSSFColor getColor(int index) {
        return getColor((short) index);
    }


    public HSSFColor findColor(byte red, byte green, byte blue) {
        byte[] b = _palette.getColor(PaletteRecord.FIRST_COLOR_INDEX);
        for (short i = PaletteRecord.FIRST_COLOR_INDEX; b != null;
             b = _palette.getColor(++i)) {
            if (b[0] == red && b[1] == green && b[2] == blue) {
                return new CustomColor(i, b);
            }
        }
        return null;
    }


    public HSSFColor findSimilarColor(byte red, byte green, byte blue) {
        return findSimilarColor(unsignedInt(red), unsignedInt(green), unsignedInt(blue));
    }


    public HSSFColor findSimilarColor(int red, int green, int blue) {
        HSSFColor result = null;
        int minColorDistance = Integer.MAX_VALUE;
        byte[] b = _palette.getColor(PaletteRecord.FIRST_COLOR_INDEX);
        for (short i = PaletteRecord.FIRST_COLOR_INDEX; b != null;
             b = _palette.getColor(++i)) {
            int colorDistance = Math.abs(red - unsignedInt(b[0])) +
                    Math.abs(green - unsignedInt(b[1])) +
                    Math.abs(blue - unsignedInt(b[2]));
            if (colorDistance < minColorDistance) {
                minColorDistance = colorDistance;
                result = getColor(i);
            }
        }
        return result;
    }


    private int unsignedInt(byte b) {
        return 0xFF & ((int) b);
    }


    public void setColorAtIndex(short index, byte red, byte green, byte blue) {
        _palette.setColor(index, red, green, blue);
    }


    public HSSFColor addColor(byte red, byte green, byte blue) {
        byte[] b = _palette.getColor(PaletteRecord.FIRST_COLOR_INDEX);
        short i;
        for (i = PaletteRecord.FIRST_COLOR_INDEX; i < PaletteRecord.STANDARD_PALETTE_SIZE + PaletteRecord.FIRST_COLOR_INDEX; b = _palette.getColor(++i)) {
            if (b == null) {
                setColorAtIndex(i, red, green, blue);
                return getColor(i);
            }
        }
        throw new RuntimeException("Could not find free color index");
    }

    private static final class CustomColor extends HSSFColor {
        private short _byteOffset;
        private byte _red;
        private byte _green;
        private byte _blue;

        public CustomColor(short byteOffset, byte[] colors) {
            this(byteOffset, colors[0], colors[1], colors[2]);
        }

        private CustomColor(short byteOffset, byte red, byte green, byte blue) {
            _byteOffset = byteOffset;
            _red = red;
            _green = green;
            _blue = blue;
        }

        public short getIndex() {
            return _byteOffset;
        }

        public short[] getTriplet() {
            return new short[]
                    {
                            (short) (_red & 0xff),
                            (short) (_green & 0xff),
                            (short) (_blue & 0xff)
                    };
        }

        public String getHexString() {
            StringBuffer sb = new StringBuffer();
            sb.append(getGnumericPart(_red));
            sb.append(':');
            sb.append(getGnumericPart(_green));
            sb.append(':');
            sb.append(getGnumericPart(_blue));
            return sb.toString();
        }

        private String getGnumericPart(byte color) {
            String s;
            if (color == 0) {
                s = "0";
            } else {
                int c = color & 0xff;
                c = (c << 8) | c;
                s = Integer.toHexString(c).toUpperCase();
                while (s.length() < 4) {
                    s = "0" + s;
                }
            }
            return s;
        }
    }
}
