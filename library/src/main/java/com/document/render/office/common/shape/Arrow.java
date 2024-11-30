
package com.document.render.office.common.shape;


public class Arrow {
    public static final byte Arrow_None = 0;
    public static final byte Arrow_Triangle = 1;
    public static final byte Arrow_Arrow = 5;
    public static final byte Arrow_Diamond = 3;
    public static final byte Arrow_Stealth = 2;
    public static final byte Arrow_Oval = 4;
    private int width = 1;
    private int length = 1;
    private byte type;

    public Arrow(byte type, int width, int length) {
        this.type = type;
        this.width = width;
        this.length = length;
    }

    public static int getArrowSize(String size) {
        if (size == null || size.equals("med")) {
            return 1;
        }
        if (size.equals("sm")) {
            return 0;
        } else if (size.equals("lg")) {
            return 2;
        } else {
            return 1;
        }
    }


    public static byte getArrowType(String type) {
        if (type != null && type.length() > 0) {
            if ("triangle".equalsIgnoreCase(type)) {
                return Arrow_Triangle;
            } else if ("arrow".equalsIgnoreCase(type)) {
                return Arrow_Arrow;
            } else if ("diamond".equalsIgnoreCase(type)) {
                return Arrow_Diamond;
            } else if ("stealth".equalsIgnoreCase(type)) {
                return Arrow_Stealth;
            } else if ("oval".equalsIgnoreCase(type)) {
                return Arrow_Oval;
            }
        }

        return Arrow_None;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }
}
