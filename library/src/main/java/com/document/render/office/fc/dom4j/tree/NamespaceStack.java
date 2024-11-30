

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.DocumentFactory;
import com.document.render.office.fc.dom4j.Namespace;
import com.document.render.office.fc.dom4j.QName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class NamespaceStack {

    private DocumentFactory documentFactory;


    private ArrayList namespaceStack = new ArrayList();


    private ArrayList namespaceCacheList = new ArrayList();


    private Map currentNamespaceCache;


    private Map rootNamespaceCache = new HashMap();


    private Namespace defaultNamespace;

    public NamespaceStack() {
        this.documentFactory = DocumentFactory.getInstance();
    }

    public NamespaceStack(DocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }


    public void push(Namespace namespace) {
        namespaceStack.add(namespace);
        namespaceCacheList.add(null);
        currentNamespaceCache = null;

        String prefix = namespace.getPrefix();

        if ((prefix == null) || (prefix.length() == 0)) {
            defaultNamespace = namespace;
        }
    }


    public Namespace pop() {
        return remove(namespaceStack.size() - 1);
    }


    public int size() {
        return namespaceStack.size();
    }


    public void clear() {
        namespaceStack.clear();
        namespaceCacheList.clear();
        rootNamespaceCache.clear();
        currentNamespaceCache = null;
    }


    public Namespace getNamespace(int index) {
        return (Namespace) namespaceStack.get(index);
    }


    public Namespace getNamespaceForPrefix(String prefix) {
        if (prefix == null) {
            prefix = "";
        }

        for (int i = namespaceStack.size() - 1; i >= 0; i--) {
            Namespace namespace = (Namespace) namespaceStack.get(i);

            if (prefix.equals(namespace.getPrefix())) {
                return namespace;
            }
        }

        return null;
    }


    public String getURI(String prefix) {
        Namespace namespace = getNamespaceForPrefix(prefix);

        return (namespace != null) ? namespace.getURI() : null;
    }


    public boolean contains(Namespace namespace) {
        String prefix = namespace.getPrefix();
        Namespace current = null;

        if ((prefix == null) || (prefix.length() == 0)) {
            current = getDefaultNamespace();
        } else {
            current = getNamespaceForPrefix(prefix);
        }

        if (current == null) {
            return false;
        }

        if (current == namespace) {
            return true;
        }

        return namespace.getURI().equals(current.getURI());
    }

    public QName getQName(String namespaceURI, String localName, String qualifiedName) {
        if (localName == null) {
            localName = qualifiedName;
        } else if (qualifiedName == null) {
            qualifiedName = localName;
        }

        if (namespaceURI == null) {
            namespaceURI = "";
        }

        String prefix = "";
        int index = qualifiedName.indexOf(":");

        if (index > 0) {
            prefix = qualifiedName.substring(0, index);

            if (localName.trim().length() == 0) {
                localName = qualifiedName.substring(index + 1);
            }
        } else if (localName.trim().length() == 0) {
            localName = qualifiedName;
        }

        Namespace namespace = createNamespace(prefix, namespaceURI);

        return pushQName(localName, qualifiedName, namespace, prefix);
    }

    public QName getAttributeQName(String namespaceURI, String localName, String qualifiedName) {
        if (qualifiedName == null) {
            qualifiedName = localName;
        }

        Map map = getNamespaceCache();
        QName answer = (QName) map.get(qualifiedName);

        if (answer != null) {
            return answer;
        }

        if (localName == null) {
            localName = qualifiedName;
        }

        if (namespaceURI == null) {
            namespaceURI = "";
        }

        Namespace namespace = null;
        String prefix = "";
        int index = qualifiedName.indexOf(":");

        if (index > 0) {
            prefix = qualifiedName.substring(0, index);
            namespace = createNamespace(prefix, namespaceURI);

            if (localName.trim().length() == 0) {
                localName = qualifiedName.substring(index + 1);
            }
        } else {

            namespace = Namespace.NO_NAMESPACE;

            if (localName.trim().length() == 0) {
                localName = qualifiedName;
            }
        }

        answer = pushQName(localName, qualifiedName, namespace, prefix);
        map.put(qualifiedName, answer);

        return answer;
    }


    public void push(String prefix, String uri) {
        if (uri == null) {
            uri = "";
        }

        Namespace namespace = createNamespace(prefix, uri);
        push(namespace);
    }


    public Namespace addNamespace(String prefix, String uri) {
        Namespace namespace = createNamespace(prefix, uri);
        push(namespace);

        return namespace;
    }


    public Namespace pop(String prefix) {
        if (prefix == null) {
            prefix = "";
        }

        Namespace namespace = null;

        for (int i = namespaceStack.size() - 1; i >= 0; i--) {
            Namespace ns = (Namespace) namespaceStack.get(i);

            if (prefix.equals(ns.getPrefix())) {
                remove(i);
                namespace = ns;

                break;
            }
        }

        if (namespace == null) {
            System.out.println("Warning: missing namespace prefix ignored: " + prefix);
        }

        return namespace;
    }

    public String toString() {
        return super.toString() + " Stack: " + namespaceStack.toString();
    }

    public DocumentFactory getDocumentFactory() {
        return documentFactory;
    }

    public void setDocumentFactory(DocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    public Namespace getDefaultNamespace() {
        if (defaultNamespace == null) {
            defaultNamespace = findDefaultNamespace();
        }

        return defaultNamespace;
    }





    protected QName pushQName(String localName, String qualifiedName, Namespace namespace,
                              String prefix) {
        if ((prefix == null) || (prefix.length() == 0)) {
            this.defaultNamespace = null;
        }

        return createQName(localName, qualifiedName, namespace);
    }


    protected QName createQName(String localName, String qualifiedName, Namespace namespace) {
        return documentFactory.createQName(localName, namespace);
    }


    protected Namespace createNamespace(String prefix, String namespaceURI) {
        return documentFactory.createNamespace(prefix, namespaceURI);
    }


    protected Namespace findDefaultNamespace() {
        for (int i = namespaceStack.size() - 1; i >= 0; i--) {
            Namespace namespace = (Namespace) namespaceStack.get(i);

            if (namespace != null) {
                String prefix = namespace.getPrefix();

                if ((prefix == null) || (namespace.getPrefix().length() == 0)) {
                    return namespace;
                }
            }
        }

        return null;
    }


    protected Namespace remove(int index) {
        Namespace namespace = (Namespace) namespaceStack.remove(index);
        namespaceCacheList.remove(index);
        defaultNamespace = null;
        currentNamespaceCache = null;

        return namespace;
    }

    protected Map getNamespaceCache() {
        if (currentNamespaceCache == null) {
            int index = namespaceStack.size() - 1;

            if (index < 0) {
                currentNamespaceCache = rootNamespaceCache;
            } else {
                currentNamespaceCache = (Map) namespaceCacheList.get(index);

                if (currentNamespaceCache == null) {
                    currentNamespaceCache = new HashMap();
                    namespaceCacheList.set(index, currentNamespaceCache);
                }
            }
        }

        return currentNamespaceCache;
    }
}


