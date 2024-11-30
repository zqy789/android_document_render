

package com.document.render.office.fc.dom4j.io;

import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.Node;

import org.xml.sax.InputSource;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

import javax.xml.transform.sax.SAXSource;



public class DocumentSource extends SAXSource {

    public static final String DOM4J_FEATURE = "http://org.dom4j.io.DoucmentSource/feature";


    private XMLReader xmlReader = new SAXWriter();


    public DocumentSource(Node node) {
        setDocument(node.getDocument());
    }


    public DocumentSource(Document document) {
        setDocument(document);
    }





    public Document getDocument() {
        DocumentInputSource source = (DocumentInputSource) getInputSource();
        return source.getDocument();
    }


    public void setDocument(Document document) {
        super.setInputSource(new DocumentInputSource(document));
    }





    public XMLReader getXMLReader() {
        return xmlReader;
    }


    public void setXMLReader(XMLReader reader) throws UnsupportedOperationException {
        if (reader instanceof SAXWriter) {
            this.xmlReader = (SAXWriter) reader;
        } else if (reader instanceof XMLFilter) {
            XMLFilter filter = (XMLFilter) reader;

            while (true) {
                XMLReader parent = filter.getParent();

                if (parent instanceof XMLFilter) {
                    filter = (XMLFilter) parent;
                } else {
                    break;
                }
            }


            filter.setParent(xmlReader);
            xmlReader = filter;
        } else {
            throw new UnsupportedOperationException();
        }
    }


    public void setInputSource(InputSource inputSource) throws UnsupportedOperationException {
        if (inputSource instanceof DocumentInputSource) {
            super.setInputSource((DocumentInputSource) inputSource);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}


