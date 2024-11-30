

package com.document.render.office.fc.dom4j.util;

import com.document.render.office.fc.dom4j.DocumentFactory;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.QName;


public class NonLazyDocumentFactory extends DocumentFactory {

    protected static transient NonLazyDocumentFactory singleton = new NonLazyDocumentFactory();


    public static DocumentFactory getInstance() {
        return singleton;
    }



    public Element createElement(QName qname) {
        return new NonLazyElement(qname);
    }
}


