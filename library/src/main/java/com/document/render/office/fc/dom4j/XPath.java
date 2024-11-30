

package com.document.render.office.fc.dom4j;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;


public interface XPath extends NodeFilter {

    String getText();


    boolean matches(Node node);


    Object evaluate(Object context);


    Object selectObject(Object context);


    List selectNodes(Object context);


    List selectNodes(Object context, XPath sortXPath);


    List selectNodes(Object context, XPath sortXPath, boolean distinct);


    Node selectSingleNode(Object context);


    String valueOf(Object context);


    Number numberValueOf(Object context);


    boolean booleanValueOf(Object context);


    void sort(List list);


    void sort(List list, boolean distinct);


    NamespaceContext getNamespaceContext();


    void setNamespaceContext(NamespaceContext namespaceContext);


    void setNamespaceURIs(Map map);






}


