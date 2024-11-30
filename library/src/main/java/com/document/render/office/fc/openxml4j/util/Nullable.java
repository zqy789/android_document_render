

package com.document.render.office.fc.openxml4j.util;


public final class Nullable<E> {

    private E value;


    public Nullable() {

    }


    public Nullable(E value) {
        this.value = value;
    }


    public E getValue() {
        return value;
    }


    public boolean hasValue() {
        return value != null;
    }


    public void nullify() {
        value = null;
    }
}
