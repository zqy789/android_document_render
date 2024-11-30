

package com.document.render.office.fc.dom4j;

import org.xml.sax.EntityResolver;

import java.util.Map;


public interface Document extends Branch {

    Element getRootElement();


    void setRootElement(Element rootElement);


    Document addComment(String comment);


    Document addProcessingInstruction(String target, String text);


    Document addProcessingInstruction(String target, Map data);


    Document addDocType(String name, String publicId, String systemId);


    DocumentType getDocType();


    void setDocType(DocumentType docType);


    EntityResolver getEntityResolver();


    void setEntityResolver(EntityResolver entityResolver);


    String getXMLEncoding();


    void setXMLEncoding(String encoding);
}


