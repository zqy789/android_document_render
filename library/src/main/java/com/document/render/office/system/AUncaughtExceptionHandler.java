
package com.document.render.office.system;

import java.lang.Thread.UncaughtExceptionHandler;


public class AUncaughtExceptionHandler implements UncaughtExceptionHandler {


    private IControl control;


    public AUncaughtExceptionHandler(IControl control) {
        this.control = control;
    }


    public void uncaughtException(Thread thread, final Throwable ex) {
        control.getSysKit().getErrorKit().writerLog(ex);
    }


    public void dispose() {
        control = null;
    }
}
