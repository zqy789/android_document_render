
package com.document.render.office.thirdpart.emf.io;


public class ActionHeader {

    int actionCode;

    long length;


    public ActionHeader(int actionCode, long length) {
        this.actionCode = actionCode;
        this.length = length;
    }


    public int getAction() {
        return actionCode;
    }


    public void setAction(int actionCode) {
        this.actionCode = actionCode;
    }


    public long getLength() {
        return length;
    }


    public void setLength(long length) {
        this.length = length;
    }
}
