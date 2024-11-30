

package com.document.render.office.fc.dom4j;


public interface Attribute extends Node {

    QName getQName();


    Namespace getNamespace();


    void setNamespace(Namespace namespace);


    String getNamespacePrefix();


    String getNamespaceURI();


    String getQualifiedName();


    String getValue();


    void setValue(String value);


    Object getData();


    void setData(Object data);
}


