
package com.document.render.office.system;

import java.io.File;



public class AbstractReader implements IReader {


    protected boolean abortReader;

    protected IControl control;


    public Object getModel() throws Exception {
        return null;
    }


    public boolean searchContent(File file, String key) throws Exception {
        return false;
    }


    public boolean isReaderFinish() {
        return true;
    }


    public void backReader() throws Exception {
    }





    public void abortReader() {
        abortReader = true;
    }


    public boolean isAborted() {
        return abortReader;
    }


    public IControl getControl() {
        return this.control;
    }


    public void dispose() {
        control = null;
    }

}
