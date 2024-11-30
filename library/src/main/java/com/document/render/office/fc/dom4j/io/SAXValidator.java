

package com.document.render.office.fc.dom4j.io;

import com.document.render.office.fc.dom4j.Document;

import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;



public class SAXValidator {

    private XMLReader xmlReader;


    private ErrorHandler errorHandler;

    public SAXValidator() {
    }

    public SAXValidator(XMLReader xmlReader) {
        this.xmlReader = xmlReader;
    }


    public void validate(Document document) throws SAXException {
        if (document != null) {
            XMLReader reader = getXMLReader();

            if (errorHandler != null) {
                reader.setErrorHandler(errorHandler);
            }

            try {
                reader.parse(new DocumentInputSource(document));
            } catch (IOException e) {
                throw new RuntimeException("Caught and exception that should " + "never happen: "
                        + e);
            }
        }
    }





    public XMLReader getXMLReader() throws SAXException {
        if (xmlReader == null) {
            xmlReader = createXMLReader();
            configureReader();
        }

        return xmlReader;
    }


    public void setXMLReader(XMLReader reader) throws SAXException {
        this.xmlReader = reader;
        configureReader();
    }


    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }


    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }





    protected XMLReader createXMLReader() throws SAXException {
        return SAXHelper.createXMLReader(true);
    }


    protected void configureReader() throws SAXException {
        ContentHandler handler = xmlReader.getContentHandler();

        if (handler == null) {
            xmlReader.setContentHandler(new DefaultHandler());
        }

        // configure validation support
        xmlReader.setFeature("http://xml.org/sax/features/validation", true);

        // configure namespace support
        xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
        xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
    }
}


