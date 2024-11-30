package com.document.render.office.pg.animate;

public class ShapeAnimation {

    public static final byte SA_ENTR = 0;

    public static final byte SA_EMPH = 1;

    public static final byte SA_EXIT = 2;

    public static final byte SA_PATH = 3;

    public static final byte SA_VERB = 4;

    public static final byte SA_MEDIACALL = 5;

    public static final int Para_BG = -1;
    public static final int Para_All = -2;
    public static final int Slide = -3;
    private int shapeID;
    private byte animType = -1;

    private int paraBegin = -2;
    private int paraEnd = -2;

    public ShapeAnimation(int shapeID, byte type) {
        this.shapeID = shapeID;
        this.animType = type;
    }

    public ShapeAnimation(int shapeID, byte type, int paraStart, int paraEnd) {
        this.shapeID = shapeID;
        this.animType = type;
        this.paraBegin = paraStart;
        this.paraEnd = paraEnd;
    }


    public int getShapeID() {
        return shapeID;
    }


    public byte getAnimationType() {
        return animType;
    }


    public int getParagraphBegin() {
        return paraBegin;
    }


    public int getParagraphEnd() {
        return paraEnd;
    }
}
