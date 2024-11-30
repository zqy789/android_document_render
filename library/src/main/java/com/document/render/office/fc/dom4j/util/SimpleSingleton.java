

package com.document.render.office.fc.dom4j.util;



public class SimpleSingleton implements SingletonStrategy {
    private String singletonClassName = null;

    private Object singletonInstance = null;

    public SimpleSingleton() {
    }

    public Object instance() {
        return singletonInstance;
    }

    public void reset() {
        if (singletonClassName != null) {
            Class clazz = null;
            try {
                clazz = Thread.currentThread().getContextClassLoader()
                        .loadClass(singletonClassName);
                singletonInstance = clazz.newInstance();
            } catch (Exception ignore) {
                try {
                    clazz = Class.forName(singletonClassName);
                    singletonInstance = clazz.newInstance();
                } catch (Exception ignore2) {
                }
            }

        }
    }

    public void setSingletonClassName(String singletonClassName) {
        this.singletonClassName = singletonClassName;
        reset();
    }

}


