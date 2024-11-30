

package com.document.render.office.fc.dom4j.util;

import java.lang.ref.WeakReference;



public class PerThreadSingleton implements SingletonStrategy {
    private String singletonClassName = null;

    private ThreadLocal perThreadCache = new ThreadLocal();

    public PerThreadSingleton() {
    }

    public void reset() {
        perThreadCache = new ThreadLocal();
    }

    public Object instance() {
        Object singletonInstancePerThread = null;

        WeakReference ref = (WeakReference) perThreadCache.get();


        if (ref == null || ref.get() == null) {
            Class clazz = null;
            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(
                        singletonClassName);
                singletonInstancePerThread = clazz.newInstance();
            } catch (Exception ignore) {
                try {
                    clazz = Class.forName(singletonClassName);
                    singletonInstancePerThread = clazz.newInstance();
                } catch (Exception ignore2) {
                }
            }
            perThreadCache.set(new WeakReference(singletonInstancePerThread));
        } else {
            singletonInstancePerThread = ref.get();
        }
        return singletonInstancePerThread;
    }

    public void setSingletonClassName(String singletonClassName) {
        this.singletonClassName = singletonClassName;
    }

}


