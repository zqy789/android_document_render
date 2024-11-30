

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.Namespace;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.Map;



public class NamespaceCache {
    private static final String CONCURRENTREADERHASHMAP_CLASS = "EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap";

    
    protected static Map cache;

    
    protected static Map noPrefixCache;

    static {
        
        try {
            Class clazz = Class.forName("java.util.concurrent.ConcurrentHashMap");
            Constructor construct = clazz.getConstructor(new Class[]{Integer.TYPE, Float.TYPE,
                    Integer.TYPE});
            cache = (Map) construct.newInstance(new Object[]{new Integer(11), new Float(0.75f),
                    new Integer(1)});
            noPrefixCache = (Map) construct.newInstance(new Object[]{new Integer(11),
                    new Float(0.75f), new Integer(1)});
        } catch (Throwable t1) {
            
            try {
                Class clazz = Class.forName(CONCURRENTREADERHASHMAP_CLASS);
                cache = (Map) clazz.newInstance();
                noPrefixCache = (Map) clazz.newInstance();
            } catch (Throwable t2) {
                
                cache = new ConcurrentReaderHashMap();
                noPrefixCache = new ConcurrentReaderHashMap();
            }
        }
    }

    
    public Namespace get(String prefix, String uri) {
        Map uriCache = getURICache(uri);
        WeakReference ref = (WeakReference) uriCache.get(prefix);
        Namespace answer = null;

        if (ref != null) {
            answer = (Namespace) ref.get();
        }

        if (answer == null) {
            synchronized (uriCache) {
                ref = (WeakReference) uriCache.get(prefix);

                if (ref != null) {
                    answer = (Namespace) ref.get();
                }

                if (answer == null) {
                    answer = createNamespace(prefix, uri);
                    uriCache.put(prefix, new WeakReference(answer));
                }
            }
        }

        return answer;
    }

    
    public Namespace get(String uri) {
        WeakReference ref = (WeakReference) noPrefixCache.get(uri);
        Namespace answer = null;

        if (ref != null) {
            answer = (Namespace) ref.get();
        }

        if (answer == null) {
            synchronized (noPrefixCache) {
                ref = (WeakReference) noPrefixCache.get(uri);

                if (ref != null) {
                    answer = (Namespace) ref.get();
                }

                if (answer == null) {
                    answer = createNamespace("", uri);
                    noPrefixCache.put(uri, new WeakReference(answer));
                }
            }
        }

        return answer;
    }

    
    protected Map getURICache(String uri) {
        Map answer = (Map) cache.get(uri);

        if (answer == null) {
            synchronized (cache) {
                answer = (Map) cache.get(uri);

                if (answer == null) {
                    answer = new ConcurrentReaderHashMap();
                    cache.put(uri, answer);
                }
            }
        }

        return answer;
    }

    
    protected Namespace createNamespace(String prefix, String uri) {
        return new Namespace(prefix, uri);
    }
}


