
package com.document.render.office.thirdpart.emf.io;

import java.io.IOException;


public class IncompleteTagException extends IOException {


    private static final long serialVersionUID = -7808675150856818588L;

    private Tag tag;

    private byte[] rest;


    public IncompleteTagException(Tag tag, byte[] rest) {
        super("Tag " + tag + " contains " + rest.length + " unread bytes");
        this.tag = tag;
        this.rest = rest;
    }


    public Tag getTag() {
        return tag;
    }


    public byte[] getBytes() {
        return rest;
    }
}
