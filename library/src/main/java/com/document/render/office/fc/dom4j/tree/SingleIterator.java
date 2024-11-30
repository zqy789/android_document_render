

package com.document.render.office.fc.dom4j.tree;

import java.util.Iterator;


public class SingleIterator implements Iterator {
    private boolean first = true;

    private Object object;

    public SingleIterator(Object object) {
        this.object = object;
    }

    public boolean hasNext() {
        return first;
    }

    public Object next() {
        Object answer = object;
        object = null;
        first = false;

        return answer;
    }

    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported by "
                + "this iterator");
    }
}


