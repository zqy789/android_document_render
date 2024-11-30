
package com.document.render.office.system;

import java.io.File;

public interface IReader {


    public Object getModel() throws Exception;


    public boolean searchContent(File file, String key) throws Exception;


    public boolean isReaderFinish();


    public void backReader() throws Exception;


    public void abortReader();


    public boolean isAborted();








    public IControl getControl();


    public void dispose();
}
