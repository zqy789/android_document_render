
package com.document.render.office.simpletext.model;



public class ElementCollectionImpl implements IElementCollection {

    public static final int CAPACITY = 5;

    protected IElement[] elems;
    private int size;


    public ElementCollectionImpl(int capacity) {
        elems = new IElement[capacity];
    }


    public void addElement(IElement element) {
        if (size >= elems.length) {
            ensureCapacity();
        }
        elems[size] = element;
        size++;
    }


    public void insertElementForIndex(IElement element, int index) {
        if (size + 1 >= elems.length) {
            ensureCapacity();
        }
        for (int i = size; i >= index; i--) {
            elems[i] = elems[i - 1];
        }
        elems[index] = element;
        size++;
    }


    public void removeElement(long offset) {
        int index = getIndex(offset);
        if (index < 0) {
            return;
        }
        removeElement(index);
    }


    public void removeElementForIndex(int index) {
        if (index < 0) {
            return;
        }
        IElement e = elems[index];
        for (int i = index + 1; i < size; i++) {
            elems[i - 1] = elems[i];
        }
        elems[size] = null;
        size--;
        e.dispose();
    }


    public IElement getElement(long offset) {
        return getElementForIndex(getIndex(offset));
    }


    public IElement getElementForIndex(int index) {
        if (index >= 0 && index < size) {
            return elems[index];
        }
        return null;
    }


    public int size() {
        return size;
    }


    protected int getIndex(long offset) {
        if (size == 0 || offset < 0 || offset >= elems[size - 1].getEndOffset()) {
            return -1;
        }
        int max = size;
        int min = 0;
        IElement element;
        long start;
        long end;
        int mid = -1;
        while (true) {
            mid = (max + min) / 2;
            element = elems[mid];
            start = element.getStartOffset();
            end = element.getEndOffset();
            if (offset >= start && offset < end) {
                break;
            } else if (start > offset) {
                max = mid - 1;
            } else if (end <= offset) {
                min = mid + 1;
            }
        }
        return mid;
    }


    private void ensureCapacity() {
        int len = size + CAPACITY;
        IElement[] e = new IElement[len];
        System.arraycopy(elems, 0, e, 0, size);
        elems = e;
    }


    @Override
    public void dispose() {
        if (elems != null) {
            for (int i = 0; i < size; i++) {
                elems[i].dispose();
                elems[i] = null;
            }
            elems = null;
        }
        size = 0;
    }
}
