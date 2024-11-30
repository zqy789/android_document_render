

package com.document.render.office.fc.dom4j.tree;

import java.util.Iterator;
import java.util.NoSuchElementException;


public abstract class FilterIterator implements Iterator {
    protected Iterator proxy;

    private Object next;

    private boolean first = true;

    public FilterIterator(Iterator proxy) {
        this.proxy = proxy;
    }

    public boolean hasNext() {
        if (first) {
            next = findNext();
            first = false;
        }

        return next != null;
    }

    public Object next() throws NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        Object answer = this.next;
        this.next = findNext();

        return answer;
    }


    public void remove() {
        throw new UnsupportedOperationException();
    }


    protected abstract boolean matches(Object element);

    protected Object findNext() {
        if (proxy != null) {
            while (proxy.hasNext()) {
                Object nextObject = proxy.next();

                if ((nextObject != null) && matches(nextObject)) {
                    return nextObject;
                }
            }

            proxy = null;
        }

        return null;
    }
}


