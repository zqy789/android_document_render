

package com.document.render.office.fc.dom4j.util;

import com.document.render.office.fc.dom4j.DocumentFactory;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.QName;


public class IndexedDocumentFactory extends DocumentFactory {

    protected static transient IndexedDocumentFactory singleton = new IndexedDocumentFactory();


    public static DocumentFactory getInstance() {
        return singleton;
    }



    public Element createElement(QName qname) {
        return new IndexedElement(qname);
    }

    public Element createElement(QName qname, int attributeCount) {
        return new IndexedElement(qname, attributeCount);
    }
}


