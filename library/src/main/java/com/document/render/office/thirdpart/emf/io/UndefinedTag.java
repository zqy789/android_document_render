
package com.document.render.office.thirdpart.emf.io;

import java.io.IOException;


public class UndefinedTag extends Tag {

    private int[] bytes;


    public UndefinedTag() {
        this(DEFAULT_TAG, new int[0]);
    }


    public UndefinedTag(int tagID, int[] bytes) {
        super(tagID, 3);
        this.bytes = bytes;
    }

    public int getTagType() {
        return 0;
    }

    public Tag read(int tagID, TaggedInputStream input, int len)
            throws IOException {

        int[] bytes = input.readUnsignedByte(len);
        UndefinedTag tag = new UndefinedTag(tagID, bytes);
        return tag;
    }

    public String toString() {
        return ("UNDEFINED TAG: " + getTag() + " length: " + bytes.length);
    }
}
