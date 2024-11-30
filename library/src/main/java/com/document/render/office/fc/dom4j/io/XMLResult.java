

package com.document.render.office.fc.dom4j.io;

import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.transform.sax.SAXResult;


public class XMLResult extends SAXResult {
    private XMLWriter xmlWriter;

    public XMLResult() {
        this(new XMLWriter());
    }

    public XMLResult(Writer writer) {
        this(new XMLWriter(writer));
    }

    public XMLResult(Writer writer, OutputFormat format) {
        this(new XMLWriter(writer, format));
    }

    public XMLResult(OutputStream out) throws UnsupportedEncodingException {
        this(new XMLWriter(out));
    }

    public XMLResult(OutputStream out, OutputFormat format)
            throws UnsupportedEncodingException {
        this(new XMLWriter(out, format));
    }

    public XMLResult(XMLWriter xmlWriter) {
        super(xmlWriter);
        this.xmlWriter = xmlWriter;
        setLexicalHandler(xmlWriter);
    }

    public XMLWriter getXMLWriter() {
        return xmlWriter;
    }

    public void setXMLWriter(XMLWriter writer) {
        this.xmlWriter = writer;
        setHandler(xmlWriter);
        setLexicalHandler(xmlWriter);
    }

    public ContentHandler getHandler() {
        return xmlWriter;
    }

    public LexicalHandler getLexicalHandler() {
        return xmlWriter;
    }
}


