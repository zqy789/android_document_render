

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.Element;

import java.util.Iterator;



public class ElementNameIterator extends FilterIterator {
    private String name;

    public ElementNameIterator(Iterator proxy, String name) {
        super(proxy);
        this.name = name;
    }


    protected boolean matches(Object object) {
        if (object instanceof Element) {
            Element element = (Element) object;

            return name.equals(element.getName());
        }

        return false;
    }
}


