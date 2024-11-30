

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.QName;

import java.util.Iterator;



public class ElementQNameIterator extends FilterIterator {
    private QName qName;

    public ElementQNameIterator(Iterator proxy, QName qName) {
        super(proxy);
        this.qName = qName;
    }


    protected boolean matches(Object object) {
        if (object instanceof Element) {
            Element element = (Element) object;

            return qName.equals(element.getQName());
        }

        return false;
    }
}


