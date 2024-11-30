


package com.document.render.office.fc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class IntMapper<T> {
    private static final int _default_size = 10;
    private List<T> elements;
    private Map<T, Integer> valueKeyMap;



    public IntMapper() {
        this(_default_size);
    }

    public IntMapper(final int initialCapacity) {
        elements = new ArrayList<T>(initialCapacity);
        valueKeyMap = new HashMap<T, Integer>(initialCapacity);
    }


    public boolean add(final T value) {
        int index = elements.size();
        elements.add(value);
        valueKeyMap.put(value, index);
        return true;
    }

    public int size() {
        return elements.size();
    }

    public T get(int index) {
        return elements.get(index);
    }

    public int getIndex(T o) {
        Integer i = valueKeyMap.get(o);
        if (i == null)
            return -1;
        return i.intValue();
    }

    public Iterator<T> iterator() {
        return elements.iterator();
    }
}

