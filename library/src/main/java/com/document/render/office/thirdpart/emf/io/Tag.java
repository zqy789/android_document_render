
package com.document.render.office.thirdpart.emf.io;

import java.io.IOException;


public abstract class Tag {


    final public static int DEFAULT_TAG = -1;
    private int tagID;
    private String name;
    private int version;

    protected Tag(int tagID, int version) {
        this.tagID = tagID;
        this.version = version;
        this.name = null;
    }


    public int getTag() {
        return tagID;
    }


    public int getVersion() {
        return version;
    }


    public String getName() {
        if (name == null) {
            name = getClass().getName();
            int dot = name.lastIndexOf(".");
            name = (dot >= 0) ? name.substring(dot + 1) : name;
        }
        return name;
    }


    public int getTagType() {
        return 0;
    }


    public abstract Tag read(int tagID, TaggedInputStream input, int len)
            throws IOException;

    public abstract String toString();
}
