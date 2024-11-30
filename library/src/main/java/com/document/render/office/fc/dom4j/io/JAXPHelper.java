

package com.document.render.office.fc.dom4j.io;

import org.xml.sax.XMLReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


class JAXPHelper {
    protected JAXPHelper() {
    }


    public static XMLReader createXMLReader(boolean validating,
                                            boolean namespaceAware) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(validating);
        factory.setNamespaceAware(namespaceAware);

        SAXParser parser = factory.newSAXParser();

        return parser.getXMLReader();
    }

    public static org.w3c.dom.Document createDocument(boolean validating,
                                                      boolean namespaceAware) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(validating);
        factory.setNamespaceAware(namespaceAware);

        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.newDocument();
    }
}


