

package com.document.render.office.fc.dom4j.jaxb;

import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.io.OutputFormat;
import com.document.render.office.fc.dom4j.io.XMLWriter;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;



public class JAXBWriter extends JAXBSupport {
    private XMLWriter xmlWriter;

    private OutputFormat outputFormat;


    public JAXBWriter(String contextPath) {
        super(contextPath);
        outputFormat = new OutputFormat();
    }


    public JAXBWriter(String contextPath, OutputFormat outputFormat) {
        super(contextPath);
        this.outputFormat = outputFormat;
    }


    public JAXBWriter(String contextPath, ClassLoader classloader) {
        super(contextPath, classloader);
    }


    public JAXBWriter(String contextPath, ClassLoader classloader, OutputFormat outputFormat) {
        super(contextPath, classloader);
        this.outputFormat = outputFormat;
    }


    public OutputFormat getOutputFormat() {
        return outputFormat;
    }


    public void setOutput(File file) throws IOException {
        getWriter().setOutputStream(new FileOutputStream(file));
    }


    public void setOutput(OutputStream outputStream) throws IOException {
        getWriter().setOutputStream(outputStream);
    }


    public void setOutput(Writer writer) throws IOException {
        getWriter().setWriter(writer);
    }


    public void startDocument() throws IOException, SAXException {
        getWriter().startDocument();
    }


    public void endDocument() throws IOException, SAXException {
        getWriter().endDocument();
    }











    public void writeElement(Element element) throws IOException {
        getWriter().write(element);
    }


    public void writeCloseElement(Element element) throws IOException {
        getWriter().writeClose(element);
    }


    public void writeOpenElement(Element element) throws IOException {
        getWriter().writeOpen(element);
    }

    private XMLWriter getWriter() throws IOException {
        if (xmlWriter == null) {
            if (this.outputFormat != null) {
                xmlWriter = new XMLWriter(outputFormat);
            } else {
                xmlWriter = new XMLWriter();
            }
        }

        return xmlWriter;
    }
}


