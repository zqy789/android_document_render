

package com.document.render.office.fc.dom4j.jaxb;

import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.DocumentException;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.ElementHandler;
import com.document.render.office.fc.dom4j.ElementPath;
import com.document.render.office.fc.dom4j.io.SAXReader;

import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;



public class JAXBReader extends JAXBSupport {
    private SAXReader reader;

    private boolean pruneElements;


    public JAXBReader(String contextPath) {
        super(contextPath);
    }


    public JAXBReader(String contextPath, ClassLoader classloader) {
        super(contextPath, classloader);
    }


    public Document read(File source) throws DocumentException {
        return getReader().read(source);
    }


    public Document read(File file, Charset charset) throws DocumentException {
        try {
            Reader xmlReader = new InputStreamReader(new FileInputStream(file), charset);

            return getReader().read(xmlReader);
        } catch (JAXBRuntimeException ex) {
            Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        } catch (FileNotFoundException ex) {
            throw new DocumentException(ex.getMessage(), ex);
        }
    }


    public Document read(InputSource source) throws DocumentException {
        try {
            return getReader().read(source);
        } catch (JAXBRuntimeException ex) {
            Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }


    public Document read(InputStream source) throws DocumentException {
        try {
            return getReader().read(source);
        } catch (JAXBRuntimeException ex) {
            Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }


    public Document read(InputStream source, String systemId) throws DocumentException {
        try {
            return getReader().read(source);
        } catch (JAXBRuntimeException ex) {
            Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }


    public Document read(Reader source) throws DocumentException {
        try {
            return getReader().read(source);
        } catch (JAXBRuntimeException ex) {
            Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }


    public Document read(Reader source, String systemId) throws DocumentException {
        try {
            return getReader().read(source);
        } catch (JAXBRuntimeException ex) {
            Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }


    public Document read(String source) throws DocumentException {
        try {
            return getReader().read(source);
        } catch (JAXBRuntimeException ex) {
            Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }


    public Document read(URL source) throws DocumentException {
        try {
            return getReader().read(source);
        } catch (JAXBRuntimeException ex) {
            Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }


    public void addObjectHandler(String path, JAXBObjectHandler handler) {
        ElementHandler eHandler = new UnmarshalElementHandler(this, handler);
        getReader().addHandler(path, eHandler);
    }


    public void removeObjectHandler(String path) {
        getReader().removeHandler(path);
    }


    public void addHandler(String path, ElementHandler handler) {
        getReader().addHandler(path, handler);
    }


    public void removeHandler(String path) {
        getReader().removeHandler(path);
    }


    public void resetHandlers() {
        getReader().resetHandlers();
    }


    public boolean isPruneElements() {
        return pruneElements;
    }


    public void setPruneElements(boolean pruneElements) {
        this.pruneElements = pruneElements;

        if (pruneElements) {
            getReader().setDefaultHandler(new PruningElementHandler());
        }
    }

    private SAXReader getReader() {
        if (reader == null) {
            reader = new SAXReader();
        }

        return reader;
    }

    private class UnmarshalElementHandler implements ElementHandler {
        private JAXBReader jaxbReader;

        private JAXBObjectHandler handler;

        public UnmarshalElementHandler(JAXBReader documentReader, JAXBObjectHandler handler) {
            this.jaxbReader = documentReader;
            this.handler = handler;
        }

        public void onStart(ElementPath elementPath) {
        }

        public void onEnd(ElementPath elementPath) {

        }
    }

    private class PruningElementHandler implements ElementHandler {
        public PruningElementHandler() {
        }

        public void onStart(ElementPath parm1) {
        }

        public void onEnd(ElementPath elementPath) {
            Element elem = elementPath.getCurrent();
            elem.detach();
            elem = null;
        }
    }
}


