

package com.document.render.office.fc.dom4j;

import java.util.List;


public interface DocumentType extends Node {

    String getElementName();


    void setElementName(String elementName);

    String getPublicID();

    void setPublicID(String publicID);

    String getSystemID();

    void setSystemID(String systemID);


    List getInternalDeclarations();


    void setInternalDeclarations(List declarations);


    List getExternalDeclarations();


    void setExternalDeclarations(List declarations);
}


