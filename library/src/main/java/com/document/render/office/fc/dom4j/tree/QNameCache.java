

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.DocumentFactory;
import com.document.render.office.fc.dom4j.Namespace;
import com.document.render.office.fc.dom4j.QName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;



public class QNameCache {

    protected Map noNamespaceCache = Collections.synchronizedMap(new WeakHashMap());


    protected Map namespaceCache = Collections.synchronizedMap(new WeakHashMap());


    private DocumentFactory documentFactory;

    public QNameCache() {
    }

    public QNameCache(DocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }


    public List getQNames() {
        List answer = new ArrayList();
        answer.addAll(noNamespaceCache.values());

        for (Iterator it = namespaceCache.values().iterator(); it.hasNext(); ) {
            Map map = (Map) it.next();
            answer.addAll(map.values());
        }

        return answer;
    }


    public QName get(String name) {
        QName answer = null;

        if (name != null) {
            answer = (QName) noNamespaceCache.get(name);
        } else {
            name = "";
        }

        if (answer == null) {
            answer = createQName(name);
            answer.setDocumentFactory(documentFactory);
            noNamespaceCache.put(name, answer);
        }

        return answer;
    }


    public QName get(String name, Namespace namespace) {
        Map cache = getNamespaceCache(namespace);
        QName answer = null;

        if (name != null) {
            answer = (QName) cache.get(name);
        } else {
            name = "";
        }

        if (answer == null) {
            answer = createQName(name, namespace);
            answer.setDocumentFactory(documentFactory);
            cache.put(name, answer);
        }

        return answer;
    }


    public QName get(String localName, Namespace namespace, String qName) {
        Map cache = getNamespaceCache(namespace);
        QName answer = null;

        if (localName != null) {
            answer = (QName) cache.get(localName);
        } else {
            localName = "";
        }

        if (answer == null) {
            answer = createQName(localName, namespace, qName);
            answer.setDocumentFactory(documentFactory);
            cache.put(localName, answer);
        }

        return answer;
    }

    public QName get(String qualifiedName, String uri) {
        int index = qualifiedName.indexOf(':');

        if (index < 0) {
            return get(qualifiedName, Namespace.get(uri));
        } else {
            String name = qualifiedName.substring(index + 1);
            String prefix = qualifiedName.substring(0, index);

            return get(name, Namespace.get(prefix, uri));
        }
    }


    public QName intern(QName qname) {
        return get(qname.getName(), qname.getNamespace(), qname.getQualifiedName());
    }


    protected Map getNamespaceCache(Namespace namespace) {
        if (namespace == Namespace.NO_NAMESPACE) {
            return noNamespaceCache;
        }

        Map answer = null;

        if (namespace != null) {
            answer = (Map) namespaceCache.get(namespace);
        }

        if (answer == null) {
            answer = createMap();
            namespaceCache.put(namespace, answer);
        }

        return answer;
    }


    protected Map createMap() {
        return Collections.synchronizedMap(new HashMap());
    }


    protected QName createQName(String name) {
        return new QName(name);
    }


    protected QName createQName(String name, Namespace namespace) {
        return new QName(name, namespace);
    }


    protected QName createQName(String name, Namespace namespace, String qualifiedName) {
        return new QName(name, namespace, qualifiedName);
    }
}


