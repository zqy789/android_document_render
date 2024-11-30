

package com.document.render.office.fc.hssf.util;

import com.document.render.office.fc.ss.usermodel.Color;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;



public class HSSFColor implements Color {
    private static Map<Integer, HSSFColor> indexHash;


    public HSSFColor() {
    }


    public final static Map<Integer, HSSFColor> getIndexHash() {
        if (indexHash == null) {
            indexHash = Collections.unmodifiableMap(createColorsByIndexMap());
        }

        return indexHash;
    }


    public final static Hashtable<Integer, HSSFColor> getMutableIndexHash() {
        return createColorsByIndexMap();
    }

    private static Hashtable<Integer, HSSFColor> createColorsByIndexMap() {
        HSSFColor[] colors = getAllColors();
        Hashtable<Integer, HSSFColor> result = new Hashtable<Integer, HSSFColor>(colors.length * 3 / 2);

        for (int i = 0; i < colors.length; i++) {
            HSSFColor color = colors[i];

            Integer index1 = Integer.valueOf(color.getIndex());
            if (result.containsKey(index1)) {
                HSSFColor prevColor = (HSSFColor) result.get(index1);
                throw new RuntimeException("Dup color index (" + index1
                        + ") for colors (" + prevColor.getClass().getName()
                        + "),(" + color.getClass().getName() + ")");
            }
            result.put(index1, color);
        }

        for (int i = 0; i < colors.length; i++) {
            HSSFColor color = colors[i];
            Integer index2 = getIndex2(color);
            if (index2 == null) {

                continue;
            }
            if (result.containsKey(index2)) {
                if (false) {
                    HSSFColor prevColor = (HSSFColor) result.get(index2);
                    throw new RuntimeException("Dup color index (" + index2
                            + ") for colors (" + prevColor.getClass().getName()
                            + "),(" + color.getClass().getName() + ")");
                }
            }
            result.put(index2, color);
        }
        return result;
    }

    private static Integer getIndex2(HSSFColor color) {

        Field f;
        try {
            f = color.getClass().getDeclaredField("index2");
        } catch (NoSuchFieldException e) {

            return null;
        }

        Short s;
        try {
            s = (Short) f.get(color);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return Integer.valueOf(s.intValue());
    }

    private static HSSFColor[] getAllColors() {

        return new HSSFColor[]{
                new BLACK(), new BROWN(), new OLIVE_GREEN(), new DARK_GREEN(),
                new DARK_TEAL(), new DARK_BLUE(), new INDIGO(), new GREY_80_PERCENT(),
                new ORANGE(), new DARK_YELLOW(), new GREEN(), new TEAL(), new BLUE(),
                new BLUE_GREY(), new GREY_50_PERCENT(), new RED(), new LIGHT_ORANGE(), new LIME(),
                new SEA_GREEN(), new AQUA(), new LIGHT_BLUE(), new VIOLET(), new GREY_40_PERCENT(),
                new PINK(), new GOLD(), new YELLOW(), new BRIGHT_GREEN(), new TURQUOISE(),
                new DARK_RED(), new SKY_BLUE(), new PLUM(), new GREY_25_PERCENT(), new ROSE(),
                new LIGHT_YELLOW(), new LIGHT_GREEN(), new LIGHT_TURQUOISE(), new PALE_BLUE(),
                new LAVENDER(), new WHITE(), new CORNFLOWER_BLUE(), new LEMON_CHIFFON(),
                new MAROON(), new ORCHID(), new CORAL(), new ROYAL_BLUE(),
                new LIGHT_CORNFLOWER_BLUE(), new TAN(),
        };
    }


    public final static Hashtable<String, HSSFColor> getTripletHash() {
        return createColorsByHexStringMap();
    }

    private static Hashtable<String, HSSFColor> createColorsByHexStringMap() {
        HSSFColor[] colors = getAllColors();
        Hashtable<String, HSSFColor> result = new Hashtable<String, HSSFColor>(colors.length * 3 / 2);

        for (int i = 0; i < colors.length; i++) {
            HSSFColor color = colors[i];

            String hexString = color.getHexString();
            if (result.containsKey(hexString)) {
                HSSFColor other = (HSSFColor) result.get(hexString);
                throw new RuntimeException(
                        "Dup color hexString (" + hexString
                                + ") for color (" + color.getClass().getName() + ") - "
                                + " already taken by (" + other.getClass().getName() + ")"
                );
            }
            result.put(hexString, color);
        }
        return result;
    }



    public short getIndex() {
        return BLACK.index;
    }



    public short[] getTriplet() {
        return BLACK.triplet;
    }





    public String getHexString() {
        return BLACK.hexString;
    }



    public final static class BLACK
            extends HSSFColor {
        public final static short index = 0x8;
        public final static short[] triplet =
                {
                        0, 0, 0
                };
        public final static String hexString = "0:0:0";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class BROWN
            extends HSSFColor {
        public final static short index = 0x3c;
        public final static short[] triplet =
                {
                        153, 51, 0
                };
        public final static String hexString = "9999:3333:0";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public static class OLIVE_GREEN
            extends HSSFColor {
        public final static short index = 0x3b;
        public final static short[] triplet =
                {
                        51, 51, 0
                };
        public final static String hexString = "3333:3333:0";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class DARK_GREEN
            extends HSSFColor {
        public final static short index = 0x3a;
        public final static short[] triplet =
                {
                        0, 51, 0
                };
        public final static String hexString = "0:3333:0";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class DARK_TEAL
            extends HSSFColor {
        public final static short index = 0x38;
        public final static short[] triplet =
                {
                        0, 51, 102
                };
        public final static String hexString = "0:3333:6666";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class DARK_BLUE
            extends HSSFColor {
        public final static short index = 0x12;
        public final static short index2 = 0x20;
        public final static short[] triplet =
                {
                        0, 0, 128
                };
        public final static String hexString = "0:0:8080";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class INDIGO
            extends HSSFColor {
        public final static short index = 0x3e;
        public final static short[] triplet =
                {
                        51, 51, 153
                };
        public final static String hexString = "3333:3333:9999";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class GREY_80_PERCENT
            extends HSSFColor {
        public final static short index = 0x3f;
        public final static short[] triplet =
                {
                        51, 51, 51
                };
        public final static String hexString = "3333:3333:3333";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class DARK_RED
            extends HSSFColor {
        public final static short index = 0x10;
        public final static short index2 = 0x25;
        public final static short[] triplet =
                {
                        128, 0, 0
                };
        public final static String hexString = "8080:0:0";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class ORANGE
            extends HSSFColor {
        public final static short index = 0x35;
        public final static short[] triplet =
                {
                        255, 102, 0
                };
        public final static String hexString = "FFFF:6666:0";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class DARK_YELLOW
            extends HSSFColor {
        public final static short index = 0x13;
        public final static short[] triplet =
                {
                        128, 128, 0
                };
        public final static String hexString = "8080:8080:0";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class GREEN
            extends HSSFColor {
        public final static short index = 0x11;
        public final static short[] triplet =
                {
                        0, 128, 0
                };
        public final static String hexString = "0:8080:0";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class TEAL
            extends HSSFColor {
        public final static short index = 0x15;
        public final static short index2 = 0x26;
        public final static short[] triplet =
                {
                        0, 128, 128
                };
        public final static String hexString = "0:8080:8080";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class BLUE
            extends HSSFColor {
        public final static short index = 0xc;
        public final static short index2 = 0x27;
        public final static short[] triplet =
                {
                        0, 0, 255
                };
        public final static String hexString = "0:0:FFFF";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class BLUE_GREY
            extends HSSFColor {
        public final static short index = 0x36;
        public final static short[] triplet =
                {
                        102, 102, 153
                };
        public final static String hexString = "6666:6666:9999";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class GREY_50_PERCENT
            extends HSSFColor {
        public final static short index = 0x17;
        public final static short[] triplet =
                {
                        128, 128, 128
                };
        public final static String hexString = "8080:8080:8080";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class RED
            extends HSSFColor {
        public final static short index = 0xa;
        public final static short[] triplet =
                {
                        255, 0, 0
                };
        public final static String hexString = "FFFF:0:0";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class LIGHT_ORANGE
            extends HSSFColor {
        public final static short index = 0x34;
        public final static short[] triplet =
                {
                        255, 153, 0
                };
        public final static String hexString = "FFFF:9999:0";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class LIME
            extends HSSFColor {
        public final static short index = 0x32;
        public final static short[] triplet =
                {
                        153, 204, 0
                };
        public final static String hexString = "9999:CCCC:0";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class SEA_GREEN
            extends HSSFColor {
        public final static short index = 0x39;
        public final static short[] triplet =
                {
                        51, 153, 102
                };
        public final static String hexString = "3333:9999:6666";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class AQUA
            extends HSSFColor {
        public final static short index = 0x31;
        public final static short[] triplet =
                {
                        51, 204, 204
                };
        public final static String hexString = "3333:CCCC:CCCC";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class LIGHT_BLUE
            extends HSSFColor {
        public final static short index = 0x30;
        public final static short[] triplet =
                {
                        51, 102, 255
                };
        public final static String hexString = "3333:6666:FFFF";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class VIOLET
            extends HSSFColor {
        public final static short index = 0x14;
        public final static short index2 = 0x24;
        public final static short[] triplet =
                {
                        128, 0, 128
                };
        public final static String hexString = "8080:0:8080";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class GREY_40_PERCENT
            extends HSSFColor {
        public final static short index = 0x37;
        public final static short[] triplet =
                {
                        150, 150, 150
                };
        public final static String hexString = "9696:9696:9696";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class PINK
            extends HSSFColor {
        public final static short index = 0xe;
        public final static short index2 = 0x21;
        public final static short[] triplet =
                {
                        255, 0, 255
                };
        public final static String hexString = "FFFF:0:FFFF";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class GOLD
            extends HSSFColor {
        public final static short index = 0x33;
        public final static short[] triplet =
                {
                        255, 204, 0
                };
        public final static String hexString = "FFFF:CCCC:0";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class YELLOW
            extends HSSFColor {
        public final static short index = 0xd;
        public final static short index2 = 0x22;
        public final static short[] triplet =
                {
                        255, 255, 0
                };
        public final static String hexString = "FFFF:FFFF:0";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class BRIGHT_GREEN
            extends HSSFColor {
        public final static short index = 0xb;
        public final static short index2 = 0x23;
        public final static short[] triplet =
                {
                        0, 255, 0
                };
        public final static String hexString = "0:FFFF:0";

        public short getIndex() {
            return index;
        }

        public String getHexString() {
            return hexString;
        }

        public short[] getTriplet() {
            return triplet;
        }
    }



    public final static class TURQUOISE
            extends HSSFColor {
        public final static short index = 0xf;
        public final static short index2 = 0x23;
        public final static short[] triplet =
                {
                        0, 255, 255
                };
        public final static String hexString = "0:FFFF:FFFF";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class SKY_BLUE
            extends HSSFColor {
        public final static short index = 0x28;
        public final static short[] triplet =
                {
                        0, 204, 255
                };
        public final static String hexString = "0:CCCC:FFFF";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class PLUM
            extends HSSFColor {
        public final static short index = 0x3d;
        public final static short index2 = 0x19;
        public final static short[] triplet =
                {
                        153, 51, 102
                };
        public final static String hexString = "9999:3333:6666";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class GREY_25_PERCENT
            extends HSSFColor {
        public final static short index = 0x16;
        public final static short[] triplet =
                {
                        192, 192, 192
                };
        public final static String hexString = "C0C0:C0C0:C0C0";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class ROSE
            extends HSSFColor {
        public final static short index = 0x2d;
        public final static short[] triplet =
                {
                        255, 153, 204
                };
        public final static String hexString = "FFFF:9999:CCCC";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class TAN
            extends HSSFColor {
        public final static short index = 0x2f;
        public final static short[] triplet =
                {
                        255, 204, 153
                };
        public final static String hexString = "FFFF:CCCC:9999";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class LIGHT_YELLOW
            extends HSSFColor {
        public final static short index = 0x2b;
        public final static short[] triplet =
                {
                        255, 255, 153
                };
        public final static String hexString = "FFFF:FFFF:9999";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class LIGHT_GREEN
            extends HSSFColor {
        public final static short index = 0x2a;
        public final static short[] triplet =
                {
                        204, 255, 204
                };
        public final static String hexString = "CCCC:FFFF:CCCC";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class LIGHT_TURQUOISE
            extends HSSFColor {
        public final static short index = 0x29;
        public final static short index2 = 0x1b;
        public final static short[] triplet =
                {
                        204, 255, 255
                };
        public final static String hexString = "CCCC:FFFF:FFFF";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class PALE_BLUE
            extends HSSFColor {
        public final static short index = 0x2c;
        public final static short[] triplet =
                {
                        153, 204, 255
                };
        public final static String hexString = "9999:CCCC:FFFF";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class LAVENDER
            extends HSSFColor {
        public final static short index = 0x2e;
        public final static short[] triplet =
                {
                        204, 153, 255
                };
        public final static String hexString = "CCCC:9999:FFFF";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class WHITE
            extends HSSFColor {
        public final static short index = 0x9;
        public final static short[] triplet =
                {
                        255, 255, 255
                };
        public final static String hexString = "FFFF:FFFF:FFFF";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }


    public final static class CORNFLOWER_BLUE
            extends HSSFColor {
        public final static short index = 0x18;
        public final static short[] triplet =
                {
                        153, 153, 255
                };
        public final static String hexString = "9999:9999:FFFF";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }



    public final static class LEMON_CHIFFON
            extends HSSFColor {
        public final static short index = 0x1a;
        public final static short[] triplet =
                {
                        255, 255, 204
                };
        public final static String hexString = "FFFF:FFFF:CCCC";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }


    public final static class MAROON
            extends HSSFColor {
        public final static short index = 0x19;
        public final static short[] triplet =
                {
                        127, 0, 0
                };
        public final static String hexString = "8000:0:0";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }


    public final static class ORCHID
            extends HSSFColor {
        public final static short index = 0x1c;
        public final static short[] triplet =
                {
                        102, 0, 102
                };
        public final static String hexString = "6666:0:6666";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }


    public final static class CORAL
            extends HSSFColor {
        public final static short index = 0x1d;
        public final static short[] triplet =
                {
                        255, 128, 128
                };
        public final static String hexString = "FFFF:8080:8080";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }


    public final static class ROYAL_BLUE
            extends HSSFColor {
        public final static short index = 0x1e;
        public final static short[] triplet =
                {
                        0, 102, 204
                };
        public final static String hexString = "0:6666:CCCC";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }


    public final static class LIGHT_CORNFLOWER_BLUE
            extends HSSFColor {
        public final static short index = 0x1f;
        public final static short[] triplet =
                {
                        204, 204, 255
                };
        public final static String hexString = "CCCC:CCCC:FFFF";

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return triplet;
        }

        public String getHexString() {
            return hexString;
        }
    }


    public final static class AUTOMATIC extends HSSFColor {
        public final static short index = 0x40;
        private static HSSFColor instance = new AUTOMATIC();

        public static HSSFColor getInstance() {
            return instance;
        }

        public short getIndex() {
            return index;
        }

        public short[] getTriplet() {
            return BLACK.triplet;
        }

        public String getHexString() {
            return BLACK.hexString;
        }
    }
}
