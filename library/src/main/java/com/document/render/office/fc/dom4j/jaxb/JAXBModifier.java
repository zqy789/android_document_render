

package com.document.render.office.fc.dom4j.jaxb;

import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.DocumentException;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.io.ElementModifier;
import com.document.render.office.fc.dom4j.io.OutputFormat;
import com.document.render.office.fc.dom4j.io.SAXModifier;
import com.document.render.office.fc.dom4j.io.XMLWriter;

import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



public class JAXBModifier extends JAXBSupport {
    private SAXModifier modifier;

    private XMLWriter xmlWriter;

    private boolean pruneElements;

    private OutputFormat outputFormat;

    private HashMap modifiers = new HashMap();


    public JAXBModifier(String contextPath) {
        super(contextPath);
        this.outputFormat = new OutputFormat();
    }


    public JAXBModifier(String contextPath, ClassLoader classloader) {
        super(contextPath, classloader);
        this.outputFormat = new OutputFormat();
    }


    public JAXBModifier(String contextPath, OutputFormat outputFormat) {
        super(contextPath);
        this.outputFormat = outputFormat;
    }


    public JAXBModifier(String contextPath, ClassLoader classloader, OutputFormat outputFormat) {
        super(contextPath, classloader);
        this.outputFormat = outputFormat;
    }


    public Document modify(File source) throws DocumentException, IOException {
        return installModifier().modify(source);
    }


    public Document modify(File source, Charset charset) throws DocumentException, IOException {
        try {
            Reader reader = new InputStreamReader(new FileInputStream(source), charset);

            return installModifier().modify(reader);
        } catch (JAXBRuntimeException ex) {
            Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        } catch (FileNotFoundException ex) {
            throw new DocumentException(ex.getMessage(), ex);
        }
    }


    public Document modify(InputSource source) throws DocumentException, IOException {
        try {
            return installModifier().modify(source);
        } catch (JAXBRuntimeException ex) {
            Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }


    public Document modify(InputStream source) throws DocumentException, IOException {
        try {
            return installModifier().modify(source);
        } catch (JAXBRuntimeException ex) {
            Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }


    public Document modify(InputStream source, String systemId) throws DocumentException,
            IOException {
        try {
            return installModifier().modify(source);
        } catch (JAXBRuntimeException ex) {
            Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }


    public Document modify(Reader r) throws DocumentException, IOException {
        try {
            return installModifier().modify(r);
        } catch (JAXBRuntimeException ex) {
            Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }


    public Document modify(Reader source, String systemId) throws DocumentException, IOException {
        try {
            return installModifier().modify(source);
        } catch (JAXBRuntimeException ex) {
            Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }


    public Document modify(String url) throws DocumentException, IOException {
        try {
            return installModifier().modify(url);
        } catch (JAXBRuntimeException ex) {
            Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }


    public Document modify(URL source) throws DocumentException, IOException {
        try {
            return installModifier().modify(source);
        } catch (JAXBRuntimeException ex) {
            Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }


    public void setOutput(File file) throws IOException {
        createXMLWriter().setOutputStream(new FileOutputStream(file));
    }


    public void setOutput(OutputStream outputStream) throws IOException {
        createXMLWriter().setOutputStream(outputStream);
    }


    public void setOutput(Writer writer) throws IOException {
        createXMLWriter().setWriter(writer);
    }


    public void addObjectModifier(String path, JAXBObjectModifier mod) {
        modifiers.put(path, mod);
    }


    public void removeObjectModifier(String path) {
        modifiers.remove(path);
        getModifier().removeModifier(path);
    }


    public void resetObjectModifiers() {
        modifiers.clear();
        getModifier().resetModifiers();
    }


    public boolean isPruneElements() {
        return pruneElements;
    }


    public void setPruneElements(boolean pruneElements) {
        this.pruneElements = pruneElements;
    }

    private SAXModifier installModifier() throws IOException {
        modifier = new SAXModifier(isPruneElements());

        modifier.resetModifiers();

        Iterator modifierIt = modifiers.entrySet().iterator();

        while (modifierIt.hasNext()) {
            Map.Entry entry = (Map.Entry) modifierIt.next();
            ElementModifier mod = new JAXBElementModifier(this,
                    (JAXBObjectModifier) entry.getValue());
            getModifier().addModifier((String) entry.getKey(), mod);
        }

        modifier.setXMLWriter(getXMLWriter());

        return modifier;
    }

    private SAXModifier getModifier() {
        if (this.modifier == null) {
            modifier = new SAXModifier(isPruneElements());
        }

        return modifier;
    }

    private XMLWriter getXMLWriter() {
        return xmlWriter;
    }

    private XMLWriter createXMLWriter() throws IOException {
        if (this.xmlWriter == null) {
            xmlWriter = new XMLWriter(outputFormat);
        }

        return xmlWriter;
    }

    private class JAXBElementModifier implements ElementModifier {
        private JAXBModifier jaxbModifier;

        private JAXBObjectModifier objectModifier;

        public JAXBElementModifier(JAXBModifier jaxbModifier, JAXBObjectModifier objectModifier) {
            this.jaxbModifier = jaxbModifier;
            this.objectModifier = objectModifier;
        }

        public Element modifyElement(Element element) throws Exception {

            return null;
        }
    }
}


