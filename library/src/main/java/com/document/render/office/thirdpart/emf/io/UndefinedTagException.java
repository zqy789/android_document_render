
package com.document.render.office.thirdpart.emf.io;

import java.io.IOException;


public class UndefinedTagException extends IOException {


    private static final long serialVersionUID = 7504997713135869344L;


    public UndefinedTagException() {
        super();
    }


    public UndefinedTagException(String msg) {
        super(msg);
    }


    public UndefinedTagException(int code) {
        super("Code: (" + code + ")");
    }
}
