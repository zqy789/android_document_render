

package com.document.render.office.fc.dom4j.util;

import com.document.render.office.fc.dom4j.Attribute;
import com.document.render.office.fc.dom4j.DocumentFactory;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.QName;


public class UserDataDocumentFactory extends DocumentFactory {

    protected static transient UserDataDocumentFactory singleton = new UserDataDocumentFactory();


    public static DocumentFactory getInstance() {
        return singleton;
    }



    public Element createElement(QName qname) {
        return new UserDataElement(qname);
    }

    public Attribute createAttribute(Element owner, QName qname, String value) {
        return new UserDataAttribute(qname, value);
    }
}


