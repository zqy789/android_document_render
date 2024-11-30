

package com.document.render.office.fc.dom4j;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


public interface Element extends Branch {




    QName getQName();


    void setQName(QName qname);


    Namespace getNamespace();


    QName getQName(String qualifiedName);


    Namespace getNamespaceForPrefix(String prefix);


    Namespace getNamespaceForURI(String uri);


    List getNamespacesForURI(String uri);


    String getNamespacePrefix();


    String getNamespaceURI();


    String getQualifiedName();


    List additionalNamespaces();


    List declaredNamespaces();





    Element addAttribute(String name, String value);


    Element addAttribute(QName qName, String value);


    Element addComment(String comment);


    Element addCDATA(String cdata);


    Element addEntity(String name, String text);


    Element addNamespace(String prefix, String uri);


    Element addProcessingInstruction(String target, String text);


    Element addProcessingInstruction(String target, Map data);


    Element addText(String text);





    void add(Attribute attribute);


    void add(CDATA cdata);


    void add(Entity entity);


    void add(Text text);


    void add(Namespace namespace);


    boolean remove(Attribute attribute);


    boolean remove(CDATA cdata);


    boolean remove(Entity entity);


    boolean remove(Namespace namespace);


    boolean remove(Text text);





    String getText();


    String getTextTrim();


    String getStringValue();


    Object getData();


    void setData(Object data);





    List attributes();


    void setAttributes(List attributes);


    int attributeCount();


    Iterator attributeIterator();


    Attribute attribute(int index);


    Attribute attribute(String name);


    Attribute attribute(QName qName);


    String attributeValue(String name);


    String attributeValue(String name, String defaultValue);


    String attributeValue(QName qName);


    String attributeValue(QName qName, String defaultValue);


    void setAttributeValue(String name, String value);


    void setAttributeValue(QName qName, String value);





    Element element(String name);


    Element element(QName qName);


    List elements();


    List elements(String name);


    List elements(QName qName);


    Iterator elementIterator();


    Iterator elementIterator(String name);


    Iterator elementIterator(QName qName);





    boolean isRootElement();


    boolean hasMixedContent();


    boolean isTextOnly();


    void appendAttributes(Element element);


    Element createCopy();


    Element createCopy(String name);


    Element createCopy(QName qName);

    String elementText(String name);

    String elementText(QName qname);

    String elementTextTrim(String name);

    String elementTextTrim(QName qname);


    Node getXPathResult(int index);
}


