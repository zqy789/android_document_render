
package com.document.render.office.thirdpart.emf.io;


public class TagHeader {

    int tagID;

    long length;


    public TagHeader(int tagID, long length) {
        this.tagID = tagID;
        this.length = length;
    }


    public int getTag() {
        return tagID;
    }


    public void setTag(int tagID) {
        this.tagID = tagID;
    }


    public long getLength() {
        return length;
    }


    public void setLength(long length) {
        this.length = length;
    }
}
