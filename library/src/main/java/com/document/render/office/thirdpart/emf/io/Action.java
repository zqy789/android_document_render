
package com.document.render.office.thirdpart.emf.io;

import java.io.IOException;


public abstract class Action {

    private int code;

    private String name;

    protected Action(int code) {
        this.code = code;

        name = getClass().getName();
        int dot = name.lastIndexOf(".");
        name = (dot >= 0) ? name.substring(dot + 1) : name;
    }


    public abstract Action read(int actionCode, TaggedInputStream input,
                                int length) throws IOException;


    public int getCode() {
        return code;
    }


    public String getName() {
        return name;
    }

    public String toString() {
        return "Action " + getName() + " (" + getCode() + ")";
    }


    public static class Unknown extends Action {
        private int[] data;


        public Unknown() {
            super(0x00);
        }


        public Unknown(int actionCode) {
            super(actionCode);
        }

        public Action read(int actionCode, TaggedInputStream input, int length)
                throws IOException {

            Unknown action = new Unknown(actionCode);
            action.data = input.readUnsignedByte(length);
            return action;
        }


        public String toString() {
            return super.toString() + " UNKNOWN!, length " + data.length;
        }
    }

}
