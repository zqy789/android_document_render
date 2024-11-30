

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.Element;

import java.util.Iterator;



public class ElementIterator extends FilterIterator {
    public ElementIterator(Iterator proxy) {
        super(proxy);
    }


    protected boolean matches(Object element) {
        return element instanceof Element;
    }
}


