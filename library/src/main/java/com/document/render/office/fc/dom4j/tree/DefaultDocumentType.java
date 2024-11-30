

package com.document.render.office.fc.dom4j.tree;

import java.util.List;


public class DefaultDocumentType extends AbstractDocumentType {

    protected String elementName;


    private String publicID;


    private String systemID;


    private List internalDeclarations;


    private List externalDeclarations;

    public DefaultDocumentType() {
    }


    public DefaultDocumentType(String elementName, String systemID) {
        this.elementName = elementName;
        this.systemID = systemID;
    }


    public DefaultDocumentType(String elementName, String publicID,
                               String systemID) {
        this.elementName = elementName;
        this.publicID = publicID;
        this.systemID = systemID;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }


    public String getPublicID() {
        return publicID;
    }


    public void setPublicID(String publicID) {
        this.publicID = publicID;
    }


    public String getSystemID() {
        return systemID;
    }


    public void setSystemID(String systemID) {
        this.systemID = systemID;
    }

    public List getInternalDeclarations() {
        return internalDeclarations;
    }

    public void setInternalDeclarations(List internalDeclarations) {
        this.internalDeclarations = internalDeclarations;
    }

    public List getExternalDeclarations() {
        return externalDeclarations;
    }

    public void setExternalDeclarations(List externalDeclarations) {
        this.externalDeclarations = externalDeclarations;
    }
}


