

package com.document.render.office.fc.dom4j;

import java.io.IOException;
import java.io.Writer;
import java.util.List;


public interface Node extends Cloneable {



    short ANY_NODE = 0;


    short ELEMENT_NODE = 1;


    short ATTRIBUTE_NODE = 2;


    short TEXT_NODE = 3;


    short CDATA_SECTION_NODE = 4;


    short ENTITY_REFERENCE_NODE = 5;





    short PROCESSING_INSTRUCTION_NODE = 7;


    short COMMENT_NODE = 8;


    short DOCUMENT_NODE = 9;


    short DOCUMENT_TYPE_NODE = 10;







    short NAMESPACE_NODE = 13;


    short UNKNOWN_NODE = 14;


    short MAX_NODE_TYPE = 14;


    boolean supportsParent();


    Element getParent();


    void setParent(Element parent);


    Document getDocument();


    void setDocument(Document document);


    boolean isReadOnly();


    boolean hasContent();


    String getName();


    void setName(String name);


    String getText();


    void setText(String text);


    String getStringValue();


    String getPath();


    String getPath(Element context);


    String getUniquePath();


    String getUniquePath(Element context);


    String asXML();


    void write(Writer writer) throws IOException;


    short getNodeType();


    String getNodeTypeName();


    Node detach();


    List selectNodes(String xpathExpression);


    Object selectObject(String xpathExpression);


    List selectNodes(String xpathExpression, String comparisonXPathExpression);


    List selectNodes(String xpathExpression, String comparisonXPathExpression,
                     boolean removeDuplicates);


    Node selectSingleNode(String xpathExpression);


    String valueOf(String xpathExpression);


    Number numberValueOf(String xpathExpression);


    boolean matches(String xpathExpression);


    XPath createXPath(String xpathExpression) throws InvalidXPathException;


    Node asXPathResult(Element parent);


    void accept(Visitor visitor);


    Object clone();
}


