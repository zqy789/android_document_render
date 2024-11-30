

package com.document.render.office.fc.dom4j.io;

import com.document.render.office.fc.dom4j.DocumentException;
import com.document.render.office.fc.dom4j.DocumentFactory;
import com.document.render.office.fc.dom4j.ElementHandler;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;





public class XPP3Reader {

    private DocumentFactory factory;








    private DispatchHandler dispatchHandler;

    public XPP3Reader() {
    }

    public XPP3Reader(DocumentFactory factory) {
        this.factory = factory;
    }


    public DocumentFactory getDocumentFactory() {
        if (factory == null) {
            factory = DocumentFactory.getInstance();
        }

        return factory;
    }


    public void setDocumentFactory(DocumentFactory documentFactory) {
        this.factory = documentFactory;
    }


    public void addHandler(String path, ElementHandler handler) {
        getDispatchHandler().addHandler(path, handler);
    }


    public void removeHandler(String path) {
        getDispatchHandler().removeHandler(path);
    }


    public void setDefaultHandler(ElementHandler handler) {
        getDispatchHandler().setDefaultHandler(handler);
    }



    protected DispatchHandler getDispatchHandler() {
        if (dispatchHandler == null) {
            dispatchHandler = new DispatchHandler();
        }

        return dispatchHandler;
    }

    protected void setDispatchHandler(DispatchHandler dispatchHandler) {
        this.dispatchHandler = dispatchHandler;
    }


    protected Reader createReader(InputStream in) throws IOException {
        return new BufferedReader(new InputStreamReader(in));
    }
}


